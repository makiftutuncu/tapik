package dev.akif.tapik.openapi

import dev.akif.tapik.Method
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Renders this document as deterministic JSON.
 *
 * @param pretty whether to indent the rendered JSON.
 * @return the complete OpenAPI document as JSON.
 */
fun OpenApiDocument.toJson(pretty: Boolean = true): String =
    Json { prettyPrint = pretty }.encodeToString(JsonElement.serializer(), json())

private fun OpenApiDocument.json(): JsonObject =
    buildJsonObject {
        put("openapi", specificationVersion)
        put("info", info.json())
        put(
            "paths",
            jsonObject(paths.mapValues { (_, pathItem) -> pathItem.json() })
        )
        if (components.schemas.isNotEmpty()) put("components", components.json())
    }

private fun OpenApiInfo.json(): JsonObject =
    buildJsonObject {
        put("title", title)
        summary?.let { put("summary", it) }
        description?.let { put("description", it) }
        put("version", version)
    }

private fun OpenApiPathItem.json(): JsonObject =
    buildJsonObject {
        operations.forEach { (method, operation) ->
            if (method != Method.CONNECT) put(method.name.lowercase(), operation.json())
        }
        operations[Method.CONNECT]?.let { connect ->
            put(
                "additionalOperations",
                buildJsonObject { put(Method.CONNECT.name, connect.json()) }
            )
        }
    }

private fun OpenApiOperation.json(): JsonObject =
    buildJsonObject {
        if (tags.isNotEmpty()) put("tags", stringArray(tags))
        summary?.let { put("summary", it) }
        description?.let { put("description", it) }
        put("operationId", operationId)
        if (parameters.isNotEmpty()) put("parameters", JsonArray(parameters.map(OpenApiParameter::json)))
        requestBody?.let { put("requestBody", it.json()) }
        put("responses", jsonObject(responses.mapValues { (_, response) -> response.json() }))
    }

private fun OpenApiParameter.json(): JsonObject =
    buildJsonObject {
        put("name", name)
        put("in", location.value)
        if (required) put("required", true)
        if (deprecated) put("deprecated", true)
        style?.let { put("style", it) }
        explode?.let { put("explode", it) }
        put("schema", schema.json())
    }

private fun OpenApiRequestBody.json(): JsonObject =
    buildJsonObject {
        if (required) put("required", true)
        put("content", jsonObject(content.mapValues { (_, mediaType) -> mediaType.json() }))
    }

private fun OpenApiResponse.json(): JsonObject =
    buildJsonObject {
        put("description", description)
        if (headers.isNotEmpty()) put("headers", jsonObject(headers.mapValues { (_, header) -> header.json() }))
        if (content.isNotEmpty()) put("content", jsonObject(content.mapValues { (_, mediaType) -> mediaType.json() }))
    }

private fun OpenApiHeader.json(): JsonObject =
    buildJsonObject {
        if (required) put("required", true)
        if (deprecated) put("deprecated", true)
        put("schema", schema.json())
    }

private fun OpenApiMediaType.json(): JsonObject = buildJsonObject { put("schema", schema.json()) }

private fun OpenApiComponents.json(): JsonObject =
    buildJsonObject {
        if (schemas.isNotEmpty()) put("schemas", jsonObject(schemas.mapValues { (_, schema) -> schema.json() }))
    }

private fun OpenApiSchema.json(): JsonObject =
    buildJsonObject {
        reference?.let { put("${'$'}ref", it) }
        when (types.size) {
            0 -> Unit
            1 -> put("type", types.single())
            else -> put("type", stringArray(types))
        }
        format?.let { put("format", it) }
        if (enumValues.isNotEmpty()) put("enum", stringArray(enumValues))
        items?.let { put("items", it.json()) }
        if (properties.isNotEmpty()) {
            put("properties", jsonObject(properties.mapValues { (_, schema) -> schema.json() }))
        }
        if (required.isNotEmpty()) put("required", stringArray(required))
        propertyNames?.let { put("propertyNames", it.json()) }
        additionalProperties?.let { put("additionalProperties", it.json()) }
        if (anyOf.isNotEmpty()) put("anyOf", JsonArray(anyOf.map(OpenApiSchema::json)))
        if (deprecated) put("deprecated", true)
        defaultValue?.let { put("default", it) }
        constantValue?.let { put("const", it) }
    }

private fun jsonObject(values: Map<String, JsonElement>): JsonObject = JsonObject(values)

private fun stringArray(values: List<String>): JsonArray =
    buildJsonArray { values.forEach { add(JsonPrimitive(it)) } }
