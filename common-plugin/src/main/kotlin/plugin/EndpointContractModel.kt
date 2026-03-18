package dev.akif.tapik.plugin

import dev.akif.tapik.Status
import dev.akif.tapik.plugin.metadata.BodyMetadata
import dev.akif.tapik.plugin.metadata.HeaderCardinality
import dev.akif.tapik.plugin.metadata.HeaderMetadata
import dev.akif.tapik.plugin.metadata.HttpEndpointMetadata
import dev.akif.tapik.plugin.metadata.OutputMatchMetadata
import dev.akif.tapik.plugin.metadata.OutputMetadata

internal data class EndpointContractModel(
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
    internal data class ResponseModel(
        val typeName: String,
        val variants: List<Variant>
    ) {
        internal data class Variant(
            val typeName: String,
            val match: OutputMatchMetadata,
            val status: Status?,
            val statusFieldName: String?,
            val description: String,
            val isObject: Boolean,
            val fields: List<Field>
        )

        internal data class Field(
            val name: String,
            val type: String
        )
    }
}

internal fun buildEndpointContractModels(
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
        statusFieldName = match.determineStatusFieldName(usedFieldNames),
        description = description,
        isObject = fields.isEmpty(),
        fields = fields
    )
}

internal fun EndpointContractModel.toGenerationContext(): KotlinEndpointGenerationContext =
    KotlinEndpointGenerationContext(
        endpointReference = endpointReference,
        methodName = methodName,
        summaryLines = summaryLines,
        detailLines = detailLines,
        response =
            KotlinEndpointResponseModel(
                typeName = response.typeName,
                variants =
                    response.variants.map { variant ->
                        KotlinEndpointResponseModel.Variant(
                            typeName = variant.typeName,
                            match = variant.match,
                            statusFieldName = variant.statusFieldName,
                            fields =
                                variant.fields.map { field ->
                                    KotlinEndpointResponseModel.Field(
                                        name = field.name,
                                        type = field.type
                                    )
                                }
                        )
                    }
            )
    )

private fun BodyMetadata.determineBodyFieldType(): String =
    renderValueType()

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
            is OutputMatchMetadata.Exact -> match.status.name.toPascalIdentifier("Response")
            is OutputMatchMetadata.AnyOf -> match.statuses.joinToString("Or") { it.name.toPascalIdentifier("Response") }
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

private fun OutputMatchMetadata.determineStatusFieldName(
    usedFieldNames: MutableSet<String>
): String? =
    when (this) {
        is OutputMatchMetadata.Exact -> null
        else -> uniqueName("responseStatus", usedFieldNames)
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

internal fun String.toEndpointContainerName(endpointsSuffix: String = "Endpoints"): String =
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
