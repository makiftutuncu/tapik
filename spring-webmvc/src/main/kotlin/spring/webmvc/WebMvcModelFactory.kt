package dev.akif.tapik.spring.webmvc

import dev.akif.tapik.*
import dev.akif.tapik.plugin.core.CompiledApi
import dev.akif.tapik.plugin.core.CompiledEndpoint
import dev.akif.tapik.plugin.core.KotlinClassClassifier
import dev.akif.tapik.plugin.core.KotlinType

internal fun webMvcApiModel(
    compiled: CompiledApi,
    packageName: String,
    serverName: String
): WebMvcApiModel {
    val api = compiled.value
    val apiSimpleName = api.javaClass.simpleName
    val apiType = requireNotNull(api.javaClass.canonicalName) {
        "Spring WebMVC generation requires a canonical API type for '${api.id}'"
    }
    val apiProperty = apiSimpleName.lowerCamel("api") + "Api"
    return WebMvcApiModel(
        packageName = packageName,
        serverName = serverName,
        apiType = apiType,
        apiProperty = apiProperty,
        endpoints = compiled.endpoints.map { endpoint -> endpoint.toModel(api.id, apiProperty) }
    )
}

private fun CompiledEndpoint.toModel(apiId: String, apiProperty: String): WebMvcEndpointModel {
    require(value.method != Method.CONNECT && value.method != Method.QUERY) {
        "${value.id} uses ${value.method}, which Spring WebMVC cannot map"
    }
    val propertyName = value.id.removePrefix("$apiId.")
    val propertyIdentifier = propertyName.kotlinIdentifier("endpoint")
    val endpointAccess = "$apiProperty.$propertyIdentifier"
    val usedNames = mutableSetOf<String>()
    val usedRawNames = mutableSetOf<String>()

    val pathTypes = type.argument(0, value.id).tupleElements("${value.id} paths")
    require(pathTypes.size == value.uri.paths.values.size) {
        "${value.id} compiled path types do not match its runtime path variables"
    }
    val paths =
        value.uri.paths.values.mapIndexed { index, path ->
            val valueType = pathTypes[index].argument(0, "${value.id} path '${path.name}'").render()
            val name = uniqueName(path.name.kotlinIdentifier("path${index + 1}"), usedNames)
            val rawName = uniqueName(name.removeSurrounding("`") + "Raw", usedRawNames)
            WebMvcWireParameter(
                name = name,
                rawName = rawName,
                wireName = path.name,
                type = valueType,
                definitionAccess = "$endpointAccess.uri.paths._${index + 1}",
                presence = Required
            ) to WebMvcParameter(name, valueType)
        }

    val queryTypes = type.argument(1, value.id).tupleElements("${value.id} queries")
    require(queryTypes.size == value.uri.queries.values.size) {
        "${value.id} compiled query types do not match its runtime query parameters"
    }
    val queries =
        value.uri.queries.values.mapIndexed { index, query ->
            val queryType = queryTypes[index]
            val elementType = queryType.argument(0, "${value.id} query '${query.name}'").render()
            val repeated = query is RepeatedQueryParameter<*, *>
            val typeName = if (repeated) "kotlin.collections.List<$elementType>" else elementType
            val presence =
                when (query) {
                    is QueryParameter<*, *> -> query.presence
                    is RepeatedQueryParameter<*, *> -> query.presence
                }
            val name = uniqueName(query.name.kotlinIdentifier("query${index + 1}"), usedNames)
            val rawName = uniqueName(name.removeSurrounding("`") + "Raw", usedRawNames)
            val access = "$endpointAccess.uri.queries._${index + 1}"
            val parameter =
                when (presence) {
                    Required -> WebMvcParameter(name, typeName)
                    Optional -> WebMvcParameter(name, "$typeName?", "null")
                    is Default<*> -> WebMvcParameter(name, typeName, "$access.presence.value")
                    is Fixed<*> -> error("Query parameters cannot be fixed")
                }
            WebMvcWireParameter(
                name = name,
                rawName = rawName,
                wireName = query.name,
                type = typeName,
                definitionAccess = access,
                presence = presence,
                repeated = repeated
            ) to parameter
        }

    val headerTypes = type.argument(2, value.id).tupleElements("${value.id} headers")
    require(headerTypes.size == value.headers.values.size) {
        "${value.id} compiled header types do not match its runtime headers"
    }
    val headers =
        value.headers.values.mapIndexed { index, header ->
            val valueType = headerTypes[index].argument(0, "${value.id} header '${header.name}'").render()
            val access = "$endpointAccess.headers._${index + 1}"
            val name = uniqueName(header.name.kotlinIdentifier("header${index + 1}"), usedNames)
            val rawName = uniqueName(name.removeSurrounding("`") + "Raw", usedRawNames)
            val parameter =
                when (header.presence) {
                    Required -> WebMvcParameter(name, valueType)
                    Optional -> WebMvcParameter(name, "$valueType?", "null")
                    is Default<*> -> WebMvcParameter(name, valueType, "$access.presence.value")
                    is Fixed<*> -> null
                }
            WebMvcWireParameter(
                name = name,
                rawName = rawName,
                wireName = header.name,
                type = valueType,
                definitionAccess = access,
                presence = header.presence
            ) to parameter
        }

    val body = requestBody(type.argument(3, value.id), value.input, endpointAccess, value.id, usedNames, usedRawNames)
    val requiredParameters =
        buildList {
            addAll(paths.map { it.second })
            addAll(queries.map { it.second }.filter { it.defaultExpression == null })
            addAll(headers.mapNotNull { it.second }.filter { it.defaultExpression == null })
            body?.takeIf { !it.optional }?.let { add(WebMvcParameter(it.name, it.type)) }
        }
    val optionalParameters =
        buildList {
            addAll(queries.map { it.second }.filter { it.defaultExpression != null })
            addAll(headers.mapNotNull { it.second }.filter { it.defaultExpression != null })
            body?.takeIf(WebMvcRequestBody::optional)?.let {
                add(WebMvcParameter(it.name, "${it.type}?", "null"))
            }
        }
    val outputs = outputs(type.argument(4, value.id), value.outputs, endpointAccess, value.id)
    val acceptParameter =
        if (outputs.any { it.bodies.isNotEmpty() }) uniqueName("accept", usedRawNames) else null

    return WebMvcEndpointModel(
        id = value.id,
        endpointAccess = endpointAccess,
        handlerName = propertyIdentifier,
        mappingName = uniqueName(propertyIdentifier.removeSurrounding("`") + "Http", mutableSetOf()),
        summary = value.documentation.summary,
        method = value.method,
        responseName = propertyName.upperCamel() + "Response",
        pathTemplate = value.uri.pathTemplate(),
        paths = paths.map { it.first },
        queries = queries.map { it.first },
        headers = headers.map { it.first },
        body = body,
        handlerParameters = requiredParameters + optionalParameters,
        outputs = outputs,
        acceptParameter = acceptParameter
    )
}

