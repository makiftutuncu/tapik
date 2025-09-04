package dev.akif.tapik

object TupleGenerator: Generator {
    override val name: String = "Tuple"
    override val module: String = "types"
    override val packages: List<String> = listOf("tuples")
    override val fileName: String = "Tuples.kt"

    override fun generate(limit: Int): String =
        """
        |sealed interface Tuple<out T> {
        |    fun toList(): List<T>
        |}
        |
        |${(0..limit).joinToString(separator = "\n\n", postfix = "\n\n") { tupleClass(it, limit) }}
        """.trimMargin()

    private fun tupleClass(index: Int, limit: Int): String {
        val data = if (index == 0) "" else "data "

        val typeParameters = if (index == 0) {
            ""
        } else {
            (1..index).joinToString(separator = ", ", prefix = ", ") { "T$it" }
        }

        val typeParametersWithConstraints = if (index == 0) {
            ""
        } else {
            (1..index).joinToString(separator = ", ", prefix = ", ") { "T$it: T" }
        }

        val constructor = if (index == 0) {
            ""
        } else {
            (1..index).joinToString(separator = "\n", prefix = "(\n", postfix = "\n)") {
                val last = it == limit
                "    val item$it: T$it${if(last) "" else ","}"
            }
        }

        val items = (1..index).joinToString(separator = ", ") { "item$it" }

        val plusMethod = if (index == limit) {
            ""
        } else {
            val nextIndex = index + 1
            val separator = if (index == 0) "" else ", "
            """
            |    operator fun <T$nextIndex : T> plus(item$nextIndex: T$nextIndex): Tuple$nextIndex<T$typeParameters, T$nextIndex> =
            |        Tuple$nextIndex($items${separator}item$nextIndex)
            |
            |""".trimMargin()
        }

        return """
        |${data}class Tuple$index<T$typeParametersWithConstraints>$constructor: Tuple<T> {
        |${plusMethod}    override fun toList(): List<T> =
        |        ${if (index == 0) "emptyList()" else "listOf($items)"}
        |}""".trimMargin()
    }
}
