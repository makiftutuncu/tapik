package dev.akif.tapik.http

import arrow.core.getOrElse
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.net.URI
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

class HttpStringCodecsTest {
    @ParameterizedTest
    @MethodSource("validMediaTypes")
    fun `mediaType codec decodes known types`(
        input: String,
        expected: MediaType
    ) {
        val codec = StringCodecs.mediaType("contentType")

        val decoded = codec.decode(input).getOrElse { fail("Failed to decode media type $input: $it") }
        assertEquals(expected, decoded)
    }

    @ParameterizedTest
    @MethodSource("invalidMediaTypes")
    fun `mediaType codec fails with descriptive error`(input: String) {
        val codec = StringCodecs.mediaType("contentType")

        val errors =
            codec
                .decode(input)
                .fold(
                    ifLeft = { it },
                    ifRight = { fail("Expected decode failure but succeeded with $it") }
                )

        assertTrue(errors.first().contains("Cannot decode 'contentType'"))
    }

    @ParameterizedTest
    @MethodSource("uriInputs")
    fun `uri codec round trips`(value: String) {
        val codec = StringCodecs.uri("location")

        val decoded = codec.decode(value).getOrElse { fail("Failed to decode URI $value: $it") }
        assertEquals(URI.create(value), decoded)
        assertEquals(value, codec.encode(decoded))
    }

    companion object {
        @JvmStatic
        fun validMediaTypes(): Stream<Arguments> =
            Stream.of(
                Arguments.of("application/json", MediaType.Json),
                Arguments.of("text/plain", MediaType.PlainText),
                Arguments.of("image/png", MediaType.Custom("image", "png"))
            )

        @JvmStatic
        fun invalidMediaTypes(): Stream<String> = Stream.of("application", "text", "noslash")

        @JvmStatic
        fun uriInputs(): Stream<String> =
            Stream.of(
                "https://tapik.dev/docs",
                "http://localhost:8080/api/users?active=true",
                "/relative/path"
            )
    }
}
