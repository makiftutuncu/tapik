package dev.akif.tapik.target.spring.restclient

import dev.akif.tapik.*
import dev.akif.tapik.common.plugin.CompiledApi
import dev.akif.tapik.common.plugin.CompiledEndpoint
import dev.akif.tapik.common.plugin.KotlinClassClassifier
import dev.akif.tapik.common.plugin.KotlinSourceType
import dev.akif.tapik.common.plugin.KotlinType
import dev.akif.tapik.common.plugin.argument
import dev.akif.tapik.common.plugin.kotlinIdentifier
import dev.akif.tapik.common.plugin.kotlinReferenceIdentifier
import dev.akif.tapik.common.plugin.kotlinVariantName
import dev.akif.tapik.common.plugin.lowerCamel
import dev.akif.tapik.common.plugin.pathTemplate
import dev.akif.tapik.common.plugin.toKotlinSourceType
import dev.akif.tapik.common.plugin.tupleElements
import dev.akif.tapik.common.plugin.uniqueKotlinName
import dev.akif.tapik.common.plugin.upperCamel

internal data class RestClientApiModel(
    val packageName: String,
    val clientName: String,
    val apiType: String,
    val apiProperty: String,
    val endpoints: List<RestClientEndpointModel>
)

internal data class RestClientEndpointModel(
    val id: String,
    val propertyName: String,
    val endpointAccess: String,
    val methodName: String,
    val summary: String?,
    val responseName: String,
    val pathTemplate: String,
    val paths: List<RestClientUriParameter>,
    val queries: List<RestClientUriParameter>,
    val headers: List<RestClientHeader>,
    val body: RestClientRequestBodyModel?,
    val parameters: List<RestClientParameter>,
    val outputs: List<RestClientOutput>
)

internal data class RestClientParameter(
    val name: String,
    val type: String,
    val defaultExpression: String? = null
)

internal data class RestClientUriParameter(
    val name: String,
    val wireName: String,
    val definitionAccess: String,
    val repeated: Boolean,
    val optional: Boolean
)

internal data class RestClientHeader(
    val name: String?,
    val wireName: String,
    val type: String,
    val definitionAccess: String,
    val presence: Presence<*>
)

internal data class RestClientRequestBodyModel(
    val parameterName: String,
    val type: String,
    val definitionAccess: String,
    val optional: Boolean
)

internal data class RestClientOutput(
    val variantName: String,
    val definitionAccess: String,
    val bodies: List<RestClientOutputBody>,
    val allowsNoBody: Boolean,
    val headers: List<RestClientOutputHeader>,
    val fixedHeaders: List<RestClientFixedOutputHeader>
)

internal data class RestClientOutputBody(
    val type: KotlinSourceType,
    val definitionAccess: String
)

internal data class RestClientOutputHeader(
    val name: String,
    val wireName: String,
    val type: KotlinSourceType,
    val definitionAccess: String,
    val presence: Presence<*>
)

internal data class RestClientFixedOutputHeader(
    val wireName: String,
    val definitionAccess: String
)

internal fun restClientApiModel(
    compiled: CompiledApi,
    packageName: String,
    clientName: String
): RestClientApiModel {
    val api = compiled.value
    val apiSimpleName = api.javaClass.simpleName
    val apiType = requireNotNull(api.javaClass.canonicalName) {
        "Spring RestClient generation requires a canonical API type for '${api.id}'"
    }
    val apiProperty = apiSimpleName.lowerCamel("api") + "Api"
    val methodNames = mutableSetOf<String>()
    val responseNames = mutableSetOf<String>()
    return RestClientApiModel(
        packageName = packageName,
        clientName = clientName,
        apiType = apiType,
        apiProperty = apiProperty,
        endpoints =
            compiled.endpoints.map { endpoint ->
                val propertyName = endpoint.value.id.removePrefix("${api.id}.")
                endpoint.toModel(
                    apiId = api.id,
                    apiProperty = apiProperty,
                    methodName = uniqueKotlinName(propertyName.kotlinIdentifier("endpoint"), methodNames),
                    responseName = uniqueKotlinName(propertyName.upperCamel() + "Response", responseNames)
                )
            }
    )
}

