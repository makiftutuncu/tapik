package dev.akif.tapik.generators.method

import dev.akif.tapik.Generator

data class BuildResponseMethodGenerator(val outputIndex: Int): Generator {
    override val module: String = "core"
    override val packages: List<String> = listOf("http")
    override val fileName: String = "BuildResponseMethodsOutput$outputIndex.kt"

    override fun generate(limit: Int): String {
        if (outputIndex == 0) {
            return """
            |fun Outputs0.buildResponse(status: Status): ResponseWithHeaders0<EmptyBody> =
            |    ResponseWithHeaders0(status, EmptyBody)
            """.trimMargin()
        }

        if (outputIndex == 1) {
            return $$"""
            |import arrow.core.raise.either
            |
            |$${(0..limit).joinToString(separator = "\n\n") { buildResponseMethodOutput1(it) }}
            |
            |private fun <B, OB: Body<B>, H: Headers, R: Response<B>> Output<OB, H>.decode(
            |   status: Status,
            |   bytes: ByteArray,
            |   buildResponse: (B) -> R
            |): R =
            |   body.codec.decode(bytes).fold(
            |       { error("Cannot decode response for status $status as ${body.codec.sourceClass.simpleName}") },
            |       { body -> buildResponse(body) }
            |   )
            """.trimMargin()
        }

        return $$"""
        |import arrow.core.raise.either
        |import dev.akif.tapik.selections.*
        |
        |$${(0..limit).joinToString(separator = "\n\n", postfix = "\n\n") { buildResponseMethod(it) }}
        |
        |private fun <B, OB: Body<B>, H: Headers, R: Response<B>, Option> Output<OB, H>.decode(
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

    private fun buildResponseMethodOutput1(index: Int): String {
        val typeParameters = if (index == 0) "" else (1..index).joinToString(separator = ", ", prefix = ", ") {
            "H$it: Any"
        }
        val outputTypeParameters = if (index == 0) "" else (1..index).joinToString(separator = ", ", prefix = "<", postfix = ">") {
            "H$it"
        }
        val responseTypeParameters = if (index == 0) "" else (1..index).joinToString(separator = ", ", prefix = ", ") {
            "H$it"
        }
        val decodeBody = if (index == 0) {
            "            ResponseWithHeaders0(status, body)"
        } else {
            val destructedValues = (1..index).joinToString(separator = ", ") { "header$it" }
            val headerValueParameters = (1..index).joinToString(separator = ", ") { "item1.headers.item$it" }
            """
            |            either {
            |                val ($destructedValues) = decodeHeaders$index(headers, $headerValueParameters).bind()
            |                ResponseWithHeaders$index(status, body, $destructedValues)
            |            }.fold(
            |                { errors -> error(errors.joinToString(separator = ": ")) },
            |                { it }
            |            )
            """.trimMargin()
        }

        return $$"""
        |fun <B1, OB1: Body<B1>$$typeParameters> Outputs1<OB1, Headers$$index$$outputTypeParameters>.buildResponse(
        |    status: Status,$${if (index == 0) "" else "\n    headers: Map<String, List<String>>,"}
        |    bytes: ByteArray
        |): ResponseWithHeaders$$index<B1$$responseTypeParameters> =
        |    when {
        |        item1.statusMatcher(status) -> item1.decode(status, bytes) { body ->
        |$$decodeBody
        |        }
        |        else -> error("Unexpected response status $status and body: ${String(bytes)}")
        |    }
        """.trimMargin()
    }

    private fun buildResponseMethod(index: Int): String {
        val bodyTypeParameters = (1..outputIndex).joinToString(separator = ", ") { "B$it, OB$it: Body<B$it>" }
        val headerTypeParameters = if (index == 0) "" else (1..index).joinToString(separator = ", ", prefix = ", ") {
            "H$it: Any"
        }
        val outputTypeParameters = if (index == 0) {
            ""
        } else {
            (1..index).joinToString(separator = ", ", prefix = "<", postfix = ">") { "H$it" }
        }
        val responseTypeParameters = if (index == 0) "" else (1..index).joinToString(separator = ", ", prefix = ", ") {
            "H$it"
        }
        val decodeBody = if (index == 0) {
            "            ResponseWithHeaders0(status, body)"
        } else {
            val destructedValues = (1..index).joinToString(separator = ", ") { "header$it" }
            val headerValueParameters = (1..index).joinToString(separator = ", ") { "item1.headers.item$it" }
            """
            |            either {
            |                val ($destructedValues) = decodeHeaders$index(headers, $headerValueParameters).bind()
            |                ResponseWithHeaders$index(status, body, $destructedValues)
            |            }.fold(
            |                { errors -> error(errors.joinToString(separator = ": ")) },
            |                { it }
            |            )
            """.trimMargin()
        }

        return $$"""
        |fun <$$bodyTypeParameters$$headerTypeParameters> Outputs$$outputIndex<OB1, Headers$$index$$outputTypeParameters>.buildResponse(
        |    status: Status,$${if (index == 0) "" else "\n    headers: Map<String, List<String>>,"}
        |    bytes: ByteArray
        |): ResponseWithHeaders$$index<B1$$responseTypeParameters> =
        |    when {
        |        item1.statusMatcher(status) -> item1.decode(status, bytes) { body ->
        |$$decodeBody
        |        }
        |        else -> error("Unexpected response status $status and body: ${String(bytes)}")
        |    }
        """.trimMargin()
    }
}
