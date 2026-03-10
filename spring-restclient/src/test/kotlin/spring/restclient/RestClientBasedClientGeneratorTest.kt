package dev.akif.tapik.spring.restclient

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
            File(rootDir, "dev/akif/tapik/clients/generated/UserEndpointsClient.kt")
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
        assertTrue(imports.contains("import dev.akif.tapik.encodeInputHeaders"))
        assertTrue(imports.contains("import dev.akif.tapik.spring.restclient.toStatus"))
        assertTrue(imports.contains("import dev.akif.tapik.clients.UserEndpoints"))
        assertTrue(content.contains("interface UserEndpointsClient : UserEndpoints.User.Client"))
        assertTrue(content.contains("interface UserEndpoints {"))
        assertTrue(content.contains("interface User {"))
        assertTrue(content.contains("data class Ok("))
        assertTrue(content.contains("val location: java.net.URI"))
        assertTrue(content.contains("interface Client : dev.akif.tapik.spring.restclient.RestClientBasedClient"))
        assertTrue(content.contains("fun user("))
        assertTrue(content.contains("): Response {"))
        assertTrue(content.contains("return Response.Ok("))
        assertTrue(content.contains("uri = dev.akif.tapik.renderURI("))
    }

    @Test
    fun `generate sanitizes conflicting identifiers`() {
        val rootDir = tempDir.toFile()

        RestClientBasedClientGenerator().generate(
            endpoints = listOf(metadataWithChallengingNames()),
            context = testContext(rootDir)
        )

        val generated =
            File(rootDir, "dev/akif/tapik/clients/generated/WildEndpointsClient.kt")
        assertTrue(
            generated.exists(),
            "Expected generated interface file, files: ${File(generated.parent).list()?.toList()}"
        )

        val content = generated.readText()
        assertTrue(content.contains("import dev.akif.tapik.spring.restclient.toStatus"))
        assertTrue(content.contains("import dev.akif.tapik.encodeInputHeaders"))
        assertTrue(content.contains("import dev.akif.tapik.clients.WildEndpoints"))
        assertTrue(content.contains("interface WildEndpointsClient : WildEndpoints.Wild.Client"))
        assertTrue(content.contains("fun wildEndpoint("))
        assertTrue(content.contains("value1stId: kotlin.Int"))
        assertTrue(
            content.contains(
                "`class`: kotlin.String? = WildEndpoints.wild.parameters.item2.asQueryParameter<kotlin.String>().default.getOrNull()"
            )
        )
        assertTrue(content.contains("uri = dev.akif.tapik.renderURI("))
        assertTrue(
            content.contains(
                "WildEndpoints.wild.parameters.item1 to WildEndpoints.wild.parameters.item1.codec.encode(value1stId)"
            )
        )
        assertTrue(content.contains("WildEndpoints.wild.input.encodeInputHeaders(xTraceId, xTraceId2)"))
        assertTrue(content.contains("data class Created("))
    }

    @Test
    fun `generate keeps required query parameters non nullable without defaults`() {
        val rootDir = tempDir.toFile()

        RestClientBasedClientGenerator().generate(
            endpoints = listOf(metadataWithRequiredQueryParameter()),
            context = testContext(rootDir)
        )

        val generated = File(rootDir, "dev/akif/tapik/clients/generated/RequiredQueryEndpointsClient.kt")
        assertTrue(generated.exists(), "Expected generated interface file")

        val content = generated.readText()
        assertTrue(content.contains("term: kotlin.String"), "Expected non-nullable required query parameter")
        assertTrue(
            !content.contains("term: kotlin.String ="),
            "Required query parameter must not have a default assignment"
        )
    }

    @Test
    fun `generate keeps renderURI argument order aligned with generated method parameter order`() {
        val rootDir = tempDir.toFile()

        RestClientBasedClientGenerator().generate(
            endpoints = listOf(metadataWithMixedQueryOrdering()),
            context = testContext(rootDir)
        )

        val generated = File(rootDir, "dev/akif/tapik/clients/generated/MixedQueryEndpointsClient.kt")
        assertTrue(generated.exists(), "Expected generated interface file")

        val content = generated.readText()
        assertTrue(content.contains("import dev.akif.tapik.clients.MixedQueryEndpoints"))
        val optionalDeclaration =
            "optionalQ: kotlin.Int? = MixedQueryEndpoints.mixed.parameters.item1.asQueryParameter<kotlin.Int>().default.getOrNull()"
        assertTrue(
            Regex(
                """fun mixed\(\s*requiredQ: kotlin\.String,\s*${Regex.escape(optionalDeclaration)}""",
                setOf(RegexOption.DOT_MATCHES_ALL)
            ).containsMatchIn(content),
            "Expected method parameters in required-then-optional order"
        )
        assertTrue(
            Regex(
                """uri = dev\.akif\.tapik\.renderURI\(\s*MixedQueryEndpoints\.mixed\.path,\s*MixedQueryEndpoints\.mixed\.parameters\.item2 to MixedQueryEndpoints\.mixed\.parameters\.item2\.codec\.encode\(requiredQ\),\s*MixedQueryEndpoints\.mixed\.parameters\.item1 to optionalQ\?\.let \{ MixedQueryEndpoints\.mixed\.parameters\.item1\.codec\.encode\(it\) \}""",
                setOf(RegexOption.DOT_MATCHES_ALL)
            ).containsMatchIn(content),
            "Expected renderURI argument order to match method parameter order"
        )
    }

    @Test
    fun `generate uses shared sealed response hierarchy for multi output endpoints`() {
        val rootDir = tempDir.toFile()

        RestClientBasedClientGenerator().generate(
            endpoints = listOf(metadataWithMultipleOutputs()),
            context = testContext(rootDir)
        )

        val generated = File(rootDir, "dev/akif/tapik/clients/generated/UsersClient.kt")
        assertTrue(generated.exists(), "Expected generated client interface")

        val content = generated.readText()
        assertTrue(content.contains("interface UsersClient : UsersEndpoints.Create.Client"))
        assertTrue(content.contains("interface UsersEndpoints {"))
        assertTrue(content.contains("interface Create {"))
        assertTrue(content.contains("sealed class Response("))
        assertTrue(content.contains("data class Created("))
        assertTrue(content.contains("val location: java.net.URI"))
        assertTrue(content.contains("data class BadRequest("))
        assertTrue(content.contains("fun create("))
        assertTrue(content.contains("): Response {"))
        assertTrue(content.contains("Response.Created(decodedBody, location)"))
        assertTrue(content.contains("Response.BadRequest(decodedBody)"))
    }

    @Test
    fun `generate adds byte array safe equality and normalizes matcher variant names`() {
        val rootDir = tempDir.toFile()

        RestClientBasedClientGenerator().generate(
            endpoints = listOf(metadataWithPredicateRawBodyOutput()),
            context = testContext(rootDir)
        )

        val generated = File(rootDir, "dev/akif/tapik/clients/generated/ExampleClient.kt")
        assertTrue(generated.exists(), "Expected generated client interface")

        val content = generated.readText()
        assertTrue(content.contains("import dev.akif.tapik.clients.Example"))
        assertTrue(content.contains("data class Response4xx("))
        assertTrue(content.contains("if (!bytes.contentEquals(other.bytes)) return false"))
        assertTrue(content.contains("result = 31 * result + bytes.contentHashCode()"))
        assertTrue(content.contains("require(Example.test1.outputs.item2.statusMatcher(status))"))
    }

    @Test
    fun `generate uses configured client and endpoints suffixes`() {
        val rootDir = tempDir.toFile()

        RestClientBasedClientGenerator().generate(
            endpoints = listOf(sampleMetadata()),
            context =
                testContext(rootDir).copy(
                    endpointsSuffix = "Contracts",
                    generatorConfiguration =
                        dev.akif.tapik.plugin.GeneratorConfiguration(
                            clientSuffix = "Api",
                            serverSuffix = "Server"
                        )
                )
        )

        val generated = File(rootDir, "dev/akif/tapik/clients/generated/UserEndpointsApi.kt")
        assertTrue(generated.exists(), "Expected generated interface file with configured name")
        assertTrue(
            generated.readText().contains("interface UserEndpointsApi"),
            "Expected generated interface declaration to use configured suffix"
        )
        assertTrue(
            generated.readText().contains("interface UserEndpointsApi : UserEndpointsContracts.User.Api"),
            "Expected aggregate interface declaration to use configured client and endpoints suffixes"
        )
    }

    private fun testContext(rootDir: File): TapikGeneratorContext =
        TapikGeneratorContext(
            outputDirectory = rootDir,
            generatedSourcesDirectory = rootDir,
            generatedPackageName = "generated",
            endpointsSuffix = "Endpoints",
            log = {},
            logDebug = {},
            logWarn = { _, _ -> },
            generatorConfiguration = GeneratorConfiguration()
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

    private fun metadataWithMixedQueryOrdering(): HttpEndpointMetadata =
        HttpEndpointMetadata(
            id = "mixed",
            propertyName = "mixed",
            description = null,
            details = null,
            method = "GET",
            path = listOf("api", "mixed"),
            parameters =
                listOf(
                    QueryParameterMetadata(
                        name = "optionalQ",
                        type = TypeMetadata("kotlin.Int"),
                        required = false,
                        default = Some(null)
                    ),
                    QueryParameterMetadata(
                        name = "requiredQ",
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
            sourceFile = "MixedQueryEndpoints",
            rawType = "HttpEndpoint"
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
                        match = OutputMatchMetadata.Exact(dev.akif.tapik.Status.CREATED),
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
                        match = OutputMatchMetadata.Exact(dev.akif.tapik.Status.BAD_REQUEST),
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
            sourceFile = "Users",
            rawType = "HttpEndpoint"
        )

    private fun metadataWithPredicateRawBodyOutput(): HttpEndpointMetadata =
        HttpEndpointMetadata(
            id = "test1",
            propertyName = "test1",
            description = null,
            details = null,
            method = "GET",
            path = listOf("example"),
            parameters = emptyList(),
            input = InputMetadata(headers = emptyList(), body = null),
            outputs =
                listOf(
                    OutputMetadata(
                        match = OutputMatchMetadata.Exact(dev.akif.tapik.Status.OK),
                        description = "OK",
                        headers = emptyList(),
                        body =
                            BodyMetadata(
                                type = TypeMetadata("dev.akif.tapik.StringBody"),
                                name = "response",
                                mediaType = "text/plain"
                            )
                    ),
                    OutputMetadata(
                        match = OutputMatchMetadata.Described("4xx"),
                        description = "4xx",
                        headers = emptyList(),
                        body =
                            BodyMetadata(
                                type = TypeMetadata("dev.akif.tapik.RawBody"),
                                name = "bytes",
                                mediaType = "application/octet-stream"
                            )
                    )
                ),
            packageName = "dev.akif.tapik.clients",
            sourceFile = "Example",
            rawType = "HttpEndpoint"
        )
}
