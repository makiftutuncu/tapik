package dev.akif.tapik.spring.restclient

import arrow.core.None
import arrow.core.Some
import dev.akif.tapik.plugin.TapikGeneratorContext
import dev.akif.tapik.plugin.metadata.*
import org.junit.jupiter.api.io.CleanupMode
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RestClientBasedClientGeneratorTest {
    companion object {
        @JvmStatic
        @TempDir(cleanup = CleanupMode.ALWAYS)
        lateinit var tempDir: Path
    }

    @Test
    fun `generate writes documented client interface`() {
        val rootDir = tempDir.toFile()

        RestClientBasedClientGenerator().generate(
            endpoints = listOf(sampleMetadata()),
            context = testContext(rootDir)
        )

        val generated =
            File(rootDir, "dev/akif/tapik/clients/UserEndpointsClient.kt")
        assertTrue(
            generated.exists(),
            "Expected generated interface file, files: ${File(generated.parent).list()?.toList()}"
        )

        val content = generated.readText().trim()
        val imports =
            content
                .lineSequence()
                .filter { it.startsWith("import ") }
                .toList()
        assertEquals(
            listOf(
                "import dev.akif.tapik.spring.restclient.toStatus",
                "import dev.akif.tapik.encodeInputHeaders"
            ),
            imports
        )
        val expected =
            """
            |package dev.akif.tapik.clients
            |
            |import dev.akif.tapik.spring.restclient.toStatus
            |import dev.akif.tapik.encodeInputHeaders
            |
            |// Generated from: dev.akif.tapik.clients.UserEndpoints
            |interface UserEndpointsClient : dev.akif.tapik.spring.restclient.RestClientBasedClient {
            |    /**
            |     * Get user by id.
            |     *
            |     * Detailed documentation for the endpoint.
            |     */
|    fun user(
|        userId: java.util.UUID,
|        xRequestId: kotlin.String,
|        page: kotlin.Int = UserEndpoints.user.parameters.item2.asQueryParameter<kotlin.Int>().getDefaultOrFail()
            |    ): dev.akif.tapik.Response1<kotlin.String, java.net.URI> {
            |        val responseEntity = interpreter.send(
|            method = UserEndpoints.user.method,
|            uri = userToURI(userId, page),
|            inputHeaders = UserEndpoints.user.input.encodeInputHeaders(xRequestId),
            |            inputBodyContentType = UserEndpoints.user.input.body.mediaType,
            |            inputBody = kotlin.ByteArray(0),
            |            outputs = UserEndpoints.user.outputs.toList()
            |        )
            |
            |        val status = responseEntity.statusCode.toStatus()
            |
            |        val headers = responseEntity.headers
            |            .mapValues { entry -> entry.value.map { it.orEmpty() } }
            |
            |        val bodyBytes = responseEntity.body ?: kotlin.ByteArray(0)
            |
            |        val decodedOutput1HeadersResult = dev.akif.tapik.decodeHeaders1(
            |            headers,
            |            UserEndpoints.user.outputs.item1.headers.item1
            |        )
            |        val decodedOutput1Headers = when (val either = decodedOutput1HeadersResult) {
            |            is arrow.core.Either.Left -> kotlin.error("Cannot decode headers: " + either.value.joinToString(": "))
            |            is arrow.core.Either.Right -> either.value
            |        }
            |        val location = decodedOutput1Headers.item1.values
            |
            |        val decodedBodyResult = UserEndpoints.user.outputs.item1.body.codec.decode(bodyBytes)
            |        val decodedBody = when (val either = decodedBodyResult) {
            |            is arrow.core.Either.Left -> kotlin.error(either.value.joinToString(": "))
            |            is arrow.core.Either.Right -> either.value
            |        }
            |
            |        return dev.akif.tapik.Response1(status, decodedBody, location)
            |    }
            |
            |    fun userToURI(
|        userId: java.util.UUID,
|        page: kotlin.Int = UserEndpoints.user.parameters.item2.asQueryParameter<kotlin.Int>().getDefaultOrFail()
            |    ): java.net.URI =
            |        dev.akif.tapik.renderURI(
            |            UserEndpoints.user.path,
            |            UserEndpoints.user.parameters.item1 to UserEndpoints.user.parameters.item1.codec.encode(userId),
            |            UserEndpoints.user.parameters.item2 to UserEndpoints.user.parameters.item2.codec.encode(page)
            |        )
            |}
            """.trimMargin()

        assertEquals(expected, content)
    }

    @Test
    fun `generate sanitizes conflicting identifiers`() {
        val rootDir = tempDir.toFile()

        RestClientBasedClientGenerator().generate(
            endpoints = listOf(metadataWithChallengingNames()),
            context = testContext(rootDir)
        )

        val generated =
            File(rootDir, "dev/akif/tapik/clients/WildEndpointsClient.kt")
        assertTrue(
            generated.exists(),
            "Expected generated interface file, files: ${File(generated.parent).list()?.toList()}"
        )

        val content = generated.readText()
        assertTrue(content.contains("import dev.akif.tapik.spring.restclient.toStatus"))
        assertTrue(content.contains("import dev.akif.tapik.encodeInputHeaders"))
        assertTrue(content.contains("fun wildEndpoint("))
        assertTrue(content.contains("value1stId: kotlin.Int"))
        assertTrue(
            content.contains(
                "`class`: kotlin.String? = WildEndpoints.wild.parameters.item2.asQueryParameter<kotlin.String>().default.getOrNull()"
            )
        )
        assertTrue(content.contains("uri = wildEndpointToURI(value1stId, `class`)"))
        assertTrue(content.contains("fun wildEndpointToURI("))
        assertTrue(content.contains("WildEndpoints.wild.input.encodeInputHeaders(xTraceId, xTraceId2)"))
    }

    @Test
    fun `generate keeps required query parameters non nullable without defaults`() {
        val rootDir = tempDir.toFile()

        RestClientBasedClientGenerator().generate(
            endpoints = listOf(metadataWithRequiredQueryParameter()),
            context = testContext(rootDir)
        )

        val generated = File(rootDir, "dev/akif/tapik/clients/RequiredQueryEndpointsClient.kt")
        assertTrue(generated.exists(), "Expected generated interface file")

        val content = generated.readText()
        assertTrue(content.contains("term: kotlin.String"), "Expected non-nullable required query parameter")
        assertTrue(
            !content.contains("term: kotlin.String ="),
            "Required query parameter must not have a default assignment"
        )
    }

    private fun testContext(rootDir: File): TapikGeneratorContext =
        TapikGeneratorContext(
            outputDirectory = rootDir,
            generatedSourcesDirectory = rootDir,
            log = {},
            logDebug = {},
            logWarn = { _, _ -> }
        )

    private fun sampleMetadata(): HttpEndpointMetadata =
        HttpEndpointMetadata(
            id = "user",
            propertyName = "user",
            description = "Get user by id.",
            details = "Detailed documentation for the endpoint.",
            method = "GET",
            path = listOf("api", "users", "{userId}"),
            parameters =
                listOf(
                    PathVariableMetadata(
                        name = "userId",
                        type = TypeMetadata("java.util.UUID")
                    ),
                    QueryParameterMetadata(
                        name = "page",
                        type = TypeMetadata("kotlin.Int"),
                        required = false,
                        default = Some("1")
                    )
                ),
            input =
                InputMetadata(
                    headers =
                        listOf(
                            HeaderMetadata(
                                name = "X-Request-ID",
                                type = TypeMetadata("kotlin.String"),
                                required = true,
                                values = emptyList()
                            )
                        ),
                    body = null
                ),
            outputs =
                listOf(
                    OutputMetadata(
                        description = "OK",
                        headers =
                            listOf(
                                HeaderMetadata(
                                    name = "Location",
                                    type = TypeMetadata("java.net.URI"),
                                    required = false,
                                    values = emptyList()
                                )
                            ),
                        body =
                            BodyMetadata(
                                type = TypeMetadata("dev.akif.tapik.StringBody"),
                                name = "string",
                                mediaType = "text/plain"
                            )
                    )
                ),
            packageName = "dev.akif.tapik.clients",
            sourceFile = "UserEndpoints",
            rawType = "HttpEndpoint"
        )

    private fun metadataWithChallengingNames(): HttpEndpointMetadata =
        HttpEndpointMetadata(
            id = "wild endpoint",
            propertyName = "wild",
            description = "Uses challenging identifiers.",
            details = null,
            method = "POST",
            path = listOf("api", "v1", "{1st-id}"),
            parameters =
                listOf(
                    PathVariableMetadata(
                        name = "1st-id",
                        type = TypeMetadata("kotlin.Int")
                    ),
                    QueryParameterMetadata(
                        name = "class",
                        type = TypeMetadata("kotlin.String"),
                        required = false,
                        default = Some(null)
                    )
                ),
            input =
                InputMetadata(
                    headers =
                        listOf(
                            HeaderMetadata(
                                "X-Trace-Id",
                                TypeMetadata("kotlin.String"),
                                required = true,
                                values = emptyList()
                            ),
                            HeaderMetadata(
                                "X Trace Id",
                                TypeMetadata("kotlin.String"),
                                required = false,
                                values = emptyList()
                            )
                        ),
                    body =
                        BodyMetadata(
                            type = TypeMetadata("dev.akif.tapik.StringBody"),
                            name = "string",
                            mediaType = "text/plain"
                        )
                ),
            outputs =
                listOf(
                    OutputMetadata(
                        description = "Created",
                        headers = emptyList(),
                        body =
                            BodyMetadata(
                                type = TypeMetadata("dev.akif.tapik.StringBody"),
                                name = "string",
                                mediaType = "text/plain"
                            )
                    )
                ),
            packageName = "dev.akif.tapik.clients",
            sourceFile = "WildEndpoints",
            rawType = "HttpEndpoint"
        )

    private fun metadataWithRequiredQueryParameter(): HttpEndpointMetadata =
        HttpEndpointMetadata(
            id = "required query",
            propertyName = "requiredQuery",
            description = null,
            details = null,
            method = "GET",
            path = listOf("api", "search"),
            parameters =
                listOf(
                    QueryParameterMetadata(
                        name = "term",
                        type = TypeMetadata("kotlin.String"),
                        required = true,
                        default = None
                    )
                ),
            input =
                InputMetadata(
                    headers = emptyList(),
                    body = null
                ),
            outputs =
                listOf(
                    OutputMetadata(
                        description = "No Content",
                        headers = emptyList(),
                        body = BodyMetadata(TypeMetadata("dev.akif.tapik.EmptyBody"))
                    )
                ),
            packageName = "dev.akif.tapik.clients",
            sourceFile = "RequiredQueryEndpoints",
            rawType = "HttpEndpoint"
        )
}
