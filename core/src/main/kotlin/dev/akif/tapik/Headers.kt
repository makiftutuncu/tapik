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
 * A typed HTTP header definition.
 *
 * @param Value decoded endpoint value type.
 * @param P presence type.
 * @property name HTTP field name.
 * @property format string format used on the wire.
 * @property presence required, optional, defaulted, or fixed presence information.
 * @throws IllegalArgumentException if [name] is not a valid HTTP field name.
 */
data class Header<Value : Any, out P : Presence<Value>>(
    val name: String,
    val format: StringFormat<Value>,
    val presence: P
) {
    init {
        require(name.isNotEmpty() && name.all(::isHttpTokenCharacter)) {
            "Header name must be a valid HTTP field name: '$name'"
        }
    }

    /** Returns this header as optional without a default value. */
    fun optional(): Header<Value, Optional> = Header(name, format, Optional)

    /** Returns this header as optional with [default] used when it is absent. */
    fun optional(default: Value): Header<Value, Default<Value>> = Header(name, format, Default(default))

    /** Returns this header fixed to [value]. */
    fun fixed(value: Value): Header<Value, Fixed<Value>> = Header(name, format, Fixed(value))

    /** Built-in header factories backed by Tapik's default formats. */
    companion object :
        NamedDefaults<
            Header<Boolean, Required>,
            Header<Byte, Required>,
            Header<Short, Required>,
            Header<Int, Required>,
            Header<Long, Required>,
            Header<Float, Required>,
            Header<Double, Required>,
            Header<BigInteger, Required>,
            Header<BigDecimal, Required>,
            Header<String, Required>,
            Header<UUID, Required>,
            Header<LocalDate, Required>,
            Header<LocalTime, Required>,
            Header<LocalDateTime, Required>,
            Header<OffsetTime, Required>,
            Header<OffsetDateTime, Required>,
            Header<Instant, Required>,
            Header<Duration, Required>,
            Header<Period, Required>
        > {
        /** Builds a required header named [name] using [format]. */
        operator fun <Value : Any> invoke(
            name: String,
            format: StringFormat<Value>
        ): Header<Value, Required> = Header(name, format, Required)

        override fun boolean(name: String): Header<Boolean, Required> = invoke(name, format.boolean)
        override fun byte(name: String): Header<Byte, Required> = invoke(name, format.byte)
        override fun short(name: String): Header<Short, Required> = invoke(name, format.short)
        override fun int(name: String): Header<Int, Required> = invoke(name, format.int)
        override fun long(name: String): Header<Long, Required> = invoke(name, format.long)
        override fun float(name: String): Header<Float, Required> = invoke(name, format.float)
        override fun double(name: String): Header<Double, Required> = invoke(name, format.double)
        override fun bigInteger(name: String): Header<BigInteger, Required> = invoke(name, format.bigInteger)
        override fun bigDecimal(name: String): Header<BigDecimal, Required> = invoke(name, format.bigDecimal)
        override fun string(name: String): Header<String, Required> = invoke(name, format.string)
        override fun uuid(name: String): Header<UUID, Required> = invoke(name, format.uuid)
        override fun localDate(name: String): Header<LocalDate, Required> = invoke(name, format.localDate)
        override fun localTime(name: String): Header<LocalTime, Required> = invoke(name, format.localTime)
        override fun localDateTime(name: String): Header<LocalDateTime, Required> =
            invoke(name, format.localDateTime)
        override fun offsetTime(name: String): Header<OffsetTime, Required> = invoke(name, format.offsetTime)
        override fun offsetDateTime(name: String): Header<OffsetDateTime, Required> =
            invoke(name, format.offsetDateTime)
        override fun instant(name: String): Header<Instant, Required> = invoke(name, format.instant)
        override fun duration(name: String): Header<Duration, Required> = invoke(name, format.duration)
        override fun period(name: String): Header<Period, Required> = invoke(name, format.period)
    }
}

private fun isHttpTokenCharacter(character: Char): Boolean =
    character in 'a'..'z' ||
        character in 'A'..'Z' ||
        character in '0'..'9' ||
        character in "!#$%&'*+-.^_`|~"

/** Shortcut to the built-in factories on [Header.Companion]. */
val header: Header.Companion
    get() = Header.Companion

/** An ordered, heterogeneous tuple of headers. */
typealias Headers = Tuple<Header<*, *>>

/** A header tuple with no values. */
typealias Headers0 = Tuple0

/** The named empty header tuple. */
val noHeaders: Headers0 = Headers0

/** A header tuple with one value. */
typealias Headers1<Header1> = Tuple1<Header<*, *>, Header1>

/** A header tuple with two values. */
typealias Headers2<Header1, Header2> = Tuple2<Header<*, *>, Header1, Header2>

/** A header tuple with three values. */
typealias Headers3<Header1, Header2, Header3> = Tuple3<Header<*, *>, Header1, Header2, Header3>

