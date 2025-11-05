package dev.akif.tapik

import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class URIWithParametersTest {
    @Test
    fun `toURI replaces path variables and appends query parameters`() {
        val userId = PathVariable.uuid("userId")
        val page = QueryParameter.int("page")
        val pageSize = QueryParameter.int("pageSize")

        val base = listOf("users") to Parameters0

        val uri =
            (base / userId / "posts" + page + pageSize).toURI(
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"),
                2,
                3
            )

        assertEquals("/users/123e4567-e89b-12d3-a456-426614174000/posts?page=2&pageSize=3", uri.toString())
    }

    @Test
    fun `toURI omits query section when no query parameters are provided`() {
        val userId = PathVariable.int("id")
        val base = listOf("users") to Parameters0

        val uri = (base / userId / "profile").toURI(42)

        assertEquals("/users/42/profile", uri.toString())
    }
}
