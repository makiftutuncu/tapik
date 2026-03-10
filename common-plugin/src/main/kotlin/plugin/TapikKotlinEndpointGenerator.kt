package dev.akif.tapik.plugin

import dev.akif.tapik.plugin.metadata.HttpEndpointMetadata
import dev.akif.tapik.plugin.metadata.OutputMatchMetadata

/**
 * Specialised Tapik generator that contributes nested Kotlin members to shared endpoint contract files.
 */
interface TapikKotlinEndpointGenerator : TapikGenerator {
    /**
     * Produces Kotlin source contributions that will be merged with other Kotlin endpoint generators.
     */
    fun contribute(
        endpoints: List<HttpEndpointMetadata>,
        context: TapikGeneratorContext
    ): TapikKotlinContribution

    override fun generate(
        endpoints: List<HttpEndpointMetadata>,
        context: TapikGeneratorContext
    ): TapikGenerationResult = TapikGenerationResult()
}

/**
 * Contribution set emitted by a [TapikKotlinEndpointGenerator].
 *
 * @property sourceFiles per-source-file Kotlin contributions to merge.
 */
data class TapikKotlinContribution(
    val sourceFiles: List<KotlinSourceFileContribution> = emptyList()
)

/**
 * Integration-specific content for one generated Kotlin source file.
 *
 * @property packageName package of the generated Kotlin source file.
 * @property sourcePackageName original package containing the source endpoint declarations.
 * @property sourceFile original endpoint source file name.
 * @property aggregateInterfaceName generated aggregate interface name for the source file.
 * @property nestedInterfaceName nested integration interface name exposed by each endpoint contract.
 * @property imports imports requested by the contributing generator.
 * @property endpointMembers integration-specific nested members keyed by endpoint property name.
 */
data class KotlinSourceFileContribution(
    val packageName: String,
    val sourcePackageName: String,
    val sourceFile: String,
    val aggregateInterfaceName: String,
    val nestedInterfaceName: String,
    val imports: Set<String> = emptySet(),
    val endpointMembers: List<KotlinEndpointMemberContribution>
)

/**
 * Nested content that should be inserted into one generated endpoint contract interface.
 *
 * @property endpointPropertyName endpoint property receiving the generated nested content.
 * @property content Kotlin source snippet to insert into the endpoint contract.
 */
data class KotlinEndpointMemberContribution(
    val endpointPropertyName: String,
    val content: String
)

/**
 * Renders one merged Kotlin endpoint contract file from shared endpoint metadata and generator-specific members.
 *
 * @param packageName package of the generated Kotlin file.
 * @param sourceFile original endpoint source file name.
 * @param endpointsSuffix suffix appended to the source-level enclosing endpoints interface.
 * @param endpoints endpoint metadata declared in the source file.
 * @param aggregateInterfaces aggregate interfaces that should extend each endpoint contract's nested integration type.
 * @param endpointMembers generator-specific content grouped by endpoint property name.
 * @param imports imports requested by contributing generators.
 * @return generated Kotlin source code for the merged endpoint contract file.
 */
fun renderMergedKotlinEndpointFile(
    packageName: String,
    sourceFile: String,
    endpointsSuffix: String = "Endpoints",
    endpoints: List<HttpEndpointMetadata>,
    aggregateInterfaces: List<AggregateInterfaceContribution>,
    endpointMembers: Map<String, List<String>>,
    imports: Set<String>
): String {
    val sortedEndpoints = endpoints.sortedBy { it.id }
    val models = buildEndpointContractModels(sortedEndpoints, sourceFile, endpointsSuffix)
    val enclosingInterfaceName = models.firstOrNull()?.enclosingInterfaceName ?: sourceFile.toEndpointContainerName(endpointsSuffix)
    val endpointImports =
        models
            .mapNotNull { it.endpointImportPath }
            .filter { it.substringBeforeLast('.', "") != packageName }
            .toSet()
    val resolvedImports = (imports + endpointImports).sorted()

    return buildString {
        appendLine("// This file is generated. Any changes will be lost.")
        appendLine("// Generated from: $packageName.$sourceFile")
        appendLine()
        appendLine("package $packageName")
        appendLine()

        resolvedImports.forEach { appendLine("import $it") }
        if (resolvedImports.isNotEmpty()) {
            appendLine()
        }

        aggregateInterfaces.forEachIndexed { index, aggregate ->
            val inheritedInterfaces =
                models.joinToString(", ") {
                    model -> "${enclosingInterfaceName}.${model.interfaceName}.${aggregate.nestedInterfaceName}"
                }
            appendLine("interface ${aggregate.name} : $inheritedInterfaces")
            if (index != aggregateInterfaces.lastIndex || models.isNotEmpty()) {
                appendLine()
            }
        }

        appendLine("interface $enclosingInterfaceName {")
        if (models.isNotEmpty()) {
            appendLine()
        }

        models.forEachIndexed { index, model ->
            appendLine("    interface ${model.interfaceName} {")
            appendLine()
            append(indentBlock(renderResponseType(model), "    "))

            endpointMembers[model.endpoint.propertyName]
                ?.takeIf { it.isNotEmpty() }
                ?.let { members ->
                    appendLine()
                    appendLine()
                    members.forEachIndexed { memberIndex, member ->
                        append(indentBlock(member, "        "))
                        if (memberIndex != members.lastIndex) {
                            appendLine()
                            appendLine()
                        } else {
                            appendLine()
                        }
                    }
                } ?: appendLine()

            appendLine("    }")
            if (index != models.lastIndex) {
                appendLine()
            }
        }

        appendLine("}")
    }
}

