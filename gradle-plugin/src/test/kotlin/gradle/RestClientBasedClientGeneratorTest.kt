package dev.akif.tapik.gradle

import dev.akif.tapik.gradle.metadata.BodyMetadata
import dev.akif.tapik.gradle.metadata.HeaderMetadata
import dev.akif.tapik.gradle.metadata.HttpEndpointMetadata
import dev.akif.tapik.gradle.metadata.OutputBodyMetadata
import dev.akif.tapik.gradle.metadata.PathVariableMetadata
import dev.akif.tapik.gradle.metadata.QueryParameterMetadata
import dev.akif.tapik.gradle.metadata.TypeMetadata
import org.junit.jupiter.api.io.CleanupMode
import java.io.File
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.jupiter.api.io.TempDir

class RestClientBasedClientGeneratorTest {
    companion object {
        @JvmStatic
        @TempDir(cleanup = CleanupMode.ALWAYS)
        lateinit var tempDir: Path
    }

    @Test
    fun `generate writes documented client interface`() {
        val rootDir = tempDir.toFile()

        RestClientBasedClientGenerator.generate(
            endpoints = listOf(sampleMetadata()),
            rootDir = rootDir
        )

        val generated =
            File(rootDir, "dev/akif/tapik/clients/UserEndpointsClient.kt")
        assertTrue(generated.exists(), "Expected generated interface file, files: ${tempDir.toFile().list().toList()}")

        val content = generated.readText().trim()
        val expected =
            """
            package dev.akif.tapik.clients

            import arrow.core.getOrElse
            import arrow.core.leftNel
            import dev.akif.tapik.*
            import dev.akif.tapik.http.*
            import dev.akif.tapik.spring.restclient.*
            import java.net.URI
            import java.util.UUID

            // Generated from: dev.akif.tapik.clients.UserEndpoints
            interface UserEndpointsClient : RestClientBasedClient {
                /**
                 * Get user by id.
                 *
                 * Detailed documentation for the endpoint.
                 */
                fun user(
                    userId: UUID,
                    X_Request_ID: String,
                    page: Int = UserEndpoints.user.uriWithParameters.parameters.item2.asQueryParameter<Int>().getDefaultOrFail()
                ): Response1<String, URI> {
                    val responseEntity = interpreter.send(
                        method = UserEndpoints.user.method,
                        uri = UserEndpoints.user.uriWithParameters.toURI(userId, page),
                        inputHeaders = UserEndpoints.user.encodeInputHeaders(X_Request_ID),
                        inputBodyContentType = UserEndpoints.user.inputBody.mediaType,
                        inputBody = ByteArray(0),
                        outputBodies = UserEndpoints.user.outputBodies.toList()
                    )

                    val status = responseEntity.statusCode.toStatus()

                    val headers = responseEntity.headers
                        .mapValues { entry -> entry.value.map { it.orEmpty() } }

                    val decodedOutputHeaders = decodeHeaders1(
                        headers,
                        UserEndpoints.user.outputHeaders.item1
                    ).getOrElse {
                        error("Cannot decode headers: " + it.joinToString(": ") )
                    }
                    val Location = decodedOutputHeaders.item1

                    val bodyBytes = responseEntity.body ?: ByteArray(0)
                    val decoded = UserEndpoints.user.outputBodies.item1.body.codec.decode(bodyBytes)
                        .map { decodedBody ->
                            Response1(
                                status,
                                decodedBody,
                                Location
                            )
                        }
                    return decoded.getOrElse { error(it.joinToString(": ") ) }

                }
            }
            """.trimIndent()

        assertEquals(expected, content)
    }

    @Test
    fun `generate sanitizes conflicting identifiers`() {
        val rootDir = tempDir.toFile()

        RestClientBasedClientGenerator.generate(
            endpoints = listOf(metadataWithChallengingNames()),
            rootDir = rootDir
        )

        val generated =
            File(rootDir, "dev/akif/tapik/clients/WildEndpointsClient.kt")
        assertTrue(generated.exists(), "Expected generated interface file, files: ${tempDir.toFile().list().toList()}")

        val content = generated.readText()
        assertTrue(content.contains("fun `wild endpoint`("))
        assertTrue(content.contains("_1st_id: Int"))
        assertTrue(content.contains("`class`: String"))
        assertTrue(content.contains("wildEndpoints.wild.encodeInputHeaders(X_Trace_Id, X_Trace_Id_2)"))
    }

    private fun sampleMetadata(): HttpEndpointMetadata =
        HttpEndpointMetadata(
            id = "user",
            propertyName = "user",
            description = "Get user by id.",
            details = "Detailed documentation for the endpoint.",
            method = "GET",
            uri = listOf("api", "users", "{userId}"),
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
            inputHeaders =
                listOf(
                    HeaderMetadata(
                        name = "X-Request-ID",
                        type = TypeMetadata("kotlin.String"),
                        required = true,
                        values = emptyList()
                    )
                ),
            inputBody = null,
            outputHeaders =
                listOf(
                    HeaderMetadata(
                        name = "Location",
                        type = TypeMetadata("java.net.URI"),
                        required = false,
                        values = emptyList()
                    )
                ),
            outputBodies =
                listOf(
                    OutputBodyMetadata(
                        description = "OK",
                        body =
                            BodyMetadata(
                                type = TypeMetadata("dev.akif.tapik.http.StringBody"),
                                name = null,
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
                    "dev.akif.tapik.http.StringBody"
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
            uri = listOf("api", "v1", "{1st-id}"),
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
            inputHeaders =
                listOf(
                    HeaderMetadata("X-Trace-Id", TypeMetadata("kotlin.String"), required = true, values = emptyList()),
                    HeaderMetadata("X Trace Id", TypeMetadata("kotlin.String"), required = false, values = emptyList())
                ),
            inputBody =
                BodyMetadata(
                    type = TypeMetadata("dev.akif.tapik.http.StringBody"),
                    name = null,
                    mediaType = "text/plain"
                ),
            outputHeaders = emptyList(),
            outputBodies =
                listOf(
                    OutputBodyMetadata(
                        description = "Created",
                        body =
                            BodyMetadata(
                                type = TypeMetadata("dev.akif.tapik.http.StringBody"),
                                name = null,
                                mediaType = "text/plain"
                            )
                    )
                ),
            packageName = "dev.akif.tapik.clients",
            sourceFile = "wildEndpoints",
            imports =
                listOf(
                    "kotlin.String",
                    "kotlin.Int",
                    "dev.akif.tapik.http.StringBody"
                ),
            rawType = "HttpEndpoint"
        )
}
