package dev.akif.tapik.spring.restclient

import dev.akif.tapik.client.Client
import org.springframework.web.client.RestClient

interface RestClientBasedClient : Client {
    val restClient: RestClient

    val interpreter: RestClientInterpreter
        get() = RestClientInterpreter(restClient)
}
