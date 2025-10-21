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
    val name: String
    val codec: StringCodec<H>
    val required: Boolean

    companion object : Defaults<Header<Unit>, Header<Boolean>, Header<Byte>, Header<Short>, Header<Int>, Header<Long>, Header<Float>, Header<Double>, Header<BigInteger>, Header<BigDecimal>, Header<String>, Header<UUID>> {
        override fun unit(name: String): Header<Unit> = HeaderInput(name, StringCodecs.unit(name))

        override fun boolean(name: String): Header<Boolean> = HeaderInput(name, StringCodecs.boolean(name))

        override fun byte(name: String): Header<Byte> = HeaderInput(name, StringCodecs.byte(name))

        override fun short(name: String): Header<Short> = HeaderInput(name, StringCodecs.short(name))

        override fun int(name: String): Header<Int> = HeaderInput(name, StringCodecs.int(name))

        override fun long(name: String): Header<Long> = HeaderInput(name, StringCodecs.long(name))

        override fun float(name: String): Header<Float> = HeaderInput(name, StringCodecs.float(name))

        override fun double(name: String): Header<Double> = HeaderInput(name, StringCodecs.double(name))

        override fun bigInteger(name: String): Header<BigInteger> = HeaderInput(name, StringCodecs.bigInteger(name))

        override fun bigDecimal(name: String): Header<BigDecimal> = HeaderInput(name, StringCodecs.bigDecimal(name))

        override fun string(name: String): Header<String> = HeaderInput(name, StringCodecs.string(name))

        override fun uuid(name: String): Header<UUID> = HeaderInput(name, StringCodecs.uuid(name))

        const val ACCEPT = "Accept"
        const val CONTENT_TYPE = "Content-Type"
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

/** Required header definition backed by a [StringCodec].
 * @property codec codec used to encode and decode header values.
 */
data class HeaderInput<H : Any>(
    override val name: String,
    override val codec: StringCodec<H>
) : Header<H> {
    override val required: Boolean = true
}

/** Optional header definition populated with explicit [values].
 * @property values concrete header values that will be sent on the wire.
 */
data class HeaderValues<H : Any>(
    override val name: String,
    override val codec: StringCodec<H>,
    val values: List<H>
) : Header<H> {
    override val required: Boolean = false
}
