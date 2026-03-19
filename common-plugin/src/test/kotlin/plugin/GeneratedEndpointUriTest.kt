package dev.akif.tapik.plugin

import arrow.core.Some
import dev.akif.tapik.Status
import dev.akif.tapik.plugin.metadata.BodyMetadata
import dev.akif.tapik.plugin.metadata.HttpEndpointMetadata
import dev.akif.tapik.plugin.metadata.InputMetadata
import dev.akif.tapik.plugin.metadata.OutputMatchMetadata
import dev.akif.tapik.plugin.metadata.OutputMetadata
import dev.akif.tapik.plugin.metadata.PathVariableMetadata
import dev.akif.tapik.plugin.metadata.QueryParameterMetadata
import dev.akif.tapik.plugin.metadata.TypeMetadata
import kotlin.test.Test
import kotlin.test.assertTrue

class GeneratedEndpointUriTest {
    @Test
    fun `renderMergedKotlinEndpointFile generates companion uri helper with ordered parameters`() {
        val endpoint =
            HttpEndpointMetadata(
                id = "user",
                propertyName = "user",
                description = null,
                details = null,
                method = "GET",
                path = listOf("api", "users", "{userId}"),
                parameters =
                    listOf(
                        QueryParameterMetadata(
                            name = "page",
                            type = TypeMetadata("kotlin.Int"),
                            required = false,
                            default = Some("1")
                        ),
                        PathVariableMetadata(
                            name = "userId",
                            type = TypeMetadata("java.util.UUID")
                        ),
                        QueryParameterMetadata(
                            name = "search-term",
                            type = TypeMetadata("kotlin.String"),
                            required = false,
                            default = Some(null)
                        )
                    ),
                input = InputMetadata(headers = emptyList(), body = null),
                outputs =
                    listOf(
                        OutputMetadata(
                            match = OutputMatchMetadata.Exact(Status.Ok),
                            description = "OK",
                            headers = emptyList(),
                            body = BodyMetadata(type = TypeMetadata("dev.akif.tapik.EmptyBody"))
                        )
                    ),
                packageName = "dev.akif.tapik.clients",
                sourceFile = "User"
            )

        val rendered =
            renderMergedKotlinEndpointFile(
                packageName = "dev.akif.tapik.clients.generated",
                sourcePackageName = endpoint.packageName,
                sourceFile = endpoint.sourceFile,
                endpointsSuffix = "Endpoints",
                endpoints = listOf(endpoint),
                aggregateInterfaces = emptyList(),
                endpointMembers = emptyMap(),
                imports = emptySet(),
                topLevelDeclarations = emptyList()
            )

        assertTrue(rendered.contains("companion object {"))
        assertTrue(
            Regex(
                """fun uri\(\s*userId: java\.util\.UUID,\s*page: kotlin\.Int = User\.user\.parameters\.item1\.asQueryParameter<kotlin\.Int>\(\)\.getDefaultOrFail\(\),\s*searchTerm: kotlin\.String\? = User\.user\.parameters\.item3\.asQueryParameter<kotlin\.String>\(\)\.default\.getOrNull\(\)""",
                setOf(RegexOption.DOT_MATCHES_ALL)
            ).containsMatchIn(rendered),
            "Expected required path variables before optional query parameters with generated defaults"
        )
        assertTrue(rendered.contains("import dev.akif.tapik.asQueryParameter"))
        assertTrue(rendered.contains("import dev.akif.tapik.getDefaultOrFail"))
        assertTrue(rendered.contains("User.user.parameters.item2 to User.user.parameters.item2.codec.encode(userId)"))
        assertTrue(rendered.contains("User.user.parameters.item1 to User.user.parameters.item1.codec.encode(page)"))
        assertTrue(
            rendered.contains(
                "User.user.parameters.item3 to searchTerm?.let { User.user.parameters.item3.codec.encode(it) }"
            )
        )
    }

    @Test
    fun `renderMergedKotlinEndpointFile generates zero argument uri helper for parameterless endpoints`() {
        val endpoint =
            HttpEndpointMetadata(
                id = "health",
                propertyName = "health",
                description = null,
                details = null,
                method = "GET",
                path = listOf("health"),
                parameters = emptyList(),
                input = InputMetadata(headers = emptyList(), body = null),
                outputs =
                    listOf(
                        OutputMetadata(
                            match = OutputMatchMetadata.Exact(Status.Ok),
                            description = "OK",
                            headers = emptyList(),
                            body = BodyMetadata(type = TypeMetadata("dev.akif.tapik.EmptyBody"))
                        )
                    ),
                packageName = "dev.akif.tapik.clients",
                sourceFile = "Health"
            )

        val rendered =
            renderMergedKotlinEndpointFile(
                packageName = "dev.akif.tapik.clients.generated",
                sourcePackageName = endpoint.packageName,
                sourceFile = endpoint.sourceFile,
                endpointsSuffix = "Endpoints",
                endpoints = listOf(endpoint),
                aggregateInterfaces = emptyList(),
                endpointMembers = emptyMap(),
                imports = emptySet(),
                topLevelDeclarations = emptyList()
            )

        assertTrue(
            rendered.contains(
                "fun uri(): java.net.URI = dev.akif.tapik.renderURI(Health.health.path)"
            )
        )
    }
}
