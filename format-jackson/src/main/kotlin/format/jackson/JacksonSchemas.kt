package dev.akif.tapik.format.jackson

import dev.akif.tapik.*
import dev.akif.tapik.common.format.SchemaDerivationException
import tools.jackson.databind.JavaType
import tools.jackson.databind.ObjectMapper
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.withNullability
import kotlin.reflect.jvm.javaType

/** Derives a Tapik schema from Jackson [format] and Kotlin [type]. */
fun deriveSchema(
    format: ObjectMapper,
    type: KType
): Schema = deriveSchema(format, type, activeObjects = emptySet(), includeNullability = true)

private fun deriveSchema(
    format: ObjectMapper,
    type: KType,
    activeObjects: Set<String>,
    includeNullability: Boolean
): Schema {
    if (includeNullability && type.isMarkedNullable) {
        return NullableSchema(
            deriveSchema(format, type.withNullability(false), activeObjects, includeNullability = false)
        )
    }

    val classifier = type.classifier as? KClass<*>
        ?: throw SchemaDerivationException("Unsupported Jackson Kotlin type '$type'")
    val name = classifier.qualifiedName ?: classifier.java.name

    if (classifier.isValue) {
        val parameter = classifier.primaryConstructor?.parameters?.singleOrNull()
            ?: throw SchemaDerivationException("Value class '$name' must have exactly one constructor parameter")
        return deriveSchema(format, parameter.type, activeObjects, includeNullability = true).named(name)
    }
    if (classifier.isSealed) {
        throw SchemaDerivationException("Unsupported sealed Jackson type '$name'")
    }
    if (name in activeObjects) {
        return ReferenceSchema(name)
    }

    return when (classifier) {
        Boolean::class -> ScalarSchema(SchemaType.BOOLEAN)
        Byte::class,
        Short::class,
        Int::class -> ScalarSchema(SchemaType.INTEGER, format = "int32")
        Long::class -> ScalarSchema(SchemaType.INTEGER, format = "int64")
        Float::class -> ScalarSchema(SchemaType.NUMBER, format = "float")
        Double::class -> ScalarSchema(SchemaType.NUMBER, format = "double")
        Char::class,
        String::class -> ScalarSchema(SchemaType.STRING)
        else -> deriveStructuredSchema(format, type, classifier, name, activeObjects)
    }
}

private fun deriveStructuredSchema(
    format: ObjectMapper,
    type: KType,
    classifier: KClass<*>,
    name: String,
    activeObjects: Set<String>
): Schema =
    when {
        classifier.java.isEnum ->
            EnumSchema(
                values = deriveEnumValues(format, classifier.java, name),
                name = name
            )
        classifier.isSubclassOf(Map::class) ->
            MapSchema(
                keys = deriveTypeArgument(format, type, 0, activeObjects),
                values = deriveTypeArgument(format, type, 1, activeObjects)
            )
        classifier.isSubclassOf(Collection::class) || classifier.java.isArray ->
            ArraySchema(items = deriveTypeArgument(format, type, 0, activeObjects))
        else -> deriveObjectSchema(format, type, classifier, name, activeObjects)
    }

private fun deriveTypeArgument(
    format: ObjectMapper,
    type: KType,
    index: Int,
    activeObjects: Set<String>
): Schema {
    val argument = type.arguments.getOrNull(index)?.type
        ?: throw SchemaDerivationException("Jackson type '$type' requires type argument ${index + 1}")
    return deriveSchema(format, argument, activeObjects, includeNullability = true)
}

