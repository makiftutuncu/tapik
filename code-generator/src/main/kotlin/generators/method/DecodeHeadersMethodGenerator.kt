package dev.akif.tapik.generators.method

import dev.akif.tapik.Generator
import kotlin.math.ceil
import kotlin.math.min

data object DecodeHeadersMethodGenerator: Generator {
    private const val ARROW_ZIP_LIMIT = 10

    override val module: String = "core"
    override val packages: List<String> = listOf("http")
    override val fileName: String = "DecodeHeadersMethods.kt"

    override fun generate(limit: Int): String =
        $$"""
        |import arrow.core.Either.Companion.zipOrAccumulate
        |import arrow.core.EitherNel
        |import arrow.core.leftNel
        |import arrow.core.right
        |import dev.akif.tapik.tuples.*
        |
        |fun <H> decodeHeaders0(): Tuple0<List<H>> =
        |   Tuple0()
        |
        |fun <H: Any, H1: H> decodeHeaders1(
        |    headers: Map<String, List<String>>,
        |    header1: Header<H1>
        |): EitherNel<String, Tuple1<List<H>, List<H1>>> =
        |   decodeHeader(headers, header1).map { Tuple1(it) }
        |
        |$${(2..limit).joinToString(separator = "\n\n") { decodeHeadersMethod(it) }}
        |
        |private fun <H: Any> decodeHeader(
        |   values: Map<String, List<String>>,
        |   header: Header<H>
        |): EitherNel<String, List<H>> =
        |    when (header) {
        |        is HeaderInput<H> -> {
        |            val values = values[header.name]
        |            if (values == null && header.required) {
        |                "Required header '${header.name}' is missing".leftNel()
        |            } else {
        |                val initial: EitherNel<String, List<H>> = emptyList<H>().right()
        |                values.orEmpty().fold(initial) { results, value ->
        |                    header.codec.decode(value).fold(
        |                        { errors -> results.mapLeft { it + errors } },
        |                        { h -> results.map { it + h } }
        |                    )
        |                }
        |            }
        |        }
        |        is HeaderValues<H> -> header.values.right()
        |    }
        |""".trimMargin()

    private fun decodeHeadersMethod(index: Int): String {
        val typeParameters = (1..index).joinToString(separator = ", ") { "H$it: H" }
        val listTypeParameters = (1..index).joinToString(separator = ", ") { "List<H$it>" }

        if (index <= ARROW_ZIP_LIMIT) {
            val valuesList = (1..index).joinToString(separator = ", ") { "values$it" }
            return """
            |fun <H: Any, $typeParameters> decodeHeaders$index(
            |    headers: Map<String, List<String>>,
            |${(1..index).joinToString(separator = "\n") {
            "    header$it: Header<H$it>${if (it == index) "" else ","}"
            }}
            |): EitherNel<String, Tuple$index<List<H>, $listTypeParameters>> =
            |    zipOrAccumulate(
            |${(1..index).joinToString(separator = "\n") {
            "        decodeHeader(headers, header$it)${if (it == index) "" else ","}"
            }}
            |    ) { $valuesList ->
            |        Tuple$index($valuesList)
            |    }
            """.trimMargin()
        }

        val partCount = ceil(index.toDouble() / ARROW_ZIP_LIMIT).toInt()
        val windowSize = ceil(index.toDouble() / partCount).toInt()

        val parts = (1..partCount).map { partIndex ->
            val startIndex = ((partIndex - 1) * windowSize) + 1
            val endIndex = min(partIndex * windowSize, index)
            val valuesList = (startIndex..endIndex).joinToString(separator = ", ") { "values$it" }
            val part =
                """
                |    val part$partIndex = zipOrAccumulate(
                |${(startIndex..endIndex).joinToString(separator = "\n") {
                "        decodeHeader(headers, header$it)${if (it == index) "" else ","}"
                }}
                |    ) { $valuesList ->
                |        Tuple${endIndex - startIndex + 1}($valuesList)
                |    }
                """.trimMargin()
            Triple(part, startIndex, endIndex)
        }

        val mergeErrors =
            """
            |${(1..partCount).joinToString(separator = ", ") { "errors$it" }} -> ${(1..partCount).joinToString(separator = " + ") { "errors$it" }}
            """.trimMargin()

        val input = parts.joinToString(separator = ", ") { (_, startIndex, endIndex) ->
            (startIndex..endIndex).joinToString(separator = ", ", prefix = "(", postfix = ")") { "values$it" }
        }

        val valuesList = parts.joinToString(separator = ", ") { (_, startIndex, endIndex) ->
            (startIndex..endIndex).joinToString(separator = ", ") { "values$it" }
        }

        return """
                |fun <H: Any, $typeParameters> decodeHeaders$index(
                |    headers: Map<String, List<String>>,
                |${(1..index).joinToString(separator = "\n") {
                "    header$it: Header<H$it>${if (it == index) "" else ","}"
                }}
                |): EitherNel<String, Tuple$index<List<H>, $listTypeParameters>> {
                |${parts.joinToString(separator = "\n\n") { (part, _, _) -> part }}
                |
                |    return zipOrAccumulate(
                |        { $mergeErrors },
                |${(1..partCount).joinToString(separator = "\n") {
                "        part$it${if(it == partCount) "" else ","}"
                }}
                |    ) { $input ->
                |        Tuple$index($valuesList)
                |    }
                |}
                """.trimMargin()
    }
}
