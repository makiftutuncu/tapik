@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.http

import dev.akif.tapik.codec.Defaults
import dev.akif.tapik.codec.StringCodec
import dev.akif.tapik.codec.StringCodecs
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URI
import java.util.UUID
import dev.akif.tapik.http.StringCodecs as HttpStringCodecs

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
        const val ACCEPT = "Accept"

        /** Canonical name for the `Content-Type` header. */
        const val CONTENT_TYPE = "Content-Type"

        /** Canonical name for the `Location` header. */
        const val LOCATION = "Location"

        /** Predefined header definition for `Accept`. */
        val Accept: Header<MediaType> = HeaderInput(ACCEPT, HttpStringCodecs.mediaType(ACCEPT))

        /** Predefined header definition for `Content-Type`. */
        val ContentType: Header<MediaType> = HeaderInput(CONTENT_TYPE, HttpStringCodecs.mediaType(CONTENT_TYPE))

        /** Predefined header definition for `Location`. */
        val Location: Header<URI> = HeaderInput(LOCATION, HttpStringCodecs.uri(LOCATION))
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
