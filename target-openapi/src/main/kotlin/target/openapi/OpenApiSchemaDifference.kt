package dev.akif.tapik.target.openapi

internal data class OpenApiSchemaDifference(
    val path: String,
    val existing: String,
    val candidate: String
)

internal fun OpenApiSchema.firstDifference(
    candidate: OpenApiSchema,
    path: String = "$"
): OpenApiSchemaDifference? {
    difference(path.field("reference"), reference, candidate.reference)?.let { return it }
    difference(path.field("types"), types, candidate.types)?.let { return it }
    difference(path.field("format"), format, candidate.format)?.let { return it }
    difference(path.field("enumValues"), enumValues, candidate.enumValues)?.let { return it }
    nestedDifference(path.field("items"), items, candidate.items)?.let { return it }
    propertiesDifference(path, properties, candidate.properties)?.let { return it }
    difference(path.field("required"), required, candidate.required)?.let { return it }
    nestedDifference(path.field("propertyNames"), propertyNames, candidate.propertyNames)?.let { return it }
    nestedDifference(
        path.field("additionalProperties"),
        additionalProperties,
        candidate.additionalProperties
    )?.let { return it }
    schemaListDifference(path.field("anyOf"), anyOf, candidate.anyOf)?.let { return it }
    schemaListDifference(path.field("oneOf"), oneOf, candidate.oneOf)?.let { return it }
    discriminatorDifference(path.field("discriminator"), discriminator, candidate.discriminator)?.let { return it }
    difference(path.field("deprecated"), deprecated, candidate.deprecated)?.let { return it }
    difference(path.field("defaultValue"), defaultValue, candidate.defaultValue)?.let { return it }
    difference(path.field("constantValue"), constantValue, candidate.constantValue)?.let { return it }
    return null
}

private fun propertiesDifference(
    path: String,
    existing: Map<String, OpenApiSchema>,
    candidate: Map<String, OpenApiSchema>
): OpenApiSchemaDifference? {
    (existing.keys + candidate.keys).sorted().forEach { name ->
        val propertyPath = path.field("properties").field(name)
        val existingProperty = existing[name]
        val candidateProperty = candidate[name]
        nestedDifference(propertyPath, existingProperty, candidateProperty)?.let { return it }
    }
    return null
}

private fun schemaListDifference(
    path: String,
    existing: List<OpenApiSchema>,
    candidate: List<OpenApiSchema>
): OpenApiSchemaDifference? {
    difference(path.field("size"), existing.size, candidate.size)?.let { return it }
    existing.indices.forEach { index ->
        existing[index].firstDifference(candidate[index], "$path[$index]")?.let { return it }
    }
    return null
}

private fun nestedDifference(
    path: String,
    existing: OpenApiSchema?,
    candidate: OpenApiSchema?
): OpenApiSchemaDifference? =
    when {
        existing == null && candidate == null -> null
        existing == null -> OpenApiSchemaDifference(path, "<absent>", "<present>")
        candidate == null -> OpenApiSchemaDifference(path, "<present>", "<absent>")
        else -> existing.firstDifference(candidate, path)
    }

private fun discriminatorDifference(
    path: String,
    existing: OpenApiDiscriminator?,
    candidate: OpenApiDiscriminator?
): OpenApiSchemaDifference? {
    when {
        existing == null && candidate == null -> return null
        existing == null -> return OpenApiSchemaDifference(path, "<absent>", "<present>")
        candidate == null -> return OpenApiSchemaDifference(path, "<present>", "<absent>")
    }

    difference(path.field("propertyName"), existing.propertyName, candidate.propertyName)?.let { return it }
    (existing.mapping.keys + candidate.mapping.keys).sorted().forEach { name ->
        val mappingPath = path.field("mapping").field(name)
        if (name !in existing.mapping) return OpenApiSchemaDifference(mappingPath, "<absent>", "<present>")
        if (name !in candidate.mapping) return OpenApiSchemaDifference(mappingPath, "<present>", "<absent>")
        difference(mappingPath, existing.mapping.getValue(name), candidate.mapping.getValue(name))?.let { return it }
    }
    return difference(path.field("defaultMapping"), existing.defaultMapping, candidate.defaultMapping)
}

private fun difference(
    path: String,
    existing: Any?,
    candidate: Any?
): OpenApiSchemaDifference? =
    if (existing == candidate) null else OpenApiSchemaDifference(path, existing.render(), candidate.render())

private fun Any?.render(): String =
    when (this) {
        null -> "null"
        is String -> "'$this'"
        else -> toString()
    }

private fun String.field(name: String): String =
    if (name.matches(SIMPLE_PATH_SEGMENT)) "$this.$name" else "$this['${name.replace("'", "\\'")}']"

private val SIMPLE_PATH_SEGMENT: Regex = Regex("[A-Za-z_][A-Za-z0-9_]*")
