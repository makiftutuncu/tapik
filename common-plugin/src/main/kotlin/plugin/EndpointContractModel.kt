package dev.akif.tapik.plugin

import dev.akif.tapik.Status
import dev.akif.tapik.plugin.metadata.BodyMetadata
import dev.akif.tapik.plugin.metadata.HeaderCardinality
import dev.akif.tapik.plugin.metadata.HeaderMetadata
import dev.akif.tapik.plugin.metadata.HttpEndpointMetadata
import dev.akif.tapik.plugin.metadata.OutputMatchMetadata
import dev.akif.tapik.plugin.metadata.OutputMetadata

/**
 * Shared naming and shape information for generated endpoint contracts.
 *
 * This model lives in `common-plugin` so all Kotlin generators can derive identical names and
 * response structures before contributing their integration-specific members.
 *
 * @property endpoint endpoint metadata backing the generated contract.
 * @property enclosingInterfaceName generated source-level contract interface name.
 * @property interfaceName generated nested endpoint contract interface name.
 * @property endpointReference short reference to the original Tapik endpoint definition.
 * @property endpointImportPath importable owner path for the original Tapik endpoint definition.
 * @property methodName generated method name used by Kotlin integrations.
 * @property summaryLines summary documentation lines rendered from the endpoint description.
 * @property detailLines detailed documentation lines rendered from the endpoint details.
 * @property response generated public response model for the endpoint.
 */
data class EndpointContractModel(
    val endpoint: HttpEndpointMetadata,
    val enclosingInterfaceName: String,
    val interfaceName: String,
    val endpointReference: String,
    val endpointImportPath: String?,
    val methodName: String,
    val summaryLines: List<String>,
    val detailLines: List<String>,
    val response: ResponseModel
) {
    /**
     * Describes the generated public response type for the endpoint.
     *
     * @property typeName public Kotlin type name exposed by generated APIs.
     * @property variants concrete response variants derived from endpoint outputs.
     */
    data class ResponseModel(
        val typeName: String,
        val variants: List<Variant>
    ) {
        /**
         * Describes one generated response variant.
         *
         * @property typeName Kotlin type name for the variant.
         * @property match status matcher metadata represented by the variant.
         * @property status exact status when the matcher is a single fixed status.
         * @property description human-readable description used for diagnostics and naming.
         * @property isObject whether the variant can be rendered as a `data object`.
         * @property fields constructor fields exposed by the variant.
         */
        data class Variant(
            val typeName: String,
            val match: OutputMatchMetadata,
            val status: Status?,
            val description: String,
            val isObject: Boolean,
            val fields: List<Field>
        )

        /**
         * Describes one constructor/property field on a generated response variant.
         *
         * @property name generated Kotlin property name.
         * @property type generated Kotlin property type.
         */
        data class Field(
            val name: String,
            val type: String
        )
    }
}

/**
 * Builds stable endpoint contract models for all endpoints in a source file.
 *
 * @param endpoints endpoint metadata declared in the same source file.
 * @param sourceFile Kotlin source file name used as the naming prefix.
 * @param endpointsSuffix suffix appended to the enclosing source-level endpoints interface.
 * @return stable contract models in endpoint declaration order.
 */
fun buildEndpointContractModels(
    endpoints: List<HttpEndpointMetadata>,
    sourceFile: String,
    endpointsSuffix: String = "Endpoints"
): List<EndpointContractModel> {
    val enclosingInterfaceName = sourceFile.toEndpointContainerName(endpointsSuffix)
    val usedInterfaceNames = mutableSetOf<String>()
    return endpoints.mapIndexed { index, endpoint ->
        val fallback = "endpoint${index + 1}"
        val interfaceName = allocateEndpointContractName(endpoint.propertyName, fallback, usedInterfaceNames)
        EndpointContractModel(
            endpoint = endpoint,
            enclosingInterfaceName = enclosingInterfaceName,
            interfaceName = interfaceName,
            endpointReference = endpoint.renderEndpointReference(),
            endpointImportPath = endpoint.renderEndpointImportPath(),
            methodName = renderIdentifier(endpoint.id),
            summaryLines = linesForDocumentation(endpoint.description),
            detailLines = linesForDocumentation(endpoint.details),
            response = buildResponseModel(endpoint, enclosingInterfaceName, interfaceName)
        )
    }
}

private fun buildResponseModel(
    endpoint: HttpEndpointMetadata,
    enclosingInterfaceName: String,
    interfaceName: String
): EndpointContractModel.ResponseModel {
    val variants = endpoint.outputs.map { output -> output.toVariantModel() }
    return EndpointContractModel.ResponseModel(
        typeName = "$enclosingInterfaceName.$interfaceName.Response",
        variants = variants
    )
}

