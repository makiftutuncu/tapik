package dev.akif.tapik.generators

import dev.akif.tapik.Generator

data object AliasesGenerator: Generator {
    override val module: String = "core"
    override val packages: List<String> = listOf("http")
    override val fileName: String = "Aliases.kt"

    override fun generate(limit: Int): String =
        """
        |import dev.akif.tapik.tuples.*
        |
        |typealias AnyHttpEndpoint = HttpEndpoint<Parameters, Headers, Body<*>, Headers, OutputBodies>
        |
        |typealias Headers = Tuple<Header<*>>
        |${(0..limit).joinToString(separator = "\n") { tupleAlias("Headers", "Header", "H", it) }}
        |
        |typealias OutputBodies = Tuple<OutputBody<*>>
        |${(0..limit).joinToString(separator = "\n") { tupleAlias("OutputBodies", "OutputBody", "B", it) }}
        |
        |typealias Parameters = Tuple<Parameter<*>>
        |${(0..limit).joinToString(separator = "\n") { tupleAlias("Parameters", "Parameter", "P", it) }}
        |
        |typealias URIWithParameters<P> = Pair<List<String>, P>
        |${(0..limit).joinToString(separator = "\n") { alias("URIWithParameters", "Parameters", "P", it) }}
        |
        """.trimMargin()

    private fun tupleAlias(name: String, type: String, prefix: String, index: Int): String {
        val typeParameters = if (index == 0) {
            ""
        } else {
            (1..index).joinToString(separator = ", ", prefix = "<", postfix = ">") { "$prefix$it" }
        }
        val arguments = if (index == 0) {
            ""
        } else {
            (1..index).joinToString(separator = ", ", prefix = ", ") { "$type<$prefix$it>" }
        }
        return "typealias $name$index$typeParameters = Tuple$index<$type<*>$arguments>"
    }

    private fun alias(name: String, type: String, prefix: String, index: Int): String {
        val typeParameters = if (index == 0) {
            ""
        } else {
            (1..index).joinToString(separator = ", ", prefix = "<", postfix = ">") { "$prefix$it" }
        }
        val arguments = if (index == 0) {
            "$type$index"
        } else {
            (1..index).joinToString(separator = ", ", prefix = "$type$index<", postfix = ">") { "$prefix$it" }
        }
        return "typealias $name$index$typeParameters = $name<$arguments>"
    }
}
