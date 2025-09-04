package dev.akif.tapik.generators.type

import dev.akif.tapik.Generator

data object ResponseGenerator: Generator {
    override val module: String = "core"
    override val packages: List<String> = listOf("http")
    override val fileName: String = "Responses.kt"

    override fun generate(limit: Int): String =
        """
        |sealed interface Response<out B> {
        |    val status: Status
        |}
        |
        |${(0..limit).joinToString(separator = "\n\n", postfix = "\n\n") { responseDataClass(it) }}
        """.trimMargin()

    private fun responseDataClass(index: Int): String {
        val headerTypeParameters = (1..index).joinToString(separator = ", ") { "H$it" }.removeSuffix(", ")
        val typeParameters = "B, $headerTypeParameters".removeSuffix(", ")

        return """
        |data class ResponseWithHeaders$index<$typeParameters>(
        |    override val status: Status,
        |    val body: B${if (index == 0) "" else (1..index).joinToString(separator = "\n", prefix = ",\n") { "    val header$it: List<H$it>${if (it == index) "" else ","}" }}
        |): Response<B>
        """.trimMargin()
    }


}
