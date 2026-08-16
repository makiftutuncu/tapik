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

/** A segment in a [Uri] path. */
sealed interface PathSegment {
    /**
     * An already-encoded literal path [value].
     *
     * @throws IllegalArgumentException if [value] is empty or contains `/`.
     */
    @JvmInline
    value class Literal(
        val value: String
    ) : PathSegment {
        init {
            require(value.isNotEmpty()) { "A literal path segment must not be empty" }
            require('/' !in value) { "A literal path segment must not contain '/': '$value'" }
        }
    }
}

/**
 * A required, typed URI path variable.
 *
 * @param Value decoded Kotlin value type.
 * @property name URI-template variable name.
 * @property format string format used on the wire.
 * @throws IllegalArgumentException if [name] is not a valid URI-template variable name.
 */
data class PathVariable<Value : Any>(
    val name: String,
    val format: StringFormat<Value>
) : PathSegment {
    init {
        require(name.isNotBlank()) { "Path variable name must not be blank" }
        require(name.none(Char::isWhitespace) && name.none { it in INVALID_PATH_VARIABLE_NAME_CHARACTERS }) {
            "Path variable name contains an invalid character: '$name'"
        }
    }

    /** Built-in path-variable factories backed by Tapik's default formats. */
    companion object :
        NamedDefaults<
            PathVariable<Boolean>,
            PathVariable<Byte>,
            PathVariable<Short>,
            PathVariable<Int>,
            PathVariable<Long>,
            PathVariable<Float>,
            PathVariable<Double>,
            PathVariable<BigInteger>,
            PathVariable<BigDecimal>,
            PathVariable<String>,
            PathVariable<UUID>,
            PathVariable<LocalDate>,
            PathVariable<LocalTime>,
            PathVariable<LocalDateTime>,
            PathVariable<OffsetTime>,
            PathVariable<OffsetDateTime>,
            PathVariable<Instant>,
            PathVariable<Duration>,
            PathVariable<Period>
        > {
        /** Builds a required path variable named [name] using [format]. */
        operator fun <Value : Any> invoke(name: String, format: StringFormat<Value>): PathVariable<Value> =
            PathVariable(name, format)

        override fun boolean(name: String): PathVariable<Boolean> = invoke(name, format.boolean)
        override fun byte(name: String): PathVariable<Byte> = invoke(name, format.byte)
        override fun short(name: String): PathVariable<Short> = invoke(name, format.short)
        override fun int(name: String): PathVariable<Int> = invoke(name, format.int)
        override fun long(name: String): PathVariable<Long> = invoke(name, format.long)
        override fun float(name: String): PathVariable<Float> = invoke(name, format.float)
        override fun double(name: String): PathVariable<Double> = invoke(name, format.double)
        override fun bigInteger(name: String): PathVariable<BigInteger> = invoke(name, format.bigInteger)
        override fun bigDecimal(name: String): PathVariable<BigDecimal> = invoke(name, format.bigDecimal)
        override fun string(name: String): PathVariable<String> = invoke(name, format.string)
        override fun uuid(name: String): PathVariable<UUID> = invoke(name, format.uuid)
        override fun localDate(name: String): PathVariable<LocalDate> = invoke(name, format.localDate)
        override fun localTime(name: String): PathVariable<LocalTime> = invoke(name, format.localTime)
        override fun localDateTime(name: String): PathVariable<LocalDateTime> = invoke(name, format.localDateTime)
        override fun offsetTime(name: String): PathVariable<OffsetTime> = invoke(name, format.offsetTime)
        override fun offsetDateTime(name: String): PathVariable<OffsetDateTime> = invoke(name, format.offsetDateTime)
        override fun instant(name: String): PathVariable<Instant> = invoke(name, format.instant)
        override fun duration(name: String): PathVariable<Duration> = invoke(name, format.duration)
        override fun period(name: String): PathVariable<Period> = invoke(name, format.period)
    }
}

