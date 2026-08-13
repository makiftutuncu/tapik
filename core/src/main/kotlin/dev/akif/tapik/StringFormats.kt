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

/** Default formats whose serialized representation is [String]. */
object StringFormats : FormatDefaults<String> {
    override val boolean: StringFormat<Boolean> =
        parsingFormat("Boolean", ScalarSchema(SchemaType.BOOLEAN), String::toBooleanStrict, Boolean::toString)

    override val byte: StringFormat<Byte> =
        parsingFormat("Byte", ScalarSchema(SchemaType.INTEGER, "int8"), String::toByte, Byte::toString)

    override val short: StringFormat<Short> =
        parsingFormat("Short", ScalarSchema(SchemaType.INTEGER, "int16"), String::toShort, Short::toString)

    override val int: StringFormat<Int> =
        parsingFormat("Int", ScalarSchema(SchemaType.INTEGER, "int32"), String::toInt, Int::toString)

    override val long: StringFormat<Long> =
        parsingFormat("Long", ScalarSchema(SchemaType.INTEGER, "int64"), String::toLong, Long::toString)

    override val float: StringFormat<Float> =
        parsingFormat("Float", ScalarSchema(SchemaType.NUMBER, "float"), String::toFloat, Float::toString)

    override val double: StringFormat<Double> =
        parsingFormat("Double", ScalarSchema(SchemaType.NUMBER, "double"), String::toDouble, Double::toString)

    override val bigInteger: StringFormat<BigInteger> =
        parsingFormat("BigInteger", ScalarSchema(SchemaType.INTEGER), ::BigInteger, BigInteger::toString)

    override val bigDecimal: StringFormat<BigDecimal> =
        parsingFormat("BigDecimal", ScalarSchema(SchemaType.NUMBER, "decimal"), ::BigDecimal, BigDecimal::toString)

    override val string: StringFormat<String> =
        Format(
            codec = Codec(decoder = Decoder { DecodeResult.Success(it) }, encoder = Encoder { it }),
            schema = ScalarSchema(SchemaType.STRING)
        )

    override val uuid: StringFormat<UUID> =
        parsingFormat("UUID", ScalarSchema(SchemaType.STRING, "uuid"), UUID::fromString, UUID::toString)

    override val localDate: StringFormat<LocalDate> =
        parsingFormat("LocalDate", ScalarSchema(SchemaType.STRING, "date"), LocalDate::parse, LocalDate::toString)

    override val localTime: StringFormat<LocalTime> =
        parsingFormat("LocalTime", ScalarSchema(SchemaType.STRING, "time-local"), LocalTime::parse, LocalTime::toString)

    override val localDateTime: StringFormat<LocalDateTime> =
        parsingFormat(
            "LocalDateTime",
            ScalarSchema(SchemaType.STRING, "date-time-local"),
            LocalDateTime::parse,
            LocalDateTime::toString
        )

    override val offsetTime: StringFormat<OffsetTime> =
        parsingFormat("OffsetTime", ScalarSchema(SchemaType.STRING, "time"), OffsetTime::parse, OffsetTime::toString)

    override val offsetDateTime: StringFormat<OffsetDateTime> =
        parsingFormat(
            "OffsetDateTime",
            ScalarSchema(SchemaType.STRING, "date-time"),
            OffsetDateTime::parse,
            OffsetDateTime::toString
        )

    override val instant: StringFormat<Instant> =
        parsingFormat("Instant", ScalarSchema(SchemaType.STRING, "date-time"), Instant::parse, Instant::toString)

    override val duration: StringFormat<Duration> =
        parsingFormat("Duration", ScalarSchema(SchemaType.STRING, "duration"), Duration::parse, Duration::toString)

    override val period: StringFormat<Period> =
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
