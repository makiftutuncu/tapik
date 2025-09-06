package dev.akif.tapik.generators.method

import dev.akif.tapik.Generator

data object BuildURIMethodGenerator: Generator {
    override val module: String = "core"
    override val packages: List<String> = listOf("http")
    override val fileName: String = "BuildURIMethods.kt"

    override fun generate(limit: Int): String =
        $$"""
        |import java.net.URI
        |
        |$${(0..limit).joinToString(separator = "\n\n") { buildURIMethod(it) }}
        |
        |private fun build(segments: List<String>, vararg parameters: Pair<Parameter<*>, String>): URI {
        |    val map = parameters.groupBy({ it.first.position }) { (parameter, value) -> parameter.name to value }
        |
        |    val path =
        |        map[ParameterPosition.Path]
        |        .orEmpty()
        |        .fold(segments.joinToString(separator = "/", prefix = "/")) { path, (name, value) ->
        |            path.replace("{$name}", value)
        |        }
        |
        |    val query =
        |        map[ParameterPosition.Query]
        |            .orEmpty()
        |            .joinToString(separator = "&") { (name, value) -> "$name=$value" }
        |            .takeIf { it.isNotEmpty() }
        |
        |    return URI.create(listOfNotNull(path, query).joinToString("?"))
        |}
        """.trimMargin()

    private fun buildURIMethod(index: Int): String {
        if (index == 0) {
            return """
            |fun <IH: Headers, IB: Body<*>, OH: Headers, OB: OutputBodies> HttpEndpoint<Parameters0, IH, IB, OH, OB>.buildURI(): URI =
            |    build(uri)
            """.trimMargin()
        }

        val typeParameters = (1..index).joinToString(separator = ", ") { "P$it: Any" }
        val parametersType = (1..index).joinToString(separator = ", ", prefix = "Parameters$index<", postfix = ">") { "P$it" }

        return """
        |fun <IH: Headers, IB: Body<*>, OH: Headers, OB: OutputBodies, $typeParameters> HttpEndpoint<$parametersType, IH, IB, OH, OB>.buildURI(
        |${(1..index).joinToString(separator = "\n") {
        "    p$it: P$it${if (it == index) "" else ","}" }
        }
        |): URI =
        |    build(
        |        uri,
        |${(1..index).joinToString(separator = "\n") {
        "        parameters.item$it to parameters.item$it.codec.encode(p$it)${if (it == index) "" else ","}"
        }}
        |    )
        """.trimMargin()
    }
}
