// This file is auto-generated. Do not edit manually as your changes will be lost.
@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.spring.restclient

import arrow.core.getOrElse
import arrow.core.leftNel
import dev.akif.tapik.http.*
import dev.akif.tapik.selections.*

context(interpreter: RestClientInterpreter)
fun <P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any, P7: Any, P8: Any, P9: Any, P10: Any, IH1: Any, IH2: Any, IH3: Any, IH4: Any, IH5: Any, IH6: Any, IH7: Any, IH8: Any, IH9: Any, IH10: Any, IB, InputBody: Body<IB>, OH1: Any, OH2: Any, OH3: Any, OH4: Any, OH5: Any, OH6: Any, OH7: Any, OH8: Any, OH9: Any, OH10: Any> HttpEndpoint<Parameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10>, Headers10<IH1, IH2, IH3, IH4, IH5, IH6, IH7, IH8, IH9, IH10>, InputBody, Headers10<OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, OutputBodies0>.sendWithRestClient(
    parameter1: P1,
    parameter2: P2,
    parameter3: P3,
    parameter4: P4,
    parameter5: P5,
    parameter6: P6,
    parameter7: P7,
    parameter8: P8,
    parameter9: P9,
    parameter10: P10,
    inputHeader1: IH1,
    inputHeader2: IH2,
    inputHeader3: IH3,
    inputHeader4: IH4,
    inputHeader5: IH5,
    inputHeader6: IH6,
    inputHeader7: IH7,
    inputHeader8: IH8,
    inputHeader9: IH9,
    inputHeader10: IH10,
    inputBody: IB
): ResponseWithoutBodyWithHeaders10<OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10> {
    val responseEntity = interpreter.send(
        method = this.method,
        uri = this.buildURI(parameter1, parameter2, parameter3, parameter4, parameter5, parameter6, parameter7, parameter8, parameter9, parameter10),
        inputHeaders = this.encodeInputHeaders(inputHeader1, inputHeader2, inputHeader3, inputHeader4, inputHeader5, inputHeader6, inputHeader7, inputHeader8, inputHeader9, inputHeader10),
        inputBodyContentType = this.inputBody.mediaType,
        inputBody = this.inputBody.codec.encode(inputBody),
        outputBodies = this.outputBodies.toList()
    )

    val status = responseEntity.statusCode.toStatus()

    val headers =
        responseEntity
            .headers
            .entries
            .associateBy({ e -> e.key }) { e ->
                e.value.toList().map { it.orEmpty() }
            }

    val (outputHeader1, outputHeader2, outputHeader3, outputHeader4, outputHeader5, outputHeader6, outputHeader7, outputHeader8, outputHeader9, outputHeader10) =
        decodeHeaders10(
            headers,
            this.outputHeaders.item1,
            this.outputHeaders.item2,
            this.outputHeaders.item3,
            this.outputHeaders.item4,
            this.outputHeaders.item5,
            this.outputHeaders.item6,
            this.outputHeaders.item7,
            this.outputHeaders.item8,
            this.outputHeaders.item9,
            this.outputHeaders.item10
        ).getOrElse {
            error("Cannot decode headers: " + it.joinToString(": "))
        }

    return ResponseWithoutBodyWithHeaders10(
        status,
        outputHeader1,
outputHeader2,
outputHeader3,
outputHeader4,
outputHeader5,
outputHeader6,
outputHeader7,
outputHeader8,
outputHeader9,
outputHeader10
    )
}

context(interpreter: RestClientInterpreter)
fun <P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any, P7: Any, P8: Any, P9: Any, P10: Any, IH1: Any, IH2: Any, IH3: Any, IH4: Any, IH5: Any, IH6: Any, IH7: Any, IH8: Any, IH9: Any, IH10: Any, IB, InputBody: Body<IB>, OH1: Any, OH2: Any, OH3: Any, OH4: Any, OH5: Any, OH6: Any, OH7: Any, OH8: Any, OH9: Any, OH10: Any, OB1, OutputBody1: Body<OB1>, OutputBodies: OutputBodies1<OutputBody1>> HttpEndpoint<Parameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10>, Headers10<IH1, IH2, IH3, IH4, IH5, IH6, IH7, IH8, IH9, IH10>, InputBody, Headers10<OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, OutputBodies>.sendWithRestClient(
    parameter1: P1,
    parameter2: P2,
    parameter3: P3,
    parameter4: P4,
    parameter5: P5,
    parameter6: P6,
    parameter7: P7,
    parameter8: P8,
    parameter9: P9,
    parameter10: P10,
    inputHeader1: IH1,
    inputHeader2: IH2,
    inputHeader3: IH3,
    inputHeader4: IH4,
    inputHeader5: IH5,
    inputHeader6: IH6,
    inputHeader7: IH7,
    inputHeader8: IH8,
    inputHeader9: IH9,
    inputHeader10: IH10,
    inputBody: IB
): ResponseWithHeaders10<OB1, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10> {
    val responseEntity = interpreter.send(
        method = this.method,
        uri = this.buildURI(parameter1, parameter2, parameter3, parameter4, parameter5, parameter6, parameter7, parameter8, parameter9, parameter10),
        inputHeaders = this.encodeInputHeaders(inputHeader1, inputHeader2, inputHeader3, inputHeader4, inputHeader5, inputHeader6, inputHeader7, inputHeader8, inputHeader9, inputHeader10),
        inputBodyContentType = this.inputBody.mediaType,
        inputBody = this.inputBody.codec.encode(inputBody),
        outputBodies = this.outputBodies.toList()
    )

    val status = responseEntity.statusCode.toStatus()

    val headers =
        responseEntity
            .headers
            .entries
            .associateBy({ e -> e.key }) { e ->
                e.value.toList().map { it.orEmpty() }
            }

    val (outputHeader1, outputHeader2, outputHeader3, outputHeader4, outputHeader5, outputHeader6, outputHeader7, outputHeader8, outputHeader9, outputHeader10) =
        decodeHeaders10(
            headers,
            this.outputHeaders.item1,
            this.outputHeaders.item2,
            this.outputHeaders.item3,
            this.outputHeaders.item4,
            this.outputHeaders.item5,
            this.outputHeaders.item6,
            this.outputHeaders.item7,
            this.outputHeaders.item8,
            this.outputHeaders.item9,
            this.outputHeaders.item10
        ).getOrElse {
            error("Cannot decode headers: " + it.joinToString(": "))
        }

    val bytes = responseEntity.body ?: ByteArray(0)

    val response = when {
        this.outputBodies.item1.statusMatcher(status) -> this.outputBodies.item1.body.codec.decode(bytes).map { decodedBody ->
            ResponseWithHeaders10(
                status,
                decodedBody,
                outputHeader1,
                outputHeader2,
                outputHeader3,
                outputHeader4,
                outputHeader5,
                outputHeader6,
                outputHeader7,
                outputHeader8,
                outputHeader9,
                outputHeader10
            )
        }
        else -> "Response output did not match".leftNel()
    }

    return response.getOrElse { error(it.joinToString(": ")) }
}