/** A header tuple with four values. */
typealias Headers4<Header1, Header2, Header3, Header4> = Tuple4<Header<*, *>, Header1, Header2, Header3, Header4>

/** A header tuple with five values. */
typealias Headers5<Header1, Header2, Header3, Header4, Header5> =
    Tuple5<Header<*, *>, Header1, Header2, Header3, Header4, Header5>

/** A header tuple with six values. */
typealias Headers6<Header1, Header2, Header3, Header4, Header5, Header6> =
    Tuple6<Header<*, *>, Header1, Header2, Header3, Header4, Header5, Header6>

/** A header tuple with seven values. */
typealias Headers7<Header1, Header2, Header3, Header4, Header5, Header6, Header7> =
    Tuple7<Header<*, *>, Header1, Header2, Header3, Header4, Header5, Header6, Header7>

/** A header tuple with eight values. */
typealias Headers8<Header1, Header2, Header3, Header4, Header5, Header6, Header7, Header8> =
    Tuple8<Header<*, *>, Header1, Header2, Header3, Header4, Header5, Header6, Header7, Header8>

private fun <H : Headers> validated(headers: H): H {
    val names = mutableSetOf<String>()
    require(headers.values.all { names.add(it.name.lowercase()) }) {
        "Header names must be unique ignoring case"
    }
    return headers
}

/** Groups one [header1]. */
fun <Header1 : Header<*, *>> headersOf(header1: Header1): Headers1<Header1> = validated(Headers1(header1))

/** Groups [header1] and [header2]. */
fun <Header1 : Header<*, *>, Header2 : Header<*, *>> headersOf(
    header1: Header1,
    header2: Header2
): Headers2<Header1, Header2> = validated(Headers2(header1, header2))

/** Groups three headers in declaration order. */
fun <Header1 : Header<*, *>, Header2 : Header<*, *>, Header3 : Header<*, *>> headersOf(
    header1: Header1,
    header2: Header2,
    header3: Header3
): Headers3<Header1, Header2, Header3> = validated(Headers3(header1, header2, header3))

/** Groups four headers in declaration order. */
fun <Header1 : Header<*, *>, Header2 : Header<*, *>, Header3 : Header<*, *>, Header4 : Header<*, *>> headersOf(
    header1: Header1,
    header2: Header2,
    header3: Header3,
    header4: Header4
): Headers4<Header1, Header2, Header3, Header4> = validated(Headers4(header1, header2, header3, header4))

/** Groups five headers in declaration order. */
fun <
    Header1 : Header<*, *>,
    Header2 : Header<*, *>,
    Header3 : Header<*, *>,
    Header4 : Header<*, *>,
    Header5 : Header<*, *>
> headersOf(
    header1: Header1,
    header2: Header2,
    header3: Header3,
    header4: Header4,
    header5: Header5
): Headers5<Header1, Header2, Header3, Header4, Header5> =
    validated(Headers5(header1, header2, header3, header4, header5))

/** Groups six headers in declaration order. */
fun <
    Header1 : Header<*, *>,
    Header2 : Header<*, *>,
    Header3 : Header<*, *>,
    Header4 : Header<*, *>,
    Header5 : Header<*, *>,
    Header6 : Header<*, *>
> headersOf(
    header1: Header1,
    header2: Header2,
    header3: Header3,
    header4: Header4,
    header5: Header5,
    header6: Header6
): Headers6<Header1, Header2, Header3, Header4, Header5, Header6> =
    validated(Headers6(header1, header2, header3, header4, header5, header6))

/** Groups seven headers in declaration order. */
fun <
    Header1 : Header<*, *>,
    Header2 : Header<*, *>,
    Header3 : Header<*, *>,
    Header4 : Header<*, *>,
    Header5 : Header<*, *>,
    Header6 : Header<*, *>,
    Header7 : Header<*, *>
> headersOf(
    header1: Header1,
    header2: Header2,
    header3: Header3,
    header4: Header4,
    header5: Header5,
    header6: Header6,
    header7: Header7
): Headers7<Header1, Header2, Header3, Header4, Header5, Header6, Header7> =
    validated(Headers7(header1, header2, header3, header4, header5, header6, header7))

/** Groups eight headers in declaration order. */
fun <
    Header1 : Header<*, *>,
    Header2 : Header<*, *>,
    Header3 : Header<*, *>,
    Header4 : Header<*, *>,
    Header5 : Header<*, *>,
    Header6 : Header<*, *>,
    Header7 : Header<*, *>,
    Header8 : Header<*, *>
> headersOf(
    header1: Header1,
    header2: Header2,
    header3: Header3,
    header4: Header4,
    header5: Header5,
    header6: Header6,
    header7: Header7,
    header8: Header8
): Headers8<Header1, Header2, Header3, Header4, Header5, Header6, Header7, Header8> =
    validated(Headers8(header1, header2, header3, header4, header5, header6, header7, header8))
