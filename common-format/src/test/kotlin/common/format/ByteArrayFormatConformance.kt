package dev.akif.tapik.common.format

import dev.akif.tapik.ByteArrayFormat
import dev.akif.tapik.DecodeResult
import dev.akif.tapik.Schema
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/** One reusable codec and schema contract for a byte-array format provider. */
class ByteArrayFormatConformanceCase<Value : Any>(
    val name: String,
    val format: () -> ByteArrayFormat<Value>,
    val value: Value,
    val encoded: String,
    val schema: Schema,
    val malformed: ByteArray = "{".encodeToByteArray()
)

/** Adds the common codec and schema expectations represented by [case]. */
fun <Value : Any> FunSpec.includeByteArrayFormatConformance(case: ByteArrayFormatConformanceCase<Value>) {
    test("${case.name} encodes and decodes") {
        val format = case.format()

        format.encode(case.value).decodeToString() shouldBe case.encoded
        format.decode(case.encoded.encodeToByteArray()) shouldBe DecodeResult.Success(case.value)
    }

    test("${case.name} reports malformed representations") {
        case.format().decode(case.malformed).shouldBeInstanceOf<DecodeResult.Failure>()
    }

    test("${case.name} derives its schema") {
        case.format().schema shouldBe case.schema
    }
}
