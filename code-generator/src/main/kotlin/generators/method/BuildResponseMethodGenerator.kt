package dev.akif.tapik.generators.method

import dev.akif.tapik.Generator

data object BuildResponseMethodGenerator: Generator {
    override val module: String = "core"
    override val packages: List<String> = listOf("http")
    override val fileName: String = "BuildResponseMethods.kt"

    override fun generate(limit: Int): String =
        $$"""
        |import arrow.core.raise.either
        |import dev.akif.tapik.selections.*
        |
        |private fun <B, OB: Body<B>, R: Response<B>, Option> OutputBody<OB>.decode(
        |   status: Status,
        |   bytes: ByteArray,
        |   buildResponse: (B) -> R,
        |   constructOption: (R) -> Option
        |): Option =
        |   body.codec.decode(bytes).fold(
        |       { error("Cannot decode response for status $status as ${body.codec.sourceClass.simpleName}") },
        |       { body -> constructOption(buildResponse(body)) }
        |   )
        """.trimMargin()
}
