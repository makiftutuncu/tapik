package dev.akif.tapik.plugin.spring.webmvc

import dev.akif.tapik.Method
import dev.akif.tapik.Presence
import dev.akif.tapik.common.plugin.KotlinSourceType

internal data class WebMvcApiModel(
    val packageName: String,
    val serverName: String,
    val apiType: String,
    val apiProperty: String,
    val endpoints: List<WebMvcEndpointModel>
)

internal data class WebMvcEndpointModel(
    val id: String,
    val endpointAccess: String,
    val handlerName: String,
    val mappingName: String,
    val summary: String?,
    val method: Method,
    val responseName: String,
    val pathTemplate: String,
    val paths: List<WebMvcWireParameter>,
    val queries: List<WebMvcWireParameter>,
    val headers: List<WebMvcWireParameter>,
    val body: WebMvcRequestBody?,
    val handlerParameters: List<WebMvcParameter>,
    val outputs: List<WebMvcOutput>,
    val repeatedQueriesParameter: String?,
    val acceptParameter: String?
)

internal data class WebMvcParameter(
    val name: String,
    val type: String,
    val defaultExpression: String? = null
)

internal data class WebMvcWireParameter(
    val name: String,
    val rawName: String,
    val wireName: String,
    val type: String,
    val definitionAccess: String,
    val presence: Presence<*>,
    val repeated: Boolean = false
)

internal data class WebMvcRequestBody(
    val name: String,
    val rawName: String,
    val contentTypeName: String,
    val type: String,
    val alternatives: List<WebMvcBodyAlternative>,
    val optional: Boolean
)

internal data class WebMvcBodyAlternative(
    val definitionAccess: String,
    val mediaType: String
)

internal data class WebMvcOutput(
    val variantName: String,
    val statusCode: Int,
    val definitionAccess: String,
    val bodies: List<WebMvcOutputBody>,
    val allowsNoBody: Boolean,
    val headers: List<WebMvcOutputHeader>
)

internal data class WebMvcOutputBody(
    val type: KotlinSourceType,
    val definitionAccess: String,
    val mediaType: String
)

internal data class WebMvcOutputHeader(
    val name: String?,
    val wireName: String,
    val type: KotlinSourceType,
    val definitionAccess: String,
    val presence: Presence<*>,
    val defaultExpression: String?
)