private fun CompiledEndpoint.toModel(
    apiId: String,
    apiProperty: String,
    methodName: String,
    responseName: String
): RestClientEndpointModel {
    val propertyName = value.id.removePrefix("$apiId.")
    val endpointAccess = "$apiProperty.${propertyName.kotlinReferenceIdentifier()}"
    val usedNames = mutableSetOf<String>()

    val pathTypes = type.argument(0, value.id).tupleElements("${value.id} paths")
    require(pathTypes.size == value.uri.paths.values.size) {
        "${value.id} compiled path types do not match its runtime path variables"
    }
    val paths =
        value.uri.paths.values.mapIndexed { index, path ->
            val pathType = pathTypes[index].argument(0, "${value.id} path '${path.name}'")
            val name = uniqueKotlinName(path.name.kotlinIdentifier("path${index + 1}"), usedNames)
            RestClientUriParameter(
                name = name,
                wireName = path.name,
                definitionAccess = "$endpointAccess.uri.paths._${index + 1}",
                repeated = false,
                optional = false
            ) to RestClientParameter(name, pathType.toKotlinSourceType("${value.id} path '${path.name}'").source)
        }

    val queryTypes = type.argument(1, value.id).tupleElements("${value.id} queries")
    require(queryTypes.size == value.uri.queries.values.size) {
        "${value.id} compiled query types do not match its runtime query parameters"
    }
    val queries =
        value.uri.queries.values.mapIndexed { index, query ->
            val queryType = queryTypes[index]
            val elementType =
                queryType
                    .argument(0, "${value.id} query '${query.name}'")
                    .toKotlinSourceType("${value.id} query '${query.name}'")
                    .source
            val repeated = query is RepeatedQueryParameter<*, *>
            val typeName = if (repeated) "kotlin.collections.List<$elementType>" else elementType
            val presence =
                when (query) {
                    is QueryParameter<*, *> -> query.presence
                    is RepeatedQueryParameter<*, *> -> query.presence
                }
            val name = uniqueKotlinName(query.name.kotlinIdentifier("query${index + 1}"), usedNames)
            val access = "$endpointAccess.uri.queries._${index + 1}"
            val parameter =
                when (presence) {
                    Required -> RestClientParameter(name, typeName)
                    Optional -> RestClientParameter(name, "$typeName?", "null")
                    is Default<*> -> RestClientParameter(name, typeName, "$access.presence.value")
                    is Fixed<*> -> error("Query parameters cannot be fixed")
                }
            RestClientUriParameter(
                name = name,
                wireName = query.name,
                definitionAccess = access,
                repeated = repeated,
                optional = presence === Optional
            ) to parameter
        }

    val headerTypes = type.argument(2, value.id).tupleElements("${value.id} headers")
    require(headerTypes.size == value.headers.values.size) {
        "${value.id} compiled header types do not match its runtime headers"
    }
    val headers =
        value.headers.values.mapIndexed { index, header ->
            val valueType =
                headerTypes[index]
                    .argument(0, "${value.id} header '${header.name}'")
                    .toKotlinSourceType("${value.id} header '${header.name}'")
                    .source
            val access = "$endpointAccess.headers._${index + 1}"
            val parameterName = uniqueKotlinName(header.name.kotlinIdentifier("header${index + 1}"), usedNames)
            val parameter =
                when (header.presence) {
                    Required -> RestClientParameter(parameterName, valueType)
                    Optional -> RestClientParameter(parameterName, "$valueType?", "null")
                    is Default<*> -> RestClientParameter(parameterName, valueType, "$access.presence.value")
                    is Fixed<*> -> null
                }
            RestClientHeader(
                name = parameter?.name,
                wireName = header.name,
                type = valueType,
                definitionAccess = access,
                presence = header.presence
            ) to parameter
        }

    val body = requestBody(type.argument(3, value.id), value.input, endpointAccess, value.id, usedNames)
    val requiredParameters =
        buildList {
            addAll(paths.map(Pair<RestClientUriParameter, RestClientParameter>::second))
            addAll(queries.map(Pair<RestClientUriParameter, RestClientParameter>::second).filter { it.defaultExpression == null })
            addAll(headers.mapNotNull(Pair<RestClientHeader, RestClientParameter?>::second).filter { it.defaultExpression == null })
            body?.takeIf { !it.optional }?.let { add(RestClientParameter(it.parameterName, it.type)) }
        }
    val optionalParameters =
        buildList {
            addAll(queries.map(Pair<RestClientUriParameter, RestClientParameter>::second).filter { it.defaultExpression != null })
            addAll(headers.mapNotNull(Pair<RestClientHeader, RestClientParameter?>::second).filter { it.defaultExpression != null })
            body?.takeIf(RestClientRequestBodyModel::optional)?.let {
                add(RestClientParameter(it.parameterName, "${it.type}?", "null"))
            }
        }

    return RestClientEndpointModel(
        id = value.id,
        propertyName = propertyName,
        endpointAccess = endpointAccess,
        methodName = methodName,
        summary = value.documentation.summary,
        responseName = responseName,
        pathTemplate = value.uri.pathTemplate(),
        paths = paths.map(Pair<RestClientUriParameter, RestClientParameter>::first),
        queries = queries.map(Pair<RestClientUriParameter, RestClientParameter>::first),
        headers = headers.map(Pair<RestClientHeader, RestClientParameter?>::first),
        body = body,
        parameters = requiredParameters + optionalParameters,
        outputs = outputs(type.argument(4, value.id), value.outputs, endpointAccess, value.id)
    )
}

