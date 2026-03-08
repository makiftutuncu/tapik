package dev.akif.tapik.spring.webmvc

import arrow.core.None
import arrow.core.Some
import dev.akif.tapik.plugin.GeneratorConfiguration
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
        assertTrue(content.lineSequence().none { it.startsWith("import ") })
        val expected =
            """
            |package dev.akif.tapik.clients
            |
            |// Generated from: dev.akif.tapik.clients.UserEndpoints
            |interface UserEndpointsController : dev.akif.tapik.Helpers {
            |    /**
            |     * Get user by id.
            |     *
            |     * Detailed documentation for the endpoint.
            |     */
|    @org.springframework.web.bind.annotation.GetMapping(path = ["/api/users/{userId}"], produces = ["text/plain"])
            |    fun user(
|        @org.springframework.web.bind.annotation.PathVariable(name = "userId") userId: java.util.UUID,
            |        @org.springframework.web.bind.annotation.RequestParam(name = "page", required = false) page: kotlin.Int = UserEndpoints.user.parameters.item2.asQueryParameter<kotlin.Int>().getDefaultOrFail(),
            |        @org.springframework.web.bind.annotation.RequestHeader(name = "X-Request-ID") xRequestId: kotlin.String
            |    ): dev.akif.tapik.Response1<kotlin.String, java.net.URI>
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
        assertTrue(content.lineSequence().none { it.startsWith("import ") })
        val expected =
            """
            |package dev.akif.tapik.clients
            |
            |// Generated from: dev.akif.tapik.clients.StatusEndpoints
            |interface StatusEndpointsController : dev.akif.tapik.Helpers {
            |    /**
            |     * HEAD endpoint with optional pieces.
            |     */
|    @org.springframework.web.bind.annotation.RequestMapping(method = [org.springframework.web.bind.annotation.RequestMethod.HEAD], path = ["/api/status/{id}"])
            |    fun headStatus(
|        @org.springframework.web.bind.annotation.PathVariable(name = "id") id: java.util.UUID,
            |        @org.springframework.web.bind.annotation.RequestParam(name = "trace-id", required = false) traceId: kotlin.String = StatusEndpoints.headStatus.parameters.item2.asQueryParameter<kotlin.String>().getDefaultOrFail(),
            |        @org.springframework.web.bind.annotation.RequestHeader(name = "X-Session-ID", required = false) xSessionId: java.util.UUID
            |    ): dev.akif.tapik.ResponseWithoutBody0
            |}
            """.trimMargin()

        assertEquals(expected, content)
    }

    @Test
    fun `generate keeps nested parameter types with fully qualified references`() {
        val rootDir = tempDir.toFile()

        SpringWebMvcControllerGenerator().generate(
            endpoints = listOf(metadataWithNestedParameterType()),
            context = testContext(rootDir)
        )

        val generated = File(rootDir, "dev/akif/tapik/api/BookEndpointsController.kt")
        assertTrue(generated.exists(), "Expected generated controller file")

        val content = generated.readText().trim()
        assertTrue(content.lineSequence().none { it.startsWith("import ") })
        assertTrue(
            content.contains(
                "@org.springframework.web.bind.annotation.RequestParam(name = \"isbn\", required = false) isbn: dev.akif.tapik.books.Book.Isbn? = BookEndpoints.list.parameters.item1.asQueryParameter<dev.akif.tapik.books.Book.Isbn>().default.getOrNull()"
            ),
            "Expected nested type to be fully qualified and nullable"
        )
        assertTrue(content.contains("interface BookEndpointsController"), "Expected controller interface")
    }

    @Test
    fun `generate keeps required query parameters non nullable with default expression`() {
        val rootDir = tempDir.toFile()

        SpringWebMvcControllerGenerator().generate(
            endpoints = listOf(metadataWithRequiredQueryParameter()),
            context = testContext(rootDir)
        )

        val generated = File(rootDir, "dev/akif/tapik/clients/RequiredQueryEndpointsController.kt")
        assertTrue(generated.exists(), "Expected generated controller file")

        val content = generated.readText()
        assertTrue(
            content.contains(
                "@org.springframework.web.bind.annotation.RequestParam(name = \"term\", required = true) term: kotlin.String = RequiredQueryEndpoints.requiredQuery.parameters.item1.asQueryParameter<kotlin.String>().getDefaultOrFail()"
            ),
            "Expected required query parameter to remain non-nullable with a default expression"
        )
    }

    @Test
    fun `generate uses configured name prefix and suffix for interface name`() {
        val rootDir = tempDir.toFile()

        SpringWebMvcControllerGenerator().generate(
            endpoints = listOf(sampleMetadata()),
            context =
                testContext(rootDir).copy(
                    generatorConfiguration =
                        dev.akif.tapik.plugin.GeneratorConfiguration(
                            optimizeImports = true,
                            namePrefix = "My",
                            nameSuffix = "Interface"
                        )
                )
        )

        val generated = File(rootDir, "dev/akif/tapik/clients/MyUserEndpointsInterface.kt")
        assertTrue(generated.exists(), "Expected generated controller file with configured name")
        assertTrue(
            generated.readText().contains("interface MyUserEndpointsInterface"),
            "Expected generated controller interface declaration to use configured prefix/suffix"
        )
    }

    private fun testContext(rootDir: File): TapikGeneratorContext =
        TapikGeneratorContext(
            outputDirectory = rootDir,
            generatedSourcesDirectory = rootDir,
            log = {},
            logDebug = {},
            logWarn = { _, _ -> },
            generatorConfiguration =
                GeneratorConfiguration(
                    optimizeImports = true,
                    namePrefix = null,
                    nameSuffix = null
                )
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
                        default = Some("trace-default")
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
            rawType = "HttpEndpoint"
        )

    private fun metadataWithNestedParameterType(): HttpEndpointMetadata =
        HttpEndpointMetadata(
            id = "list",
            propertyName = "list",
            description = null,
            details = null,
            method = "GET",
            path = listOf("books"),
            parameters =
                listOf(
                    QueryParameterMetadata(
                        name = "isbn",
                        type = TypeMetadata("dev.akif.tapik.books.Book.Isbn"),
                        required = false,
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
            packageName = "dev.akif.tapik.api",
            sourceFile = "BookEndpoints",
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
