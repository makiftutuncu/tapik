package dev.akif.tapik

import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class URIWithParametersTest {
    @Test
    fun `toURI replaces path variables and appends query parameters`() {
        val userId = PathVariable.uuid("userId")
        val page = QueryParameter.int("page")
        val pageSize = QueryParameter.int("pageSize")

        val base = listOf("users") to emptyParameters()
        val template = base / userId / "posts" + page + pageSize

        val uri =
            renderURI(
                template.first,
                template.second.item1 to
                    template.second.item1.codec
                        .encode(UUID.fromString("123e4567-e89b-12d3-a456-426614174000")),
                template.second.item2 to
                    template.second.item2.codec
                        .encode(2),
                template.second.item3 to
                    template.second.item3.codec
                        .encode(3)
            )

        assertEquals("/users/123e4567-e89b-12d3-a456-426614174000/posts?page=2&pageSize=3", uri.toString())
    }

    @Test
    fun `toURI omits query section when no query parameters are provided`() {
        val userId = PathVariable.int("id")
        val base = listOf("users") to emptyParameters()
        val template = base / userId / "profile"

        val uri =
            renderURI(
                template.first,
                template.second.item1 to
                    template.second.item1.codec
                        .encode(42)
            )

        assertEquals("/users/42/profile", uri.toString())
    }

    @Test
    fun `toURI inserts path separator when starting from a base string and a path variable`() {
        val userId = PathVariable.int("id")
        val template = "users" / userId / "profile"

        val uri =
            renderURI(
                template.first,
                template.second.item1 to
                    template.second.item1.codec
                        .encode(42)
            )

        assertEquals("/users/42/profile", uri.toString())
    }

    @Test
    fun `renderURI omits optional query values when null`() {
        val id = PathVariable.int("id")
        val page = QueryParameter.int("page")
        val search = QueryParameter.string("search").optional(null)

        val uri =
            renderURI(
                listOf("users", "{id}", "posts"),
                id to id.codec.encode(42),
                page to page.codec.encode(1),
                search to null
            )

        assertEquals("/users/42/posts?page=1", uri.toString())
    }

    @Test
    fun `renderURI fails when required query value is null`() {
        val page = QueryParameter.int("page")

        assertFailsWith<IllegalArgumentException> {
            renderURI(listOf("users"), page to null)
        }
    }

    @Test
    fun `renderURI fails when path variable value is missing`() {
        assertFailsWith<IllegalArgumentException> {
            renderURI(listOf("users", "{id}"))
        }
    }
}
