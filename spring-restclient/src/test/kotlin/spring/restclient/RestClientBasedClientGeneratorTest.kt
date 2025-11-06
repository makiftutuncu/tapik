package dev.akif.tapik.spring.restclient

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
        val expected =
            """
            |package dev.akif.tapik.clients
            |
            |import arrow.core.getOrElse
            |import dev.akif.tapik.*
            |import dev.akif.tapik.spring.restclient.*
            |import java.net.URI
            |import java.util.UUID
            |
            |// Generated from: dev.akif.tapik.clients.UserEndpoints
            |interface UserEndpointsClient : RestClientBasedClient {
            |    /**
            |     * Get user by id.
            |     *
            |     * Detailed documentation for the endpoint.
            |     */
            |    fun user(
|        userId: UUID,
|        xRequestId: String,
|        page: Int = UserEndpoints.user.parameters.item2.asQueryParameter<Int>().getDefaultOrFail()
            |    ): Response1<String, URI> {
            |        val responseEntity = interpreter.send(
|            method = UserEndpoints.user.method,
|            uri = UserEndpoints.user.toURI(userId, page),
            |            inputHeaders = UserEndpoints.user.input.encodeInputHeaders(xRequestId),
            |            inputBodyContentType = UserEndpoints.user.input.body.mediaType,
            |            inputBody = ByteArray(0),
            |            outputs = UserEndpoints.user.outputs.toList()
            |        )
            |
            |        val status = responseEntity.statusCode.toStatus()
            |
            |        val headers = responseEntity.headers
            |            .mapValues { entry -> entry.value.map { it.orEmpty() } }
            |
            |        val bodyBytes = responseEntity.body ?: ByteArray(0)
            |
            |        val decodedOutput1Headers = decodeHeaders1(
            |            headers,
            |            UserEndpoints.user.outputs.item1.headers.item1
            |        ).getOrElse {
            |            error("Cannot decode headers: " + it.joinToString(": ") )
            |        }
            |        val location = decodedOutput1Headers.item1.values
            |
            |        val decodedBody = UserEndpoints.user.outputs.item1.body.codec.decode(bodyBytes)
            |            .getOrElse { error(it.joinToString(": ") ) }
            |
            |        return Response1(status, decodedBody, location)
            |    }
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
        assertTrue(content.contains("fun wildEndpoint("))
        assertTrue(content.contains("value1stId: Int"))
        assertTrue(content.contains("`class`: String"))
        assertTrue(content.contains("WildEndpoints.wild.input.encodeInputHeaders(xTraceId, xTraceId2)"))
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
                        default = "1"
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
            imports =
                listOf(
                    "java.util.UUID",
                    "java.net.URI",
                    "dev.akif.tapik.StringBody"
                ),
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
                        required = true,
                        default = null
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
            imports =
                listOf(
                    "kotlin.String",
                    "kotlin.Int",
                    "dev.akif.tapik.StringBody"
                ),
            rawType = "HttpEndpoint"
        )
}
