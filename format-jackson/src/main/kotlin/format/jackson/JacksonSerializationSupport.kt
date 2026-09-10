package dev.akif.tapik.format.jackson

import com.fasterxml.jackson.annotation.JsonFormat
import dev.akif.tapik.common.format.SchemaDerivationException
import tools.jackson.core.JacksonException
import tools.jackson.databind.*
import tools.jackson.databind.introspect.Annotated
import tools.jackson.databind.introspect.AnnotatedMember
import tools.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper
import tools.jackson.databind.ser.BeanPropertyWriter
import tools.jackson.databind.ser.BeanSerializer
import tools.jackson.databind.ser.UnrolledBeanSerializer
import tools.jackson.databind.ser.bean.BeanSerializerBase
import tools.jackson.databind.ser.jdk.*
import tools.jackson.databind.ser.std.StdConvertingSerializer
import tools.jackson.databind.ser.std.ToStringSerializer

internal fun requireSupportedJacksonSerialization(mapper: ObjectMapper, type: JavaType) {
    // Enum schemas are checked against every constant's actual encoded string instead.
    if (type.isEnumType) return
    val location = type.toCanonical()
    val configuration = mapper.serializationConfig()
    val introspector = configuration.classIntrospectorInstance()
    val classDefinition = introspector.introspectClassAnnotations(type)
    val description = introspector.introspectForSerialization(type, classDefinition)
    requireSupportedAnnotations(configuration, description.classInfo, location)
    if (description.findJsonValueAccessor() != null) unsupportedJacksonShape(location, "@JsonValue")

    try {
        mapper.acceptJsonFormatVisitor(type, object : JsonFormatVisitorWrapper.Base() {
            override fun setContext(context: SerializationContext) {
                super.setContext(context)
                val serializer = context.findRootValueSerializer(type)
                requireSupportedSerializer(serializer, type, location)
                if (serializer is BeanSerializerBase) {
                    serializer.properties().forEach { property ->
                        val propertyLocation = "$location.${property.name}"
                        val writer = property as? BeanPropertyWriter
                            ?: unsupportedJacksonShape(propertyLocation, "custom property writer")
                        writer.member?.let { requireSupportedAnnotations(configuration, it, propertyLocation) }
                        if (writer.typeSerializer != null) unsupportedJacksonShape(propertyLocation, "polymorphic serialization")
                        requireSupportedSerializer(
                            writer.serializer ?: context.findPrimaryPropertySerializer(writer.type, writer),
                            writer.type,
                            propertyLocation
                        )
                    }
                }
            }
        })
    } catch (failure: JacksonException) {
        unsupportedJacksonShape(location, failure.message ?: "serializer resolution failed")
    }
}

private fun requireSupportedAnnotations(configuration: SerializationConfig, annotated: Annotated, location: String) {
    val introspector = configuration.annotationIntrospector
    val converter = introspector.findSerializationConverter(configuration, annotated)
    val nullSerializer = introspector.findNullSerializer(configuration, annotated)
    if (introspector.findSerializer(configuration, annotated) != null ||
        introspector.findContentSerializer(configuration, annotated) != null ||
        introspector.findKeySerializer(configuration, annotated) != null ||
        nullSerializer != null && !nullSerializer.isKotlinValueClassSerializer() ||
        converter != null && converter.javaClass.name !in KOTLIN_VALUE_CLASS_CONVERTERS ||
        annotated is AnnotatedMember && introspector.findSerializationContentConverter(configuration, annotated) != null
    ) {
        unsupportedJacksonShape(location, "custom serializer or converter")
    }
    val shape = introspector.findFormat(configuration, annotated)?.shape
    if (shape != null && shape != JsonFormat.Shape.ANY && shape != JsonFormat.Shape.NATURAL) {
        unsupportedJacksonShape(location, "@JsonFormat shape $shape")
    }
}

private fun requireSupportedSerializer(
    serializer: ValueSerializer<*>,
    type: JavaType,
    location: String
) {
    if (type.isEnumType) return
    // Kotlin's boxing converter erases its output type. Schema derivation checks the Kotlin value class and its
    // underlying type separately, including the serializers selected for each. Other converters are unsupported.
    if (serializer.isKotlinValueClassSerializer()) return
    val implementation = serializer.javaClass
    if (implementation !in supportedSerializers(type)) {
        unsupportedJacksonShape(location, "serializer '${implementation.name}'")
    }
}

private fun Any.isKotlinValueClassSerializer(): Boolean =
    this is StdConvertingSerializer && converter.javaClass.name in KOTLIN_VALUE_CLASS_CONVERTERS

private fun supportedSerializers(type: JavaType): Set<Class<*>> =
    when (type.rawClass.kotlin) {
        Boolean::class -> setOf(BooleanSerializer::class.java)
        Byte::class -> setOf(NumberSerializers.IntLikeSerializer::class.java)
        Short::class -> setOf(NumberSerializers.ShortSerializer::class.java)
        Int::class -> setOf(NumberSerializers.IntegerSerializer::class.java)
        Long::class -> setOf(NumberSerializers.LongSerializer::class.java)
        Float::class -> setOf(NumberSerializers.FloatSerializer::class.java)
        Double::class -> setOf(NumberSerializers.DoubleSerializer::class.java)
        Char::class -> setOf(ToStringSerializer::class.java)
        String::class -> setOf(StringSerializer::class.java)
        else -> when {
            type.isMapLikeType -> setOf(MapSerializer::class.java)
            type.isCollectionLikeType -> STANDARD_COLLECTION_SERIALIZERS
            type.isArrayType -> setOf(ObjectArraySerializer::class.java, StringArraySerializer::class.java)
            else -> setOf(BeanSerializer::class.java, UnrolledBeanSerializer::class.java)
        }
    }

private fun unsupportedJacksonShape(location: String, behavior: String): Nothing =
    throw SchemaDerivationException(
        "Unsupported Jackson $behavior at '$location'; provide an explicit schema with jsonFormat(schema = ...) or jsonBody(schema = ...)"
    )

private val STANDARD_COLLECTION_SERIALIZERS: Set<Class<*>> =
    setOf(
        CollectionSerializer::class.java,
        IndexedListSerializer::class.java,
        IndexedStringListSerializer::class.java,
        StringCollectionSerializer::class.java,
        EnumSetSerializer::class.java
    )

private val KOTLIN_VALUE_CLASS_CONVERTERS: Set<String> =
    setOf("Int", "Long", "String", "JavaUuid", "Generic").flatMapTo(linkedSetOf()) { type ->
        listOf("Box", "Unbox").map { operation -> "tools.jackson.module.kotlin.${type}ValueClass${operation}Converter" }
    }
