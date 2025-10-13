package dev.akif.app

import dev.akif.tapik.spring.restclient.*
import org.springframework.web.client.RestClient

fun main() {
    val interpreter = RestClientInterpreter(RestClient.create("http://localhost:8080"))
//    val response = Users.get.sendWithRestClient(interpreter = interpreter, id = 1L, proxied = false)
//    println(response)
}
