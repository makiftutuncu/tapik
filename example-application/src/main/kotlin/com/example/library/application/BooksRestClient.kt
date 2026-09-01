package com.example.library.application

import com.example.library.contract.Books
import com.example.library.generated.BooksClient
import dev.akif.tapik.target.spring.restclient.RestClientTransport
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class BooksRestClient(
    builder: RestClient.Builder,
    @Value("\${library.books.base-url:http://localhost:8080}") baseUrl: String
) : BooksClient {
    override val booksApi: Books = Books

    override val restClientTransport: RestClientTransport =
        RestClientTransport(builder.baseUrl(baseUrl).build())
}