private fun requestBody(
    inputType: KotlinType,
    input: Input,
    endpointAccess: String,
    endpointId: String,
    usedNames: MutableSet<String>,
    usedRawNames: MutableSet<String>
): WebMvcRequestBody? {
    if (input is NoInput) return null
    require(input is BodyInput<*>) { "$endpointId has unsupported input '${input::class.simpleName}'" }
    val bodyTypes = inputType.argument(0, "$endpointId input").tupleElements("$endpointId input bodies")
    require(bodyTypes.size == input.bodies.values.size) {
        "$endpointId compiled input body types do not match its runtime bodies"
    }
    val encoded = input.bodies.values.withIndex().filter { (_, body) -> body is Body<*> }
    if (encoded.isEmpty()) return null
    val types = encoded.map { (index, _) -> bodyTypes[index].argument(0, "$endpointId request body").render() }
    require(types.distinct().size == 1) { "$endpointId request body alternatives must decode to one value type" }
    val name = uniqueName("body", usedNames)
    return WebMvcRequestBody(
        name = name,
        rawName = uniqueName("bodyBytes", usedRawNames),
        contentTypeName = uniqueName("contentType", usedRawNames),
        type = types.first(),
        alternatives =
            encoded.map { (index, alternative) ->
                WebMvcBodyAlternative(
                    definitionAccess = "$endpointAccess.input.bodies._${index + 1}",
                    mediaType = (alternative as Body<*>).mediaType.value
                )
            },
        optional = input.bodies.values.any { body -> body is NoBody }
    )
}

private fun outputs(
    outputsType: KotlinType,
    outputs: Outputs,
    endpointAccess: String,
    endpointId: String
): List<WebMvcOutput> {
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
                WebMvcOutputBody(
                    type = bodyType.argument(0, "$endpointId output body").render(),
                    definitionAccess = "$outputAccess.bodies._${bodyIndex + 1}",
                    mediaType = body.mediaType.value
                )
            }
        require(bodies.map(WebMvcOutputBody::type).distinct().size <= 1) {
            "$endpointId output ${index + 1} body alternatives must encode one value type"
        }
        val usedHeaderNames = mutableSetOf<String>().apply { if (bodies.isNotEmpty()) add("body") }
        val headers =
            output.headers.values.mapIndexed { headerIndex, header ->
                val headerType = requireNotNull(headerTypes?.getOrNull(headerIndex)) {
                    "$endpointId output ${index + 1} is missing its compiled header type"
                }
                val rawType = headerType.argument(0, "$endpointId output header '${header.name}'").render()
                val renderedType =
                    when (header.presence) {
                        Optional, is Default<*> -> "$rawType?"
                        Required, is Fixed<*> -> rawType
                    }
                WebMvcOutputHeader(
                    name =
                        if (header.presence is Fixed<*>) null
                        else uniqueName(header.name.lowerCamel("header${headerIndex + 1}"), usedHeaderNames),
                    wireName = header.name,
                    type = renderedType,
                    definitionAccess = "$outputAccess.headers._${headerIndex + 1}",
                    presence = header.presence,
                    defaultExpression =
                        when (header.presence) {
                            Optional, is Default<*> -> "null"
                            Required, is Fixed<*> -> null
                        }
                )
            }
        WebMvcOutput(
            variantName = exact.status.variantName(),
            statusCode = exact.status.code,
            definitionAccess = outputAccess,
            bodies = bodies,
            allowsNoBody = output.bodies.values.any { body -> body is NoBody },
            headers = headers
        )
    }
}

private fun Uri<*, *>.pathTemplate(): String =
    if (segments.isEmpty()) "/" else
        segments.joinToString(separator = "/", prefix = "/") { segment ->
            when (segment) {
                is PathSegment.Literal -> segment.value
                is PathVariable<*> -> "{${segment.name}}"
            }
        }

private fun Status.variantName(): String =
    when (this) {
        Status.Ok -> "Ok"
        Status.Created -> "Created"
        Status.NoContent -> "NoContent"
        Status.BadRequest -> "BadRequest"
        Status.NotFound -> "NotFound"
        Status.Conflict -> "Conflict"
        Status.InternalServerError -> "InternalServerError"
        else -> "Status$code"
    }

private fun uniqueName(requested: String, used: MutableSet<String>): String {
    if (used.add(requested)) return requested
    val raw = requested.removeSurrounding("`")
    var suffix = 2
    while (!used.add(raw + suffix)) suffix++
    return raw + suffix
}