/**
 * Describes a generated aggregate interface that groups endpoint-specific nested integration interfaces.
 *
 * @property name generated aggregate interface name.
 * @property nestedInterfaceName nested interface inherited from each endpoint contract.
 */
data class AggregateInterfaceContribution(
    val name: String,
    val nestedInterfaceName: String
)

private fun renderResponseType(
    model: EndpointContractModel
): String = renderSealedResponseHierarchy(model)

private fun renderSealedResponseHierarchy(
    model: EndpointContractModel
): String =
    buildString {
        appendLine("    sealed class Response(")
        appendLine("        open val status: dev.akif.tapik.Status")
        appendLine("    ) {")
        model.response.variants.forEachIndexed { index, variant ->
            append(renderVariantDeclaration(model, variant, outputIndex = index + 1))
            if (index != model.response.variants.lastIndex) {
                appendLine()
                appendLine()
            } else {
                appendLine()
            }
        }
        append("    }")
    }

private fun renderVariantDeclaration(
    model: EndpointContractModel,
    variant: EndpointContractModel.ResponseModel.Variant,
    outputIndex: Int
): String {
    val statusValidation = renderStatusValidation(variant, model.endpointReference, outputIndex)
    val constructorFields = buildConstructorFields(variant)
    val superType =
        when (variant.match) {
            is OutputMatchMetadata.Exact -> " : Response(dev.akif.tapik.Status.${variant.match.status.name})"
            else -> " : Response(status)"
        }

    if (variant.isObject && variant.match is OutputMatchMetadata.Exact) {
        return "        data object ${variant.typeName}$superType"
    }

    return buildString {
        appendLine("        data class ${variant.typeName}(")
        appendLine(constructorFields.joinToString(separator = ",\n") { "            $it" })
        append("        )$superType")
        val bodySections =
            listOfNotNull(
                statusValidation?.let {
                    buildString {
                        appendLine("init {")
                        appendLine("    $it")
                        append("}")
                    }
                },
                renderByteArrayOverrides(variant)
            )
        if (bodySections.isNotEmpty()) {
            appendLine(" {")
            bodySections.forEachIndexed { index, section ->
                append(indentBlock(section, "            "))
                if (index != bodySections.lastIndex) {
                    appendLine()
                    appendLine()
                } else {
                    appendLine()
                }
            }
            append("        }")
        }
    }
}

private fun buildConstructorFields(
    variant: EndpointContractModel.ResponseModel.Variant
): List<String> =
    buildList {
        if (variant.match !is OutputMatchMetadata.Exact) {
            add("override val status: dev.akif.tapik.Status")
        }
        addAll(variant.fields.map { field -> "val ${field.name}: ${field.type}" })
    }

private fun renderByteArrayOverrides(
    variant: EndpointContractModel.ResponseModel.Variant
): String? {
    if (variant.fields.none { it.type == "kotlin.ByteArray" }) {
        return null
    }

    val equalityChecks =
        buildList {
            if (variant.match !is OutputMatchMetadata.Exact) {
                add("status != other.status")
            }
            addAll(
                variant.fields.map { field ->
                    if (field.type == "kotlin.ByteArray") {
                        "!${field.name}.contentEquals(other.${field.name})"
                    } else {
                        "${field.name} != other.${field.name}"
                    }
                }
            )
        }

    val hashExpressions =
        buildList {
            if (variant.match !is OutputMatchMetadata.Exact) {
                add("status.hashCode()")
            }
            addAll(
                variant.fields.map { field ->
                    if (field.type == "kotlin.ByteArray") {
                        "${field.name}.contentHashCode()"
                    } else {
                        "${field.name}.hashCode()"
                    }
                }
            )
        }

    return buildString {
        appendLine("override fun equals(other: kotlin.Any?): kotlin.Boolean {")
        appendLine("    if (this === other) return true")
        appendLine("    if (other !is ${variant.typeName}) return false")
        equalityChecks.forEach { check ->
            appendLine("    if ($check) return false")
        }
        appendLine()
        appendLine("    return true")
        appendLine("}")
        appendLine()
        appendLine("override fun hashCode(): kotlin.Int {")
        if (hashExpressions.isEmpty()) {
            appendLine("    return 0")
        } else {
            appendLine("    var result = ${hashExpressions.first()}")
            hashExpressions.drop(1).forEach { expression ->
                appendLine("    result = 31 * result + $expression")
            }
            appendLine("    return result")
        }
        append("}")
    }
}

private fun renderStatusValidation(
    variant: EndpointContractModel.ResponseModel.Variant,
    endpointExpression: String,
    outputIndex: Int
): String? =
    when (val match = variant.match) {
        is OutputMatchMetadata.Exact -> null
        is OutputMatchMetadata.AnyOf -> {
            val statuses =
                match.statuses.joinToString(prefix = "setOf(", separator = ", ", postfix = ")") {
                    "dev.akif.tapik.Status.${it.name}"
                }
            """require(status in $statuses) { "Status ${'$'}status does not match ${variant.description}" }"""
        }

        is OutputMatchMetadata.Described ->
            """require($endpointExpression.outputs.item$outputIndex.statusMatcher(status)) { "Status ${'$'}status does not match ${variant.description}" }"""

        OutputMatchMetadata.Unmatched -> {
            val precedingMatchers =
                (1 until outputIndex).joinToString(separator = " && ") { index ->
                    "!$endpointExpression.outputs.item$index.statusMatcher(status)"
                }
            """require(${precedingMatchers.ifBlank { "true" }}) { "Status ${'$'}status does not match ${variant.description}" }"""
        }
    }

private fun indentBlock(
    text: String,
    indentation: String
): String =
    text
        .lines()
        .joinToString(separator = "\n") { line ->
            if (line.isBlank()) {
                line
            } else {
                indentation + line
            }
        }
