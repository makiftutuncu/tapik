package dev.akif.tapik.target.openapi

import dev.akif.tapik.*

internal class SchemaRegistry(
    private val componentNaming: OpenApiComponentNaming
) {
    val components: LinkedHashMap<String, OpenApiSchema> = linkedMapOf()
    private val references: MutableSet<String> = linkedSetOf()
    private val active: MutableSet<String> = mutableSetOf()
    private val discriminatedUnions: MutableList<OpenApiSchema> = mutableListOf()

    fun schema(schema: Schema): OpenApiSchema {
        val schemaName = schema.name
        if (schema is ReferenceSchema &&
            (schemaName == null || componentNaming.name(schemaName) == componentNaming.name(schema.reference))
        ) return schemaReference(schema.reference)

        if (schemaName != null) {
            val name = componentNaming.name(schemaName)
            requireValidComponentName(name)
            val existing = components[name]
            if (existing != null) {
                val candidate = inline(schema)
                if (existing != candidate) {
                    throw OpenApiGenerationException(
                        "OpenAPI schema component '$name' has conflicting definitions"
                    )
                }
            } else if (active.add(name)) {
                components[name] = inline(schema)
                active.remove(name)
            }
            return componentReference(name)
        }

        return inline(schema)
    }

    fun requireResolvedReferences() {
        val unresolved = references - components.keys
        if (unresolved.isNotEmpty()) {
            throw OpenApiGenerationException(
                "OpenAPI schemas contain unresolved references: ${unresolved.joinToString()}"
            )
        }
        discriminatedUnions.forEach { union ->
            val discriminator = requireNotNull(union.discriminator)
            if (discriminator.defaultMapping == null) {
                union.oneOf.forEach { alternative ->
                    if (!requiresProperty(alternative, discriminator.propertyName)) {
                        throw OpenApiGenerationException(
                            "OpenAPI discriminator '${discriminator.propertyName}' needs defaultMapping because " +
                                "alternative '${alternative.reference}' does not require the property"
                        )
                    }
                }
            }
        }
    }

    private fun requiresProperty(schema: OpenApiSchema, property: String, visited: Set<String> = emptySet()): Boolean {
        if (property in schema.required) return true
        schema.reference?.let { reference ->
            if (reference in visited) return false
            val target = components.getValue(reference.removePrefix("#/components/schemas/"))
            return requiresProperty(target, property, visited + reference)
        }
        return (schema.oneOf.isNotEmpty() && schema.oneOf.all { requiresProperty(it, property, visited) }) ||
            (schema.anyOf.isNotEmpty() && schema.anyOf.all { requiresProperty(it, property, visited) })
    }

    private fun inline(schema: Schema): OpenApiSchema =
        when (schema) {
            is ScalarSchema ->
                OpenApiSchema(
                    types = listOf(schema.type.openApiType),
                    format = schema.format
                )
            is ArraySchema ->
                OpenApiSchema(
                    types = listOf("array"),
                    items = schema(schema.items)
                )
            is EnumSchema ->
                OpenApiSchema(
                    types = listOf("string"),
                    enumValues = schema.values
                )
            is ObjectSchema ->
                OpenApiSchema(
                    types = listOf("object"),
                    properties =
                        schema.properties.mapValues { (_, property) ->
                            schema(property.schema).copy(deprecated = property.deprecated)
                        },
                    required =
                        schema.properties
                            .filterValues(SchemaProperty::required)
                            .keys
                            .toList()
                )
            is MapSchema ->
                OpenApiSchema(
                    types = listOf("object"),
                    propertyNames = mapKeySchema(schema.keys),
                    additionalProperties = schema(schema.values)
                )
            is NullableSchema ->
                OpenApiSchema(
                    anyOf =
                        listOf(
                            schema(schema.schema),
                            OpenApiSchema(types = listOf("null"))
                        )
                )
            is UnionSchema -> union(schema)
            is ReferenceSchema -> schemaReference(schema.reference)
        }

    private fun union(union: UnionSchema): OpenApiSchema {
        val alternatives = union.alternatives.map(::schema)
        if (alternatives.distinct().size != alternatives.size) {
            throw OpenApiGenerationException(
                "OpenAPI union alternatives must remain unique after component naming"
            )
        }

        val discriminator = union.discriminator?.openApi(alternatives)
        return OpenApiSchema(oneOf = alternatives, discriminator = discriminator).also { schema ->
            if (discriminator != null) discriminatedUnions += schema
        }
    }

    private fun SchemaDiscriminator.openApi(alternatives: List<OpenApiSchema>): OpenApiDiscriminator {
        val alternativeReferences =
            alternatives.map { alternative ->
                alternative.reference
                    ?: throw OpenApiGenerationException(
                        "OpenAPI discriminated union alternatives must be named or referenced schemas"
                    )
            }
        val mapping =
            mapping.mapValues { (_, target) ->
                requireListedDiscriminatorTarget(target, alternativeReferences)
            }
        val defaultMapping =
            defaultMapping?.let { target ->
                requireListedDiscriminatorTarget(target, alternativeReferences)
            }

        return OpenApiDiscriminator(
            propertyName = propertyName,
            mapping = mapping,
            defaultMapping = defaultMapping
        )
    }

    private fun requireListedDiscriminatorTarget(
        target: ReferenceSchema,
        alternatives: List<String>
    ): String {
        val reference = requireNotNull(schema(target).reference)
        if (reference !in alternatives) {
            throw OpenApiGenerationException(
                "OpenAPI discriminator mapping target '${target.reference}' must be a union alternative"
            )
        }
        return reference
    }

    private fun mapKeySchema(keys: Schema): OpenApiSchema =
        when (keys) {
            is ScalarSchema -> {
                if (keys.type != SchemaType.STRING) {
                    throw OpenApiGenerationException(
                        "OpenAPI map keys must use a string schema, but found ${keys.type}"
                    )
                }
                schema(keys)
            }
            is EnumSchema -> schema(keys)
            else ->
                throw OpenApiGenerationException(
                    "Unsupported OpenAPI map key schema '${keys::class.qualifiedName}'"
                )
        }

    private fun schemaReference(name: String): OpenApiSchema =
        componentReference(componentNaming.name(name))

    private fun componentReference(componentName: String): OpenApiSchema {
        requireValidComponentName(componentName)
        references += componentName
        val escaped = componentName.replace("~", "~0").replace("/", "~1")
        return OpenApiSchema(reference = "#/components/schemas/$escaped")
    }

    private fun requireValidComponentName(name: String) {
        if (!COMPONENT_NAME.matches(name)) {
            throw OpenApiGenerationException(
                "OpenAPI schema component name '$name' must match ${COMPONENT_NAME.pattern}"
            )
        }
    }
}

private val COMPONENT_NAME: Regex = Regex("^[A-Za-z0-9._-]+$")

private val SchemaType.openApiType: String
    get() =
        when (this) {
            SchemaType.BOOLEAN -> "boolean"
            SchemaType.INTEGER -> "integer"
            SchemaType.NUMBER -> "number"
            SchemaType.STRING -> "string"
        }
