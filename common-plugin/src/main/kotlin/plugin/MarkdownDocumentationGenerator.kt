package dev.akif.tapik.plugin

import dev.akif.tapik.plugin.metadata.*
import java.io.File

/**
 * A generator that creates Markdown documentation from tapik endpoint definitions.
 */
object MarkdownDocumentationGenerator {
    private const val MARGIN = ">>>"
    private const val BR = "<br />"

    /**
     * Generates Markdown documentation for the given endpoints and writes it to a file in the given root directory.
     *
     * @param endpoints The endpoints to generate documentation for.
     * @param rootDir The root directory to write the generated documentation to.
     */
    fun generate(endpoints: List<HttpEndpointMetadata>, rootDir: File) {
        val content = endpoints
            .groupBy { it.packageName }
            .values
            .sortedBy { it.first().packageName }
            .joinToString("\n\n") { endpointsInPackage ->
                endpointsInPackage
                    .groupBy { it.sourceFile }
                    .values
                    .joinToString(separator = "\n\n") { endpointsInFile ->
                        endpointsInFile.joinToString(
                            prefix = h1("${endpointsInFile.first().sourceFile}\n\n"),
                            separator = "\n\n---\n\n"
                        ) {
                            it.document()
                        }
                    }
            }
        File(rootDir, "API.md").writeText(content)
    }

    private fun HttpEndpointMetadata.document(): String =
        listOfNotNull(
            h2("${mono(id)}${description?.let { ": $it" }.orEmpty()}"),
            mono("$method ${path.joinToString(separator = "/", prefix = "/")}"),
            details,
            documentParameters(parameters),
            documentHeaders("Input", input.headers),
            documentInput(input.body),
            documentOutputs(outputs)
        ).joinToString("\n\n")

    private fun documentParameters(parameters: List<ParameterMetadata>): String? {
        return if (parameters.isEmpty()) {
            null
        } else {
            val headers = listOf("Name", "Type", "Position", "Required", "Default Value")
            val rows =
                parameters.map {
                    val (name, type, position, required, default) = when (it) {
                        is PathVariableMetadata ->
                            listOf(it.name, it.type.toString(), "Path", "true", "")

                        is QueryParameterMetadata ->
                            listOf(it.name, it.type.toString(), "Query", it.required.toString(), it.default.orEmpty())
                    }
                    listOf(
                        mono(name),
                        type,
                        "In $position",
                        required,
                        default
                    )
                }
            """${h3("URI Parameters")}
        $MARGIN
        $MARGIN${table(headers, rows)}""".trimMargin(MARGIN)
        }
    }

    private fun documentHeaders(
        type: String,
        headers: List<HeaderMetadata>
    ): String? =
        if (headers.isEmpty()) {
            null
        } else {
            val tableHeaders = listOf("Name", "Type", "Value(s)", "Required")
            val rows =
                headers.map { header ->
                    val values = inlineList(header.values) { mono(it) }
                    listOf(
                        mono(header.name),
                        header.type.toString(),
                        values,
                        header.required.toString()
                    )
                }
            """${h3("$type Headers")}
        $MARGIN
        $MARGIN${table(tableHeaders, rows)}""".trimMargin(MARGIN)
        }

    private fun documentBodyMetadata(body: BodyMetadata): String {
        val name = body.name?.let { """ named "$it"""" }.orEmpty()
        val codec = body.mediaType?.let { """ as ${mono(it)}""" }.orEmpty()
        val type = if (body.type.arguments.isEmpty()) {
            body.type.toString()
        } else {
            body.type.arguments.first().toString()
        }
        return """${mono(type)}$name$codec"""
    }

    private fun documentInput(input: BodyMetadata?): String? =
        if (input == null) {
            null
        } else {
            """${h3("Input")}
        $MARGIN
        $MARGIN${documentBodyMetadata(input)}""".trimMargin(MARGIN)
        }

    private fun documentOutputs(outputs: List<OutputMetadata>): String? =
        if (outputs.isEmpty()) {
            null
        } else {
            val headers = listOf("Description", "Headers", "Body")
            val rows =
                outputs.map { output ->
                    val headerSummary =
                        if (output.headers.isEmpty()) {
                            "-"
                        } else {
                            output.headers.joinToString(separator = "<br>") { header ->
                                val values = inlineList(header.values) { mono(it) }
                                buildString {
                                    append(mono(header.name))
                                    append(": ")
                                    append(header.type)
                                    if (values.isNotEmpty()) {
                                        append(" = ")
                                        append(values)
                                    }
                                }
                            }
                        }
                    listOf(
                        output.description,
                        headerSummary,
                        documentBodyMetadata(output.body)
                    )
                }
            """${h3("Outputs")}
        $MARGIN
        $MARGIN${table(headers, rows)}""".trimMargin(MARGIN)
        }

    private fun h(
        level: Int,
        text: String
    ): String = "${"#".repeat(level)} $text"

    private fun h1(text: String): String = h(1, text)

    private fun h2(text: String): String = h(2, text)

    private fun h3(text: String): String = h(3, text)

    private fun mono(text: String): String = "`$text`"

    private fun table(
        headers: List<String>,
        rows: List<List<String>>
    ): String =
        """${headers.joinToString(separator = " | ", prefix = "| ", postfix = " |")}
    $MARGIN${headers.joinToString(separator = " | ", prefix = "| ", postfix = " |") { "-".repeat(it.length) }}
    $MARGIN${
            rows.joinToString(separator = "\n") { columns ->
                columns.joinToString(separator = " | ", prefix = "| ", postfix = " |")
            }
        }
    """.trimMargin(MARGIN)

    private fun <T> inlineList(
        items: Iterable<T>,
        document: (T) -> String
    ): String =
        if (items.count() == 1) {
            document(items.first())
        } else {
            items.zip(1..items.count()).joinToString(separator = BR) { (item, index) -> "$index. ${document(item)}" }
        }
}
