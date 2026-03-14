@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

import dev.akif.tapik.codec.Defaults
import dev.akif.tapik.codec.StringCodec
import dev.akif.tapik.codec.StringCodecs
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URI
import java.util.UUID

/** Describes an HTTP header definition used by Tapik endpoints. */
sealed interface Header<H : Any> {
    /** Canonical HTTP header name. */
    val name: String

    /** Codec responsible for encoding and decoding header values. */
    val codec: StringCodec<H>

    /**
     * Indicates whether the header is required on the wire.
     *
     * `true` when the header must be supplied, `false` when Tapik will emit stored values instead.
     */
    val required: Boolean

    /** Factory helpers and predefined header constants. */
    companion object : Defaults<Header<Unit>, Header<Boolean>, Header<Byte>, Header<Short>, Header<Int>, Header<Long>, Header<Float>, Header<Double>, Header<BigInteger>, Header<BigDecimal>, Header<String>, Header<UUID>> {
        private fun mediaTypeHeader(name: String): Header<MediaType> =
            HeaderInput(name, HttpStringCodecs.mediaType(name))

        private fun uriHeader(name: String): Header<URI> = HeaderInput(name, HttpStringCodecs.uri(name))

        /**
         * Creates a required [Header] definition backed by the unit codec.
         *
         * @param name canonical header name.
         * @return header expecting a unit payload.
         */
        override fun unit(name: String): Header<Unit> = HeaderInput(name, StringCodecs.unit(name))

        /**
         * Creates a required [Header] definition backed by the boolean codec.
         *
         * @param name canonical header name.
         * @return header expecting a boolean payload.
         */
        override fun boolean(name: String): Header<Boolean> = HeaderInput(name, StringCodecs.boolean(name))

        /**
         * Creates a required [Header] definition backed by the byte codec.
         *
         * @param name canonical header name.
         * @return header expecting a byte payload.
         */
        override fun byte(name: String): Header<Byte> = HeaderInput(name, StringCodecs.byte(name))

        /**
         * Creates a required [Header] definition backed by the short codec.
         *
         * @param name canonical header name.
         * @return header expecting a short payload.
         */
        override fun short(name: String): Header<Short> = HeaderInput(name, StringCodecs.short(name))

        /**
         * Creates a required [Header] definition backed by the int codec.
         *
         * @param name canonical header name.
         * @return header expecting an integer payload.
         */
        override fun int(name: String): Header<Int> = HeaderInput(name, StringCodecs.int(name))

        /**
         * Creates a required [Header] definition backed by the long codec.
         *
         * @param name canonical header name.
         * @return header expecting a long payload.
         */
        override fun long(name: String): Header<Long> = HeaderInput(name, StringCodecs.long(name))

        /**
         * Creates a required [Header] definition backed by the float codec.
         *
         * @param name canonical header name.
         * @return header expecting a float payload.
         */
        override fun float(name: String): Header<Float> = HeaderInput(name, StringCodecs.float(name))

        /**
         * Creates a required [Header] definition backed by the double codec.
         *
         * @param name canonical header name.
         * @return header expecting a double payload.
         */
        override fun double(name: String): Header<Double> = HeaderInput(name, StringCodecs.double(name))

        /**
         * Creates a required [Header] definition backed by the big integer codec.
         *
         * @param name canonical header name.
         * @return header expecting a [BigInteger] payload.
         */
        override fun bigInteger(name: String): Header<BigInteger> = HeaderInput(name, StringCodecs.bigInteger(name))

        /**
         * Creates a required [Header] definition backed by the big decimal codec.
         *
         * @param name canonical header name.
         * @return header expecting a [BigDecimal] payload.
         */
        override fun bigDecimal(name: String): Header<BigDecimal> = HeaderInput(name, StringCodecs.bigDecimal(name))

        /**
         * Creates a required [Header] definition backed by the string codec.
         *
         * @param name canonical header name.
         * @return header expecting a [String] payload.
         */
        override fun string(name: String): Header<String> = HeaderInput(name, StringCodecs.string(name))

        /**
         * Creates a required [Header] definition backed by the UUID codec.
         *
         * @param name canonical header name.
         * @return header expecting a [UUID] payload.
         */
        override fun uuid(name: String): Header<UUID> = HeaderInput(name, StringCodecs.uuid(name))

        /** Canonical name for the `Accept` header. */
        const val ACCEPT_VALUE = "Accept"

        /** Canonical name for the `Accept-Charset` header. */
        const val ACCEPT_CHARSET_VALUE = "Accept-Charset"

        /** Canonical name for the `Accept-Encoding` header. */
        const val ACCEPT_ENCODING_VALUE = "Accept-Encoding"

        /** Canonical name for the `Accept-Language` header. */
        const val ACCEPT_LANGUAGE_VALUE = "Accept-Language"

        /** Canonical name for the `Accept-Ranges` header. */
        const val ACCEPT_RANGES_VALUE = "Accept-Ranges"

        /** Canonical name for the `Allow` header. */
        const val ALLOW_VALUE = "Allow"

        /** Canonical name for the `Authorization` header. */
        const val AUTHORIZATION_VALUE = "Authorization"

        /** Canonical name for the `Cache-Control` header. */
        const val CACHE_CONTROL_VALUE = "Cache-Control"

        /** Canonical name for the `Connection` header. */
        const val CONNECTION_VALUE = "Connection"

        /** Canonical name for the `Content-Disposition` header. */
        const val CONTENT_DISPOSITION_VALUE = "Content-Disposition"

        /** Canonical name for the `Content-Encoding` header. */
        const val CONTENT_ENCODING_VALUE = "Content-Encoding"

        /** Canonical name for the `Content-Language` header. */
        const val CONTENT_LANGUAGE_VALUE = "Content-Language"

        /** Canonical name for the `Content-Length` header. */
        const val CONTENT_LENGTH_VALUE = "Content-Length"

        /** Canonical name for the `Content-Location` header. */
        const val CONTENT_LOCATION_VALUE = "Content-Location"

        /** Canonical name for the `Content-Range` header. */
        const val CONTENT_RANGE_VALUE = "Content-Range"

        /** Canonical name for the `Content-Type` header. */
        const val CONTENT_TYPE_VALUE = "Content-Type"

        /** Canonical name for the `Cookie` header. */
        const val COOKIE_VALUE = "Cookie"

        /** Canonical name for the `ETag` header. */
        const val ETAG_VALUE = "ETag"

        /** Canonical name for the `Host` header. */
        const val HOST_VALUE = "Host"

        /** Canonical name for the `If-Match` header. */
        const val IF_MATCH_VALUE = "If-Match"

        /** Canonical name for the `If-Modified-Since` header. */
        const val IF_MODIFIED_SINCE_VALUE = "If-Modified-Since"

        /** Canonical name for the `If-None-Match` header. */
        const val IF_NONE_MATCH_VALUE = "If-None-Match"

        /** Canonical name for the `If-Unmodified-Since` header. */
        const val IF_UNMODIFIED_SINCE_VALUE = "If-Unmodified-Since"

        /** Canonical name for the `Last-Modified` header. */
        const val LAST_MODIFIED_VALUE = "Last-Modified"

        /** Canonical name for the `Link` header. */
        const val LINK_VALUE = "Link"

        /** Canonical name for the `Location` header. */
        const val LOCATION_VALUE = "Location"

        /** Canonical name for the `Origin` header. */
        const val ORIGIN_VALUE = "Origin"

        /** Canonical name for the `Referer` header. */
        const val REFERER_VALUE = "Referer"

        /** Canonical name for the `Retry-After` header. */
        const val RETRY_AFTER_VALUE = "Retry-After"

        /** Canonical name for the `Server` header. */
        const val SERVER_VALUE = "Server"

        /** Canonical name for the `Set-Cookie` header. */
        const val SET_COOKIE_VALUE = "Set-Cookie"

        /** Canonical name for the `User-Agent` header. */
        const val USER_AGENT_VALUE = "User-Agent"

        /** Canonical name for the `Vary` header. */
        const val VARY_VALUE = "Vary"

        /** Canonical name for the `WWW-Authenticate` header. */
        const val WWW_AUTHENTICATE_VALUE = "WWW-Authenticate"

        /** Predefined header definition for `Accept`. */
        val Accept: Header<MediaType> = mediaTypeHeader(ACCEPT_VALUE)

        /** Predefined header definition for `Accept-Charset`. */
        val AcceptCharset: Header<String> = string(ACCEPT_CHARSET_VALUE)

        /** Predefined header definition for `Accept-Encoding`. */
        val AcceptEncoding: Header<String> = string(ACCEPT_ENCODING_VALUE)

        /** Predefined header definition for `Accept-Language`. */
        val AcceptLanguage: Header<String> = string(ACCEPT_LANGUAGE_VALUE)

        /** Predefined header definition for `Accept-Ranges`. */
        val AcceptRanges: Header<String> = string(ACCEPT_RANGES_VALUE)

        /** Predefined header definition for `Allow`. */
        val Allow: Header<String> = string(ALLOW_VALUE)

        /** Predefined header definition for `Authorization`. */
        val Authorization: Header<String> = string(AUTHORIZATION_VALUE)

        /** Predefined header definition for `Cache-Control`. */
        val CacheControl: Header<String> = string(CACHE_CONTROL_VALUE)

        /** Predefined header definition for `Connection`. */
        val Connection: Header<String> = string(CONNECTION_VALUE)

        /** Predefined header definition for `Content-Disposition`. */
        val ContentDisposition: Header<String> = string(CONTENT_DISPOSITION_VALUE)

        /** Predefined header definition for `Content-Encoding`. */
        val ContentEncoding: Header<String> = string(CONTENT_ENCODING_VALUE)

        /** Predefined header definition for `Content-Language`. */
        val ContentLanguage: Header<String> = string(CONTENT_LANGUAGE_VALUE)

        /** Predefined header definition for `Content-Length`. */
        val ContentLength: Header<Long> = long(CONTENT_LENGTH_VALUE)

        /** Predefined header definition for `Content-Location`. */
        val ContentLocation: Header<URI> = uriHeader(CONTENT_LOCATION_VALUE)

        /** Predefined header definition for `Content-Range`. */
        val ContentRange: Header<String> = string(CONTENT_RANGE_VALUE)

        /** Predefined header definition for `Content-Type`. */
        val ContentType: Header<MediaType> = mediaTypeHeader(CONTENT_TYPE_VALUE)

        /** Predefined header definition for `Cookie`. */
        val Cookie: Header<String> = string(COOKIE_VALUE)

        /** Predefined header definition for `ETag`. */
        val Etag: Header<String> = string(ETAG_VALUE)

        /** Predefined header definition for `Host`. */
        val Host: Header<String> = string(HOST_VALUE)

        /** Predefined header definition for `If-Match`. */
        val IfMatch: Header<String> = string(IF_MATCH_VALUE)

        /** Predefined header definition for `If-Modified-Since`. */
        val IfModifiedSince: Header<String> = string(IF_MODIFIED_SINCE_VALUE)

        /** Predefined header definition for `If-None-Match`. */
        val IfNoneMatch: Header<String> = string(IF_NONE_MATCH_VALUE)

        /** Predefined header definition for `If-Unmodified-Since`. */
        val IfUnmodifiedSince: Header<String> = string(IF_UNMODIFIED_SINCE_VALUE)

        /** Predefined header definition for `Last-Modified`. */
        val LastModified: Header<String> = string(LAST_MODIFIED_VALUE)

        /** Predefined header definition for `Link`. */
        val Link: Header<String> = string(LINK_VALUE)

        /** Predefined header definition for `Location`. */
        val Location: Header<URI> = uriHeader(LOCATION_VALUE)

        /** Predefined header definition for `Origin`. */
        val Origin: Header<String> = string(ORIGIN_VALUE)

        /** Predefined header definition for `Referer`. */
        val Referer: Header<URI> = uriHeader(REFERER_VALUE)

        /** Predefined header definition for `Retry-After`. */
        val RetryAfter: Header<String> = string(RETRY_AFTER_VALUE)

        /** Predefined header definition for `Server`. */
        val Server: Header<String> = string(SERVER_VALUE)

        /** Predefined header definition for `Set-Cookie`. */
        val SetCookie: Header<String> = string(SET_COOKIE_VALUE)

        /** Predefined header definition for `User-Agent`. */
        val UserAgent: Header<String> = string(USER_AGENT_VALUE)

        /** Predefined header definition for `Vary`. */
        val Vary: Header<String> = string(VARY_VALUE)

        /** Predefined header definition for `WWW-Authenticate`. */
        val WwwAuthenticate: Header<String> = string(WWW_AUTHENTICATE_VALUE)
    }

    /**
     * Creates a [HeaderValues] instance using the provided values, marking the header as optional.
     *
     * @param first mandatory first header value.
     * @param rest optional additional values that will be appended in the given order.
     * @return a [HeaderValues] instance capturing the supplied values.
     * @see HeaderValues
     */
    operator fun invoke(
        first: H,
        vararg rest: H
    ): Header<H> = HeaderValues(name, codec, listOf(first, *rest))
}

/** Required header definition backed by a [StringCodec]. */
data class HeaderInput<H : Any>(
    /** Canonical header name. */
    override val name: String,
    /** Codec used to encode and decode header values. */
    override val codec: StringCodec<H>
) : Header<H> {
    /** Flag signalling that the header must be present. */
    override val required: Boolean = true
}

/** Optional header definition populated with explicit [values]. */
data class HeaderValues<H : Any>(
    /** Canonical header name. */
    override val name: String,
    /** Codec used to encode and decode header values. */
    override val codec: StringCodec<H>,
    /** Concrete header values that will be sent on the wire. */
    val values: List<H>
) : Header<H> {
    /** Flag signalling the header is optional because values are pre-supplied. */
    override val required: Boolean = false
}
