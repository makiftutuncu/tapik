package dev.akif.tapik.generated

import dev.akif.tapik.spring.toSpringMediaType
import dev.akif.tapik.spring.selectResponseMediaType

public interface BooksServer {
    public val booksApi: dev.akif.tapik.fixtures.library.Books

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
    ): ListResponse

    @org.springframework.web.bind.annotation.GetMapping(path = ["/books"], produces = ["application/json"])
    public fun listHttp(
        @org.springframework.web.bind.annotation.RequestHeader(name = "X-Request-Id", required = true) xRequestIdRaw: kotlin.String,
        @org.springframework.web.bind.annotation.RequestParam(name = "page", required = false) pageRaw: kotlin.String? = null,
        @org.springframework.web.bind.annotation.RequestParam queryParameters: org.springframework.util.MultiValueMap<kotlin.String, kotlin.String>,
        @org.springframework.web.bind.annotation.RequestHeader(name = org.springframework.http.HttpHeaders.ACCEPT, required = false) accept: kotlin.String? = null
    ): org.springframework.http.ResponseEntity<kotlin.ByteArray> {
        val authorIdRaw = queryParameters["authorId"]
        val page = pageRaw?.let { raw -> decodeString(booksApi.list.uri.queries._1.format, raw, "Books.list query page") } ?: booksApi.list.uri.queries._1.presence.value
        val authorId = authorIdRaw?.let { raw -> decodeStrings(booksApi.list.uri.queries._2.format, raw, "Books.list query authorId") }
        val xRequestId = decodeString(booksApi.list.headers._1.format, xRequestIdRaw, "Books.list header X-Request-Id")
        val response =
            list(
                xRequestId = xRequestId,
                page = page,
                authorId = authorId
            )

        return when (response) {
            is ListResponse.Ok -> {
                val headers = kotlin.collections.emptyMap<kotlin.String, kotlin.collections.List<kotlin.String>>()
                val encodedBody =
                    when (selectResponseMediaType(accept, kotlin.collections.listOf(booksApi.list.outputs._1.bodies._1.mediaType))) {
                        booksApi.list.outputs._1.bodies._1.mediaType -> booksApi.list.outputs._1.bodies._1.mediaType to booksApi.list.outputs._1.bodies._1.format.encode(response.body)
                        else -> notAcceptable(accept, "Books.list")
                    }
                responseEntity(200, headers, encodedBody)
            }
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
    ): GetResponse

    @org.springframework.web.bind.annotation.GetMapping(path = ["/books/{bookId}"])
    public fun getHttp(
        @org.springframework.web.bind.annotation.PathVariable(name = "bookId") bookIdRaw: kotlin.String,
        @org.springframework.web.bind.annotation.RequestHeader(name = "X-Request-Id", required = true) xRequestIdRaw: kotlin.String,
        @org.springframework.web.bind.annotation.RequestHeader(name = org.springframework.http.HttpHeaders.ACCEPT, required = false) accept: kotlin.String? = null
    ): org.springframework.http.ResponseEntity<kotlin.ByteArray> {
        val bookId = decodeString(booksApi.get.uri.paths._1.format, bookIdRaw, "Books.get path bookId")
        val xRequestId = decodeString(booksApi.get.headers._1.format, xRequestIdRaw, "Books.get header X-Request-Id")
        val response =
            get(
                bookId = bookId,
                xRequestId = xRequestId
            )

        return when (response) {
            is GetResponse.Ok -> {
                val headers = kotlin.collections.emptyMap<kotlin.String, kotlin.collections.List<kotlin.String>>()
                val encodedBody =
                    when (selectResponseMediaType(accept, kotlin.collections.listOf(booksApi.get.outputs._1.bodies._1.mediaType))) {
                        booksApi.get.outputs._1.bodies._1.mediaType -> booksApi.get.outputs._1.bodies._1.mediaType to booksApi.get.outputs._1.bodies._1.format.encode(response.body)
                        else -> notAcceptable(accept, "Books.get")
                    }
                responseEntity(200, headers, encodedBody)
            }
            is GetResponse.NotFound -> {
                val headers = kotlin.collections.emptyMap<kotlin.String, kotlin.collections.List<kotlin.String>>()
                val encodedBody: kotlin.Pair<dev.akif.tapik.MediaType, kotlin.ByteArray>? = null
                responseEntity(404, headers, encodedBody)
            }
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
    ): CreateResponse

    @org.springframework.web.bind.annotation.PostMapping(path = ["/books"], consumes = ["application/json"])
    public fun createHttp(
        @org.springframework.web.bind.annotation.RequestHeader(name = "X-Request-Id", required = true) xRequestIdRaw: kotlin.String,
        @org.springframework.web.bind.annotation.RequestBody(required = true) bodyBytes: kotlin.ByteArray,
        @org.springframework.web.bind.annotation.RequestHeader(name = org.springframework.http.HttpHeaders.CONTENT_TYPE, required = true) contentType: kotlin.String,
        @org.springframework.web.bind.annotation.RequestHeader(name = org.springframework.http.HttpHeaders.ACCEPT, required = false) accept: kotlin.String? = null
    ): org.springframework.http.ResponseEntity<kotlin.ByteArray> {
        val xRequestId = decodeString(booksApi.create.headers._1.format, xRequestIdRaw, "Books.create header X-Request-Id")
        val body =
            when {
                mediaTypeCompatible(contentType, booksApi.create.input.bodies._1.mediaType) -> decodeBody(booksApi.create.input.bodies._1.format, bodyBytes, "Books.create body")
                else -> unsupportedMediaType(contentType, "Books.create")
            }
        val response =
            create(
                xRequestId = xRequestId,
                body = body
            )

        return when (response) {
            is CreateResponse.Created -> {
                val headers = buildMap {
                    put("Location", listOf(booksApi.create.outputs._1.headers._1.format.encode(response.location)))
                }
                val encodedBody =
                    when (selectResponseMediaType(accept, kotlin.collections.listOf(booksApi.create.outputs._1.bodies._1.mediaType))) {
                        booksApi.create.outputs._1.bodies._1.mediaType -> booksApi.create.outputs._1.bodies._1.mediaType to booksApi.create.outputs._1.bodies._1.format.encode(response.body)
                        else -> notAcceptable(accept, "Books.create")
                    }
                responseEntity(201, headers, encodedBody)
            }
            is CreateResponse.BadRequest -> {
                val headers = kotlin.collections.emptyMap<kotlin.String, kotlin.collections.List<kotlin.String>>()
                val encodedBody: kotlin.Pair<dev.akif.tapik.MediaType, kotlin.ByteArray>? = null
                responseEntity(400, headers, encodedBody)
            }
        }
    }

    private fun <Value : kotlin.Any> decodeString(
        format: dev.akif.tapik.StringFormat<Value>,
        raw: kotlin.String,
        location: kotlin.String
    ): Value = decode(format.decode(raw), location)

    private fun <Value : kotlin.Any> decodeStrings(
        format: dev.akif.tapik.Format<Value, kotlin.collections.List<kotlin.String>>,
        raw: kotlin.collections.List<kotlin.String>,
        location: kotlin.String
    ): Value = decode(format.decode(raw), location)

    private fun <Value : kotlin.Any> decodeBody(
        format: dev.akif.tapik.ByteArrayFormat<Value>,
        raw: kotlin.ByteArray,
        location: kotlin.String
    ): Value = decode(format.decode(raw), location)

    private fun <Value : kotlin.Any> decode(
        result: dev.akif.tapik.DecodeResult<Value>,
        location: kotlin.String
    ): Value =
        when (result) {
            is dev.akif.tapik.DecodeResult.Success -> result.value
            is dev.akif.tapik.DecodeResult.Failure ->
                badRequest("Cannot decode $location: " + result.errors.joinToString { it.message })
        }

    private fun mediaTypeCompatible(actual: kotlin.String?, expected: dev.akif.tapik.MediaType): kotlin.Boolean =
        try {
            actual != null && org.springframework.http.MediaType.parseMediaType(actual).isCompatibleWith(expected.toSpringMediaType())
        } catch (_: org.springframework.http.InvalidMediaTypeException) {
            false
        }

    private fun responseEntity(
        status: kotlin.Int,
        headers: kotlin.collections.Map<kotlin.String, kotlin.collections.List<kotlin.String>>,
        body: kotlin.Pair<dev.akif.tapik.MediaType, kotlin.ByteArray>?
    ): org.springframework.http.ResponseEntity<kotlin.ByteArray> {
        val springHeaders = org.springframework.http.HttpHeaders()
        headers.forEach { (name, values) -> springHeaders.addAll(name, values) }
        body?.let { (mediaType, _) -> springHeaders.contentType = mediaType.toSpringMediaType() }
        return org.springframework.http.ResponseEntity(body?.second, springHeaders, status)
    }

    private fun badRequest(message: kotlin.String): kotlin.Nothing =
        throw org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, message)

    private fun unsupportedMediaType(actual: kotlin.String?, endpointId: kotlin.String): kotlin.Nothing =
        throw org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported Content-Type $actual for $endpointId")

    private fun notAcceptable(accept: kotlin.String?, endpointId: kotlin.String): kotlin.Nothing =
        throw org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_ACCEPTABLE, "No response body matches Accept $accept for $endpointId")
}
