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

/**
 * Scalar formats sharing a serialized [Representation].
 *
 * @param Representation serialized representation type.
 */
interface FormatDefaults<Representation : Any> {
    /** The standard [Boolean] format. */
    val boolean: Format<Boolean, Representation>

    /** The standard [Byte] format. */
    val byte: Format<Byte, Representation>

    /** The standard [Short] format. */
    val short: Format<Short, Representation>

    /** The standard [Int] format. */
    val int: Format<Int, Representation>

    /** The standard [Long] format. */
    val long: Format<Long, Representation>

    /** The standard [Float] format. */
    val float: Format<Float, Representation>

    /** The standard [Double] format. */
    val double: Format<Double, Representation>

    /** The standard [BigInteger] format. */
    val bigInteger: Format<BigInteger, Representation>

    /** The standard [BigDecimal] format. */
    val bigDecimal: Format<BigDecimal, Representation>

    /** The identity [String] format. */
    val string: Format<String, Representation>

    /** The standard [UUID] format. */
    val uuid: Format<UUID, Representation>

    /** The standard [LocalDate] format. */
    val localDate: Format<LocalDate, Representation>

    /** The standard [LocalTime] format. */
    val localTime: Format<LocalTime, Representation>

    /** The standard [LocalDateTime] format. */
    val localDateTime: Format<LocalDateTime, Representation>

    /** The standard [OffsetTime] format. */
    val offsetTime: Format<OffsetTime, Representation>

    /** The standard [OffsetDateTime] format. */
    val offsetDateTime: Format<OffsetDateTime, Representation>

    /** The standard [Instant] format. */
    val instant: Format<Instant, Representation>

    /** The standard [Duration] format. */
    val duration: Format<Duration, Representation>

    /** The standard [Period] format. */
    val period: Format<Period, Representation>
}
