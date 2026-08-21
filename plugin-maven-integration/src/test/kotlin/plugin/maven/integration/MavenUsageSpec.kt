package dev.akif.tapik.plugin.maven.integration

import dev.akif.tapik.ApiRegistry
import dev.akif.tapik.plugin.maven.contract.integration.Author
import dev.akif.tapik.plugin.maven.contract.integration.Authors
import dev.akif.tapik.plugin.maven.integration.generated.AuthorsClient
import dev.akif.tapik.plugin.maven.integration.generated.AuthorsServer
import dev.akif.tapik.spring.restclient.RestClientTransport
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.header
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
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
                "target/generated-sources/tapik-restclient/dev/akif/tapik/plugin/maven/integration/generated/AuthorsClient.kt"
            )
        ) shouldBe true
        Class.forName("dev.akif.tapik.plugin.maven.integration.generated.AuthorsClient").isInterface shouldBe true
        Files.isRegularFile(
            Path.of(
                "target/generated-sources/tapik-webmvc/dev/akif/tapik/plugin/maven/integration/generated/AuthorsServer.kt"
            )
        ) shouldBe true
        Class.forName("dev.akif.tapik.plugin.maven.integration.generated.AuthorsServer").isInterface shouldBe true

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
            .expect(requestTo("https://library.example/authors?name=Ursula"))
            .andExpect(method(HttpMethod.GET))
            .andExpect(header("X-Request-Id", "request-1"))
            .andRespond(
                withSuccess(
                    """[{"id":"author-1","name":"Ursula K. Le Guin"}]""",
                    MediaType.APPLICATION_JSON
                ).header("x-api-version", "1")
            )

        client.list(xRequestId = "request-1", name = "Ursula") shouldBe
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
            server.expect(requestTo("https://library.example/authors")).andRespond(response)

            shouldThrow<IllegalStateException> { client.list(xRequestId = "request-1") }
                .message shouldContain "X-API-Version"
            server.verify()
        }
    }

    test("serve a response through a generated WebMVC interface") {
        val mvc = MockMvcBuilders.standaloneSetup(AuthorsController()).build()

        mvc
            .perform(
                get("/authors")
                    .queryParam("name", "Ursula")
                    .header("X-Request-Id", "request-1")
            )
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string("""[{"id":"author-1","name":"Ursula K. Le Guin"}]"""))

        mvc
            .perform(
                get("/authors")
                    .queryParam("name", "Ursula")
                    .header("X-Request-Id", "request-1")
                    .accept(MediaType.APPLICATION_XML)
            ).andExpect(status().isNotAcceptable)

        mvc
            .perform(
                get("/authors")
                    .queryParam("name", "Ursula")
                    .header("X-Request-Id", "request-1")
                    .header("Accept", "application/json;q=0, application/xml;q=1")
            ).andExpect(status().isNotAcceptable)

        mvc
            .perform(
                get("/authors")
                    .queryParam("name", "none")
                    .header("X-Request-Id", "request-1")
                    .accept(MediaType.TEXT_PLAIN)
            ).andExpect(status().isNoContent)
    }
})

@RestController
private class AuthorsController : AuthorsServer {
    override val authorsApi: Authors = Authors()

    override fun list(
        xRequestId: String,
        name: String?
    ): AuthorsServer.ListResponse =
        if (name == "none") {
            AuthorsServer.ListResponse.NoContent
        } else {
            AuthorsServer.ListResponse.Ok(
                body = listOf(Author(id = "author-1", name = "Ursula K. Le Guin"))
            )
        }
}

private fun generated(api: String): String =
    Files.readString(Path.of("target/generated/tapik/$api.openapi.json"))

private fun expected(api: String): String =
    requireNotNull(MavenUsageSpec::class.java.getResource("/openapi/${api.lowercase()}.json"))
        .readText()
        .trimEnd()