context(interpreter: RestClientInterpreter)
fun <P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any, P7: Any, P8: Any, P9: Any, P10: Any, IH1: Any, IH2: Any, IH3: Any, IH4: Any, IH5: Any, IH6: Any, IH7: Any, IH8: Any, IH9: Any, IH10: Any, IB, InputBody: Body<IB>, OH1: Any, OH2: Any, OH3: Any, OH4: Any, OH5: Any, OH6: Any, OH7: Any, OH8: Any, OH9: Any, OH10: Any, OB1, OutputBody1: Body<OB1>, OB2, OutputBody2: Body<OB2>, OutputBodies: OutputBodies2<OutputBody1, OutputBody2>> HttpEndpoint<Parameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10>, Headers10<IH1, IH2, IH3, IH4, IH5, IH6, IH7, IH8, IH9, IH10>, InputBody, Headers10<OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, OutputBodies>.sendWithRestClient(
    parameter1: P1,
    parameter2: P2,
    parameter3: P3,
    parameter4: P4,
    parameter5: P5,
    parameter6: P6,
    parameter7: P7,
    parameter8: P8,
    parameter9: P9,
    parameter10: P10,
    inputHeader1: IH1,
    inputHeader2: IH2,
    inputHeader3: IH3,
    inputHeader4: IH4,
    inputHeader5: IH5,
    inputHeader6: IH6,
    inputHeader7: IH7,
    inputHeader8: IH8,
    inputHeader9: IH9,
    inputHeader10: IH10,
    inputBody: IB
): Selection2<ResponseWithHeaders10<OB1, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB2, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>> {
    val responseEntity = interpreter.send(
        method = this.method,
        uri = this.buildURI(parameter1, parameter2, parameter3, parameter4, parameter5, parameter6, parameter7, parameter8, parameter9, parameter10),
        inputHeaders = this.encodeInputHeaders(inputHeader1, inputHeader2, inputHeader3, inputHeader4, inputHeader5, inputHeader6, inputHeader7, inputHeader8, inputHeader9, inputHeader10),
        inputBodyContentType = this.inputBody.mediaType,
        inputBody = this.inputBody.codec.encode(inputBody),
        outputBodies = this.outputBodies.toList()
    )

    val status = responseEntity.statusCode.toStatus()

    val headers =
        responseEntity
            .headers
            .entries
            .associateBy({ e -> e.key }) { e ->
                e.value.toList().map { it.orEmpty() }
            }

    val (outputHeader1, outputHeader2, outputHeader3, outputHeader4, outputHeader5, outputHeader6, outputHeader7, outputHeader8, outputHeader9, outputHeader10) =
        decodeHeaders10(
            headers,
            this.outputHeaders.item1,
            this.outputHeaders.item2,
            this.outputHeaders.item3,
            this.outputHeaders.item4,
            this.outputHeaders.item5,
            this.outputHeaders.item6,
            this.outputHeaders.item7,
            this.outputHeaders.item8,
            this.outputHeaders.item9,
            this.outputHeaders.item10
        ).getOrElse {
            error("Cannot decode headers: " + it.joinToString(": "))
        }

    val bytes = responseEntity.body ?: ByteArray(0)

    val response = when {
        this.outputBodies.item1.statusMatcher(status) -> this.outputBodies.item1.body.codec.decode(bytes).map { decodedBody ->
            Selection2.Option1(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item2.statusMatcher(status) -> this.outputBodies.item2.body.codec.decode(bytes).map { decodedBody ->
            Selection2.Option2(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        else -> "Response output did not match".leftNel()
    }

    return response.getOrElse { error(it.joinToString(": ")) }
}

context(interpreter: RestClientInterpreter)
fun <P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any, P7: Any, P8: Any, P9: Any, P10: Any, IH1: Any, IH2: Any, IH3: Any, IH4: Any, IH5: Any, IH6: Any, IH7: Any, IH8: Any, IH9: Any, IH10: Any, IB, InputBody: Body<IB>, OH1: Any, OH2: Any, OH3: Any, OH4: Any, OH5: Any, OH6: Any, OH7: Any, OH8: Any, OH9: Any, OH10: Any, OB1, OutputBody1: Body<OB1>, OB2, OutputBody2: Body<OB2>, OB3, OutputBody3: Body<OB3>, OutputBodies: OutputBodies3<OutputBody1, OutputBody2, OutputBody3>> HttpEndpoint<Parameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10>, Headers10<IH1, IH2, IH3, IH4, IH5, IH6, IH7, IH8, IH9, IH10>, InputBody, Headers10<OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, OutputBodies>.sendWithRestClient(
    parameter1: P1,
    parameter2: P2,
    parameter3: P3,
    parameter4: P4,
    parameter5: P5,
    parameter6: P6,
    parameter7: P7,
    parameter8: P8,
    parameter9: P9,
    parameter10: P10,
    inputHeader1: IH1,
    inputHeader2: IH2,
    inputHeader3: IH3,
    inputHeader4: IH4,
    inputHeader5: IH5,
    inputHeader6: IH6,
    inputHeader7: IH7,
    inputHeader8: IH8,
    inputHeader9: IH9,
    inputHeader10: IH10,
    inputBody: IB
): Selection3<ResponseWithHeaders10<OB1, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB2, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB3, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>> {
    val responseEntity = interpreter.send(
        method = this.method,
        uri = this.buildURI(parameter1, parameter2, parameter3, parameter4, parameter5, parameter6, parameter7, parameter8, parameter9, parameter10),
        inputHeaders = this.encodeInputHeaders(inputHeader1, inputHeader2, inputHeader3, inputHeader4, inputHeader5, inputHeader6, inputHeader7, inputHeader8, inputHeader9, inputHeader10),
        inputBodyContentType = this.inputBody.mediaType,
        inputBody = this.inputBody.codec.encode(inputBody),
        outputBodies = this.outputBodies.toList()
    )

    val status = responseEntity.statusCode.toStatus()

    val headers =
        responseEntity
            .headers
            .entries
            .associateBy({ e -> e.key }) { e ->
                e.value.toList().map { it.orEmpty() }
            }

    val (outputHeader1, outputHeader2, outputHeader3, outputHeader4, outputHeader5, outputHeader6, outputHeader7, outputHeader8, outputHeader9, outputHeader10) =
        decodeHeaders10(
            headers,
            this.outputHeaders.item1,
            this.outputHeaders.item2,
            this.outputHeaders.item3,
            this.outputHeaders.item4,
            this.outputHeaders.item5,
            this.outputHeaders.item6,
            this.outputHeaders.item7,
            this.outputHeaders.item8,
            this.outputHeaders.item9,
            this.outputHeaders.item10
        ).getOrElse {
            error("Cannot decode headers: " + it.joinToString(": "))
        }

    val bytes = responseEntity.body ?: ByteArray(0)

    val response = when {
        this.outputBodies.item1.statusMatcher(status) -> this.outputBodies.item1.body.codec.decode(bytes).map { decodedBody ->
            Selection3.Option1(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item2.statusMatcher(status) -> this.outputBodies.item2.body.codec.decode(bytes).map { decodedBody ->
            Selection3.Option2(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item3.statusMatcher(status) -> this.outputBodies.item3.body.codec.decode(bytes).map { decodedBody ->
            Selection3.Option3(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        else -> "Response output did not match".leftNel()
    }

    return response.getOrElse { error(it.joinToString(": ")) }
}

context(interpreter: RestClientInterpreter)
fun <P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any, P7: Any, P8: Any, P9: Any, P10: Any, IH1: Any, IH2: Any, IH3: Any, IH4: Any, IH5: Any, IH6: Any, IH7: Any, IH8: Any, IH9: Any, IH10: Any, IB, InputBody: Body<IB>, OH1: Any, OH2: Any, OH3: Any, OH4: Any, OH5: Any, OH6: Any, OH7: Any, OH8: Any, OH9: Any, OH10: Any, OB1, OutputBody1: Body<OB1>, OB2, OutputBody2: Body<OB2>, OB3, OutputBody3: Body<OB3>, OB4, OutputBody4: Body<OB4>, OutputBodies: OutputBodies4<OutputBody1, OutputBody2, OutputBody3, OutputBody4>> HttpEndpoint<Parameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10>, Headers10<IH1, IH2, IH3, IH4, IH5, IH6, IH7, IH8, IH9, IH10>, InputBody, Headers10<OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, OutputBodies>.sendWithRestClient(
    parameter1: P1,
    parameter2: P2,
    parameter3: P3,
    parameter4: P4,
    parameter5: P5,
    parameter6: P6,
    parameter7: P7,
    parameter8: P8,
    parameter9: P9,
    parameter10: P10,
    inputHeader1: IH1,
    inputHeader2: IH2,
    inputHeader3: IH3,
    inputHeader4: IH4,
    inputHeader5: IH5,
    inputHeader6: IH6,
    inputHeader7: IH7,
    inputHeader8: IH8,
    inputHeader9: IH9,
    inputHeader10: IH10,
    inputBody: IB
): Selection4<ResponseWithHeaders10<OB1, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB2, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB3, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB4, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>> {
    val responseEntity = interpreter.send(
        method = this.method,
        uri = this.buildURI(parameter1, parameter2, parameter3, parameter4, parameter5, parameter6, parameter7, parameter8, parameter9, parameter10),
        inputHeaders = this.encodeInputHeaders(inputHeader1, inputHeader2, inputHeader3, inputHeader4, inputHeader5, inputHeader6, inputHeader7, inputHeader8, inputHeader9, inputHeader10),
        inputBodyContentType = this.inputBody.mediaType,
        inputBody = this.inputBody.codec.encode(inputBody),
        outputBodies = this.outputBodies.toList()
    )

    val status = responseEntity.statusCode.toStatus()

    val headers =
        responseEntity
            .headers
            .entries
            .associateBy({ e -> e.key }) { e ->
                e.value.toList().map { it.orEmpty() }
            }

    val (outputHeader1, outputHeader2, outputHeader3, outputHeader4, outputHeader5, outputHeader6, outputHeader7, outputHeader8, outputHeader9, outputHeader10) =
        decodeHeaders10(
            headers,
            this.outputHeaders.item1,
            this.outputHeaders.item2,
            this.outputHeaders.item3,
            this.outputHeaders.item4,
            this.outputHeaders.item5,
            this.outputHeaders.item6,
            this.outputHeaders.item7,
            this.outputHeaders.item8,
            this.outputHeaders.item9,
            this.outputHeaders.item10
        ).getOrElse {
            error("Cannot decode headers: " + it.joinToString(": "))
        }

    val bytes = responseEntity.body ?: ByteArray(0)

    val response = when {
        this.outputBodies.item1.statusMatcher(status) -> this.outputBodies.item1.body.codec.decode(bytes).map { decodedBody ->
            Selection4.Option1(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item2.statusMatcher(status) -> this.outputBodies.item2.body.codec.decode(bytes).map { decodedBody ->
            Selection4.Option2(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item3.statusMatcher(status) -> this.outputBodies.item3.body.codec.decode(bytes).map { decodedBody ->
            Selection4.Option3(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item4.statusMatcher(status) -> this.outputBodies.item4.body.codec.decode(bytes).map { decodedBody ->
            Selection4.Option4(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        else -> "Response output did not match".leftNel()
    }

    return response.getOrElse { error(it.joinToString(": ")) }
}

context(interpreter: RestClientInterpreter)
fun <P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any, P7: Any, P8: Any, P9: Any, P10: Any, IH1: Any, IH2: Any, IH3: Any, IH4: Any, IH5: Any, IH6: Any, IH7: Any, IH8: Any, IH9: Any, IH10: Any, IB, InputBody: Body<IB>, OH1: Any, OH2: Any, OH3: Any, OH4: Any, OH5: Any, OH6: Any, OH7: Any, OH8: Any, OH9: Any, OH10: Any, OB1, OutputBody1: Body<OB1>, OB2, OutputBody2: Body<OB2>, OB3, OutputBody3: Body<OB3>, OB4, OutputBody4: Body<OB4>, OB5, OutputBody5: Body<OB5>, OutputBodies: OutputBodies5<OutputBody1, OutputBody2, OutputBody3, OutputBody4, OutputBody5>> HttpEndpoint<Parameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10>, Headers10<IH1, IH2, IH3, IH4, IH5, IH6, IH7, IH8, IH9, IH10>, InputBody, Headers10<OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, OutputBodies>.sendWithRestClient(
    parameter1: P1,
    parameter2: P2,
    parameter3: P3,
    parameter4: P4,
    parameter5: P5,
    parameter6: P6,
    parameter7: P7,
    parameter8: P8,
    parameter9: P9,
    parameter10: P10,
    inputHeader1: IH1,
    inputHeader2: IH2,
    inputHeader3: IH3,
    inputHeader4: IH4,
    inputHeader5: IH5,
    inputHeader6: IH6,
    inputHeader7: IH7,
    inputHeader8: IH8,
    inputHeader9: IH9,
    inputHeader10: IH10,
    inputBody: IB
): Selection5<ResponseWithHeaders10<OB1, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB2, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB3, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB4, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB5, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>> {
    val responseEntity = interpreter.send(
        method = this.method,
        uri = this.buildURI(parameter1, parameter2, parameter3, parameter4, parameter5, parameter6, parameter7, parameter8, parameter9, parameter10),
        inputHeaders = this.encodeInputHeaders(inputHeader1, inputHeader2, inputHeader3, inputHeader4, inputHeader5, inputHeader6, inputHeader7, inputHeader8, inputHeader9, inputHeader10),
        inputBodyContentType = this.inputBody.mediaType,
        inputBody = this.inputBody.codec.encode(inputBody),
        outputBodies = this.outputBodies.toList()
    )

    val status = responseEntity.statusCode.toStatus()

    val headers =
        responseEntity
            .headers
            .entries
            .associateBy({ e -> e.key }) { e ->
                e.value.toList().map { it.orEmpty() }
            }

    val (outputHeader1, outputHeader2, outputHeader3, outputHeader4, outputHeader5, outputHeader6, outputHeader7, outputHeader8, outputHeader9, outputHeader10) =
        decodeHeaders10(
            headers,
            this.outputHeaders.item1,
            this.outputHeaders.item2,
            this.outputHeaders.item3,
            this.outputHeaders.item4,
            this.outputHeaders.item5,
            this.outputHeaders.item6,
            this.outputHeaders.item7,
            this.outputHeaders.item8,
            this.outputHeaders.item9,
            this.outputHeaders.item10
        ).getOrElse {
            error("Cannot decode headers: " + it.joinToString(": "))
        }

    val bytes = responseEntity.body ?: ByteArray(0)

    val response = when {
        this.outputBodies.item1.statusMatcher(status) -> this.outputBodies.item1.body.codec.decode(bytes).map { decodedBody ->
            Selection5.Option1(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item2.statusMatcher(status) -> this.outputBodies.item2.body.codec.decode(bytes).map { decodedBody ->
            Selection5.Option2(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item3.statusMatcher(status) -> this.outputBodies.item3.body.codec.decode(bytes).map { decodedBody ->
            Selection5.Option3(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item4.statusMatcher(status) -> this.outputBodies.item4.body.codec.decode(bytes).map { decodedBody ->
            Selection5.Option4(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item5.statusMatcher(status) -> this.outputBodies.item5.body.codec.decode(bytes).map { decodedBody ->
            Selection5.Option5(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        else -> "Response output did not match".leftNel()
    }

    return response.getOrElse { error(it.joinToString(": ")) }
}

context(interpreter: RestClientInterpreter)
fun <P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any, P7: Any, P8: Any, P9: Any, P10: Any, IH1: Any, IH2: Any, IH3: Any, IH4: Any, IH5: Any, IH6: Any, IH7: Any, IH8: Any, IH9: Any, IH10: Any, IB, InputBody: Body<IB>, OH1: Any, OH2: Any, OH3: Any, OH4: Any, OH5: Any, OH6: Any, OH7: Any, OH8: Any, OH9: Any, OH10: Any, OB1, OutputBody1: Body<OB1>, OB2, OutputBody2: Body<OB2>, OB3, OutputBody3: Body<OB3>, OB4, OutputBody4: Body<OB4>, OB5, OutputBody5: Body<OB5>, OB6, OutputBody6: Body<OB6>, OutputBodies: OutputBodies6<OutputBody1, OutputBody2, OutputBody3, OutputBody4, OutputBody5, OutputBody6>> HttpEndpoint<Parameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10>, Headers10<IH1, IH2, IH3, IH4, IH5, IH6, IH7, IH8, IH9, IH10>, InputBody, Headers10<OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, OutputBodies>.sendWithRestClient(
    parameter1: P1,
    parameter2: P2,
    parameter3: P3,
    parameter4: P4,
    parameter5: P5,
    parameter6: P6,
    parameter7: P7,
    parameter8: P8,
    parameter9: P9,
    parameter10: P10,
    inputHeader1: IH1,
    inputHeader2: IH2,
    inputHeader3: IH3,
    inputHeader4: IH4,
    inputHeader5: IH5,
    inputHeader6: IH6,
    inputHeader7: IH7,
    inputHeader8: IH8,
    inputHeader9: IH9,
    inputHeader10: IH10,
    inputBody: IB
): Selection6<ResponseWithHeaders10<OB1, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB2, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB3, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB4, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB5, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB6, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>> {
    val responseEntity = interpreter.send(
        method = this.method,
        uri = this.buildURI(parameter1, parameter2, parameter3, parameter4, parameter5, parameter6, parameter7, parameter8, parameter9, parameter10),
        inputHeaders = this.encodeInputHeaders(inputHeader1, inputHeader2, inputHeader3, inputHeader4, inputHeader5, inputHeader6, inputHeader7, inputHeader8, inputHeader9, inputHeader10),
        inputBodyContentType = this.inputBody.mediaType,
        inputBody = this.inputBody.codec.encode(inputBody),
        outputBodies = this.outputBodies.toList()
    )

    val status = responseEntity.statusCode.toStatus()

    val headers =
        responseEntity
            .headers
            .entries
            .associateBy({ e -> e.key }) { e ->
                e.value.toList().map { it.orEmpty() }
            }

    val (outputHeader1, outputHeader2, outputHeader3, outputHeader4, outputHeader5, outputHeader6, outputHeader7, outputHeader8, outputHeader9, outputHeader10) =
        decodeHeaders10(
            headers,
            this.outputHeaders.item1,
            this.outputHeaders.item2,
            this.outputHeaders.item3,
            this.outputHeaders.item4,
            this.outputHeaders.item5,
            this.outputHeaders.item6,
            this.outputHeaders.item7,
            this.outputHeaders.item8,
            this.outputHeaders.item9,
            this.outputHeaders.item10
        ).getOrElse {
            error("Cannot decode headers: " + it.joinToString(": "))
        }

    val bytes = responseEntity.body ?: ByteArray(0)

    val response = when {
        this.outputBodies.item1.statusMatcher(status) -> this.outputBodies.item1.body.codec.decode(bytes).map { decodedBody ->
            Selection6.Option1(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item2.statusMatcher(status) -> this.outputBodies.item2.body.codec.decode(bytes).map { decodedBody ->
            Selection6.Option2(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item3.statusMatcher(status) -> this.outputBodies.item3.body.codec.decode(bytes).map { decodedBody ->
            Selection6.Option3(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item4.statusMatcher(status) -> this.outputBodies.item4.body.codec.decode(bytes).map { decodedBody ->
            Selection6.Option4(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item5.statusMatcher(status) -> this.outputBodies.item5.body.codec.decode(bytes).map { decodedBody ->
            Selection6.Option5(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item6.statusMatcher(status) -> this.outputBodies.item6.body.codec.decode(bytes).map { decodedBody ->
            Selection6.Option6(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        else -> "Response output did not match".leftNel()
    }

    return response.getOrElse { error(it.joinToString(": ")) }
}

context(interpreter: RestClientInterpreter)
fun <P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any, P7: Any, P8: Any, P9: Any, P10: Any, IH1: Any, IH2: Any, IH3: Any, IH4: Any, IH5: Any, IH6: Any, IH7: Any, IH8: Any, IH9: Any, IH10: Any, IB, InputBody: Body<IB>, OH1: Any, OH2: Any, OH3: Any, OH4: Any, OH5: Any, OH6: Any, OH7: Any, OH8: Any, OH9: Any, OH10: Any, OB1, OutputBody1: Body<OB1>, OB2, OutputBody2: Body<OB2>, OB3, OutputBody3: Body<OB3>, OB4, OutputBody4: Body<OB4>, OB5, OutputBody5: Body<OB5>, OB6, OutputBody6: Body<OB6>, OB7, OutputBody7: Body<OB7>, OutputBodies: OutputBodies7<OutputBody1, OutputBody2, OutputBody3, OutputBody4, OutputBody5, OutputBody6, OutputBody7>> HttpEndpoint<Parameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10>, Headers10<IH1, IH2, IH3, IH4, IH5, IH6, IH7, IH8, IH9, IH10>, InputBody, Headers10<OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, OutputBodies>.sendWithRestClient(
    parameter1: P1,
    parameter2: P2,
    parameter3: P3,
    parameter4: P4,
    parameter5: P5,
    parameter6: P6,
    parameter7: P7,
    parameter8: P8,
    parameter9: P9,
    parameter10: P10,
    inputHeader1: IH1,
    inputHeader2: IH2,
    inputHeader3: IH3,
    inputHeader4: IH4,
    inputHeader5: IH5,
    inputHeader6: IH6,
    inputHeader7: IH7,
    inputHeader8: IH8,
    inputHeader9: IH9,
    inputHeader10: IH10,
    inputBody: IB
): Selection7<ResponseWithHeaders10<OB1, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB2, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB3, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB4, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB5, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB6, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB7, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>> {
    val responseEntity = interpreter.send(
        method = this.method,
        uri = this.buildURI(parameter1, parameter2, parameter3, parameter4, parameter5, parameter6, parameter7, parameter8, parameter9, parameter10),
        inputHeaders = this.encodeInputHeaders(inputHeader1, inputHeader2, inputHeader3, inputHeader4, inputHeader5, inputHeader6, inputHeader7, inputHeader8, inputHeader9, inputHeader10),
        inputBodyContentType = this.inputBody.mediaType,
        inputBody = this.inputBody.codec.encode(inputBody),
        outputBodies = this.outputBodies.toList()
    )

    val status = responseEntity.statusCode.toStatus()

    val headers =
        responseEntity
            .headers
            .entries
            .associateBy({ e -> e.key }) { e ->
                e.value.toList().map { it.orEmpty() }
            }

    val (outputHeader1, outputHeader2, outputHeader3, outputHeader4, outputHeader5, outputHeader6, outputHeader7, outputHeader8, outputHeader9, outputHeader10) =
        decodeHeaders10(
            headers,
            this.outputHeaders.item1,
            this.outputHeaders.item2,
            this.outputHeaders.item3,
            this.outputHeaders.item4,
            this.outputHeaders.item5,
            this.outputHeaders.item6,
            this.outputHeaders.item7,
            this.outputHeaders.item8,
            this.outputHeaders.item9,
            this.outputHeaders.item10
        ).getOrElse {
            error("Cannot decode headers: " + it.joinToString(": "))
        }

    val bytes = responseEntity.body ?: ByteArray(0)

    val response = when {
        this.outputBodies.item1.statusMatcher(status) -> this.outputBodies.item1.body.codec.decode(bytes).map { decodedBody ->
            Selection7.Option1(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item2.statusMatcher(status) -> this.outputBodies.item2.body.codec.decode(bytes).map { decodedBody ->
            Selection7.Option2(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item3.statusMatcher(status) -> this.outputBodies.item3.body.codec.decode(bytes).map { decodedBody ->
            Selection7.Option3(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item4.statusMatcher(status) -> this.outputBodies.item4.body.codec.decode(bytes).map { decodedBody ->
            Selection7.Option4(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item5.statusMatcher(status) -> this.outputBodies.item5.body.codec.decode(bytes).map { decodedBody ->
            Selection7.Option5(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item6.statusMatcher(status) -> this.outputBodies.item6.body.codec.decode(bytes).map { decodedBody ->
            Selection7.Option6(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item7.statusMatcher(status) -> this.outputBodies.item7.body.codec.decode(bytes).map { decodedBody ->
            Selection7.Option7(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        else -> "Response output did not match".leftNel()
    }

    return response.getOrElse { error(it.joinToString(": ")) }
}

context(interpreter: RestClientInterpreter)
fun <P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any, P7: Any, P8: Any, P9: Any, P10: Any, IH1: Any, IH2: Any, IH3: Any, IH4: Any, IH5: Any, IH6: Any, IH7: Any, IH8: Any, IH9: Any, IH10: Any, IB, InputBody: Body<IB>, OH1: Any, OH2: Any, OH3: Any, OH4: Any, OH5: Any, OH6: Any, OH7: Any, OH8: Any, OH9: Any, OH10: Any, OB1, OutputBody1: Body<OB1>, OB2, OutputBody2: Body<OB2>, OB3, OutputBody3: Body<OB3>, OB4, OutputBody4: Body<OB4>, OB5, OutputBody5: Body<OB5>, OB6, OutputBody6: Body<OB6>, OB7, OutputBody7: Body<OB7>, OB8, OutputBody8: Body<OB8>, OutputBodies: OutputBodies8<OutputBody1, OutputBody2, OutputBody3, OutputBody4, OutputBody5, OutputBody6, OutputBody7, OutputBody8>> HttpEndpoint<Parameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10>, Headers10<IH1, IH2, IH3, IH4, IH5, IH6, IH7, IH8, IH9, IH10>, InputBody, Headers10<OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, OutputBodies>.sendWithRestClient(
    parameter1: P1,
    parameter2: P2,
    parameter3: P3,
    parameter4: P4,
    parameter5: P5,
    parameter6: P6,
    parameter7: P7,
    parameter8: P8,
    parameter9: P9,
    parameter10: P10,
    inputHeader1: IH1,
    inputHeader2: IH2,
    inputHeader3: IH3,
    inputHeader4: IH4,
    inputHeader5: IH5,
    inputHeader6: IH6,
    inputHeader7: IH7,
    inputHeader8: IH8,
    inputHeader9: IH9,
    inputHeader10: IH10,
    inputBody: IB
): Selection8<ResponseWithHeaders10<OB1, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB2, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB3, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB4, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB5, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB6, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB7, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB8, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>> {
    val responseEntity = interpreter.send(
        method = this.method,
        uri = this.buildURI(parameter1, parameter2, parameter3, parameter4, parameter5, parameter6, parameter7, parameter8, parameter9, parameter10),
        inputHeaders = this.encodeInputHeaders(inputHeader1, inputHeader2, inputHeader3, inputHeader4, inputHeader5, inputHeader6, inputHeader7, inputHeader8, inputHeader9, inputHeader10),
        inputBodyContentType = this.inputBody.mediaType,
        inputBody = this.inputBody.codec.encode(inputBody),
        outputBodies = this.outputBodies.toList()
    )

    val status = responseEntity.statusCode.toStatus()

    val headers =
        responseEntity
            .headers
            .entries
            .associateBy({ e -> e.key }) { e ->
                e.value.toList().map { it.orEmpty() }
            }

    val (outputHeader1, outputHeader2, outputHeader3, outputHeader4, outputHeader5, outputHeader6, outputHeader7, outputHeader8, outputHeader9, outputHeader10) =
        decodeHeaders10(
            headers,
            this.outputHeaders.item1,
            this.outputHeaders.item2,
            this.outputHeaders.item3,
            this.outputHeaders.item4,
            this.outputHeaders.item5,
            this.outputHeaders.item6,
            this.outputHeaders.item7,
            this.outputHeaders.item8,
            this.outputHeaders.item9,
            this.outputHeaders.item10
        ).getOrElse {
            error("Cannot decode headers: " + it.joinToString(": "))
        }

    val bytes = responseEntity.body ?: ByteArray(0)

    val response = when {
        this.outputBodies.item1.statusMatcher(status) -> this.outputBodies.item1.body.codec.decode(bytes).map { decodedBody ->
            Selection8.Option1(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item2.statusMatcher(status) -> this.outputBodies.item2.body.codec.decode(bytes).map { decodedBody ->
            Selection8.Option2(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item3.statusMatcher(status) -> this.outputBodies.item3.body.codec.decode(bytes).map { decodedBody ->
            Selection8.Option3(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item4.statusMatcher(status) -> this.outputBodies.item4.body.codec.decode(bytes).map { decodedBody ->
            Selection8.Option4(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item5.statusMatcher(status) -> this.outputBodies.item5.body.codec.decode(bytes).map { decodedBody ->
            Selection8.Option5(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item6.statusMatcher(status) -> this.outputBodies.item6.body.codec.decode(bytes).map { decodedBody ->
            Selection8.Option6(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item7.statusMatcher(status) -> this.outputBodies.item7.body.codec.decode(bytes).map { decodedBody ->
            Selection8.Option7(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item8.statusMatcher(status) -> this.outputBodies.item8.body.codec.decode(bytes).map { decodedBody ->
            Selection8.Option8(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        else -> "Response output did not match".leftNel()
    }

    return response.getOrElse { error(it.joinToString(": ")) }
}

context(interpreter: RestClientInterpreter)
fun <P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any, P7: Any, P8: Any, P9: Any, P10: Any, IH1: Any, IH2: Any, IH3: Any, IH4: Any, IH5: Any, IH6: Any, IH7: Any, IH8: Any, IH9: Any, IH10: Any, IB, InputBody: Body<IB>, OH1: Any, OH2: Any, OH3: Any, OH4: Any, OH5: Any, OH6: Any, OH7: Any, OH8: Any, OH9: Any, OH10: Any, OB1, OutputBody1: Body<OB1>, OB2, OutputBody2: Body<OB2>, OB3, OutputBody3: Body<OB3>, OB4, OutputBody4: Body<OB4>, OB5, OutputBody5: Body<OB5>, OB6, OutputBody6: Body<OB6>, OB7, OutputBody7: Body<OB7>, OB8, OutputBody8: Body<OB8>, OB9, OutputBody9: Body<OB9>, OutputBodies: OutputBodies9<OutputBody1, OutputBody2, OutputBody3, OutputBody4, OutputBody5, OutputBody6, OutputBody7, OutputBody8, OutputBody9>> HttpEndpoint<Parameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10>, Headers10<IH1, IH2, IH3, IH4, IH5, IH6, IH7, IH8, IH9, IH10>, InputBody, Headers10<OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, OutputBodies>.sendWithRestClient(
    parameter1: P1,
    parameter2: P2,
    parameter3: P3,
    parameter4: P4,
    parameter5: P5,
    parameter6: P6,
    parameter7: P7,
    parameter8: P8,
    parameter9: P9,
    parameter10: P10,
    inputHeader1: IH1,
    inputHeader2: IH2,
    inputHeader3: IH3,
    inputHeader4: IH4,
    inputHeader5: IH5,
    inputHeader6: IH6,
    inputHeader7: IH7,
    inputHeader8: IH8,
    inputHeader9: IH9,
    inputHeader10: IH10,
    inputBody: IB
): Selection9<ResponseWithHeaders10<OB1, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB2, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB3, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB4, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB5, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB6, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB7, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB8, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB9, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>> {
    val responseEntity = interpreter.send(
        method = this.method,
        uri = this.buildURI(parameter1, parameter2, parameter3, parameter4, parameter5, parameter6, parameter7, parameter8, parameter9, parameter10),
        inputHeaders = this.encodeInputHeaders(inputHeader1, inputHeader2, inputHeader3, inputHeader4, inputHeader5, inputHeader6, inputHeader7, inputHeader8, inputHeader9, inputHeader10),
        inputBodyContentType = this.inputBody.mediaType,
        inputBody = this.inputBody.codec.encode(inputBody),
        outputBodies = this.outputBodies.toList()
    )

    val status = responseEntity.statusCode.toStatus()

    val headers =
        responseEntity
            .headers
            .entries
            .associateBy({ e -> e.key }) { e ->
                e.value.toList().map { it.orEmpty() }
            }

    val (outputHeader1, outputHeader2, outputHeader3, outputHeader4, outputHeader5, outputHeader6, outputHeader7, outputHeader8, outputHeader9, outputHeader10) =
        decodeHeaders10(
            headers,
            this.outputHeaders.item1,
            this.outputHeaders.item2,
            this.outputHeaders.item3,
            this.outputHeaders.item4,
            this.outputHeaders.item5,
            this.outputHeaders.item6,
            this.outputHeaders.item7,
            this.outputHeaders.item8,
            this.outputHeaders.item9,
            this.outputHeaders.item10
        ).getOrElse {
            error("Cannot decode headers: " + it.joinToString(": "))
        }

    val bytes = responseEntity.body ?: ByteArray(0)

    val response = when {
        this.outputBodies.item1.statusMatcher(status) -> this.outputBodies.item1.body.codec.decode(bytes).map { decodedBody ->
            Selection9.Option1(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item2.statusMatcher(status) -> this.outputBodies.item2.body.codec.decode(bytes).map { decodedBody ->
            Selection9.Option2(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item3.statusMatcher(status) -> this.outputBodies.item3.body.codec.decode(bytes).map { decodedBody ->
            Selection9.Option3(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item4.statusMatcher(status) -> this.outputBodies.item4.body.codec.decode(bytes).map { decodedBody ->
            Selection9.Option4(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item5.statusMatcher(status) -> this.outputBodies.item5.body.codec.decode(bytes).map { decodedBody ->
            Selection9.Option5(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item6.statusMatcher(status) -> this.outputBodies.item6.body.codec.decode(bytes).map { decodedBody ->
            Selection9.Option6(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item7.statusMatcher(status) -> this.outputBodies.item7.body.codec.decode(bytes).map { decodedBody ->
            Selection9.Option7(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item8.statusMatcher(status) -> this.outputBodies.item8.body.codec.decode(bytes).map { decodedBody ->
            Selection9.Option8(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item9.statusMatcher(status) -> this.outputBodies.item9.body.codec.decode(bytes).map { decodedBody ->
            Selection9.Option9(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        else -> "Response output did not match".leftNel()
    }

    return response.getOrElse { error(it.joinToString(": ")) }
}

context(interpreter: RestClientInterpreter)
fun <P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any, P7: Any, P8: Any, P9: Any, P10: Any, IH1: Any, IH2: Any, IH3: Any, IH4: Any, IH5: Any, IH6: Any, IH7: Any, IH8: Any, IH9: Any, IH10: Any, IB, InputBody: Body<IB>, OH1: Any, OH2: Any, OH3: Any, OH4: Any, OH5: Any, OH6: Any, OH7: Any, OH8: Any, OH9: Any, OH10: Any, OB1, OutputBody1: Body<OB1>, OB2, OutputBody2: Body<OB2>, OB3, OutputBody3: Body<OB3>, OB4, OutputBody4: Body<OB4>, OB5, OutputBody5: Body<OB5>, OB6, OutputBody6: Body<OB6>, OB7, OutputBody7: Body<OB7>, OB8, OutputBody8: Body<OB8>, OB9, OutputBody9: Body<OB9>, OB10, OutputBody10: Body<OB10>, OutputBodies: OutputBodies10<OutputBody1, OutputBody2, OutputBody3, OutputBody4, OutputBody5, OutputBody6, OutputBody7, OutputBody8, OutputBody9, OutputBody10>> HttpEndpoint<Parameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10>, Headers10<IH1, IH2, IH3, IH4, IH5, IH6, IH7, IH8, IH9, IH10>, InputBody, Headers10<OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, OutputBodies>.sendWithRestClient(
    parameter1: P1,
    parameter2: P2,
    parameter3: P3,
    parameter4: P4,
    parameter5: P5,
    parameter6: P6,
    parameter7: P7,
    parameter8: P8,
    parameter9: P9,
    parameter10: P10,
    inputHeader1: IH1,
    inputHeader2: IH2,
    inputHeader3: IH3,
    inputHeader4: IH4,
    inputHeader5: IH5,
    inputHeader6: IH6,
    inputHeader7: IH7,
    inputHeader8: IH8,
    inputHeader9: IH9,
    inputHeader10: IH10,
    inputBody: IB
): Selection10<ResponseWithHeaders10<OB1, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB2, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB3, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB4, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB5, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB6, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB7, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB8, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB9, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>, ResponseWithHeaders10<OB10, OH1, OH2, OH3, OH4, OH5, OH6, OH7, OH8, OH9, OH10>> {
    val responseEntity = interpreter.send(
        method = this.method,
        uri = this.buildURI(parameter1, parameter2, parameter3, parameter4, parameter5, parameter6, parameter7, parameter8, parameter9, parameter10),
        inputHeaders = this.encodeInputHeaders(inputHeader1, inputHeader2, inputHeader3, inputHeader4, inputHeader5, inputHeader6, inputHeader7, inputHeader8, inputHeader9, inputHeader10),
        inputBodyContentType = this.inputBody.mediaType,
        inputBody = this.inputBody.codec.encode(inputBody),
        outputBodies = this.outputBodies.toList()
    )

    val status = responseEntity.statusCode.toStatus()

    val headers =
        responseEntity
            .headers
            .entries
            .associateBy({ e -> e.key }) { e ->
                e.value.toList().map { it.orEmpty() }
            }

    val (outputHeader1, outputHeader2, outputHeader3, outputHeader4, outputHeader5, outputHeader6, outputHeader7, outputHeader8, outputHeader9, outputHeader10) =
        decodeHeaders10(
            headers,
            this.outputHeaders.item1,
            this.outputHeaders.item2,
            this.outputHeaders.item3,
            this.outputHeaders.item4,
            this.outputHeaders.item5,
            this.outputHeaders.item6,
            this.outputHeaders.item7,
            this.outputHeaders.item8,
            this.outputHeaders.item9,
            this.outputHeaders.item10
        ).getOrElse {
            error("Cannot decode headers: " + it.joinToString(": "))
        }

    val bytes = responseEntity.body ?: ByteArray(0)

    val response = when {
        this.outputBodies.item1.statusMatcher(status) -> this.outputBodies.item1.body.codec.decode(bytes).map { decodedBody ->
            Selection10.Option1(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item2.statusMatcher(status) -> this.outputBodies.item2.body.codec.decode(bytes).map { decodedBody ->
            Selection10.Option2(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item3.statusMatcher(status) -> this.outputBodies.item3.body.codec.decode(bytes).map { decodedBody ->
            Selection10.Option3(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item4.statusMatcher(status) -> this.outputBodies.item4.body.codec.decode(bytes).map { decodedBody ->
            Selection10.Option4(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item5.statusMatcher(status) -> this.outputBodies.item5.body.codec.decode(bytes).map { decodedBody ->
            Selection10.Option5(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item6.statusMatcher(status) -> this.outputBodies.item6.body.codec.decode(bytes).map { decodedBody ->
            Selection10.Option6(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item7.statusMatcher(status) -> this.outputBodies.item7.body.codec.decode(bytes).map { decodedBody ->
            Selection10.Option7(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item8.statusMatcher(status) -> this.outputBodies.item8.body.codec.decode(bytes).map { decodedBody ->
            Selection10.Option8(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item9.statusMatcher(status) -> this.outputBodies.item9.body.codec.decode(bytes).map { decodedBody ->
            Selection10.Option9(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        this.outputBodies.item10.statusMatcher(status) -> this.outputBodies.item10.body.codec.decode(bytes).map { decodedBody ->
            Selection10.Option10(
                ResponseWithHeaders10(
                    status,
                    decodedBody,
                    outputHeader1,
                    outputHeader2,
                    outputHeader3,
                    outputHeader4,
                    outputHeader5,
                    outputHeader6,
                    outputHeader7,
                    outputHeader8,
                    outputHeader9,
                    outputHeader10
                )
            )
        }
        else -> "Response output did not match".leftNel()
    }

    return response.getOrElse { error(it.joinToString(": ")) }
}