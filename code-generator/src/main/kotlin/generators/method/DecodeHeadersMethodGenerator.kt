package dev.akif.tapik.generators.method

import dev.akif.tapik.Generator

data object DecodeHeadersMethodGenerator: Generator {
    override val module: String = "core"
    override val packages: List<String> = listOf("http")
    override val fileName: String = "DecodeHeadersMethods.kt"

    override fun generate(limit: Int): String =
        $$"""
        |import arrow.core.EitherNel
        |import arrow.core.leftNel
        |import arrow.core.raise.either
        |import arrow.core.right
        |import dev.akif.tapik.tuples.*
        |
        |fun <H> decodeHeaders0(): Tuple0<List<H>> =
        |   Tuple0()
        |
        |$${(1..limit).joinToString(separator = "\n\n") { decodeHeadersMethod(it) }}
        |
        |private fun <H: Any> decodeHeader(
        |   headers: Map<String, List<String>>,
        |   header: Header<H>
        |): EitherNel<String, List<H>> =
        |    when (header) {
        |        is HeaderInput<H> -> {
        |            val values = headers[header.name]
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
        return """
        |fun <H: Any, $typeParameters> decodeHeaders$index(
        |    headers: Map<String, List<String>>,
        |${(1..index).joinToString(separator = "\n") {
        "    header$it: Header<H$it>${if (it == index) "" else ","}"
        }}
        |): EitherNel<String, Tuple$index<List<H>, $listTypeParameters>> =
        |    either {
        |        Tuple$index(
        |${(1..index).joinToString(separator = "\n") {
        "            decodeHeader(headers, header$it).bind()${if (it == index) "" else ","}"
        }}
        |        )
        |    }
        """.trimMargin()
    }
}
