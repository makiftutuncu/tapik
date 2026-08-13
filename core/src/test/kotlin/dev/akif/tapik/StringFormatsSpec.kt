package dev.akif.tapik

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
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
    test("implement scalar defaults for String representations") {
        val defaults: FormatDefaults<String> = StringFormats

        defaults.boolean.encode(true) shouldBe "true"
        defaults.uuid.encode(UUID.fromString("123e4567-e89b-12d3-a456-426614174000")) shouldBe
            "123e4567-e89b-12d3-a456-426614174000"
    }

    test("provide string and scalar formats") {
        format.string.decode("tapik") shouldBe DecodeResult.Success("tapik")
        format.boolean.decode("true") shouldBe DecodeResult.Success(true)
        format.byte.decode("8") shouldBe DecodeResult.Success(8.toByte())
        format.short.decode("16") shouldBe DecodeResult.Success(16.toShort())
        format.int.decode("32") shouldBe DecodeResult.Success(32)
        format.long.decode("64") shouldBe DecodeResult.Success(64L)
        format.float.decode("1.5") shouldBe DecodeResult.Success(1.5F)
        format.double.decode("2.5") shouldBe DecodeResult.Success(2.5)
        format.bigInteger.decode("12345678901234567890") shouldBe
            DecodeResult.Success(BigInteger("12345678901234567890"))
        format.bigDecimal.decode("1234567890.123456789") shouldBe
            DecodeResult.Success(BigDecimal("1234567890.123456789"))

        val uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
        format.uuid.decode(uuid.toString()) shouldBe DecodeResult.Success(uuid)
        format.uuid.encode(uuid) shouldBe uuid.toString()
    }

    test("describe scalar formats with OpenAPI-compatible schemas") {
        format.boolean.schema shouldBe ScalarSchema(SchemaType.BOOLEAN)
        format.byte.schema shouldBe ScalarSchema(SchemaType.INTEGER, "int8")
        format.short.schema shouldBe ScalarSchema(SchemaType.INTEGER, "int16")
        format.int.schema shouldBe ScalarSchema(SchemaType.INTEGER, "int32")
        format.long.schema shouldBe ScalarSchema(SchemaType.INTEGER, "int64")
        format.float.schema shouldBe ScalarSchema(SchemaType.NUMBER, "float")
        format.double.schema shouldBe ScalarSchema(SchemaType.NUMBER, "double")
        format.bigInteger.schema shouldBe ScalarSchema(SchemaType.INTEGER)
        format.bigDecimal.schema shouldBe ScalarSchema(SchemaType.NUMBER, "decimal")
        format.string.schema shouldBe ScalarSchema(SchemaType.STRING)
        format.uuid.schema shouldBe ScalarSchema(SchemaType.STRING, "uuid")
    }

    test("return a structured failure for invalid scalar input") {
        val failure = format.int.decode("not-an-int").shouldBeInstanceOf<DecodeResult.Failure>()

        failure.errors.single().message shouldBe "Cannot decode 'not-an-int' as Int"
        failure.errors.single().cause.shouldBeInstanceOf<NumberFormatException>()
    }

    test("provide lossless standard date and time formats") {
        verifyRoundTrip(format.localDate, LocalDate.parse("2026-08-13"))
        verifyRoundTrip(format.localTime, LocalTime.parse("19:30:15.123"))
        verifyRoundTrip(format.localDateTime, LocalDateTime.parse("2026-08-13T19:30:15.123"))
        verifyRoundTrip(format.offsetTime, OffsetTime.parse("19:30:15.123+03:00"))
        verifyRoundTrip(format.offsetDateTime, OffsetDateTime.parse("2026-08-13T19:30:15.123+03:00"))
        verifyRoundTrip(format.instant, Instant.parse("2026-08-13T16:30:15.123Z"))
        verifyRoundTrip(format.duration, Duration.parse("PT2H30M"))
        verifyRoundTrip(format.period, Period.parse("P1Y2M3D"))
    }

    test("describe date and time formats") {
        format.localDate.schema shouldBe ScalarSchema(SchemaType.STRING, "date")
        format.localTime.schema shouldBe ScalarSchema(SchemaType.STRING, "time-local")
        format.localDateTime.schema shouldBe ScalarSchema(SchemaType.STRING, "date-time-local")
        format.offsetTime.schema shouldBe ScalarSchema(SchemaType.STRING, "time")
        format.offsetDateTime.schema shouldBe ScalarSchema(SchemaType.STRING, "date-time")
        format.instant.schema shouldBe ScalarSchema(SchemaType.STRING, "date-time")
        format.duration.schema shouldBe ScalarSchema(SchemaType.STRING, "duration")
        format.period.schema shouldBe ScalarSchema(SchemaType.STRING, "duration")
    }
})

private fun <Value : Any> verifyRoundTrip(format: StringFormat<Value>, value: Value) {
    format.decode(format.encode(value)) shouldBe DecodeResult.Success(value)
}
