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
 * @property documentation human-readable parameter documentation.
 * @throws IllegalArgumentException if [name] is not a valid HTTP field name.
 */
data class Header<Value : Any, out P : Presence<Value>>(
    val name: String,
    val format: StringFormat<Value>,
    val presence: P,
    val documentation: ParameterDocumentation = ParameterDocumentation()
) {
    init {
        require(name.isNotEmpty() && name.all(::isHttpTokenCharacter)) {
            "Header name must be a valid HTTP field name: '$name'"
        }
    }

    /** Returns this header as optional without a default value. */
    fun optional(): Header<Value, Optional> = Header(name, format, Optional, documentation)

    /** Returns this header as optional with [default] used when it is absent. */
    fun optional(default: Value): Header<Value, Default<Value>> = Header(name, format, Default(default), documentation)

    /** Returns this header fixed to [value]. */
    fun fixed(value: Value): Header<Value, Fixed<Value>> = Header(name, format, Fixed(value), documentation)

    /** Replaces this header's description. */
    fun description(description: String): Header<Value, P> =
        copy(documentation = documentation.copy(description = description))

    /** Replaces this header's deprecation flag. */
    fun deprecated(deprecated: Boolean = true): Header<Value, P> =
        copy(documentation = documentation.copy(deprecated = deprecated))

    /** Built-in header factories backed by tapik's default formats. */
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
        /**
         * Builds a required header named [name] using [format].
         *
         * @param description optional human-readable description.
         * @param deprecated whether consumers should avoid this header.
         * @throws IllegalArgumentException when [description] is present but blank.
         */
        operator fun <Value : Any> invoke(
            name: String,
            format: StringFormat<Value>,
            description: String? = null,
            deprecated: Boolean = false
        ): Header<Value, Required> =
            Header(name, format, Required, ParameterDocumentation(description, deprecated))

        /** Builds a required header for enum [Value] using exact constant names. */
        inline fun <reified Value : Enum<Value>> enumValue(name: String): Header<Value, Required> =
            invoke(name, format.enumValue())

        /** The `Accept` request header. */
        val Accept: Header<String, Required> = string("Accept")

        /** The `Accept-Encoding` request header. */
        val AcceptEncoding: Header<String, Required> = string("Accept-Encoding")

        /** The `Accept-Language` request header. */
        val AcceptLanguage: Header<String, Required> = string("Accept-Language")

        /** The `Authorization` request header. */
        val Authorization: Header<String, Required> = string("Authorization")

        /** The `Cache-Control` request or response header. */
        val CacheControl: Header<String, Required> = string("Cache-Control")

        /** The `Connection` request or response header. */
        val Connection: Header<String, Required> = string("Connection")

        /** The `Content-Disposition` content header. */
        val ContentDisposition: Header<String, Required> = string("Content-Disposition")

        /** The `Content-Encoding` content header. */
        val ContentEncoding: Header<String, Required> = string("Content-Encoding")

        /** The `Content-Language` content header. */
        val ContentLanguage: Header<String, Required> = string("Content-Language")

        /** The `Content-Length` content header. */
        val ContentLength: Header<Long, Required> = long("Content-Length")

        /** The `Content-Location` content header. */
        val ContentLocation: Header<String, Required> = string("Content-Location")

        /** The `Content-Range` response header. */
        val ContentRange: Header<String, Required> = string("Content-Range")

        /** The `Content-Type` content header. */
        val ContentType: Header<String, Required> = string("Content-Type")

        /** The `Cookie` request header. */
        val Cookie: Header<String, Required> = string("Cookie")

        /** The `Date` request or response header. */
        val Date: Header<String, Required> = string("Date")

        /** The `ETag` response header. */
        val ETag: Header<String, Required> = string("ETag")

        /** The `Expires` response header. */
        val Expires: Header<String, Required> = string("Expires")

        /** The `Host` request header. */
        val Host: Header<String, Required> = string("Host")

        /** The `If-Match` conditional request header. */
        val IfMatch: Header<String, Required> = string("If-Match")

        /** The `If-Modified-Since` conditional request header. */
        val IfModifiedSince: Header<String, Required> = string("If-Modified-Since")

        /** The `If-None-Match` conditional request header. */
        val IfNoneMatch: Header<String, Required> = string("If-None-Match")

        /** The `If-Range` conditional request header. */
        val IfRange: Header<String, Required> = string("If-Range")

        /** The `If-Unmodified-Since` conditional request header. */
        val IfUnmodifiedSince: Header<String, Required> = string("If-Unmodified-Since")

        /** The `Last-Modified` response header. */
        val LastModified: Header<String, Required> = string("Last-Modified")

        /** The `Location` response header. */
        val Location: Header<String, Required> = string("Location")

        /** The `Origin` request header. */
        val Origin: Header<String, Required> = string("Origin")

        /** The `Pragma` request or response header. */
        val Pragma: Header<String, Required> = string("Pragma")

        /** The `Range` request header. */
        val Range: Header<String, Required> = string("Range")

        /** The `Referer` request header. */
        val Referer: Header<String, Required> = string("Referer")

        /** The `Retry-After` response header. */
        val RetryAfter: Header<String, Required> = string("Retry-After")

        /** The `Server` response header. */
        val Server: Header<String, Required> = string("Server")

        /** The `Set-Cookie` response header. */
        val SetCookie: Header<String, Required> = string("Set-Cookie")

        /** The `Transfer-Encoding` request or response header. */
        val TransferEncoding: Header<String, Required> = string("Transfer-Encoding")

        /** The `Upgrade` request or response header. */
        val Upgrade: Header<String, Required> = string("Upgrade")

        /** The `User-Agent` request header. */
        val UserAgent: Header<String, Required> = string("User-Agent")

        /** The `Vary` response header. */
        val Vary: Header<String, Required> = string("Vary")

        /** The `WWW-Authenticate` response header. */
        val WWWAuthenticate: Header<String, Required> = string("WWW-Authenticate")

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

internal fun <H : Headers> validatedHeaders(headers: H): H {
    val names = mutableSetOf<String>()
    require(headers.values.all { names.add(it.name.lowercase()) }) {
        "Header names must be unique ignoring case"
    }
    return headers
}

/** Groups one [header1]. */
fun <Header1 : Header<*, *>> headersOf(header1: Header1): Headers1<Header1> =
    validatedHeaders(Headers1(header1))

/** Groups [header1] and [header2]. */
fun <Header1 : Header<*, *>, Header2 : Header<*, *>> headersOf(
    header1: Header1,
    header2: Header2
): Headers2<Header1, Header2> = validatedHeaders(Headers2(header1, header2))

/** Groups three headers in declaration order. */
fun <Header1 : Header<*, *>, Header2 : Header<*, *>, Header3 : Header<*, *>> headersOf(
    header1: Header1,
    header2: Header2,
    header3: Header3
): Headers3<Header1, Header2, Header3> = validatedHeaders(Headers3(header1, header2, header3))

/** Groups four headers in declaration order. */
fun <Header1 : Header<*, *>, Header2 : Header<*, *>, Header3 : Header<*, *>, Header4 : Header<*, *>> headersOf(
    header1: Header1,
    header2: Header2,
    header3: Header3,
    header4: Header4
): Headers4<Header1, Header2, Header3, Header4> = validatedHeaders(Headers4(header1, header2, header3, header4))

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
    validatedHeaders(Headers5(header1, header2, header3, header4, header5))

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
    validatedHeaders(Headers6(header1, header2, header3, header4, header5, header6))

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
    validatedHeaders(Headers7(header1, header2, header3, header4, header5, header6, header7))

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
    validatedHeaders(Headers8(header1, header2, header3, header4, header5, header6, header7, header8))

/**
 * Initializes this headerless draft endpoint with [headers].
 *
 * This bulk modifier is unavailable after any header has been added.
 */
fun <P : Paths, Q : Queries, H : NonEmptyTuple<Header<*, *>>, I : Input, O : Outputs>
    Endpoint<P, Q, Headers0, I, O, Draft>.headers(
        headers: H
    ): Endpoint<P, Q, H, I, O, Draft> = withHeaders(validatedHeaders(headers))

private fun <P : Paths, Q : Queries, H : Headers, I : Input, O : Outputs>
    Endpoint<P, Q, *, I, O, Draft>.withHeaders(
        headers: H
    ): Endpoint<P, Q, H, I, O, Draft> =
        Endpoint(
            method = method,
            uri = uri,
            headers = headers,
            input = input,
            outputs = outputs,
            documentation = documentation,
            tags = tags,
            state = state
        )

private fun <P : Paths, Q : Queries, H : Headers, I : Input, O : Outputs>
    Endpoint<P, Q, *, I, O, Draft>.append(
        header: Header<*, *>,
        headers: H
    ): Endpoint<P, Q, H, I, O, Draft> {
        require(this.headers.values.none { it.name.equals(header.name, ignoreCase = true) }) {
            "Header '${header.name}' is already defined"
        }
        return withHeaders(headers)
    }

/** Appends the first request [header]. */
@JvmName("headerlessEndpointHeader")
fun <P : Paths, Q : Queries, I : Input, O : Outputs, HeaderType : Header<*, *>>
    Endpoint<P, Q, Headers0, I, O, Draft>.header(
        header: HeaderType
    ): Endpoint<P, Q, Headers1<HeaderType>, I, O, Draft> = append(header, Headers1(header))

/** Appends a second request [header]. */
@JvmName("endpointWithOneHeaderHeader")
fun <P : Paths, Q : Queries, I : Input, O : Outputs, Header1 : Header<*, *>, HeaderType : Header<*, *>>
    Endpoint<P, Q, Headers1<Header1>, I, O, Draft>.header(
        header: HeaderType
    ): Endpoint<P, Q, Headers2<Header1, HeaderType>, I, O, Draft> =
        append(header, Headers2(headers._1, header))

/** Appends a third request [header]. */
@JvmName("endpointWithTwoHeadersHeader")
fun <
    P : Paths,
    Q : Queries,
    I : Input,
    O : Outputs,
    Header1 : Header<*, *>,
    Header2 : Header<*, *>,
    HeaderType : Header<*, *>
> Endpoint<P, Q, Headers2<Header1, Header2>, I, O, Draft>.header(
    header: HeaderType
): Endpoint<P, Q, Headers3<Header1, Header2, HeaderType>, I, O, Draft> =
    append(header, Headers3(headers._1, headers._2, header))

/** Appends a fourth request [header]. */
@JvmName("endpointWithThreeHeadersHeader")
fun <
    P : Paths,
    Q : Queries,
    I : Input,
    O : Outputs,
    Header1 : Header<*, *>,
    Header2 : Header<*, *>,
    Header3 : Header<*, *>,
    HeaderType : Header<*, *>
> Endpoint<P, Q, Headers3<Header1, Header2, Header3>, I, O, Draft>.header(
    header: HeaderType
): Endpoint<P, Q, Headers4<Header1, Header2, Header3, HeaderType>, I, O, Draft> =
    append(header, Headers4(headers._1, headers._2, headers._3, header))

/** Appends a fifth request [header]. */
@JvmName("endpointWithFourHeadersHeader")
fun <
    P : Paths,
    Q : Queries,
    I : Input,
    O : Outputs,
    Header1 : Header<*, *>,
    Header2 : Header<*, *>,
    Header3 : Header<*, *>,
    Header4 : Header<*, *>,
    HeaderType : Header<*, *>
> Endpoint<P, Q, Headers4<Header1, Header2, Header3, Header4>, I, O, Draft>.header(
    header: HeaderType
): Endpoint<P, Q, Headers5<Header1, Header2, Header3, Header4, HeaderType>, I, O, Draft> =
    append(header, Headers5(headers._1, headers._2, headers._3, headers._4, header))

/** Appends a sixth request [header]. */
@JvmName("endpointWithFiveHeadersHeader")
fun <
    P : Paths,
    Q : Queries,
    I : Input,
    O : Outputs,
    Header1 : Header<*, *>,
    Header2 : Header<*, *>,
    Header3 : Header<*, *>,
    Header4 : Header<*, *>,
    Header5 : Header<*, *>,
    HeaderType : Header<*, *>
> Endpoint<P, Q, Headers5<Header1, Header2, Header3, Header4, Header5>, I, O, Draft>.header(
    header: HeaderType
): Endpoint<P, Q, Headers6<Header1, Header2, Header3, Header4, Header5, HeaderType>, I, O, Draft> =
    append(header, Headers6(headers._1, headers._2, headers._3, headers._4, headers._5, header))

/** Appends a seventh request [header]. */
@JvmName("endpointWithSixHeadersHeader")
fun <
    P : Paths,
    Q : Queries,
    I : Input,
    O : Outputs,
    Header1 : Header<*, *>,
    Header2 : Header<*, *>,
    Header3 : Header<*, *>,
    Header4 : Header<*, *>,
    Header5 : Header<*, *>,
    Header6 : Header<*, *>,
    HeaderType : Header<*, *>
> Endpoint<P, Q, Headers6<Header1, Header2, Header3, Header4, Header5, Header6>, I, O, Draft>.header(
    header: HeaderType
): Endpoint<P, Q, Headers7<Header1, Header2, Header3, Header4, Header5, Header6, HeaderType>, I, O, Draft> =
    append(
        header,
        Headers7(headers._1, headers._2, headers._3, headers._4, headers._5, headers._6, header)
    )

/** Appends an eighth request [header]. */
@JvmName("endpointWithSevenHeadersHeader")
fun <
    P : Paths,
    Q : Queries,
    I : Input,
    O : Outputs,
    Header1 : Header<*, *>,
    Header2 : Header<*, *>,
    Header3 : Header<*, *>,
    Header4 : Header<*, *>,
    Header5 : Header<*, *>,
    Header6 : Header<*, *>,
    Header7 : Header<*, *>,
    HeaderType : Header<*, *>
> Endpoint<P, Q, Headers7<Header1, Header2, Header3, Header4, Header5, Header6, Header7>, I, O, Draft>.header(
    header: HeaderType
): Endpoint<P, Q, Headers8<Header1, Header2, Header3, Header4, Header5, Header6, Header7, HeaderType>, I, O, Draft> =
    append(
        header,
        Headers8(
            headers._1,
            headers._2,
            headers._3,
            headers._4,
            headers._5,
            headers._6,
            headers._7,
            header
        )
    )
