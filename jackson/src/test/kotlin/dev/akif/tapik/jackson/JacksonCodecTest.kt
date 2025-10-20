package dev.akif.tapik.jackson

import arrow.core.getOrElse
import dev.akif.tapik.http.MediaType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.test.fail

class JacksonCodecTest {
    private data class User(
        val id: Int,
        val name: String
    )

    @Test
    fun `jacksonCodec round trips an object`() {
        val codec = jacksonCodec<User>("user")
        val user = User(1, "Tapik")

        val encoded = codec.encode(user)
        val decoded = codec.decode(encoded).getOrElse { fail("Unexpected decode failure") }

        assertEquals(user, decoded)
    }

    @Test
    fun `jacksonCodec surfaces mapper failures`() {
        val codec = jacksonCodec<User>("user")

        val failure = codec.decode("not-json".toByteArray())

        val errors =
            failure.fold(
                ifLeft = { it },
                ifRight = { fail("Expected decode failure but succeeded with $it") }
            )
        assertTrue(errors.first().contains("Cannot decode 'user' as User"))
    }

    @Test
    fun `jsonBody uses json media type`() {
        val body = jsonBody<User>("user")

        assertIs<MediaType.Json>(body.mediaType)
    }
}
