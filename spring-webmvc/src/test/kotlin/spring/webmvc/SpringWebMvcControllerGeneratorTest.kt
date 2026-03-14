package dev.akif.tapik.spring.webmvc

import arrow.core.None
import arrow.core.Some
import dev.akif.tapik.plugin.GeneratorConfiguration
import dev.akif.tapik.plugin.TapikGeneratorContext
import dev.akif.tapik.plugin.TapikLogger
import dev.akif.tapik.plugin.metadata.*
import dev.akif.tapik.plugin.writeMergedKotlinSourceFiles
import org.junit.jupiter.api.io.CleanupMode
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.test.Test
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

        val generated = generate(listOf(sampleMetadata()), testContext(rootDir))
        assertTrue(generated.exists(), "Expected generated controller file")

        val content = generated.readText().trim()
        assertTrue(content.contains("import dev.akif.tapik.clients.UserEndpoints"))
        assertTrue(content.contains("interface UserEndpointsServer : UserEndpoints.User.Server"))
        assertTrue(content.contains("interface UserEndpoints {"))
        assertTrue(content.contains("interface User {"))
        assertTrue(content.contains("data class Ok("))
        assertTrue(content.contains("val location: java.net.URI"))
        assertTrue(content.contains("interface Server {"))
        assertTrue(
            content.contains(
                "@org.springframework.web.bind.annotation.GetMapping(path = [\"/api/users/{userId}\"], produces = [\"text/plain\"])"
            )
        )
        assertTrue(content.contains("fun user("))
        assertTrue(content.contains("): Response"))
    }

    @Test
    fun `generate sanitizes identifiers and supports non standard methods`() {
        val rootDir = tempDir.toFile()

        val generated = generate(listOf(metadataWithChallengingNames()), testContext(rootDir))
        assertTrue(generated.exists(), "Expected generated controller file")

        val content = generated.readText().trim()
        assertTrue(content.contains("import dev.akif.tapik.clients.StatusEndpoints"))
        assertTrue(content.contains("interface StatusEndpointsServer : StatusEndpoints.HeadStatus.Server"))
        assertTrue(content.contains("data class NoContent("))
        assertTrue(
            content.contains(
                "@org.springframework.web.bind.annotation.RequestMapping(method = [org.springframework.web.bind.annotation.RequestMethod.HEAD], path = [\"/api/status/{id}\"])"
            )
        )
        assertTrue(content.contains("fun headStatus("))
        assertTrue(content.contains("): Response"))
    }

    @Test
    fun `generate keeps nested parameter types with fully qualified references`() {
        val rootDir = tempDir.toFile()

        val generated = generate(listOf(metadataWithNestedParameterType()), testContext(rootDir))
        assertTrue(generated.exists(), "Expected generated controller file")

        val content = generated.readText().trim()
        assertTrue(content.contains("import dev.akif.tapik.api.BookEndpoints"))
        assertTrue(
            content.contains(
                "@org.springframework.web.bind.annotation.RequestParam(name = \"isbn\", required = false) isbn: dev.akif.tapik.books.Book.Isbn? = BookEndpoints.list.parameters.item1.asQueryParameter<dev.akif.tapik.books.Book.Isbn>().default.getOrNull()"
            ),
            "Expected nested type to be fully qualified and nullable"
        )
        assertTrue(content.contains("interface BookEndpointsServer"), "Expected controller interface")
    }

    @Test
    fun `generate keeps required query parameters non nullable with default expression`() {
        val rootDir = tempDir.toFile()

        val generated = generate(listOf(metadataWithRequiredQueryParameter()), testContext(rootDir))
        assertTrue(generated.exists(), "Expected generated controller file")

        val content = generated.readText()
        assertTrue(content.contains("import dev.akif.tapik.clients.RequiredQueryEndpoints"))
        assertTrue(
            content.contains(
                "@org.springframework.web.bind.annotation.RequestParam(name = \"term\", required = true) term: kotlin.String = RequiredQueryEndpoints.requiredQuery.parameters.item1.asQueryParameter<kotlin.String>().getDefaultOrFail()"
            ),
            "Expected required query parameter to remain non-nullable with a default expression"
        )
    }

    @Test
    fun `generate uses configured server and endpoints suffixes`() {
        val rootDir = tempDir.toFile()

        val generated =
            generate(
                listOf(sampleMetadata()),
                testContext(rootDir).copy(
                    endpointsSuffix = "Contracts",
                    generatorConfiguration =
                        dev.akif.tapik.plugin.GeneratorConfiguration(
                            clientSuffix = "Client",
                            serverSuffix = "Api"
                        )
                )
            )

        assertTrue(generated.exists(), "Expected generated controller file with configured name")
        assertTrue(
            generated.readText().contains("interface UserEndpointsApi"),
            "Expected generated controller interface declaration to use configured suffix"
        )
        assertTrue(
            generated.readText().contains("interface UserEndpointsApi : UserEndpointsContracts.User.Api"),
            "Expected aggregate controller interface declaration to use configured server and endpoints suffixes"
        )
    }

    @Test
    fun `generate uses shared sealed response hierarchy for multi output endpoints`() {
        val rootDir = tempDir.toFile()

        val generated = generate(listOf(metadataWithMultipleOutputs()), testContext(rootDir))
        assertTrue(generated.exists(), "Expected generated controller file")

        val content = generated.readText()
        assertTrue(content.contains("interface UsersServer : UsersEndpoints.Create.Server"))
        assertTrue(content.contains("interface UsersEndpoints {"))
        assertTrue(content.contains("interface Create {"))
        assertTrue(content.contains("sealed class Response("))
        assertTrue(content.contains("data class Created("))
        assertTrue(content.contains("val location: java.net.URI"))
        assertTrue(content.contains("data class BadRequest("))
        assertTrue(
            content.contains(
                "@org.springframework.web.bind.annotation.PostMapping(path = [\"/api/v1/users\"], consumes = [\"application/json\"], produces = [\"application/json\"])"
            )
        )
        assertTrue(content.contains("fun create("))
        assertTrue(content.contains("): Response"))
    }

    private fun testContext(rootDir: File): TapikGeneratorContext =
        TapikGeneratorContext(
            outputDirectory = rootDir,
            generatedSourcesDirectory = rootDir,
            generatedPackageName = "generated",
            endpointsSuffix = "Endpoints",
            logger = TapikLogger.Console,
            generatorConfiguration = GeneratorConfiguration()
        )

    private fun generate(
        endpoints: List<HttpEndpointMetadata>,
        context: TapikGeneratorContext
    ): File {
        val generator = SpringWebMvcControllerGenerator()
        val contribution = generator.contribute(endpoints, context)
        return writeMergedKotlinSourceFiles(
            endpoints = endpoints,
            sourceFiles = contribution.sourceFiles,
            generatedSourcesDirectory = context.generatedSourcesDirectory,
            endpointsSuffix = context.endpointsSuffix,
            logWarn = { _, _ -> }
        ).single()
    }

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
            sourceFile = "UserEndpoints"
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
            sourceFile = "StatusEndpoints"
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
            sourceFile = "BookEndpoints"
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
            sourceFile = "RequiredQueryEndpoints"
        )

    private fun metadataWithMultipleOutputs(): HttpEndpointMetadata =
        HttpEndpointMetadata(
            id = "create",
            propertyName = "create",
            description = "Create new user",
            details = "This endpoint creates a new user with given information.",
            method = "POST",
            path = listOf("api", "v1", "users"),
            parameters = emptyList(),
            input =
                InputMetadata(
                    headers =
                        listOf(
                            HeaderMetadata(
                                name = "Accept",
                                type = TypeMetadata("dev.akif.tapik.MediaType"),
                                required = false,
                                values = listOf("application/json", "text/plain")
                            )
                        ),
                    body =
                        BodyMetadata(
                            type =
                                TypeMetadata(
                                    "dev.akif.tapik.JsonBody",
                                    arguments = listOf(TypeMetadata("dev.akif.tapik.clients.CreateUserRequest"))
                                ),
                            name = "createUserRequest",
                            mediaType = "application/json"
                        )
                ),
            outputs =
                listOf(
                    OutputMetadata(
                        match = OutputMatchMetadata.Exact(dev.akif.tapik.Status.Created),
                        description = "Created",
                        headers =
                            listOf(
                                HeaderMetadata(
                                    name = "Location",
                                    type = TypeMetadata("java.net.URI")
                                )
                            ),
                        body =
                            BodyMetadata(
                                type =
                                    TypeMetadata(
                                        "dev.akif.tapik.JsonBody",
                                        arguments = listOf(TypeMetadata("dev.akif.tapik.clients.UserResponse"))
                                    ),
                                name = "response",
                                mediaType = "application/json"
                            )
                    ),
                    OutputMetadata(
                        match = OutputMatchMetadata.Exact(dev.akif.tapik.Status.BadRequest),
                        description = "Bad Request",
                        headers = emptyList(),
                        body =
                            BodyMetadata(
                                type =
                                    TypeMetadata(
                                        "dev.akif.tapik.JsonBody",
                                        arguments = listOf(TypeMetadata("dev.akif.tapik.clients.APIError"))
                                    ),
                                name = "error",
                                mediaType = "application/json"
                            )
                    )
                ),
            packageName = "dev.akif.tapik.clients",
            sourceFile = "Users"
        )
}
