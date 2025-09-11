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
        |${(0..limit).joinToString(separator = "\n\n") { responseDataClass(it, withoutBody = true) }}
        |
        |${(0..limit).joinToString(separator = "\n\n", postfix = "\n\n") { responseDataClass(it, withoutBody = false) }}
        """.trimMargin()

    private fun responseDataClass(index: Int, withoutBody: Boolean): String {
        val headerTypeParameters = (1..index).joinToString(separator = ", ") { "H$it" }.removeSuffix(", ")
        val typeParameters = "${if (withoutBody) "" else "B, "}$headerTypeParameters".removeSuffix(", ")

        return """
        |data class Response${if (withoutBody) "WithoutBody" else ""}WithHeaders$index${if (typeParameters.isEmpty()) "" else "<$typeParameters>"}(
        |    override val status: Status${if (withoutBody) "" else ",\n    val body: B"}${if (index == 0) "" else ","}
        |${if (index == 0) "" else (1..index).joinToString(separator = "\n") {
        "    val header$it: List<H$it>${if (it == index) "" else ","}"
        }}${if (index == 0) "" else "\n"}): Response<${if (withoutBody) "Nothing" else "B"}>
        """.trimMargin()
    }


}
