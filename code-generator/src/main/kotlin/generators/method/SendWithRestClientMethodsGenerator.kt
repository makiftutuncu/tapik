package dev.akif.tapik.generators.method

import dev.akif.tapik.Generator

data class SendWithRestClientMethodsGenerator(
    val parameterIndex: Int,
    val inputHeaderIndex: Int,
    val outputHeaderIndex: Int
): Generator {
    override val module: String = "spring-restclient"
    override val packages: List<String> = listOf("spring", "restclient")
    override val fileName: String = "SendMethodsForParameters${parameterIndex}InputHeaders${inputHeaderIndex}OutputHeaders${outputHeaderIndex}.kt"

    override fun generate(limit: Int): String {
        return """
        |import arrow.core.getOrElse
        |import arrow.core.leftNel
        |import dev.akif.tapik.http.*
        |import dev.akif.tapik.selections.*
        |
        |${(0..limit).joinToString(separator = "\n\n") { sendWithRestClientMethod(it) }}
        """.trimMargin()
    }

    private fun sendWithRestClientMethod(index: Int): String {
        val parameterTypeParametersWithConstraints = if (parameterIndex == 0) {
            ""
        } else {
            (1..parameterIndex).joinToString(separator = ", ", postfix = ", ") { "P$it: Any" }
        }
        val inputHeadersTypeParametersWithConstraints = if (inputHeaderIndex == 0) {
            ""
        } else {
            (1..inputHeaderIndex).joinToString(separator = ", ", postfix = ", ") { "IH$it: Any" }
        }
        val outputHeadersTypeParametersWithConstraints = if (outputHeaderIndex == 0) {
            ""
        } else {
            (1..outputHeaderIndex).joinToString(separator = ", ", postfix = ", ") { "OH$it: Any" }
        }
        val outputBodiesTypeParametersWithConstraints = if (index == 0) {
            ""
        } else {
            (1..index).joinToString(separator = ", ", postfix = ", ") { "OB$it, OutputBody$it: Body<OB$it>" }
        }
        val outputBodiesType = if (index == 0) {
            "OutputBodies0"
        } else {
            (1..index).joinToString(separator = ", ", prefix = "OutputBodies$index<", postfix = ">") { "OutputBody$it" }
        }
        val outputBodiesTypeParameter = if (index == 0) {
            ""
        } else {
            "OutputBodies: $outputBodiesType, "
        }
        val parameterType = if (parameterIndex == 0) {
            "Parameters0"
        } else {
            (1..parameterIndex).joinToString(separator = ", ", prefix = "Parameters$parameterIndex<", postfix = ">") { "P$it" }
        }
        val inputHeadersType = if (inputHeaderIndex == 0) {
            "Headers0"
        } else {
            (1..inputHeaderIndex).joinToString(separator = ", ", prefix = "Headers$inputHeaderIndex<", postfix = ">") { "IH$it" }
        }
        val outputHeadersType = if (outputHeaderIndex == 0) {
            "Headers0"
        } else {
            (1..outputHeaderIndex).joinToString(separator = ", ", prefix = "Headers$outputHeaderIndex<", postfix = ">") { "OH$it" }
        }

        val typeParameters =
            "${parameterTypeParametersWithConstraints}${inputHeadersTypeParametersWithConstraints}IB, InputBody: Body<IB>, ${outputHeadersTypeParametersWithConstraints}${outputBodiesTypeParametersWithConstraints}${outputBodiesTypeParameter}".trim().removeSuffix(",")

        val inputParameters = buildList {
            (1..parameterIndex).forEach { add("parameter$it: P$it") }
            (1..inputHeaderIndex).forEach { add("inputHeader$it: IH$it") }
            add("inputBody: IB")
        }.withIndex().toList()

        val returnType = if (index == 0) {
            if (outputHeaderIndex == 0) {
                "ResponseWithoutBodyWithHeaders0"
            } else {
                (1..outputHeaderIndex).joinToString(separator = ", ", prefix = "ResponseWithoutBodyWithHeaders$outputHeaderIndex<", postfix = ">") { "OH$it" }
            }
        } else {
            val options = (1..index).map { outputBodyIndex ->
                if (outputHeaderIndex == 0) {
                    "ResponseWithHeaders0<OB$outputBodyIndex>"
                } else {
                    (1..outputHeaderIndex).joinToString(separator = ", ", prefix = "ResponseWithHeaders$outputHeaderIndex<OB$outputBodyIndex, ", postfix = ">") { "OH$it" }
                }
            }
            if (options.size == 1) {
                options.first()
            } else {
                options.joinToString(separator = ", ", prefix = "Selection${options.size}<", postfix = ">")
            }
        }

        val parameterArguments = if (parameterIndex == 0) {
            ""
        } else {
            (1..parameterIndex).joinToString(separator = ", ") { "parameter$it" }
        }

        val inputHeaderArguments = if (inputHeaderIndex == 0) {
            ""
        } else {
            (1..inputHeaderIndex).joinToString(separator = ", ") { "inputHeader$it" }
        }

        val outputHeaders =
            """
            |        decodeHeaders$outputHeaderIndex(
            |            headers,
            |${(1..outputHeaderIndex).joinToString(separator = "\n") {
            "            this.outputHeaders.item$it${if (it == outputHeaderIndex) "" else ","}" }}
            |        )
            """.trimMargin()

        val returnValue = if (index == 0) {
            if (outputHeaderIndex == 0) {
                "    return ResponseWithoutBodyWithHeaders0(status)"
            } else {
                """
                |    return ResponseWithoutBodyWithHeaders$outputHeaderIndex(
                |        status,
                |${(1..outputHeaderIndex).joinToString(separator = "\n") {
                "        outputHeader$it${if (it == outputHeaderIndex) "" else ","}" }}
                |    )
                """.trimIndent()
            }
        } else {
            """
            |    val bytes = responseEntity.body ?: ByteArray(0)
            |
            |    val response = when {
            |${(1..index).joinToString(separator = "\n") {
            """
            |        this.outputBodies.item$it.statusMatcher(status) -> this.outputBodies.item$it.body.codec.decode(bytes).map { decodedBody ->
            |${if (index == 1) {
            """
            |            ResponseWithHeaders$outputHeaderIndex(
            |                status,
            |                decodedBody${if (outputHeaderIndex == 0) "" else ","}
            |${(1..outputHeaderIndex).joinToString(separator = "\n") { hi ->
            "                outputHeader$hi${if (hi == outputHeaderIndex) "" else ","}" }}
            |            )
            """.trimMargin()
            } else {
            """
            |            Selection$index.Option$it(
            |                ResponseWithHeaders$outputHeaderIndex(
            |                    status,
            |                    decodedBody${if (outputHeaderIndex == 0) "" else ","}
            |${(1..outputHeaderIndex).joinToString(separator = "\n") { hi ->
            "                    outputHeader$hi${if (hi == outputHeaderIndex) "" else ","}" }}
            |                )
            |            )
            """.trimMargin()
            }}
            |        }
            """.trimMargin()
            }}
            |        else -> "Response output did not match".leftNel()
            |    }
            |
            |    return response.getOrElse { error(it.joinToString(": ")) }
            """.trimMargin()
        }

        return """
        |context(interpreter: RestClientInterpreter)
        |fun <$typeParameters> HttpEndpoint<$parameterType, $inputHeadersType, InputBody, $outputHeadersType, ${if (index == 0) "OutputBodies0" else "OutputBodies" }>.sendWithRestClient(
        |${inputParameters.joinToString(separator = "\n") { (i, p) ->
        "    $p${if (i == inputParameters.size - 1) "" else ","}"
        }}
        |): $returnType {
        |    val responseEntity = interpreter.send(
        |        method = this.method,
        |        uri = this.buildURI($parameterArguments),
        |        inputHeaders = this.encodeInputHeaders($inputHeaderArguments),
        |        inputBodyContentType = this.inputBody.mediaType,
        |        inputBody = this.inputBody.codec.encode(inputBody),
        |        outputBodies = this.outputBodies.toList()
        |    )
        |
        |    val status = responseEntity.statusCode.toStatus()
        |${if (outputHeaderIndex == 0) "" else {
        """
        |
        |    val headers =
        |        responseEntity
        |            .headers
        |            .entries
        |            .associateBy({ e -> e.key }) { e ->
        |                e.value.toList().map { it.orEmpty() }
        |            }
        |
        |    val (${(1..outputHeaderIndex).joinToString(separator = ", ") { "outputHeader$it" }}) =
        |${outputHeaders}.getOrElse {
        |            error("Cannot decode headers: " + it.joinToString(": "))
        |        }
        |
        """.trimMargin()
        }}
        |$returnValue
        |}
        """.trimMargin()
    }
}
