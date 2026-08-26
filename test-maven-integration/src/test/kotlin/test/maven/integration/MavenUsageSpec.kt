package dev.akif.tapik.test.maven.integration

import dev.akif.tapik.ApiRegistry
import dev.akif.tapik.test.maven.contract.Author
import dev.akif.tapik.test.maven.contract.Authors
import dev.akif.tapik.test.maven.contract.CreateAuthor
import dev.akif.tapik.test.maven.integration.generated.AuthorsClient
import dev.akif.tapik.test.maven.integration.generated.AuthorsServer
import dev.akif.tapik.plugin.spring.restclient.RestClientTransport
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.content as requestContent
import org.springframework.test.web.client.match.MockRestRequestMatchers.header
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.test.web.client.response.MockRestResponseCreators.withStatus
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header as responseHeader
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.client.RestClient
import org.springframework.web.bind.annotation.RestController
import java.nio.file.Files
import java.nio.file.Path
import java.util.ServiceLoader

class MavenUsageSpec : FunSpec({
    test("compile API definitions and generate OpenAPI through one Tapik Maven plugin") {
        val apis = ServiceLoader.load(ApiRegistry::class.java).flatMap(ApiRegistry::apis)
        apis.map { api -> api.id }.sorted() shouldContainExactly listOf("Authors", "Books")
        apis.single { api -> api.id == "Authors" }.shouldBeInstanceOf<Authors>()

        Files.isRegularFile(
            Path.of(
                "target/generated-sources/tapik-restclient/dev/akif/tapik/test/maven/integration/generated/AuthorsClient.kt"
            )
        ) shouldBe true
        Class.forName("dev.akif.tapik.test.maven.integration.generated.AuthorsClient").isInterface shouldBe true
        Files.isRegularFile(
            Path.of(
                "target/generated-sources/tapik-webmvc/dev/akif/tapik/test/maven/integration/generated/AuthorsServer.kt"
            )
        ) shouldBe true
        Class.forName("dev.akif.tapik.test.maven.integration.generated.AuthorsServer").isInterface shouldBe true

        generated("Authors") shouldBe expected("Authors")
        generated("Books") shouldBe expected("Books")
    }

    test("execute a client generated from a contract dependency") {
        val builder = RestClient.builder().baseUrl("https://library.example")
        val server = MockRestServiceServer.bindTo(builder).build()
        val client =
            object : AuthorsClient {
                override val authorsApi = Authors()
                override val restClientTransport = RestClientTransport(builder.build())
            }

        server
            .expect(requestTo("https://library.example/authors?name=Ursula&page=1"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("X-Request-Id", "request-1"))
            .andExpect(header("X-Client", "tapik"))
            .andRespond(
                withSuccess(
                    """[{"id":"author-1","name":"Ursula K. Le Guin"}]""",
                    MediaType.APPLICATION_JSON
                ).header("x-api-version", "1")
            )

        client.list(xRequestId = "request-1", name = listOf("Ursula")) shouldBe
            AuthorsClient.ListResponse.Ok(
                body = listOf(Author(id = "author-1", name = "Ursula K. Le Guin"))
            )
        server.verify()
    }

    test("reject missing, mismatched, or repeated fixed response headers") {
        listOf(emptyList(), listOf("2"), listOf("1", "1")).forEach { apiVersions ->
            val builder = RestClient.builder().baseUrl("https://library.example")
            val server = MockRestServiceServer.bindTo(builder).build()
            val client =
                object : AuthorsClient {
                    override val authorsApi = Authors()
                    override val restClientTransport = RestClientTransport(builder.build())
                }
            val response =
                withSuccess(
                    """[{"id":"author-1","name":"Ursula K. Le Guin"}]""",
                    MediaType.APPLICATION_JSON
                )
            if (apiVersions.isNotEmpty()) {
                response.header("X-API-Version", *apiVersions.toTypedArray())
            }
            server.expect(requestTo("https://library.example/authors?page=1")).andRespond(response)

            shouldThrow<IllegalStateException> { client.list(xRequestId = "request-1") }
                .message shouldContain "X-API-Version"
            server.verify()
        }
    }

    test("execute create response alternatives through a generated client") {
        val builder = RestClient.builder().baseUrl("https://library.example")
        val server = MockRestServiceServer.bindTo(builder).build()
        val client =
            object : AuthorsClient {
                override val authorsApi = Authors()
                override val restClientTransport = RestClientTransport(builder.build())
            }

        server
            .expect(requestTo("https://library.example/authors"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(header("X-Request-Id", "request-1"))
            .andExpect(requestContent().contentType(MediaType.APPLICATION_JSON))
            .andExpect(requestContent().string("""{"name":"Octavia E. Butler"}"""))
            .andRespond(
                withStatus(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Location", "/authors/author-2")
                    .body("""{"id":"author-2","name":"Octavia E. Butler"}""")
            )
        server
            .expect(requestTo("https://library.example/authors"))
            .andRespond(withStatus(HttpStatus.BAD_REQUEST))

        client.create(
            xRequestId = "request-1",
            body = CreateAuthor(name = "Octavia E. Butler")
        ) shouldBe
            AuthorsClient.CreateResponse.Created(
                body = Author(id = "author-2", name = "Octavia E. Butler"),
                location = "/authors/author-2"
            )

        client.create(
            xRequestId = "request-1",
            body = CreateAuthor(name = "invalid")
        ) shouldBe AuthorsClient.CreateResponse.BadRequest
        server.verify()
    }

    test("serve a response through a generated WebMVC interface") {
        val controller = AuthorsController()
        val mvc = MockMvcBuilders.standaloneSetup(controller).build()

        mvc
            .perform(
                get("/authors")
                    .queryParam("name", "Ursula")
                    .header("X-Request-Id", "request-1")
                    .header("X-Client", "tapik")
            )
            .andExpect(status().isOk)
            .andExpect(responseHeader().string("X-API-Version", "1"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string("""[{"id":"author-1","name":"Ursula K. Le Guin"}]"""))
        controller.names shouldBe listOf("Ursula")
        controller.page shouldBe 1

        mvc
            .perform(
                get("/authors")
                    .queryParam("name", "Ursula,Octavia")
                    .header("X-Request-Id", "request-1")
                    .header("X-Client", "tapik")
            ).andExpect(status().isOk)
        controller.names shouldBe listOf("Ursula,Octavia")

        mvc
            .perform(
                get("/authors")
                    .queryParam("name", "Ursula", "Octavia")
                    .header("X-Request-Id", "request-1")
                    .header("X-Client", "tapik")
            ).andExpect(status().isOk)
        controller.names shouldBe listOf("Ursula", "Octavia")

        mvc
            .perform(
                get("/authors")
                    .queryParam("name", "Ursula")
                    .header("X-Request-Id", "request-1")
                    .header("X-Client", "tapik")
                    .accept(MediaType.APPLICATION_XML)
            ).andExpect(status().isNotAcceptable)

        mvc
            .perform(
                get("/authors")
                    .queryParam("name", "Ursula")
                    .header("X-Request-Id", "request-1")
                    .header("X-Client", "tapik")
                    .header("Accept", "application/json;q=0, application/xml;q=1")
            ).andExpect(status().isNotAcceptable)

        mvc
            .perform(
                get("/authors")
                    .queryParam("name", "none")
                    .header("X-Request-Id", "request-1")
                    .header("X-Client", "tapik")
                    .accept(MediaType.TEXT_PLAIN)
            ).andExpect(status().isNoContent)

        mvc
            .perform(
                get("/authors")
                    .queryParam("page", "not-an-integer")
                    .header("X-Request-Id", "request-1")
                    .header("X-Client", "tapik")
            ).andExpect(status().isBadRequest)

        mvc
            .perform(
                get("/authors")
                    .header("X-Request-Id", "request-1")
                    .header("X-Client", "another-client")
            ).andExpect(status().isBadRequest)
    }

    test("execute create requests through a generated WebMVC interface") {
        val controller = AuthorsController()
        val mvc = MockMvcBuilders.standaloneSetup(controller).build()

        mvc
            .perform(
                post("/authors")
                    .header("X-Request-Id", "request-1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content("""{"name":"Octavia E. Butler"}""")
            )
            .andExpect(status().isCreated)
            .andExpect(responseHeader().string("Location", "/authors/author-2"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string("""{"id":"author-2","name":"Octavia E. Butler"}"""))
        controller.created shouldBe CreateAuthor(name = "Octavia E. Butler")

        mvc
            .perform(
                post("/authors")
                    .header("X-Request-Id", "request-1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("not-json")
            ).andExpect(status().isBadRequest)

        mvc
            .perform(
                post("/authors")
                    .header("X-Request-Id", "request-1")
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("Octavia E. Butler")
            ).andExpect(status().isUnsupportedMediaType)

        mvc
            .perform(
                post("/authors")
                    .header("X-Request-Id", "request-1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"name":"invalid"}""")
            )
            .andExpect(status().isBadRequest)
            .andExpect(content().bytes(byteArrayOf()))
    }
})

@RestController
private class AuthorsController : AuthorsServer {
    override val authorsApi: Authors = Authors()
    var names: List<String>? = null
    var page: Int? = null
    var created: CreateAuthor? = null

    override fun list(
        xRequestId: String,
        name: List<String>?,
        page: Int
    ): AuthorsServer.ListResponse =
        if (name == listOf("none")) {
            AuthorsServer.ListResponse.NoContent
        } else {
            names = name
            this.page = page
            AuthorsServer.ListResponse.Ok(
                body = listOf(Author(id = "author-1", name = "Ursula K. Le Guin"))
            )
        }

    override fun create(
        xRequestId: String,
        body: CreateAuthor
    ): AuthorsServer.CreateResponse =
        if (body.name == "invalid") {
            AuthorsServer.CreateResponse.BadRequest
        } else {
            created = body
            AuthorsServer.CreateResponse.Created(
                body = Author(id = "author-2", name = body.name),
                location = "/authors/author-2"
            )
        }
}

private fun generated(api: String): String =
    Files.readString(Path.of("target/generated/tapik/$api.openapi.json"))

private fun expected(api: String): String =
    requireNotNull(MavenUsageSpec::class.java.getResource("/openapi/${api.lowercase()}.json"))
        .readText()
        .trimEnd()
