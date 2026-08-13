package dev.akif.tapik

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

/** Cached string formats for Kotlin and Java standard scalar types. */
object string {
    /** The standard [Boolean] string format. */
    val boolean: StringFormat<Boolean> =
        parsingFormat("Boolean", ScalarSchema(SchemaType.BOOLEAN), String::toBooleanStrict, Boolean::toString)

    /** The standard [Byte] string format. */
    val byte: StringFormat<Byte> =
        parsingFormat("Byte", ScalarSchema(SchemaType.INTEGER, "int8"), String::toByte, Byte::toString)

    /** The standard [Short] string format. */
    val short: StringFormat<Short> =
        parsingFormat("Short", ScalarSchema(SchemaType.INTEGER, "int16"), String::toShort, Short::toString)

    /** The standard [Int] string format. */
    val int: StringFormat<Int> =
        parsingFormat("Int", ScalarSchema(SchemaType.INTEGER, "int32"), String::toInt, Int::toString)

    /** The standard [Long] string format. */
    val long: StringFormat<Long> =
        parsingFormat("Long", ScalarSchema(SchemaType.INTEGER, "int64"), String::toLong, Long::toString)

    /** The standard [Float] string format. */
    val float: StringFormat<Float> =
        parsingFormat("Float", ScalarSchema(SchemaType.NUMBER, "float"), String::toFloat, Float::toString)

    /** The standard [Double] string format. */
    val double: StringFormat<Double> =
        parsingFormat("Double", ScalarSchema(SchemaType.NUMBER, "double"), String::toDouble, Double::toString)

    /** The standard [BigInteger] string format. */
    val bigInteger: StringFormat<BigInteger> =
        parsingFormat("BigInteger", ScalarSchema(SchemaType.INTEGER), ::BigInteger, BigInteger::toString)

    /** The standard [BigDecimal] string format. */
    val bigDecimal: StringFormat<BigDecimal> =
        parsingFormat("BigDecimal", ScalarSchema(SchemaType.NUMBER, "decimal"), ::BigDecimal, BigDecimal::toString)

    /** The identity [String] format. */
    val string: StringFormat<String> =
        Format(
            codec = Codec(decoder = Decoder { DecodeResult.Success(it) }, encoder = Encoder { it }),
            schema = ScalarSchema(SchemaType.STRING)
        )

    /** The standard [UUID] string format. */
    val uuid: StringFormat<UUID> =
        parsingFormat("UUID", ScalarSchema(SchemaType.STRING, "uuid"), UUID::fromString, UUID::toString)

    /** The ISO-8601 [LocalDate] string format. */
    val localDate: StringFormat<LocalDate> =
        parsingFormat("LocalDate", ScalarSchema(SchemaType.STRING, "date"), LocalDate::parse, LocalDate::toString)

    /** The ISO-8601 [LocalTime] string format. */
    val localTime: StringFormat<LocalTime> =
        parsingFormat(
            "LocalTime",
            ScalarSchema(SchemaType.STRING, "time-local"),
            LocalTime::parse,
            LocalTime::toString
        )

    /** The ISO-8601 [LocalDateTime] string format. */
    val localDateTime: StringFormat<LocalDateTime> =
        parsingFormat(
            "LocalDateTime",
            ScalarSchema(SchemaType.STRING, "date-time-local"),
            LocalDateTime::parse,
            LocalDateTime::toString
        )

    /** The ISO-8601 [OffsetTime] string format. */
    val offsetTime: StringFormat<OffsetTime> =
        parsingFormat("OffsetTime", ScalarSchema(SchemaType.STRING, "time"), OffsetTime::parse, OffsetTime::toString)

    /** The ISO-8601 [OffsetDateTime] string format. */
    val offsetDateTime: StringFormat<OffsetDateTime> =
        parsingFormat(
            "OffsetDateTime",
            ScalarSchema(SchemaType.STRING, "date-time"),
            OffsetDateTime::parse,
            OffsetDateTime::toString
        )

    /** The ISO-8601 [Instant] string format. */
    val instant: StringFormat<Instant> =
        parsingFormat("Instant", ScalarSchema(SchemaType.STRING, "date-time"), Instant::parse, Instant::toString)

    /** The ISO-8601 [Duration] string format. */
    val duration: StringFormat<Duration> =
        parsingFormat("Duration", ScalarSchema(SchemaType.STRING, "duration"), Duration::parse, Duration::toString)

    /** The ISO-8601 [Period] string format. */
    val period: StringFormat<Period> =
        parsingFormat("Period", ScalarSchema(SchemaType.STRING, "duration"), Period::parse, Period::toString)
}

private fun <Value : Any> parsingFormat(
    typeName: String,
    schema: ScalarSchema,
    decode: (String) -> Value,
    encode: (Value) -> String
): StringFormat<Value> =
    Format(
        codec =
            Codec(
                decoder =
                    Decoder { representation ->
                        try {
                            DecodeResult.Success(decode(representation))
                        } catch (cause: Exception) {
                            DecodeResult.Failure(
                                DecodeError(
                                    message = "Cannot decode '$representation' as $typeName",
                                    cause = cause
                                )
                            )
                        }
                    },
                encoder = Encoder(encode)
            ),
        schema = schema
    )
