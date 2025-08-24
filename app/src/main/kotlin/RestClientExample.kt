package dev.akif.app

import dev.akif.tapik.spring.restclient.*
import org.springframework.web.client.RestClient

fun main() {
    val interpreter = RestClientInterpreter(RestClient.create("http://localhost:8080"))

    val response = interpreter.sendWithRestClient(Users.get, 1L, false)

    response.handle(
        { (_, user) -> println("Got user: $user") },
        { (status, body) -> println("Error $status: $body") },
    )
}
