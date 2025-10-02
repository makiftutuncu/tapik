package dev.akif.app

import dev.akif.tapik.spring.restclient.*
import org.springframework.web.client.RestClient

fun main() {
    val interpreter = RestClientInterpreter(RestClient.create("http://localhost:8080"))

//    val response =
//        with(interpreter) {
//            Users.get.sendWithRestClient(id = 1L, Proxied = false)
//        }
//
//    println(response)
}
