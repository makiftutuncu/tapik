package dev.akif.tapik.generators.method

import dev.akif.tapik.Generator

data object EncodeHeadersMethodGenerator: Generator {
    override val module: String = "core"
    override val packages: List<String> = listOf("http")
    override val fileName: String = "EncodeHeadersMethods.kt"

    override fun generate(limit: Int): String =
        """
        |${(0..limit).joinToString(separator = "\n\n") { encodeInputHeadersMethod(it) }}
        |
        |${(0..limit).joinToString(separator = "\n\n") { encodeOutputHeadersMethod(it) }}
        |
        |private fun build(vararg pairs: Pair<String, String>): Map<String, List<String>> =
        |    listOf(*pairs).groupBy({ it.first }) { it.second }
        |
        |private fun <H: Any> encodeHeader(pair: Pair<Header<H>, H>): Pair<String, String> {
        |    val (header, value) = pair
        |    return header.name to header.codec.encode(value)
        |}
        """.trimMargin()

    private fun encodeInputHeadersMethod(index: Int): String {
        if (index == 0) {
            return """
            |fun <P: Parameters, IB: Body<*>, OH: Headers, OB: OutputBodies> HttpEndpoint<P, Headers0, IB, OH, OB>.encodeInputHeaders(): Map<String, List<String>> =
            |    emptyMap()
            """.trimMargin()
        }

        val typeParameters = (1..index).joinToString(separator = ", ") { "H$it: Any" }
        val headersType = (1..index).joinToString(separator = ", ", prefix = "Headers$index<", postfix = ">") { "H$it" }

        return """
        |fun <P: Parameters, IB: Body<*>, OH: Headers, OB: OutputBodies, $typeParameters> HttpEndpoint<P, $headersType, IB, OH, OB>.encodeInputHeaders(
        |${(1..index).joinToString(separator = "\n") {
        "    h$it: H$it${if (it == index) "" else ","}" }
        }
        |): Map<String, List<String>> =
        |    build(
        |${(1..index).joinToString(separator = "\n") {
        "        encodeHeader(inputHeaders.item$it to h$it)${if (it == index) "" else ","}"
        }}
        |    )
        """.trimMargin()
    }

    private fun encodeOutputHeadersMethod(index: Int): String {
        if (index == 0) {
            return """
            |fun <P: Parameters, IH: Headers, IB: Body<*>, OB: OutputBodies> HttpEndpoint<P, IH, IB, Headers0, OB>.encodeOutputHeaders(): Map<String, List<String>> =
            |    emptyMap()
            """.trimMargin()
        }

        val typeParameters = (1..index).joinToString(separator = ", ") { "H$it: Any" }
        val headersType = (1..index).joinToString(separator = ", ", prefix = "Headers$index<", postfix = ">") { "H$it" }

        return """
        |fun <P: Parameters, IH: Headers, IB: Body<*>, OB: OutputBodies, $typeParameters> HttpEndpoint<P, IH, IB, $headersType, OB>.encodeOutputHeaders(
        |${(1..index).joinToString(separator = "\n") {
        "    h$it: H$it${if (it == index) "" else ","}" }
        }
        |): Map<String, List<String>> =
        |    build(
        |${(1..index).joinToString(separator = "\n") {
        "        encodeHeader(outputHeaders.item$it to h$it)${if (it == index) "" else ","}"
        }}
        |    )
        """.trimMargin()
    }
}
