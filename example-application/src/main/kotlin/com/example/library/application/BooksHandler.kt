package com.example.library.application

import com.example.library.contract.Book
import com.example.library.contract.Books
import com.example.library.generated.contract.Books.BooksServer
import com.example.library.generated.contract.Books.ListResponse
import org.springframework.stereotype.Component

@Component
class BooksHandler : BooksServer {
    override val booksApi: Books = Books

    override fun list(): ListResponse =
        ListResponse.Ok(
            body = listOf(Book(isbn = "978-0-575-07065-3", title = "The Left Hand of Darkness"))
        )
}
