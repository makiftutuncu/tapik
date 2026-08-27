package dev.akif.tapik.generated

import dev.akif.tapik.ByteArrayFormat
import dev.akif.tapik.DecodeResult
import dev.akif.tapik.Format
import dev.akif.tapik.StringFormat
import dev.akif.tapik.common.spring.selectResponseMediaType
import dev.akif.tapik.common.spring.toSpringMediaType
import dev.akif.tapik.test.fixtures.library.AuthorId
import dev.akif.tapik.test.fixtures.library.Book
import dev.akif.tapik.test.fixtures.library.BookId
import dev.akif.tapik.test.fixtures.library.Books
import dev.akif.tapik.test.fixtures.library.CreateBook
import java.util.UUID
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.InvalidMediaTypeException
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

public interface BooksServer {
    public val booksApi: Books

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
    ): ListResponse

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
    ): GetResponse

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
    ): CreateResponse
}

@ConditionalOnSingleCandidate(BooksServer::class)
@RestController
internal class BooksGeneratedController(
    private val handler: BooksServer
) {
    private val booksApi: Books
        get() = handler.booksApi

    @GetMapping(path = ["/books"], produces = ["application/json"])
    public fun list(
        @RequestHeader(name = "X-Request-Id", required = true) xRequestIdRaw: String,
        @RequestParam(name = "page", required = false) pageRaw: String? = null,
        @RequestParam queryParameters: MultiValueMap<String, String>,
        @RequestHeader(name = HttpHeaders.ACCEPT, required = false) accept: String? = null
    ): ResponseEntity<ByteArray> {
        val authorIdRaw = queryParameters["authorId"]
        val page = pageRaw?.let { raw -> decodeString(booksApi.list.uri.queries._1.format, raw, "Books.list query page") } ?: booksApi.list.uri.queries._1.presence.value
        val authorId = authorIdRaw?.let { raw -> decodeStrings(booksApi.list.uri.queries._2.format, raw, "Books.list query authorId") }
        val xRequestId = decodeString(booksApi.list.headers._1.format, xRequestIdRaw, "Books.list header X-Request-Id")
        val response =
            handler.list(
                xRequestId = xRequestId,
                page = page,
                authorId = authorId
            )

        return when (response) {
            is BooksServer.ListResponse.Ok -> {
                val headers = emptyMap<String, List<String>>()
                val encodedBody =
                    when (selectResponseMediaType(accept, listOf(booksApi.list.outputs._1.bodies._1.mediaType))) {
                        booksApi.list.outputs._1.bodies._1.mediaType -> booksApi.list.outputs._1.bodies._1.mediaType to booksApi.list.outputs._1.bodies._1.format.encode(response.body)
                        else -> notAcceptable(accept, "Books.list")
                    }
                responseEntity(200, headers, encodedBody)
            }
        }
    }

    @GetMapping(path = ["/books/{bookId}"])
    public fun get(
        @PathVariable(name = "bookId") bookIdRaw: String,
        @RequestHeader(name = "X-Request-Id", required = true) xRequestIdRaw: String,
        @RequestHeader(name = HttpHeaders.ACCEPT, required = false) accept: String? = null
    ): ResponseEntity<ByteArray> {
        val bookId = decodeString(booksApi.get.uri.paths._1.format, bookIdRaw, "Books.get path bookId")
        val xRequestId = decodeString(booksApi.get.headers._1.format, xRequestIdRaw, "Books.get header X-Request-Id")
        val response =
            handler.get(
                bookId = bookId,
                xRequestId = xRequestId
            )

        return when (response) {
            is BooksServer.GetResponse.Ok -> {
                val headers = emptyMap<String, List<String>>()
                val encodedBody =
                    when (selectResponseMediaType(accept, listOf(booksApi.get.outputs._1.bodies._1.mediaType))) {
                        booksApi.get.outputs._1.bodies._1.mediaType -> booksApi.get.outputs._1.bodies._1.mediaType to booksApi.get.outputs._1.bodies._1.format.encode(response.body)
                        else -> notAcceptable(accept, "Books.get")
                    }
                responseEntity(200, headers, encodedBody)
            }
            is BooksServer.GetResponse.NotFound -> {
                val headers = emptyMap<String, List<String>>()
                val encodedBody: Pair<dev.akif.tapik.MediaType, ByteArray>? = null
                responseEntity(404, headers, encodedBody)
            }
        }
    }

    @PostMapping(path = ["/books"], consumes = ["application/json"])
    public fun create(
        @RequestHeader(name = "X-Request-Id", required = true) xRequestIdRaw: String,
        @RequestBody(required = true) bodyBytes: ByteArray,
        @RequestHeader(name = HttpHeaders.CONTENT_TYPE, required = true) contentType: String,
        @RequestHeader(name = HttpHeaders.ACCEPT, required = false) accept: String? = null
    ): ResponseEntity<ByteArray> {
        val xRequestId = decodeString(booksApi.create.headers._1.format, xRequestIdRaw, "Books.create header X-Request-Id")
        val body =
            when {
                mediaTypeCompatible(contentType, booksApi.create.input.bodies._1.mediaType) -> decodeBody(booksApi.create.input.bodies._1.format, bodyBytes, "Books.create body")
                else -> unsupportedMediaType(contentType, "Books.create")
            }
        val response =
            handler.create(
                xRequestId = xRequestId,
                body = body
            )

        return when (response) {
            is BooksServer.CreateResponse.Created -> {
                val headers = buildMap {
                    put("Location", listOf(booksApi.create.outputs._1.headers._1.format.encode(response.location)))
                }
                val encodedBody =
                    when (selectResponseMediaType(accept, listOf(booksApi.create.outputs._1.bodies._1.mediaType))) {
                        booksApi.create.outputs._1.bodies._1.mediaType -> booksApi.create.outputs._1.bodies._1.mediaType to booksApi.create.outputs._1.bodies._1.format.encode(response.body)
                        else -> notAcceptable(accept, "Books.create")
                    }
                responseEntity(201, headers, encodedBody)
            }
            is BooksServer.CreateResponse.BadRequest -> {
                val headers = emptyMap<String, List<String>>()
                val encodedBody: Pair<dev.akif.tapik.MediaType, ByteArray>? = null
                responseEntity(400, headers, encodedBody)
            }
        }
    }

    private fun <Value : Any> decodeString(
        format: StringFormat<Value>,
        raw: String,
        location: String
    ): Value = decode(format.decode(raw), location)

    private fun <Value : Any> decodeStrings(
        format: Format<Value, List<String>>,
        raw: List<String>,
        location: String
    ): Value = decode(format.decode(raw), location)

    private fun <Value : Any> decodeBody(
        format: ByteArrayFormat<Value>,
        raw: ByteArray,
        location: String
    ): Value = decode(format.decode(raw), location)

    private fun <Value : Any> decode(
        result: DecodeResult<Value>,
        location: String
    ): Value =
        when (result) {
            is DecodeResult.Success -> result.value
            is DecodeResult.Failure ->
                badRequest("Cannot decode $location: " + result.errors.joinToString { it.message })
        }

    private fun mediaTypeCompatible(actual: String?, expected: dev.akif.tapik.MediaType): Boolean =
        try {
            actual != null && org.springframework.http.MediaType.parseMediaType(actual).isCompatibleWith(expected.toSpringMediaType())
        } catch (_: InvalidMediaTypeException) {
            false
        }

    private fun responseEntity(
        status: Int,
        headers: Map<String, List<String>>,
        body: Pair<dev.akif.tapik.MediaType, ByteArray>?
    ): ResponseEntity<ByteArray> {
        val springHeaders = HttpHeaders()
        headers.forEach { (name, values) -> springHeaders.addAll(name, values) }
        body?.let { (mediaType, _) -> springHeaders.contentType = mediaType.toSpringMediaType() }
        return ResponseEntity(body?.second, springHeaders, status)
    }

    private fun badRequest(message: String): Nothing =
        throw ResponseStatusException(HttpStatus.BAD_REQUEST, message)

    private fun unsupportedMediaType(actual: String?, endpointId: String): Nothing =
        throw ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported Content-Type $actual for $endpointId")

    private fun notAcceptable(accept: String?, endpointId: String): Nothing =
        throw ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "No response body matches Accept $accept for $endpointId")
}
