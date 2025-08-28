package dev.akif.app

import dev.akif.tapik.http.MediaType
import dev.akif.tapik.spring.restclient.*
import org.springframework.web.client.RestClient

fun main() {
    val interpreter = RestClientInterpreter(RestClient.create("http://localhost:8080"))

    val response = with(interpreter) {
        Users.create.send(MediaType.Json, CreateUserRequest("Akif", "new"))
    }

    response.handle(
        { (_, body) -> println("Got: $body") },
        { (status, problem) -> println("Error $status: $problem") }
    )
}