private const val INVALID_PATH_VARIABLE_NAME_CHARACTERS: String = "/{}?#"

/** Shortcut to the built-in factories on [PathVariable.Companion]. */
val path: PathVariable.Companion
    get() = PathVariable.Companion

/** An ordered, heterogeneous tuple of path variables. */
typealias Paths = Tuple<PathVariable<*>>

/** A path-variable tuple with no values. */
typealias Paths0 = Tuple0

/** A path-variable tuple with one value. */
typealias Paths1<Value1> = Tuple1<PathVariable<*>, PathVariable<Value1>>

/** A path-variable tuple with two values. */
typealias Paths2<Value1, Value2> = Tuple2<PathVariable<*>, PathVariable<Value1>, PathVariable<Value2>>

/** A path-variable tuple with three values. */
typealias Paths3<Value1, Value2, Value3> =
    Tuple3<PathVariable<*>, PathVariable<Value1>, PathVariable<Value2>, PathVariable<Value3>>

/** A path-variable tuple with four values. */
typealias Paths4<Value1, Value2, Value3, Value4> =
    Tuple4<PathVariable<*>, PathVariable<Value1>, PathVariable<Value2>, PathVariable<Value3>, PathVariable<Value4>>

/** A path-variable tuple with five values. */
typealias Paths5<Value1, Value2, Value3, Value4, Value5> =
    Tuple5<
        PathVariable<*>,
        PathVariable<Value1>,
        PathVariable<Value2>,
        PathVariable<Value3>,
        PathVariable<Value4>,
        PathVariable<Value5>
    >

/** A path-variable tuple with six values. */
typealias Paths6<Value1, Value2, Value3, Value4, Value5, Value6> =
    Tuple6<
        PathVariable<*>,
        PathVariable<Value1>,
        PathVariable<Value2>,
        PathVariable<Value3>,
        PathVariable<Value4>,
        PathVariable<Value5>,
        PathVariable<Value6>
    >

/** A path-variable tuple with seven values. */
typealias Paths7<Value1, Value2, Value3, Value4, Value5, Value6, Value7> =
    Tuple7<
        PathVariable<*>,
        PathVariable<Value1>,
        PathVariable<Value2>,
        PathVariable<Value3>,
        PathVariable<Value4>,
        PathVariable<Value5>,
        PathVariable<Value6>,
        PathVariable<Value7>
    >

/** A path-variable tuple with eight values. */
typealias Paths8<Value1, Value2, Value3, Value4, Value5, Value6, Value7, Value8> =
    Tuple8<
        PathVariable<*>,
        PathVariable<Value1>,
        PathVariable<Value2>,
        PathVariable<Value3>,
        PathVariable<Value4>,
        PathVariable<Value5>,
        PathVariable<Value6>,
        PathVariable<Value7>,
        PathVariable<Value8>
    >

/** Appends the non-empty segments in an already-encoded [fragment]. */
operator fun <P : Paths> Uri<P, Queries0>.div(fragment: String): Uri<P, Queries0> {
    val normalizedFragment = fragment.trim('/')

    require(normalizedFragment.isNotEmpty()) { "URI path fragment must contain at least one segment" }

    val newSegments = normalizedFragment.split('/')
    require(newSegments.none(String::isEmpty)) {
        "URI path fragment must not contain empty segments: '$fragment'"
    }

    return Uri(
        segments = segments + newSegments.map(PathSegment::Literal),
        paths = paths,
        queries = queries
    )
}

private fun <P : Paths> Uri<*, Queries0>.append(
    variable: PathVariable<*>,
    newPaths: P
): Uri<P, Queries0> {
    require(paths.values.none { it.name == variable.name }) {
        "Path variable '${variable.name}' is already defined"
    }

    return Uri(
        segments = segments + variable,
        paths = newPaths,
        queries = queries
    )
}

