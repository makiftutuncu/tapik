package com.example.library.application

import com.example.library.contract.Author
import com.example.library.contract.Authors
import com.example.library.contract.CreateAuthor
import com.example.library.generated.AuthorsServer
import org.springframework.stereotype.Component

@Component
class AuthorsHandler : AuthorsServer {
    override val authorsApi: Authors = Authors()

    override fun list(
        xRequestId: String,
        name: List<String>?,
        page: Int
    ): AuthorsServer.ListResponse =
        AuthorsServer.ListResponse.Ok(
            body = listOf(Author(id = "author-1", name = "Ursula K. Le Guin"))
        )

    override fun create(
        xRequestId: String,
        body: CreateAuthor
    ): AuthorsServer.CreateResponse =
        AuthorsServer.CreateResponse.Created(
            body = Author(id = "author-2", name = body.name),
            location = "/authors/author-2"
        )
}
