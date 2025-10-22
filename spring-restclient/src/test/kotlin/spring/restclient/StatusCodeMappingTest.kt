package dev.akif.tapik.spring.restclient

import dev.akif.tapik.Status
import org.springframework.http.HttpStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class StatusCodeMappingTest {
    @Test
    fun `Status converts to HttpStatusCode`() {
        val code = Status.CREATED.toHttpStatusCode()

        assertEquals(HttpStatus.CREATED.value(), code.value())
    }

    @Test
    fun `HttpStatus converts back to Status`() {
        assertEquals(Status.OK, HttpStatus.OK.toStatus())
    }
}
