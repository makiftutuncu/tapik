package dev.akif.tapik

import dev.akif.tapik.codec.Codec
import dev.akif.tapik.codec.StringCodec
import java.net.URI

/** Utility string codecs for HTTP-specific types. */
object HttpStringCodecs {
    /**
     * Builds a string codec for [MediaType] values.
     *
     * @param name friendly identifier that will appear in error messages.
     * @return codec that formats media types as `major/minor` pairs.
     * @throws IllegalArgumentException when the encoded string does not contain a `/` separator.
     */
    fun mediaType(name: String): StringCodec<MediaType> =
        Codec.unsafe(name, { "${it.major}/${it.minor}" }) {
            val parts = it.split("/")
            MediaType.of(parts[0], parts[1])
        }

    /**
     * Builds a string codec for [URI] values.
     *
     * @param name friendly identifier that will appear in error messages.
     * @return codec that delegates parsing to [URI.create].
     * @throws IllegalArgumentException when the text cannot be parsed as a URI.
     */
    fun uri(name: String): StringCodec<URI> = Codec.unsafe(name, { it.toString() }) { URI.create(it) }
}