private fun requestBody(
    inputType: KotlinType,
    input: Input,
    endpointAccess: String,
    endpointId: String,
    usedNames: MutableSet<String>
): RestClientRequestBodyModel? {
    if (input is NoInput) return null
    require(input is BodyInput<*>) { "$endpointId has unsupported input '${input::class.simpleName}'" }
    val bodyTypes = inputType.argument(0, "$endpointId input").tupleElements("$endpointId input bodies")
    require(bodyTypes.size == input.bodies.values.size) {
        "$endpointId compiled input body types do not match its runtime bodies"
    }
    val encoded = input.bodies.values.withIndex().filter { (_, body) -> body is Body<*> }
    require(encoded.size <= 1) { "$endpointId has multiple request body representations, which are not supported" }
    val indexed = encoded.singleOrNull() ?: return null
    val valueType =
        bodyTypes[indexed.index]
            .argument(0, "$endpointId request body")
            .toKotlinSourceType("$endpointId request body")
            .source
    return RestClientRequestBodyModel(
        parameterName = uniqueKotlinName("body", usedNames),
        type = valueType,
        definitionAccess = "$endpointAccess.input.bodies._${indexed.index + 1}",
        optional = input.bodies.values.any { body -> body is NoBody }
    )
}

private fun outputs(
    outputsType: KotlinType,
    outputs: Outputs,
    endpointAccess: String,
    endpointId: String
): List<RestClientOutput> {
    val compiledTypes =
        if ((outputsType.classifier as? KotlinClassClassifier)?.name == "dev.akif.tapik.DefaultOutput") {
            emptyList()
        } else {
            outputsType.tupleElements("$endpointId outputs")
        }
    require(compiledTypes.isEmpty() || compiledTypes.size == outputs.values.size) {
        "$endpointId compiled output types do not match its runtime outputs"
    }
    return outputs.values.mapIndexed { index, alternative ->
        val output = alternative as? Output<*, *, *>
            ?: throw IllegalArgumentException("$endpointId has unsupported output '${alternative::class.simpleName}'")
        val exact = output.matcher as? ExactStatus
            ?: throw IllegalArgumentException("$endpointId has an unsupported non-exact status matcher")
        val outputAccess =
            if (compiledTypes.isEmpty()) {
                "($endpointAccess.outputs.values[$index] as dev.akif.tapik.Output<*, *, *>)"
            } else {
                "$endpointAccess.outputs._${index + 1}"
            }
        val outputType = compiledTypes.getOrNull(index)
        val bodyTypes = outputType?.argument(1, "$endpointId output ${index + 1}")?.tupleElements("$endpointId output bodies")
        val headerTypes = outputType?.argument(2, "$endpointId output ${index + 1}")?.tupleElements("$endpointId output headers")
        val bodies =
            output.bodies.values.mapIndexedNotNull { bodyIndex, body ->
                if (body !is Body<*>) return@mapIndexedNotNull null
                val bodyType = requireNotNull(bodyTypes?.getOrNull(bodyIndex)) {
                    "$endpointId output ${index + 1} is missing its compiled body type"
                }
                RestClientOutputBody(
                    type =
                        bodyType
                            .argument(0, "$endpointId output body")
                            .toKotlinSourceType("$endpointId output body"),
                    definitionAccess = "$outputAccess.bodies._${bodyIndex + 1}"
                )
            }
        val usedHeaderNames = mutableSetOf<String>()
        val headers = mutableListOf<RestClientOutputHeader>()
        val fixedHeaders = mutableListOf<RestClientFixedOutputHeader>()
        output.headers.values.forEachIndexed { headerIndex, header ->
            val headerType = requireNotNull(headerTypes?.getOrNull(headerIndex)) {
                "$endpointId output ${index + 1} is missing its compiled header type"
            }
            val definitionAccess = "$outputAccess.headers._${headerIndex + 1}"
            if (header.presence is Fixed<*>) {
                fixedHeaders += RestClientFixedOutputHeader(header.name, definitionAccess)
            } else {
                val rawType =
                    headerType
                        .argument(0, "$endpointId output header '${header.name}'")
                        .toKotlinSourceType("$endpointId output header '${header.name}'")
                val renderedType = if (header.presence === Optional) rawType.asNullable() else rawType
                headers += RestClientOutputHeader(
                    name = uniqueKotlinName(header.name.lowerCamel("header${headerIndex + 1}"), usedHeaderNames),
                    wireName = header.name,
                    type = renderedType,
                    definitionAccess = definitionAccess,
                    presence = header.presence
                )
            }
        }
        RestClientOutput(
            variantName = exact.status.kotlinVariantName(),
            definitionAccess = outputAccess,
            bodies = bodies,
            allowsNoBody = output.bodies.values.any { body -> body is NoBody },
            headers = headers,
            fixedHeaders = fixedHeaders
        )
    }
}
