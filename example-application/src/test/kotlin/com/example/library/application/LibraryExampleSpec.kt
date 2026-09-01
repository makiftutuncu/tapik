package com.example.library.application

import com.example.library.generated.BooksClient
import com.example.library.generated.BooksServer
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.springframework.http.MediaType
import org.springframework.boot.WebApplicationType
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient
import java.nio.file.Files
import java.nio.file.Path

class LibraryExampleSpec : FunSpec({
    test("generate every documented target from the contract artifact") {
        BooksClient::class.java.isInterface shouldBe true
        BooksServer::class.java.isInterface shouldBe true
        val openApi = Path.of("target/generated/tapik/Books.openapi.yml")
        Files.isRegularFile(openApi) shouldBe true
        Files.readString(openApi) shouldContain "/books:"
    }

    test("implement the generated server in application source") {
        val response = BooksHandler().list()

        response shouldBe
            BooksServer.ListResponse.Ok(
                body =
                    listOf(
                        com.example.library.contract.Book(
                            isbn = "978-0-575-07065-3",
                            title = "The Left Hand of Darkness"
                        )
                    )
            )
    }

    test("call the contract through the generated RestClient") {
        val builder = RestClient.builder()
        val server = MockRestServiceServer.bindTo(builder).build()
        val client = BooksRestClient(builder, "https://library.example")

        server
            .expect(requestTo("https://library.example/books"))
            .andRespond(
                withSuccess(
                    """[{"isbn":"978-0-575-07065-3","title":"The Left Hand of Darkness"}]""",
                    MediaType.APPLICATION_JSON
                )
            )

        client.list() shouldBe
            BooksClient.ListResponse.Ok(
                body =
                    listOf(
                        com.example.library.contract.Book(
                            isbn = "978-0-575-07065-3",
                            title = "The Left Hand of Darkness"
                        )
                    )
            )
        server.verify()
    }

    test("start the application with the generated client component") {
        SpringApplicationBuilder(LibraryApplication::class.java)
            .web(WebApplicationType.NONE)
            .properties("spring.main.banner-mode=off")
            .run()
            .use { context ->
                context.getBean(BooksRestClient::class.java)::class shouldBe BooksRestClient::class
            }
    }
})
