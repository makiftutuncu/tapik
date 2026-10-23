package com.example.library.application

import com.example.library.contract.Book
import com.example.library.generated.contract.Books.BooksClient
import com.example.library.generated.contract.Books.BooksServer
import com.example.library.generated.contract.Books.ListResponse
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.web.client.RestClient
import java.nio.file.Files
import java.nio.file.Path

class GradleLibraryExampleSpec : FunSpec({
    test("generate every documented target from the contract project") {
        BooksClient::class.java.isInterface shouldBe true
        BooksServer::class.java.isInterface shouldBe true
        val openApi = Path.of("build/generated/tapik/documentation/Books.openapi.yml")
        Files.isRegularFile(openApi) shouldBe true
        Files.readString(openApi) shouldContain "/books:"
    }

    test("implement the generated server in application source") {
        BooksHandler().list() shouldBe
            ListResponse.Ok(
                body = listOf(Book(isbn = "978-0-575-07065-3", title = "The Left Hand of Darkness"))
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
            ListResponse.Ok(
                body = listOf(Book(isbn = "978-0-575-07065-3", title = "The Left Hand of Darkness"))
            )
        server.verify()
    }
})
