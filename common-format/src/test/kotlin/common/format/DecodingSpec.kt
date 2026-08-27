package dev.akif.tapik.common.format

import dev.akif.tapik.DecodeResult
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DecodingSpec : FunSpec({
    test("capture decoding exceptions with their cause") {
        val cause = IllegalArgumentException("invalid value")

        decodeCatching<String>("decoding failed") { throw cause } shouldBe
            DecodeResult.Failure(
                error =
                    dev.akif.tapik.DecodeError(
                        message = "invalid value",
                        cause = cause
                    )
            )
    }

    test("use the fallback message when an exception has none") {
        val cause = IllegalArgumentException()

        decodeCatching<String>("decoding failed") { throw cause } shouldBe
            DecodeResult.Failure(
                error =
                    dev.akif.tapik.DecodeError(
                        message = "decoding failed",
                        cause = cause
                    )
            )
    }
})
