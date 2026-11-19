package dev.akif.tapik.test.fixtures.documentation

import dev.akif.tapik.*

// tag::custom-byte-format[]
internal data class TrackingCode(val value: String)

internal val trackingCodeFormat: ByteArrayFormat<TrackingCode> =
    Format(
        codec =
            Codec(
                decoder = Decoder { bytes ->
                    val value = bytes.decodeToString()
                    if (value.startsWith("trk-")) {
                        DecodeResult.Success(TrackingCode(value))
                    } else {
                        DecodeResult.Failure(DecodeError("Tracking codes must start with 'trk-'"))
                    }
                },
                encoder = Encoder { it.value.encodeToByteArray() }
            ),
        schema = ScalarSchema(SchemaType.STRING).named("TrackingCode")
    )
// end::custom-byte-format[]

// tag::custom-byte-body[]
internal val trackingCodeBody = body(MediaType("text/plain"), trackingCodeFormat)
// end::custom-byte-body[]
