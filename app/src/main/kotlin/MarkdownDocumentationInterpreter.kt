package dev.akif.app

import dev.akif.tapik.codec.Codec
import dev.akif.tapik.http.*

class MarkdownDocumentationInterpreter(
    private val api: List<AnyHttpEndpoint>
) {
    companion object {
        private const val MARGIN = ">>>"
        private const val BR = "<br />"
    }

    fun generate(): String = api.joinToString("\n\n---\n\n") { it.document() }.trim()

    private fun AnyHttpEndpoint.document(): String {
        val parameterList = parameters.toList()
        return listOfNotNull(
            h2(description ?: id),
            description?.let { "Operation id: ${mono(id)}" },
            details,
            mono("$method ${uri.joinToString(separator = "/", prefix = "/")}"),
            documentParameters(parameterList),
            documentHeaders("Input", inputHeaders.toList()),
            documentInput(inputBody),
            documentHeaders("Output", outputHeaders.toList()),
            documentOutputs(outputBodies.toList())
        ).joinToString("\n\n")
    }

    private fun documentParameters(
        parameters: List<Parameter<*>>
    ): String? {
        val uriParameters = parameters.filter { it is PathVariable<*> || it is QueryParameter<*> }
        return if (uriParameters.isEmpty()) {
            null
        } else {
            val headers = listOf("Name", "Type", "Position", "Required", "Default Value")
            val rows = uriParameters.map {
                listOf(
                    mono(it.name),
                    documentCodec(it.codec),
                    "In ${it.position}",
                    it.required.toString(),
                    (it as? QueryParameter<*>)?.default?.let { d -> "$d" }.orEmpty()
                )
            }
            """${h3("URI Parameters")}
            $MARGIN
            $MARGIN${table(headers, rows)}""".trimMargin(MARGIN)
        }
    }

    private fun documentHeaders(type: String, headers: List<Header<*>>): String? {
        return if (headers.isEmpty()) {
            null
        } else {
            val tableHeaders = listOf("Name", "Type", "Value(s)", "Required")
            val rows = headers.map { header ->
                val values = (header as? HeaderValues<*>)?.values?.let { vs -> inlineList(vs) { mono("$it") } }.orEmpty()
                listOf(
                    mono(header.name),
                    documentCodec(header.codec),
                    values,
                    header.required.toString()
                )
            }
            """${h3("$type Headers")}
            $MARGIN
            $MARGIN${table(tableHeaders, rows)}""".trimMargin(MARGIN)
        }
    }

    private fun documentInput(input: Body<*>): String? =
        documentBody(input)?.let {
            """${h3("Input")}
            $MARGIN
            $MARGIN$it""".trimMargin(MARGIN)
        }

    private fun documentOutputs(outputBodies: List<OutputBody<*>>): String? =
        if (outputBodies.isEmpty()) {
            null
        } else {
            val headers = listOf("Status", "Body")
            val rows = outputBodies.map { output ->
                val (statusMatcher, body) = output
                listOf(
                    when (statusMatcher) {
                        is StatusMatcher.Is -> documentStatus(statusMatcher.status)
                        is StatusMatcher.AnyOf -> inlineList(statusMatcher.statuses) { documentStatus(it) }
                        is StatusMatcher.Predicate -> statusMatcher.description
                        StatusMatcher.Unmatched -> "Any unmatched status"
                    },
                    documentBody(body).orEmpty(),
                )
            }
            """${h3("Outputs")}
            $MARGIN
            $MARGIN${table(headers, rows)}""".trimMargin(MARGIN)
        }

    private fun documentStatus(status: Status): String = "${status.code} ${status.name}"

    private fun documentBody(body: Body<*>): String? =
        when (body) {
            EmptyBody -> null
            is JsonBody<*> -> "${documentCodec(body.codec)}${" as ${documentMediaType(body.mediaType)}" }"
            is RawBody -> "Raw bytes${body.mediaType?.let { " as ${documentMediaType(it)}" }.orEmpty()}"
            is StringBody -> "Plain text"
        }

    private fun documentCodec(codec: Codec<*, *>): String = mono(codec.sourceClass.simpleName.orEmpty())

    private fun documentMediaType(mediaType: MediaType): String = mono(mediaType.toString())

    private fun h(level: Int, text: String): String = "${"#".repeat(level)} $text"
    private fun h2(text: String): String = h(2, text)
    private fun h3(text: String): String = h(3, text)

    private fun mono(text: String): String = "`$text`"

    private fun table(headers: List<String>, rows: List<List<String>>): String =
        """${headers.joinToString(separator = " | ", prefix = "| ", postfix = " |")}
        $MARGIN${headers.joinToString(separator = " | ", prefix = "| ", postfix = " |") { "-".repeat(it.length) }}
        $MARGIN${
            rows.joinToString(separator = "\n") { columns ->
                columns.joinToString(separator = " | ", prefix = "| ", postfix = " |")
            }
        }
        """.trimMargin(MARGIN)

    private fun <T> inlineList(items: Iterable<T>, document: (T) -> String): String =
        if (items.count() == 1) {
            document(items.first())
        } else {
            items.zip(1..items.count()).joinToString(separator = BR) { (item, index)  -> "$index. ${document(item)}" }
        }
}