private fun OutputMetadata.toVariantModel(): EndpointContractModel.ResponseModel.Variant {
    val usedFieldNames = mutableSetOf<String>()
    val fields =
        buildList {
            if (body.type.simpleName() != "EmptyBody") {
                add(
                    EndpointContractModel.ResponseModel.Field(
                        name = uniqueName(sanitizeIdentifier(body.name, "body"), usedFieldNames),
                        type = body.determineBodyFieldType()
                    )
                )
            }
            headers.forEachIndexed { index, header ->
                add(
                    EndpointContractModel.ResponseModel.Field(
                        name = uniqueName(sanitizeIdentifier(header.name, "header${index + 1}"), usedFieldNames),
                        type = header.renderFieldType()
                    )
                )
            }
        }

    return EndpointContractModel.ResponseModel.Variant(
        typeName = renderVariantTypeName(match, description),
        match = match,
        status = match.asExactStatusOrNull(),
        description = description,
        isObject = fields.isEmpty(),
        fields = fields
    )
}

private fun BodyMetadata.determineBodyFieldType(): String =
    when (type.simpleName()) {
        "JsonBody" -> type.arguments.firstOrNull()?.render() ?: "kotlin.Any"
        "StringBody" -> "kotlin.String"
        "RawBody" -> "kotlin.ByteArray"
        else -> type.arguments.firstOrNull()?.render() ?: "kotlin.Any"
    }

private fun HeaderMetadata.renderFieldType(): String {
    val scalarType = type.render()
    return if (cardinality == HeaderCardinality.Multiple) {
        "kotlin.collections.List<$scalarType>"
    } else {
        scalarType
    }
}

private fun renderVariantTypeName(
    match: OutputMatchMetadata,
    description: String
): String {
    val rawBase =
        when (match) {
            is OutputMatchMetadata.Exact -> match.status.name.lowercase().snakeToPascalCase()
            is OutputMatchMetadata.AnyOf -> match.statuses.joinToString("Or") { it.name.lowercase().snakeToPascalCase() }
            is OutputMatchMetadata.Described -> description
            OutputMatchMetadata.Unmatched -> "Unmatched"
        }

    return rawBase.toPascalIdentifier("Response")
}

private fun OutputMatchMetadata.asExactStatusOrNull(): Status? =
    when (this) {
        is OutputMatchMetadata.Exact -> status
        else -> null
    }

private fun allocateEndpointContractName(
    rawName: String?,
    fallback: String,
    usedNames: MutableSet<String>
): String {
    val base = sanitizeIdentifier(rawName, fallback)
    val capitalized = base.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    var candidate = capitalized
    var index = 2
    while (!usedNames.add(candidate)) {
        candidate = "$capitalized$index"
        index++
    }
    return candidate
}

/**
 * Normalises a source file name into the shared enclosing endpoint-contract interface name.
 *
 * Source files that already end with `Endpoints` keep their name; others receive the `Endpoints`
 * suffix so generated contracts consistently nest endpoint interfaces under a single source-level
 * container.
 *
 * @receiver source file name that declared the endpoints.
 * @return enclosing endpoint-contract interface name.
 */
fun String.toEndpointContainerName(endpointsSuffix: String = "Endpoints"): String =
    if (endsWith(endpointsSuffix)) {
        this
    } else {
        this + endpointsSuffix
    }

private fun HttpEndpointMetadata.renderEndpointReference(): String {
    val endpointProperty = renderIdentifier(propertyName)
    return if (sourceFile.endsWith("Kt")) {
        endpointProperty
    } else {
        "$sourceFile.$endpointProperty"
    }
}

private fun HttpEndpointMetadata.renderEndpointImportPath(): String? {
    val packagePrefix = packageName.takeIf(String::isNotBlank) ?: return null
    val endpointProperty = renderIdentifier(propertyName)
    return if (sourceFile.endsWith("Kt")) {
        "$packagePrefix.$endpointProperty"
    } else {
        "$packagePrefix.$sourceFile"
    }
}

private fun String.snakeToPascalCase(): String =
    split('_')
        .filter { it.isNotBlank() }
        .joinToString(separator = "") { part ->
            part.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }

private val TYPE_NAME_BOUNDARY = Regex("([a-z0-9])([A-Z])")
private val TYPE_NAME_SEPARATOR = Regex("[^A-Za-z0-9]+")

private fun String.toPascalIdentifier(fallback: String): String {
    val normalized =
        replace(TYPE_NAME_BOUNDARY, "$1 $2")
            .split(TYPE_NAME_SEPARATOR)
            .flatMap { token -> token.split(' ') }
            .map { it.filter(Char::isLetterOrDigit) }
            .filter { it.isNotBlank() }
            .joinToString(separator = "") { token ->
                token.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
            }

    val resolved = normalized.ifBlank { fallback }
    return if (resolved.firstOrNull()?.isDigit() == true) {
        fallback + resolved
    } else {
        resolved
    }
}
