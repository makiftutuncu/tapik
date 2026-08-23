package dev.akif.tapik.generated

public interface BooksClient {
    public val booksApi: dev.akif.tapik.fixtures.library.Books

    public val restClientTransport: dev.akif.tapik.spring.restclient.RestClientTransport

    public sealed interface ListResponse {
        public data class Ok(
            public val body: kotlin.collections.List<dev.akif.tapik.fixtures.library.Book>
        ) : ListResponse
    }

    /** List books */
    public fun list(
        xRequestId: java.util.UUID,
        page: kotlin.Int = booksApi.list.uri.queries._1.presence.value,
        authorId: kotlin.collections.List<dev.akif.tapik.fixtures.library.AuthorId>? = null
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
                dev.akif.tapik.spring.restclient.selectResponseBodyMediaType(response = response, offered = kotlin.collections.listOf(booksApi.list.outputs._1.bodies._1.mediaType), allowsNoBody = false, endpointId = "Books.list")
                val decodedBody = decodeBody(booksApi.list.outputs._1.bodies._1.format, response.body, "Books.list")
                ListResponse.Ok(decodedBody)
            }
            else -> kotlin.error("Unexpected status ${response.status.code} for Books.list")
        }
    }

    public sealed interface GetResponse {
        public data class Ok(
            public val body: dev.akif.tapik.fixtures.library.Book
        ) : GetResponse
        public data object NotFound : GetResponse
    }

    /** Get a book */
    public fun get(
        bookId: dev.akif.tapik.fixtures.library.BookId,
        xRequestId: java.util.UUID
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
                dev.akif.tapik.spring.restclient.selectResponseBodyMediaType(response = response, offered = kotlin.collections.listOf(booksApi.get.outputs._1.bodies._1.mediaType), allowsNoBody = false, endpointId = "Books.get")
                val decodedBody = decodeBody(booksApi.get.outputs._1.bodies._1.format, response.body, "Books.get")
                GetResponse.Ok(decodedBody)
            }
            booksApi.get.outputs._2.matcher.matches(response.status) -> {
                dev.akif.tapik.spring.restclient.selectResponseBodyMediaType(response = response, offered = kotlin.collections.emptyList(), allowsNoBody = true, endpointId = "Books.get")
                GetResponse.NotFound
            }
            else -> kotlin.error("Unexpected status ${response.status.code} for Books.get")
        }
    }

    public sealed interface CreateResponse {
        public data class Created(
            public val body: dev.akif.tapik.fixtures.library.Book,
            public val location: kotlin.String
        ) : CreateResponse
        public data object BadRequest : CreateResponse
    }

    /** Create a book */
    public fun create(
        xRequestId: java.util.UUID,
        body: dev.akif.tapik.fixtures.library.CreateBook
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
                body = dev.akif.tapik.spring.restclient.RestClientRequestBody(booksApi.create.input.bodies._1.mediaType, booksApi.create.input.bodies._1.format.encode(body))
            )

        return when {
            booksApi.create.outputs._1.matcher.matches(response.status) -> {
                dev.akif.tapik.spring.restclient.selectResponseBodyMediaType(response = response, offered = kotlin.collections.listOf(booksApi.create.outputs._1.bodies._1.mediaType), allowsNoBody = false, endpointId = "Books.create")
                val decodedBody = decodeBody(booksApi.create.outputs._1.bodies._1.format, response.body, "Books.create")
                val location = decodeHeader(booksApi.create.outputs._1.headers._1.format, response.headers.entries.firstOrNull { (name, _) -> name.equals("Location", ignoreCase = true) }?.value?.firstOrNull() ?: kotlin.error("Missing response header Location for Books.create"), "Books.create")
                CreateResponse.Created(decodedBody, location)
            }
            booksApi.create.outputs._2.matcher.matches(response.status) -> {
                dev.akif.tapik.spring.restclient.selectResponseBodyMediaType(response = response, offered = kotlin.collections.emptyList(), allowsNoBody = true, endpointId = "Books.create")
                CreateResponse.BadRequest
            }
            else -> kotlin.error("Unexpected status ${response.status.code} for Books.create")
        }
    }

    private fun <Value : kotlin.Any> decodeBody(
        format: dev.akif.tapik.ByteArrayFormat<Value>,
        bytes: kotlin.ByteArray,
        endpointId: kotlin.String
    ): Value =
        when (val result = format.decode(bytes)) {
            is dev.akif.tapik.DecodeResult.Success -> result.value
            is dev.akif.tapik.DecodeResult.Failure -> kotlin.error("Cannot decode response body for $endpointId: " + result.errors.joinToString { it.message })
        }

    private fun <Value : kotlin.Any> decodeHeader(
        format: dev.akif.tapik.StringFormat<Value>,
        value: kotlin.String,
        endpointId: kotlin.String
    ): Value =
        when (val result = format.decode(value)) {
            is dev.akif.tapik.DecodeResult.Success -> result.value
            is dev.akif.tapik.DecodeResult.Failure -> kotlin.error("Cannot decode response header for $endpointId: " + result.errors.joinToString { it.message })
        }

}