private fun deriveObjectSchema(
    format: ObjectMapper,
    type: KType,
    classifier: KClass<*>,
    name: String,
    activeObjects: Set<String>
): ObjectSchema {
    val jacksonType = format.typeFactory.constructType(type.javaType)
    val configuration = format.serializationConfig()
    val introspector = configuration.classIntrospectorInstance()
    val classDefinition = introspector.introspectClassAnnotations(jacksonType)
    val description = introspector.introspectForSerialization(jacksonType, classDefinition)
    val ignored = description.ignoredPropertyNames
    val definitions = description.findProperties()
    val properties = classifier.memberProperties.associateBy(KProperty1<*, *>::name)
    val parameters = classifier.primaryConstructor?.parameters.orEmpty().associateBy(KParameter::name)
    val schemaProperties = linkedMapOf<String, SchemaProperty>()
    val nestedActiveObjects = activeObjects + name

    definitions
        .asSequence()
        .filter { definition -> definition.couldSerialize() && definition.name !in ignored }
        .forEach { definition ->
            val internalName = definition.internalName
            val property = properties[internalName]
            val parameter = parameters[internalName]
            val propertySchema =
                property?.returnType?.let { propertyType ->
                    deriveSchema(format, propertyType, nestedActiveObjects, includeNullability = true)
                } ?: deriveJavaSchema(format, definition.primaryType, nestedActiveObjects)
            val previous =
                schemaProperties.put(
                    definition.name,
                    SchemaProperty(
                        schema = propertySchema,
                        required = parameter?.isOptional == false || parameter == null && definition.isRequired,
                        deprecated =
                            property?.findAnnotation<Deprecated>() != null ||
                                parameter?.findAnnotation<Deprecated>() != null
                    )
                )
            if (previous != null) {
                throw SchemaDerivationException("Duplicate Jackson property '${definition.name}' for '$name'")
            }
        }

    return ObjectSchema(properties = schemaProperties, name = name)
}

private fun deriveJavaSchema(
    format: ObjectMapper,
    type: JavaType,
    activeObjects: Set<String>
): Schema {
    val raw = type.rawClass.kotlin
    val name = raw.qualifiedName ?: type.rawClass.name
    if (name in activeObjects) {
        return ReferenceSchema(name)
    }
    return when {
        type.rawClass.isBooleanType() -> ScalarSchema(SchemaType.BOOLEAN)
        type.rawClass.isIntegralType() ->
            ScalarSchema(
                SchemaType.INTEGER,
                if (type.rawClass == Long::class.java || type.rawClass == Long::class.javaObjectType) "int64" else "int32"
            )
        type.rawClass.isFloatingPointType() ->
            ScalarSchema(
                SchemaType.NUMBER,
                if (type.rawClass == Float::class.java || type.rawClass == Float::class.javaObjectType) "float" else "double"
            )
        type.isEnumType ->
            EnumSchema(deriveEnumValues(format, type.rawClass, name), name)
        type.isMapLikeType ->
            MapSchema(
                keys = deriveJavaSchema(format, type.keyType, activeObjects),
                values = deriveJavaSchema(format, type.contentType, activeObjects)
            )
        type.isCollectionLikeType || type.isArrayType ->
            ArraySchema(deriveJavaSchema(format, type.contentType, activeObjects))
        CharSequence::class.java.isAssignableFrom(type.rawClass) || type.rawClass == Char::class.java ->
            ScalarSchema(SchemaType.STRING)
        else ->
            throw SchemaDerivationException(
                "Jackson property type '$type' on '$name' has no corresponding Kotlin property"
            )
    }
}

private fun deriveEnumValues(
    format: ObjectMapper,
    enumType: Class<*>,
    name: String
): List<String> =
    enumType.enumConstants.map { constant ->
        val encoded = format.readTree(format.writeValueAsBytes(constant))
        if (!encoded.isString) {
            throw SchemaDerivationException("Jackson enum '$name' must serialize as strings")
        }
        encoded.stringValue()
    }

private fun Class<*>.isBooleanType(): Boolean = this == Boolean::class.java || this == Boolean::class.javaObjectType

private fun Class<*>.isIntegralType(): Boolean =
    this == Byte::class.java ||
        this == Byte::class.javaObjectType ||
        this == Short::class.java ||
        this == Short::class.javaObjectType ||
        this == Int::class.java ||
        this == Int::class.javaObjectType ||
        this == Long::class.java ||
        this == Long::class.javaObjectType

private fun Class<*>.isFloatingPointType(): Boolean =
    this == Float::class.java ||
        this == Float::class.javaObjectType ||
        this == Double::class.java ||
        this == Double::class.javaObjectType
