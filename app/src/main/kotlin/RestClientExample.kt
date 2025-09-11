package dev.akif.app

import dev.akif.tapik.http.*
import dev.akif.tapik.spring.restclient.*
import org.springframework.web.client.RestClient

fun main() {
    val interpreter = RestClientInterpreter(RestClient.create("http://localhost:8080"))

    val testEndpoint by http {
        put
            .uri("tests" / path.int("id"))
            .inputHeader(Header.Accept)
            .inputBody { stringBody() }
            .outputHeader(header.ContentType)
            .outputBody { stringBody() }
            .outputBody { rawBody() }
    }

//    val response = with(interpreter) {
//        testEndpoint.sendWithRestClient(1, MediaType.PlainText, "test")
//    }
//
//    println(response)
}