/** Appends the first path [variable]. */
@JvmName("uriWithNoPathsDivPathVariable")
operator fun <Value : Any> Uri<Paths0, Queries0>.div(
    variable: PathVariable<Value>
): Uri<Paths1<Value>, Queries0> = append(variable, Paths1(variable))

/** Appends a second path [variable]. */
@JvmName("uriWithOnePathDivPathVariable")
operator fun <Value1 : Any, Value : Any> Uri<Paths1<Value1>, Queries0>.div(
    variable: PathVariable<Value>
): Uri<Paths2<Value1, Value>, Queries0> = append(variable, Paths2(paths._1, variable))

/** Appends a third path [variable]. */
@JvmName("uriWithTwoPathsDivPathVariable")
operator fun <Value1 : Any, Value2 : Any, Value : Any>
    Uri<Paths2<Value1, Value2>, Queries0>.div(
        variable: PathVariable<Value>
    ): Uri<Paths3<Value1, Value2, Value>, Queries0> =
        append(variable, Paths3(paths._1, paths._2, variable))

/** Appends a fourth path [variable]. */
@JvmName("uriWithThreePathsDivPathVariable")
operator fun <Value1 : Any, Value2 : Any, Value3 : Any, Value : Any>
    Uri<Paths3<Value1, Value2, Value3>, Queries0>.div(
        variable: PathVariable<Value>
    ): Uri<Paths4<Value1, Value2, Value3, Value>, Queries0> =
        append(variable, Paths4(paths._1, paths._2, paths._3, variable))

/** Appends a fifth path [variable]. */
@JvmName("uriWithFourPathsDivPathVariable")
operator fun <Value1 : Any, Value2 : Any, Value3 : Any, Value4 : Any, Value : Any>
    Uri<Paths4<Value1, Value2, Value3, Value4>, Queries0>.div(
        variable: PathVariable<Value>
    ): Uri<Paths5<Value1, Value2, Value3, Value4, Value>, Queries0> =
        append(variable, Paths5(paths._1, paths._2, paths._3, paths._4, variable))

/** Appends a sixth path [variable]. */
@JvmName("uriWithFivePathsDivPathVariable")
operator fun <Value1 : Any, Value2 : Any, Value3 : Any, Value4 : Any, Value5 : Any, Value : Any>
    Uri<Paths5<Value1, Value2, Value3, Value4, Value5>, Queries0>.div(
        variable: PathVariable<Value>
    ): Uri<Paths6<Value1, Value2, Value3, Value4, Value5, Value>, Queries0> =
        append(variable, Paths6(paths._1, paths._2, paths._3, paths._4, paths._5, variable))

/** Appends a seventh path [variable]. */
@JvmName("uriWithSixPathsDivPathVariable")
operator fun <Value1 : Any, Value2 : Any, Value3 : Any, Value4 : Any, Value5 : Any, Value6 : Any, Value : Any>
    Uri<Paths6<Value1, Value2, Value3, Value4, Value5, Value6>, Queries0>.div(
        variable: PathVariable<Value>
    ): Uri<Paths7<Value1, Value2, Value3, Value4, Value5, Value6, Value>, Queries0> =
        append(variable, Paths7(paths._1, paths._2, paths._3, paths._4, paths._5, paths._6, variable))

/** Appends an eighth path [variable]. */
@JvmName("uriWithSevenPathsDivPathVariable")
operator fun <
    Value1 : Any,
    Value2 : Any,
    Value3 : Any,
    Value4 : Any,
    Value5 : Any,
    Value6 : Any,
    Value7 : Any,
    Value : Any
> Uri<Paths7<Value1, Value2, Value3, Value4, Value5, Value6, Value7>, Queries0>.div(
    variable: PathVariable<Value>
): Uri<Paths8<Value1, Value2, Value3, Value4, Value5, Value6, Value7, Value>, Queries0> =
    append(variable, Paths8(paths._1, paths._2, paths._3, paths._4, paths._5, paths._6, paths._7, variable))
