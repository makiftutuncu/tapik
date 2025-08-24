package dev.akif.tapik.http

import dev.akif.tapik.codec.Codec
import dev.akif.tapik.codec.StringCodec
import dev.akif.tapik.http.MediaType.Companion.of
import java.net.URI

object StringCodecs {
    fun mediaType(name: String): StringCodec<MediaType> =
        Codec.unsafe(name, { "${it.major}/${it.minor}" }) {
            val parts = it.split("/")
            of(parts[0], parts[1])
        }

    fun uri(name: String): StringCodec<URI> =
        Codec.unsafe(name, { it.toString() }) { URI.create(it) }
}
