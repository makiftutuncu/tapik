package dev.akif.tapik

object SelectionsGenerator: Generator {
    override val name: String = "Selection"
    override val module: String = "types"
    override val packages: List<String> = listOf("selections")
    override val fileName: String = "Selections.kt"

    override fun generate(limit: Int): String =
        """
        |sealed interface Selection
        |
        |${(1..limit).joinToString(separator = "\n\n", postfix = "\n\n") { selectionInterface(it) }}
        """.trimMargin()

    private fun selectionInterface(index: Int): String {
        val typeParameters = (1..index).joinToString(separator = ", ") { "out T$it" }

        val selectMethod =
            """
            |    fun <R> select(
            |${(1..index).joinToString(separator = "\n") { "        when$it: (T$it) -> R${if (it == index) "" else ","}" }}
            |    ): R =
            |        when (this) {
            |${(1..index).joinToString(separator = "\n") { "            is Option$it<T$it> -> when$it(value)" }}
            |        }""".trimMargin()

        return """|sealed interface Selection$index<$typeParameters>: Selection {
                  |${(1..index).joinToString(separator = "\n") { "    ${optionValueClass(it, index)}" }}
                  |
                  |$selectMethod
                  |}""".trimMargin()
    }

    private fun optionValueClass(optionIndex: Int, index: Int): String {
        val selectionTypeArguments = (1..index).joinToString(separator = ", ") { if (it == optionIndex) "T" else "Nothing" }
        return """@JvmInline value class Option$optionIndex<T>(val value: T): Selection${index}<$selectionTypeArguments>"""
    }
}
