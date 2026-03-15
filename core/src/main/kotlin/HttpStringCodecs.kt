package dev.akif.tapik

import dev.akif.tapik.codec.Codec
import dev.akif.tapik.codec.StringCodec
import java.net.URI

/** String codecs for HTTP-specific scalar types used by Tapik's core models. */
object HttpStringCodecs {
    /** Builds a codec that renders [MediaType] values as `major/minor`. */
    fun mediaType(name: String): StringCodec<MediaType> =
        Codec.unsafe(name, { "${it.major}/${it.minor}" }) {
            val parts = it.split("/")
            MediaType.of(parts[0], parts[1])
        }

    /** Builds a codec that delegates URI parsing to [URI.create]. */
    fun uri(name: String): StringCodec<URI> = Codec.unsafe(name, { it.toString() }) { URI.create(it) }
}
