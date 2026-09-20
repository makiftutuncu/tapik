package dev.akif.tapik.common.plugin

import dev.akif.tapik.*

/**
 * Stable generated type names for one endpoint.
 *
 * @property response response sealed-interface name.
 * @property requestBody optional request-body choice sealed-interface name.
 * @property requestBodyVariants request-body variant names in representation order.
 * @property responseVariants response variant names in output order.
 */
class EndpointTypeNames(
    val response: String,
    val requestBody: String?,
    requestBodyVariants: List<String>,
    responseVariants: List<String>
) {
    val requestBodyVariants: List<String> = requestBodyVariants.snapshotList()
    val responseVariants: List<String> = responseVariants.snapshotList()
}

/**
 * Allocates target-neutral endpoint type names for [compiled] in endpoint declaration order.
 *
 * The returned names depend only on the compiled contract and may be shared by client and server generators.
 */
fun endpointTypeNames(compiled: CompiledApi): List<EndpointTypeNames> {
    val typeNames = mutableSetOf<String>()
    return compiled.endpoints.map { endpoint ->
        val nameSource = endpoint.kotlinNameSource().upperCamel()
        val response = uniqueKotlinName(nameSource + "Response", typeNames)
        val requestBodyVariants = endpoint.requestBodyVariantNames()
        val requestBody =
            if (requestBodyVariants.size > 1) uniqueKotlinName(nameSource + "RequestBody", typeNames) else null
        EndpointTypeNames(
            response = response,
            requestBody = requestBody,
            requestBodyVariants = requestBodyVariants,
            responseVariants = endpoint.responseVariantNames()
        )
    }
}

/**
 * Generates the target-neutral endpoint input/output types for one compiled API.
 *
 * The artifact is explicitly shareable by independent client and server target executions.
 *
 * @param compiled API and exact compiled endpoint types.
 * @param packageName stable generated package for the API.
 * @return one Kotlin source artifact containing all endpoint-owned types.
 */
fun endpointTypesArtifact(
    compiled: CompiledApi,
    packageName: String
): GeneratedArtifact {
    val apiTypeName = compiled.value.javaClass.simpleName
    val names = endpointTypeNames(compiled)
    val content =
        buildString {
            appendLine("package $packageName")
            compiled.endpoints.zip(names).forEach { (endpoint, endpointNames) ->
                endpointNames.requestBody?.let { requestBodyName ->
                    appendLine()
                    appendRequestBodyType(endpoint, endpointNames, requestBodyName)
                }
                appendLine()
                appendResponseType(endpoint, endpointNames)
            }
        }.trimEnd().optimizeKotlinImports(packageName)
    return GeneratedArtifact(
        relativePath = packageName.replace('.', '/') + "/${apiTypeName}Endpoints.kt",
        mediaType = "text/x-kotlin",
        kind = ArtifactKind.SOURCE,
        content = content,
        sharingKey = "endpoint-types:$packageName:$apiTypeName"
    )
}

private fun StringBuilder.appendRequestBodyType(
    endpoint: CompiledEndpoint,
    names: EndpointTypeNames,
    requestBodyName: String
) {
    val valueType = endpoint.requestBodyValueType()
    appendSealedInterface(
        name = requestBodyName,
        variants = names.requestBodyVariants.map { name ->
            KotlinSealedVariant(name, listOf(KotlinDataField("body", valueType)))
        }
    )
}

private fun StringBuilder.appendResponseType(
    endpoint: CompiledEndpoint,
    names: EndpointTypeNames
) {
    val outputTypes = endpoint.outputTypes()
    appendSealedInterface(
        name = names.response,
        variants =
            endpoint.value.outputs.values.mapIndexed { index, alternative ->
                val output = alternative as? Output<*, *, *>
                    ?: throw IllegalArgumentException(
                        "${endpoint.value.id} has unsupported output '${alternative::class.simpleName}'"
                    )
                KotlinSealedVariant(
                    name = names.responseVariants[index],
                    fields = responseFields(endpoint, index, output, outputTypes.getOrNull(index))
                )
            }
    )
}

