package dev.akif.tapik.target.spring.restclient

import dev.akif.tapik.common.plugin.KotlinSourceType

internal fun StringBuilder.appendResponse(endpoint: RestClientEndpointModel) {
    appendLine("    public sealed interface ${endpoint.responseName} {")
    endpoint.outputs.forEach { output ->
        val fields = output.fields()
        if (fields.isEmpty()) {
            appendLine("        public data object ${output.variantName} : ${endpoint.responseName}")
        } else {
            appendLine("        public data class ${output.variantName}(")
            fields.forEachIndexed { index, field ->
                val suffix = if (index == fields.lastIndex) "" else ","
                appendLine("            public val ${field.name}: ${field.type.source}$suffix")
            }
            if (fields.any(RestClientResponseField::isByteArray)) {
                appendLine("        ) : ${endpoint.responseName} {")
                appendContentEquals(output.variantName, fields)
                appendLine()
                appendContentHashCode(fields)
                appendLine("        }")
            } else {
                appendLine("        ) : ${endpoint.responseName}")
            }
        }
    }
    appendLine("    }")
}

private fun StringBuilder.appendContentEquals(
    variantName: String,
    fields: List<RestClientResponseField>
) {
    appendLine("            override fun equals(other: kotlin.Any?): kotlin.Boolean =")
    appendLine("                this === other ||")
    appendLine("                    (")
    appendLine("                        other is $variantName &&")
    fields.forEachIndexed { index, field ->
        val suffix = if (index == fields.lastIndex) "" else " &&"
        appendLine("                            ${field.equalsExpression()}$suffix")
    }
    appendLine("                    )")
}

private fun StringBuilder.appendContentHashCode(fields: List<RestClientResponseField>) {
    appendLine("            override fun hashCode(): kotlin.Int {")
    appendLine("                var result = ${fields.first().hashCodeExpression()}")
    fields.drop(1).forEach { field ->
        appendLine("                result = 31 * result + ${field.hashCodeExpression()}")
    }
    appendLine("                return result")
    appendLine("            }")
}

private fun RestClientResponseField.equalsExpression(): String =
    when {
        !isByteArray() -> "$name == other.$name"
        type.expandedType.nullable -> "$name?.contentEquals(other.$name) ?: (other.$name == null)"
        else -> "$name.contentEquals(other.$name)"
    }

private fun RestClientResponseField.hashCodeExpression(): String =
    when {
        isByteArray() && type.expandedType.nullable -> "$name?.contentHashCode() ?: 0"
        isByteArray() -> "$name.contentHashCode()"
        type.expandedType.nullable -> "$name?.hashCode() ?: 0"
        else -> "$name.hashCode()"
    }

private fun RestClientResponseField.isByteArray(): Boolean = type.expandsTo("kotlin.ByteArray")

private fun RestClientOutput.fields(): List<RestClientResponseField> =
    buildList {
        bodies.firstOrNull()?.let { body ->
            val type = if (allowsNoBody) body.type.asNullable() else body.type
            add(RestClientResponseField("body", type))
        }
        headers.forEach { header -> add(RestClientResponseField(header.name, header.type)) }
    }

private data class RestClientResponseField(
    val name: String,
    val type: KotlinSourceType
)
