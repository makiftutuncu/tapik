package dev.akif.app

import dev.akif.app.example.ExampleKtClient
import org.springframework.web.client.RestClient

class Service(override val restClient: RestClient): UsersClient, ExampleKtClient

fun main() {
    val service = Service(RestClient.create("http://localhost:8080"))
    service.get(id = 1L, proxied = false).select(
        { it.body.name },
        { it.body.message }
    )
}
