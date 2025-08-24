package dev.akif.app

import dev.akif.tapik.http.AnyHttpEndpoint
import dev.akif.tapik.http.Body
import dev.akif.tapik.http.EmptyBody
import dev.akif.tapik.http.Header
import dev.akif.tapik.http.JsonBody
import dev.akif.tapik.http.Parameter
import dev.akif.tapik.http.PathVariable
import dev.akif.tapik.http.QueryParameter
import dev.akif.tapik.http.RawBody
import dev.akif.tapik.http.StringBody

class MarkdownDocumentationInterpreter(private val api: List<AnyHttpEndpoint>) {
    fun generate(): String = api.joinToString("\n\n") { it.document() }.trim()

    private fun AnyHttpEndpoint.document(): String {
        val path = parameters.toList().filterIsInstance<PathVariable<*>>()
        val query = parameters.toList().filterIsInstance<QueryParameter<*>>()
        val headers = parameters.toList().filterIsInstance<Header<*>>()

        return """
        \/## $id${description?.let { ": $it" } ?: ""}${details?.let { "\n\n$it" } ?: ""}
        \/
        \/`$method ${uri.joinToString(separator = "/", prefix = "/")}`
        \/
        \/${documentParameters(path, query)}
        \/
        \/${documentHeaders(headers)}
        \/
        \/${documentInput(input)}
        """.trimMargin("\\/").trim()
    }

    private fun documentParameters(path: List<Parameter<*>>, query: List<Parameter<*>>): String =
        if (path.isEmpty() && query.isEmpty()) {
            ""
        } else {
            val all = path.map { "| ${it.name} | ${it.codec.sourceClass.simpleName} | In Path |" } + query.map { "| ${it.name} | ${it.codec.sourceClass.simpleName} | In Query |" }
            """
            \/### URI Parameters
            \/
            \/| Name | Type | Position |
            \/| ---- | ---- | -------- |${all.joinToString("\n", prefix = "\n")}
            """.trimMargin("\\/").trim()
        }

    private fun documentHeaders(headers: List<Parameter<*>>): String =
        if (headers.isEmpty()) "" else
        """
        \/### Headers
        \/
        \/| Name | Type |
        \/| ---- | ---- |
        \/${headers.joinToString("\n") { "| ${it.name} | ${it.codec.sourceClass.simpleName} |" }}
        """.trimMargin("\\/").trim()

    private fun documentInput(input: Body<*>): String =
        if (input == EmptyBody) "" else
        """
        \/### Input
        \/
        \/${when (input) {
            EmptyBody -> ""
            is JsonBody<*> -> "`${input.codec.sourceClass.simpleName}` as `${input.mediaType}`"
            is RawBody -> "Raw bytes as `${input.mediaType}`"
            is StringBody -> "Plain string"
        }}
        """.trimMargin("\\/").trim()
}

