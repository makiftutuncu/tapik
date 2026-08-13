package dev.akif.tapik

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.Period
import java.util.UUID

class StringFormatsSpec : FunSpec({
    test("cache built-in formats") {
        string.uuid shouldBeSameInstanceAs string.uuid
        string.localDate shouldBeSameInstanceAs string.localDate
    }

    test("provide string and scalar formats") {
        string.string.decode("tapik") shouldBe DecodeResult.Success("tapik")
        string.boolean.decode("true") shouldBe DecodeResult.Success(true)
        string.byte.decode("8") shouldBe DecodeResult.Success(8.toByte())
        string.short.decode("16") shouldBe DecodeResult.Success(16.toShort())
        string.int.decode("32") shouldBe DecodeResult.Success(32)
        string.long.decode("64") shouldBe DecodeResult.Success(64L)
        string.float.decode("1.5") shouldBe DecodeResult.Success(1.5F)
        string.double.decode("2.5") shouldBe DecodeResult.Success(2.5)
        string.bigInteger.decode("12345678901234567890") shouldBe
            DecodeResult.Success(BigInteger("12345678901234567890"))
        string.bigDecimal.decode("1234567890.123456789") shouldBe
            DecodeResult.Success(BigDecimal("1234567890.123456789"))

        val uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
        string.uuid.decode(uuid.toString()) shouldBe DecodeResult.Success(uuid)
        string.uuid.encode(uuid) shouldBe uuid.toString()
    }

    test("describe scalar formats with OpenAPI-compatible schemas") {
        string.boolean.schema shouldBe ScalarSchema(SchemaType.BOOLEAN)
        string.byte.schema shouldBe ScalarSchema(SchemaType.INTEGER, "int8")
        string.short.schema shouldBe ScalarSchema(SchemaType.INTEGER, "int16")
        string.int.schema shouldBe ScalarSchema(SchemaType.INTEGER, "int32")
        string.long.schema shouldBe ScalarSchema(SchemaType.INTEGER, "int64")
        string.float.schema shouldBe ScalarSchema(SchemaType.NUMBER, "float")
        string.double.schema shouldBe ScalarSchema(SchemaType.NUMBER, "double")
        string.bigInteger.schema shouldBe ScalarSchema(SchemaType.INTEGER)
        string.bigDecimal.schema shouldBe ScalarSchema(SchemaType.NUMBER, "decimal")
        string.string.schema shouldBe ScalarSchema(SchemaType.STRING)
        string.uuid.schema shouldBe ScalarSchema(SchemaType.STRING, "uuid")
    }

    test("return a structured failure for invalid scalar input") {
        val failure = string.int.decode("not-an-int").shouldBeInstanceOf<DecodeResult.Failure>()

        failure.errors.single().message shouldBe "Cannot decode 'not-an-int' as Int"
        failure.errors.single().cause.shouldBeInstanceOf<NumberFormatException>()
    }

    test("provide lossless standard date and time formats") {
        verifyRoundTrip(string.localDate, LocalDate.parse("2026-08-13"))
        verifyRoundTrip(string.localTime, LocalTime.parse("19:30:15.123"))
        verifyRoundTrip(string.localDateTime, LocalDateTime.parse("2026-08-13T19:30:15.123"))
        verifyRoundTrip(string.offsetTime, OffsetTime.parse("19:30:15.123+03:00"))
        verifyRoundTrip(string.offsetDateTime, OffsetDateTime.parse("2026-08-13T19:30:15.123+03:00"))
        verifyRoundTrip(string.instant, Instant.parse("2026-08-13T16:30:15.123Z"))
        verifyRoundTrip(string.duration, Duration.parse("PT2H30M"))
        verifyRoundTrip(string.period, Period.parse("P1Y2M3D"))
    }

    test("describe date and time formats") {
        string.localDate.schema shouldBe ScalarSchema(SchemaType.STRING, "date")
        string.localTime.schema shouldBe ScalarSchema(SchemaType.STRING, "time-local")
        string.localDateTime.schema shouldBe ScalarSchema(SchemaType.STRING, "date-time-local")
        string.offsetTime.schema shouldBe ScalarSchema(SchemaType.STRING, "time")
        string.offsetDateTime.schema shouldBe ScalarSchema(SchemaType.STRING, "date-time")
        string.instant.schema shouldBe ScalarSchema(SchemaType.STRING, "date-time")
        string.duration.schema shouldBe ScalarSchema(SchemaType.STRING, "duration")
        string.period.schema shouldBe ScalarSchema(SchemaType.STRING, "duration")
    }
})

private fun <Value : Any> verifyRoundTrip(format: StringFormat<Value>, value: Value) {
    format.decode(format.encode(value)) shouldBe DecodeResult.Success(value)
}
