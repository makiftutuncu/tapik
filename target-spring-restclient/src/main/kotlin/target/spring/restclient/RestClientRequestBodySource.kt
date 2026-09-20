package dev.akif.tapik.target.spring.restclient

internal fun StringBuilder.appendRequestBodyArgument(body: RestClientRequestBodyModel?) {
    when {
        body == null -> append("                body = null")
        body.choiceTypeName == null -> appendSingleRequestBody(body)
        else -> appendRequestBodyChoice(body)
    }
}

private fun StringBuilder.appendSingleRequestBody(body: RestClientRequestBodyModel) {
    val alternative = body.alternatives.single()
    if (body.optional) {
        append(
            "                body = ${body.parameterName}?.let { value -> " +
                "dev.akif.tapik.target.spring.restclient.RestClientRequestBody(" +
                "${alternative.definitionAccess}.mediaType, ${alternative.definitionAccess}.format.encode(value)) }"
        )
    } else {
        append(
            "                body = dev.akif.tapik.target.spring.restclient.RestClientRequestBody(" +
                "${alternative.definitionAccess}.mediaType, " +
                "${alternative.definitionAccess}.format.encode(${body.parameterName}))"
        )
    }
}

private fun StringBuilder.appendRequestBodyChoice(body: RestClientRequestBodyModel) {
    val choiceTypeName = requireNotNull(body.choiceTypeName)
    appendLine("                body =")
    if (body.optional) {
        appendLine("                    ${body.parameterName}?.let { requestBody ->")
        appendLine("                        when (requestBody) {")
        body.alternatives.forEach { alternative ->
            appendChoiceBranch(choiceTypeName, alternative, "requestBody", "                            ")
        }
        appendLine("                        }")
        append("                    }")
    } else {
        appendLine("                    when (${body.parameterName}) {")
        body.alternatives.forEach { alternative ->
            appendChoiceBranch(choiceTypeName, alternative, body.parameterName, "                        ")
        }
        append("                    }")
    }
}

private fun StringBuilder.appendChoiceBranch(
    choiceTypeName: String,
    alternative: RestClientRequestBodyAlternative,
    valueName: String,
    indentation: String
) {
    appendLine(
        "$indentation is $choiceTypeName.${alternative.variantName} -> " +
            "dev.akif.tapik.target.spring.restclient.RestClientRequestBody(" +
            "${alternative.definitionAccess}.mediaType, " +
            "${alternative.definitionAccess}.format.encode($valueName.body))"
    )
}
