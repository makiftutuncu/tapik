package dev.akif.tapik.spring.webmvc

import dev.akif.tapik.plugin.core.KotlinSourceType

internal fun StringBuilder.appendResponses(endpoint: WebMvcEndpointModel) {
    appendLine("    public sealed interface ${endpoint.responseName} {")
    endpoint.outputs.forEach { output ->
        val fields = output.fields()
        if (fields.isEmpty()) {
            appendLine("        public data object ${output.variantName} : ${endpoint.responseName}")
        } else {
            appendLine("        public data class ${output.variantName}(")
            fields.forEachIndexed { index, field ->
                val suffix = if (index == fields.lastIndex) "" else ","
                val default = field.defaultExpression?.let { expression -> " = $expression" }.orEmpty()
                appendLine("            public val ${field.name}: ${field.type.source}$default$suffix")
            }
            if (fields.any(WebMvcResponseField::isByteArray)) {
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

private fun WebMvcOutput.fields(): List<WebMvcResponseField> =
    buildList {
        bodies.firstOrNull()?.let { body ->
            val type = if (allowsNoBody) body.type.asNullable() else body.type
            add(WebMvcResponseField("body", type, if (allowsNoBody) "null" else null))
        }
        headers.forEach { header ->
            header.name?.let { name -> add(WebMvcResponseField(name, header.type, header.defaultExpression)) }
        }
    }

private fun StringBuilder.appendContentEquals(
    variantName: String,
    fields: List<WebMvcResponseField>
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

private fun StringBuilder.appendContentHashCode(fields: List<WebMvcResponseField>) {
    appendLine("            override fun hashCode(): kotlin.Int {")
    appendLine("                var result = ${fields.first().hashCodeExpression()}")
    fields.drop(1).forEach { field ->
        appendLine("                result = 31 * result + ${field.hashCodeExpression()}")
    }
    appendLine("                return result")
    appendLine("            }")
}

private fun WebMvcResponseField.equalsExpression(): String =
    when {
        !isByteArray() -> "$name == other.$name"
        type.expandedType.nullable -> "$name?.contentEquals(other.$name) ?: (other.$name == null)"
        else -> "$name.contentEquals(other.$name)"
    }

private fun WebMvcResponseField.hashCodeExpression(): String =
    when {
        isByteArray() && type.expandedType.nullable -> "$name?.contentHashCode() ?: 0"
        isByteArray() -> "$name.contentHashCode()"
        type.expandedType.nullable -> "$name?.hashCode() ?: 0"
        else -> "$name.hashCode()"
    }

private fun WebMvcResponseField.isByteArray(): Boolean = type.expandsTo("kotlin.ByteArray")

private data class WebMvcResponseField(
    val name: String,
    val type: KotlinSourceType,
    val defaultExpression: String?
)
