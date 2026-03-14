package dev.akif.tapik.plugin

import dev.akif.tapik.Status
import dev.akif.tapik.plugin.metadata.BodyMetadata
import dev.akif.tapik.plugin.metadata.HeaderCardinality
import dev.akif.tapik.plugin.metadata.HeaderMetadata
import dev.akif.tapik.plugin.metadata.HttpEndpointMetadata
import dev.akif.tapik.plugin.metadata.InputMetadata
import dev.akif.tapik.plugin.metadata.OutputMatchMetadata
import dev.akif.tapik.plugin.metadata.OutputMetadata
import dev.akif.tapik.plugin.metadata.TypeMetadata
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EndpointContractModelTest {
    @Test
    fun `buildEndpointContractModels creates sealed response hierarchy for multiple outputs`() {
        val model =
            buildEndpointContractModels(
                endpoints = listOf(usersCreateMetadata()),
                sourceFile = "Users"
            ).single()

        assertEquals("UsersEndpoints", model.enclosingInterfaceName)
        assertEquals("Create", model.interfaceName)
        assertEquals("UsersEndpoints.Create.Response", model.response.typeName)
        assertEquals(listOf("Created", "BadRequest"), model.response.variants.map { it.typeName })
        assertEquals(Status.Created, model.response.variants.first().status)
        assertEquals(
            listOf(
                EndpointContractModel.ResponseModel.Field("response", "dev.akif.tapik.test.UserResponse"),
                EndpointContractModel.ResponseModel.Field("location", "java.net.URI")
            ),
            model.response.variants.first().fields
        )
    }

    @Test
    fun `buildEndpointContractModels keeps single output endpoints in response hierarchy`() {
        val model =
            buildEndpointContractModels(
                endpoints =
                    listOf(
                        HttpEndpointMetadata(
                            id = "list",
                            propertyName = "list",
                            description = "List users",
                            details = null,
                            method = "GET",
                            path = listOf("api", "users"),
                            parameters = emptyList(),
                            input = InputMetadata(headers = emptyList(), body = null),
                            outputs =
                                listOf(
                                    OutputMetadata(
                                        match = OutputMatchMetadata.Exact(Status.Ok),
                                        description = "OK",
                                        headers = emptyList(),
                                        body =
                                            BodyMetadata(
                                                type = TypeMetadata("dev.akif.tapik.JsonBody", arguments = listOf(TypeMetadata("dev.akif.tapik.test.UserPage"))),
                                                name = "response",
                                                mediaType = "application/json"
                                            )
                                    )
                                ),
                            packageName = "dev.akif.tapik.test",
                            sourceFile = "Users"
                        )
                    ),
                sourceFile = "Users"
            ).single()

        assertEquals("UsersEndpoints.List.Response", model.response.typeName)
        assertEquals(
            listOf(EndpointContractModel.ResponseModel.Field("response", "dev.akif.tapik.test.UserPage")),
            model.response.variants.single().fields
        )
    }

    @Test
    fun `buildEndpointContractModels renders repeated output headers as lists`() {
        val model =
            buildEndpointContractModels(
                endpoints =
                    listOf(
                        HttpEndpointMetadata(
                            id = "download",
                            propertyName = "download",
                            description = null,
                            details = null,
                            method = "GET",
                            path = listOf("files"),
                            parameters = emptyList(),
                            input = InputMetadata(headers = emptyList(), body = null),
                            outputs =
                                listOf(
                                    OutputMetadata(
                                        match = OutputMatchMetadata.Described("downloaded"),
                                        description = "downloaded",
                                        headers =
                                            listOf(
                                                HeaderMetadata(
                                                    name = "X-Trace-Id",
                                                    type = TypeMetadata("kotlin.String"),
                                                    cardinality = HeaderCardinality.Multiple
                                                )
                                            ),
                                        body = BodyMetadata(type = TypeMetadata("dev.akif.tapik.EmptyBody"))
                                    )
                                ),
                            packageName = "dev.akif.tapik.test",
                            sourceFile = "Files"
                        )
                    ),
                sourceFile = "Files"
            ).single()

        assertEquals("FilesEndpoints", model.enclosingInterfaceName)
        assertEquals("Download", model.interfaceName)
        assertEquals("FilesEndpoints.Download.Response", model.response.typeName)
        assertEquals(
            listOf(
                EndpointContractModel.ResponseModel.Field(
                    "xTraceId",
                    "kotlin.collections.List<kotlin.String>"
                )
            ),
            model.response.variants.single().fields
        )
    }

    @Test
    fun `buildEndpointContractModels prefixes matcher descriptions that start with digits`() {
        val model =
            buildEndpointContractModels(
                endpoints =
                    listOf(
                        HttpEndpointMetadata(
                            id = "test1",
                            propertyName = "test1",
                            description = null,
                            details = null,
                            method = "GET",
                            path = listOf("test1"),
                            parameters = emptyList(),
                            input = InputMetadata(headers = emptyList(), body = null),
                            outputs =
                                listOf(
                                    OutputMetadata(
                                        match = OutputMatchMetadata.Exact(Status.Ok),
                                        description = "OK",
                                        headers = emptyList(),
                                        body = BodyMetadata(type = TypeMetadata("dev.akif.tapik.StringBody"), name = "response")
                                    ),
                                    OutputMetadata(
                                        match = OutputMatchMetadata.Described("4xx"),
                                        description = "4xx",
                                        headers = emptyList(),
                                        body = BodyMetadata(type = TypeMetadata("dev.akif.tapik.RawBody"), name = "bytes")
                                    )
                                ),
                            packageName = "dev.akif.tapik.test",
                            sourceFile = "Example"
                        )
                    ),
                sourceFile = "Example"
            ).single()

        assertEquals(listOf("Ok", "Response4xx"), model.response.variants.map { it.typeName })
    }

    private fun usersCreateMetadata(): HttpEndpointMetadata =
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
                                type = TypeMetadata("dev.akif.tapik.MediaType")
                            )
                        ),
                    body =
                        BodyMetadata(
                            type = TypeMetadata("dev.akif.tapik.JsonBody", arguments = listOf(TypeMetadata("dev.akif.tapik.test.CreateUserRequest"))),
                            name = "createUserRequest",
                            mediaType = "application/json"
                        )
                ),
            outputs =
                listOf(
                    OutputMetadata(
                        match = OutputMatchMetadata.Exact(Status.Created),
                        description = "Created",
                        headers =
                            listOf(
                                HeaderMetadata(
                                    name = "Location",
                                    type = TypeMetadata("java.net.URI"),
                                    cardinality = HeaderCardinality.Single
                                )
                            ),
                        body =
                            BodyMetadata(
                                type = TypeMetadata("dev.akif.tapik.JsonBody", arguments = listOf(TypeMetadata("dev.akif.tapik.test.UserResponse"))),
                                name = "response",
                                mediaType = "application/json"
                            )
                    ),
                    OutputMetadata(
                        match = OutputMatchMetadata.Exact(Status.BadRequest),
                        description = "Bad Request",
                        headers = emptyList(),
                        body =
                            BodyMetadata(
                                type = TypeMetadata("dev.akif.tapik.JsonBody", arguments = listOf(TypeMetadata("dev.akif.tapik.test.APIError"))),
                                name = "error",
                                mediaType = "application/json"
                            )
                    )
                ),
            packageName = "dev.akif.tapik.test",
            sourceFile = "Users"
        )
}
