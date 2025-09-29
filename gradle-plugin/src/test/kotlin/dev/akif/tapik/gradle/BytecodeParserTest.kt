package dev.akif.tapik.gradle

import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.junit.jupiter.api.Test

class BytecodeParserTest {
    @Test
    fun `parseHttpEndpoint parses HttpEndpoint signature`() {
        val signature = "()Ldev/akif/tapik/http/HttpEndpoint<Ldev/akif/tapik/tuples/Tuple0<Ldev/akif/tapik/http/Parameter<*>;>;Ldev/akif/tapik/tuples/Tuple1<Ldev/akif/tapik/http/Header<*>;Ldev/akif/tapik/http/Header<Ldev/akif/tapik/http/MediaType;>;>;Ldev/akif/tapik/http/JsonBody<Ldev/akif/app/CreateUserRequest;>;Ldev/akif/tapik/tuples/Tuple1<Ldev/akif/tapik/http/Header<*>;Ldev/akif/tapik/http/Header<Ljava/net/URI;>;>;Ldev/akif/tapik/tuples/Tuple2<Ldev/akif/tapik/http/OutputBody<*>;Ldev/akif/tapik/http/OutputBody<Ldev/akif/tapik/http/JsonBody<Ldev/akif/app/UserResponse;>;>;Ldev/akif/tapik/http/OutputBody<Ldev/akif/tapik/http/JsonBody<Ldev/akif/app/APIError;>;>;>;>;"

        val result = BytecodeParser.parseHttpEndpoint(signature, "dev/akif/app/Users", "getCreate")

        val expected = HttpEndpointDescription(
            name = "create",
            packageName = "dev.akif.app",
            file = "Users",
            parameters = TypeDescription("Parameters0"),
            inputHeaders = TypeDescription("Headers1", listOf(TypeDescription("MediaType"))),
            inputBody = TypeDescription("JsonBody", listOf(TypeDescription("CreateUserRequest"))),
            outputHeaders = TypeDescription("Headers1", listOf(TypeDescription("URI"))),
            outputBodies = TypeDescription(
                "OutputBodies2",
                listOf(
                    TypeDescription("JsonBody", listOf(TypeDescription("UserResponse"))),
                    TypeDescription("JsonBody", listOf(TypeDescription("APIError")))
                )
            ),
            imports = listOf(
                "dev.akif.app.APIError",
                "dev.akif.app.CreateUserRequest",
                "dev.akif.app.UserResponse",
                "dev.akif.tapik.http.Headers1",
                "dev.akif.tapik.http.HttpEndpoint",
                "dev.akif.tapik.http.JsonBody",
                "dev.akif.tapik.http.MediaType",
                "dev.akif.tapik.http.OutputBodies2",
                "dev.akif.tapik.http.Parameters0",
                "java.net.URI"
            ),
            rawType = "HttpEndpoint<Parameters0, Headers1<MediaType>, JsonBody<CreateUserRequest>, Headers1<URI>, OutputBodies2<JsonBody<UserResponse>, JsonBody<APIError>>>"
        )

        assertNotNull(result)
        assertEquals(expected, result)
    }

    @Test
    fun `parseHttpEndpoint returns null for non HttpEndpoint signature`() {
        val signature = "()Ljava/lang/String;"

        val result = BytecodeParser.parseHttpEndpoint(signature, "dev/akif/app/Users", "getCreate")

        assertNull(result)
    }
}
