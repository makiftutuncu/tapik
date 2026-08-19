package dev.akif.tapik.spring.restclient

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
                appendLine("            public val ${field.name}: ${field.type}$suffix")
            }
            if (fields.any(RestClientParameter::isByteArray)) {
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
    fields: List<RestClientParameter>
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

private fun StringBuilder.appendContentHashCode(fields: List<RestClientParameter>) {
    appendLine("            override fun hashCode(): kotlin.Int {")
    appendLine("                var result = ${fields.first().hashCodeExpression()}")
    fields.drop(1).forEach { field ->
        appendLine("                result = 31 * result + ${field.hashCodeExpression()}")
    }
    appendLine("                return result")
    appendLine("            }")
}

private fun RestClientParameter.equalsExpression(): String =
    when {
        !isByteArray() -> "$name == other.$name"
        type.endsWith('?') -> "$name?.contentEquals(other.$name) ?: (other.$name == null)"
        else -> "$name.contentEquals(other.$name)"
    }

private fun RestClientParameter.hashCodeExpression(): String =
    when {
        isByteArray() && type.endsWith('?') -> "$name?.contentHashCode() ?: 0"
        isByteArray() -> "$name.contentHashCode()"
        type.endsWith('?') -> "$name?.hashCode() ?: 0"
        else -> "$name.hashCode()"
    }

private fun RestClientParameter.isByteArray(): Boolean = type.removeSuffix("?") == "kotlin.ByteArray"

private fun RestClientOutput.fields(): List<RestClientParameter> =
    buildList {
        bodies.firstOrNull()?.let { body ->
            val type = if (allowsNoBody) "${body.type}?" else body.type
            add(RestClientParameter("body", type))
        }
        headers.forEach { header -> add(RestClientParameter(header.name, header.type)) }
    }