private fun responseFields(
    endpoint: CompiledEndpoint,
    outputIndex: Int,
    output: Output<*, *, *>,
    outputType: KotlinType?
): List<KotlinDataField> =
    buildList {
        if (output.matcher !is ExactStatus) add(KotlinDataField("status", statusKotlinSourceType))
        val bodyTypes =
            outputType
                ?.argument(1, "${endpoint.value.id} output ${outputIndex + 1}")
                ?.tupleElements("${endpoint.value.id} output bodies")
        val encodedBodies =
            output.bodies.values.mapIndexedNotNull { bodyIndex, body ->
                if (body !is Body<*>) return@mapIndexedNotNull null
                requireNotNull(bodyTypes?.getOrNull(bodyIndex)) {
                    "${endpoint.value.id} output ${outputIndex + 1} is missing its compiled body type"
                }.argument(0, "${endpoint.value.id} output body")
                    .toKotlinSourceType("${endpoint.value.id} output body")
            }
        require(encodedBodies.distinct().size <= 1) {
            "${endpoint.value.id} output ${outputIndex + 1} body representations must encode one value type"
        }
        encodedBodies.firstOrNull()?.let { bodyType ->
            val allowsNoBody = output.bodies.values.any { body -> body is NoBody }
            add(KotlinDataField("body", if (allowsNoBody) bodyType.asNullable() else bodyType, if (allowsNoBody) "null" else null))
        }

        val headerTypes =
            outputType
                ?.argument(2, "${endpoint.value.id} output ${outputIndex + 1}")
                ?.tupleElements("${endpoint.value.id} output headers")
        val usedNames = mapTo(mutableSetOf(), KotlinDataField::name)
        output.headers.values.forEachIndexed { headerIndex, header ->
            if (header.presence is Fixed<*>) return@forEachIndexed
            val rawType =
                requireNotNull(headerTypes?.getOrNull(headerIndex)) {
                    "${endpoint.value.id} output ${outputIndex + 1} is missing its compiled header type"
                }.argument(0, "${endpoint.value.id} output header '${header.name}'")
                    .toKotlinSourceType("${endpoint.value.id} output header '${header.name}'")
            val optional = header.presence === Optional || header.presence is Default<*>
            add(
                KotlinDataField(
                    name = uniqueKotlinName(header.name.lowerCamel("header${headerIndex + 1}"), usedNames),
                    type = if (optional) rawType.asNullable() else rawType,
                    defaultExpression = if (optional) "null" else null
                )
            )
        }
    }

private fun CompiledEndpoint.requestBodyVariantNames(): List<String> {
    val input = value.input
    if (input is NoInput) return emptyList()
    require(input is BodyInput<*>) { "${value.id} has unsupported input '${input::class.simpleName}'" }
    val usedNames = mutableSetOf<String>()
    return input.bodies.values.mapNotNull { alternative ->
        val body = alternative as? Body<*> ?: return@mapNotNull null
        uniqueKotlinName(body.mediaType.value.substringBefore(';').substringAfter('/').upperCamel(), usedNames)
    }
}

private fun CompiledEndpoint.requestBodyValueType(): KotlinSourceType {
    val input = value.input as BodyInput<*>
    val bodyTypes = type.argument(3, value.id).argument(0, "${value.id} input").tupleElements("${value.id} input bodies")
    require(bodyTypes.size == input.bodies.values.size) {
        "${value.id} compiled input body types do not match its runtime bodies"
    }
    val valueTypes =
        input.bodies.values.mapIndexedNotNull { index, alternative ->
            if (alternative !is Body<*>) return@mapIndexedNotNull null
            bodyTypes[index]
                .argument(0, "${value.id} request body")
                .toKotlinSourceType("${value.id} request body")
        }
    require(valueTypes.distinct().size == 1) {
        "${value.id} request body representations must encode one value type"
    }
    return valueTypes.first()
}

private fun CompiledEndpoint.responseVariantNames(): List<String> {
    val usedNames = mutableSetOf<String>()
    return value.outputs.values.map { alternative ->
        val output = alternative as? Output<*, *, *>
            ?: throw IllegalArgumentException("${value.id} has unsupported output '${alternative::class.simpleName}'")
        uniqueKotlinName(output.matcher.kotlinVariantName(), usedNames)
    }
}

private fun CompiledEndpoint.outputTypes(): List<KotlinType> {
    val outputsType = type.argument(4, value.id)
    val compiledTypes =
        if ((outputsType.classifier as? KotlinClassClassifier)?.name == "dev.akif.tapik.DefaultOutput") {
            emptyList()
        } else {
            outputsType.tupleElements("${value.id} outputs")
        }
    require(compiledTypes.isEmpty() || compiledTypes.size == value.outputs.values.size) {
        "${value.id} compiled output types do not match its runtime outputs"
    }
    return compiledTypes
}
