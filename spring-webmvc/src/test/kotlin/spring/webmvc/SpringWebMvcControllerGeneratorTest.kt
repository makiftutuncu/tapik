package dev.akif.tapik.spring.webmvc

import dev.akif.tapik.plugin.TapikGeneratorContext
import dev.akif.tapik.plugin.metadata.*
import org.junit.jupiter.api.io.CleanupMode
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SpringWebMvcControllerGeneratorTest {
    companion object {
        @JvmStatic
        @TempDir(cleanup = CleanupMode.ALWAYS)
        lateinit var tempDir: Path
    }

    @Test
    fun `generate writes documented controller interface`() {
        val rootDir = tempDir.toFile()

        SpringWebMvcControllerGenerator().generate(
            endpoints = listOf(sampleMetadata()),
            context = testContext(rootDir)
        )

        val generated = File(rootDir, "dev/akif/tapik/clients/UserEndpointsController.kt")
        assertTrue(generated.exists(), "Expected generated controller file")

        val content = generated.readText().trim()
        val expected =
            """
            |package dev.akif.tapik.clients
            |
            |import dev.akif.tapik.*
|import java.net.URI
|import java.util.UUID
|import org.springframework.web.bind.annotation.*
|import org.springframework.web.bind.annotation.PathVariable as SpringPathVariable
            |
            |// Generated from: dev.akif.tapik.clients.UserEndpoints
            |interface UserEndpointsController {
            |    /**
            |     * Get user by id.
            |     *
            |     * Detailed documentation for the endpoint.
            |     */
            |    @GetMapping(path = ["/api/users/{userId}"], produces = ["text/plain"])
            |    fun user(
|        @SpringPathVariable(name = "userId") userId: UUID,
            |        @RequestParam(name = "page", required = false, defaultValue = "1") page: Int,
            |        @RequestHeader(name = "X-Request-ID") xRequestId: String
            |    ): Response1<String, URI>
            |}
            """.trimMargin()

        assertEquals(expected, content)
    }

    @Test
    fun `generate sanitizes identifiers and supports non standard methods`() {
        val rootDir = tempDir.toFile()

        SpringWebMvcControllerGenerator().generate(
            endpoints = listOf(metadataWithChallengingNames()),
            context = testContext(rootDir)
        )

        val generated = File(rootDir, "dev/akif/tapik/clients/StatusEndpointsController.kt")
        assertTrue(generated.exists(), "Expected generated controller file")

        val content = generated.readText().trim()
        val expected =
            """
            |package dev.akif.tapik.clients
            |
            |import dev.akif.tapik.*
|import java.util.UUID
|import org.springframework.web.bind.annotation.*
|import org.springframework.web.bind.annotation.PathVariable as SpringPathVariable
            |
            |// Generated from: dev.akif.tapik.clients.StatusEndpoints
            |interface StatusEndpointsController {
            |    /**
            |     * HEAD endpoint with optional pieces.
            |     */
            |    @RequestMapping(method = [RequestMethod.HEAD], path = ["/api/status/{id}"])
            |    fun headStatus(
|        @SpringPathVariable(name = "id") id: UUID,
            |        @RequestParam(name = "trace-id", required = false, defaultValue = "trace-default") traceId: String,
            |        @RequestHeader(name = "X-Session-ID", required = false) xSessionId: UUID
            |    ): ResponseWithoutBody0
            |}
            """.trimMargin()

        assertEquals(expected, content)
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
            id = "head status",
            propertyName = "headStatus",
            description = "HEAD endpoint with optional pieces.",
            details = null,
            method = "HEAD",
            path = listOf("api", "status", "{id}"),
            parameters =
                listOf(
                    PathVariableMetadata(
                        name = "id",
                        type = TypeMetadata("java.util.UUID")
                    ),
                    QueryParameterMetadata(
                        name = "trace-id",
                        type = TypeMetadata("kotlin.String"),
                        required = false,
                        default = "trace-default"
                    )
                ),
            input =
                InputMetadata(
                    headers =
                        listOf(
                            HeaderMetadata(
                                name = "X-Session-ID",
                                type = TypeMetadata("java.util.UUID"),
                                required = false,
                                values = emptyList()
                            )
                        ),
                    body = null
                ),
            outputs =
                listOf(
                    OutputMetadata(
                        description = "No Content",
                        headers = emptyList(),
                        body =
                            BodyMetadata(
                                type = TypeMetadata("dev.akif.tapik.EmptyBody"),
                                name = "empty",
                                mediaType = null
                            )
                    )
                ),
            packageName = "dev.akif.tapik.clients",
            sourceFile = "StatusEndpoints",
            imports =
                listOf(
                    "java.util.UUID",
                    "dev.akif.tapik.EmptyBody"
                ),
            rawType = "HttpEndpoint"
        )
}
