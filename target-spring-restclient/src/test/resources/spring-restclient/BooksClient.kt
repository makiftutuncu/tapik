package dev.akif.tapik.generated

import dev.akif.tapik.ByteArrayFormat
import dev.akif.tapik.DecodeResult
import dev.akif.tapik.StringFormat
import dev.akif.tapik.target.spring.restclient.RestClientRequestBody
import dev.akif.tapik.target.spring.restclient.RestClientTransport
import dev.akif.tapik.target.spring.restclient.selectResponseBodyMediaType
import dev.akif.tapik.test.fixtures.library.AuthorId
import dev.akif.tapik.test.fixtures.library.Book
import dev.akif.tapik.test.fixtures.library.BookId
import dev.akif.tapik.test.fixtures.library.Books
import dev.akif.tapik.test.fixtures.library.CreateBook
import java.util.UUID

public interface BooksClient {
    public val booksApi: Books

    public val restClientTransport: RestClientTransport

    public sealed interface ListResponse {
        public data class Ok(
            public val body: List<Book>
        ) : ListResponse
    }

    /** List books */
    public fun list(
        xRequestId: UUID,
        page: Int = booksApi.list.uri.queries._1.presence.value,
        authorId: List<AuthorId>? = null
    ): ListResponse {
        val endpoint = booksApi.list
        val response =
            restClientTransport.exchange(
                method = endpoint.method,
                uri = { uriBuilder ->
                    uriBuilder
                        .path("/books")
                        .queryParam("page", booksApi.list.uri.queries._1.format.encode(page))
                        .apply { authorId?.let { values -> queryParam("authorId", *booksApi.list.uri.queries._2.format.encode(values).toTypedArray()) } }
                        .build()
                },
                headers = buildMap {
                    put("X-Request-Id", listOf(booksApi.list.headers._1.format.encode(xRequestId)))
                },
                body = null
            )

        return when {
            booksApi.list.outputs._1.matcher.matches(response.status) -> {
                selectResponseBodyMediaType(response = response, offered = listOf(booksApi.list.outputs._1.bodies._1.mediaType), allowsNoBody = false, endpointId = "Books.list")
                val decodedBody = decodeBody(booksApi.list.outputs._1.bodies._1.format, response.body, "Books.list")
                ListResponse.Ok(decodedBody)
            }
            else -> error("Unexpected status ${response.status.code} for Books.list")
        }
    }

    public sealed interface GetResponse {
        public data class Ok(
            public val body: Book
        ) : GetResponse
        public data object NotFound : GetResponse
    }

    /** Get a book */
    public fun get(
        bookId: BookId,
        xRequestId: UUID
    ): GetResponse {
        val endpoint = booksApi.get
        val response =
            restClientTransport.exchange(
                method = endpoint.method,
                uri = { uriBuilder ->
                    uriBuilder
                        .path("/books/{bookId}")
                        .build(
                            mapOf(
                                "bookId" to booksApi.get.uri.paths._1.format.encode(bookId)
                            )
                        )
                },
                headers = buildMap {
                    put("X-Request-Id", listOf(booksApi.get.headers._1.format.encode(xRequestId)))
                },
                body = null
            )

        return when {
            booksApi.get.outputs._1.matcher.matches(response.status) -> {
                selectResponseBodyMediaType(response = response, offered = listOf(booksApi.get.outputs._1.bodies._1.mediaType), allowsNoBody = false, endpointId = "Books.get")
                val decodedBody = decodeBody(booksApi.get.outputs._1.bodies._1.format, response.body, "Books.get")
                GetResponse.Ok(decodedBody)
            }
            booksApi.get.outputs._2.matcher.matches(response.status) -> {
                selectResponseBodyMediaType(response = response, offered = emptyList(), allowsNoBody = true, endpointId = "Books.get")
                GetResponse.NotFound
            }
            else -> error("Unexpected status ${response.status.code} for Books.get")
        }
    }

    public sealed interface CreateResponse {
        public data class Created(
            public val body: Book,
            public val location: String
        ) : CreateResponse
        public data object BadRequest : CreateResponse
    }

    /** Create a book */
    public fun create(
        xRequestId: UUID,
        body: CreateBook
    ): CreateResponse {
        val endpoint = booksApi.create
        val response =
            restClientTransport.exchange(
                method = endpoint.method,
                uri = { uriBuilder ->
                    uriBuilder
                        .path("/books")
                        .build()
                },
                headers = buildMap {
                    put("X-Request-Id", listOf(booksApi.create.headers._1.format.encode(xRequestId)))
                },
                body = RestClientRequestBody(booksApi.create.input.bodies._1.mediaType, booksApi.create.input.bodies._1.format.encode(body))
            )

        return when {
            booksApi.create.outputs._1.matcher.matches(response.status) -> {
                selectResponseBodyMediaType(response = response, offered = listOf(booksApi.create.outputs._1.bodies._1.mediaType), allowsNoBody = false, endpointId = "Books.create")
                val decodedBody = decodeBody(booksApi.create.outputs._1.bodies._1.format, response.body, "Books.create")
                val location = decodeHeader(booksApi.create.outputs._1.headers._1.format, response.headers.entries.firstOrNull { (name, _) -> name.equals("Location", ignoreCase = true) }?.value?.firstOrNull() ?: error("Missing response header Location for Books.create"), "Books.create")
                CreateResponse.Created(decodedBody, location)
            }
            booksApi.create.outputs._2.matcher.matches(response.status) -> {
                selectResponseBodyMediaType(response = response, offered = emptyList(), allowsNoBody = true, endpointId = "Books.create")
                CreateResponse.BadRequest
            }
            else -> error("Unexpected status ${response.status.code} for Books.create")
        }
    }

    private fun <Value : Any> decodeBody(
        format: ByteArrayFormat<Value>,
        bytes: ByteArray,
        endpointId: String
    ): Value =
        when (val result = format.decode(bytes)) {
            is DecodeResult.Success -> result.value
            is DecodeResult.Failure -> error("Cannot decode response body for $endpointId: " + result.errors.joinToString { it.message })
        }

    private fun <Value : Any> decodeHeader(
        format: StringFormat<Value>,
        value: String,
        endpointId: String
    ): Value =
        when (val result = format.decode(value)) {
            is DecodeResult.Success -> result.value
            is DecodeResult.Failure -> error("Cannot decode response header for $endpointId: " + result.errors.joinToString { it.message })
        }

}
