package dev.akif.tapik.common.plugin

/**
 * One constructor field in a generated Kotlin data variant.
 *
 * @property name generated Kotlin property name.
 * @property type source-facing and expanded property type.
 * @property defaultExpression optional Kotlin expression rendered as the property's default value.
 */
data class KotlinDataField(
    val name: String,
    val type: KotlinSourceType,
    val defaultExpression: String? = null
)

/**
 * One generated variant of a sealed Kotlin interface.
 *
 * A variant without [fields] is rendered as a data object; otherwise it is rendered as a data class.
 *
 * @property name generated Kotlin variant name.
 * @property fields constructor fields in declaration order.
 */
data class KotlinSealedVariant(
    val name: String,
    val fields: List<KotlinDataField> = emptyList()
)

/**
 * Appends a public sealed interface and its [variants] using [indentation] before every top-level line.
 *
 * Data variants containing byte arrays receive content-based `equals` and `hashCode` implementations, including when
 * the source type is an alias of `ByteArray`.
 */
fun StringBuilder.appendSealedInterface(
    name: String,
    variants: List<KotlinSealedVariant>,
    indentation: String = ""
) {
    appendLine("${indentation}public sealed interface $name {")
    variants.forEach { variant -> appendVariant(name, variant, indentation) }
    appendLine("$indentation}")
}

private fun StringBuilder.appendVariant(
    interfaceName: String,
    variant: KotlinSealedVariant,
    indentation: String
) {
    val memberIndentation = "$indentation    "
    if (variant.fields.isEmpty()) {
        appendLine("${memberIndentation}public data object ${variant.name} : $interfaceName")
        return
    }

    appendLine("${memberIndentation}public data class ${variant.name}(")
    variant.fields.forEachIndexed { index, field ->
        val suffix = if (index == variant.fields.lastIndex) "" else ","
        val default = field.defaultExpression?.let { expression -> " = $expression" }.orEmpty()
        appendLine("$memberIndentation    public val ${field.name}: ${field.type.source}$default$suffix")
    }
    if (variant.fields.any(KotlinDataField::isByteArray)) {
        appendLine("$memberIndentation) : $interfaceName {")
        appendContentEquals(variant, memberIndentation)
        appendLine()
        appendContentHashCode(variant.fields, memberIndentation)
        appendLine("$memberIndentation}")
    } else {
        appendLine("$memberIndentation) : $interfaceName")
    }
}

private fun StringBuilder.appendContentEquals(
    variant: KotlinSealedVariant,
    indentation: String
) {
    appendLine("$indentation    override fun equals(other: kotlin.Any?): kotlin.Boolean =")
    appendLine("$indentation        this === other ||")
    appendLine("$indentation            (")
    appendLine("$indentation                other is ${variant.name} &&")
    variant.fields.forEachIndexed { index, field ->
        val suffix = if (index == variant.fields.lastIndex) "" else " &&"
        appendLine("$indentation                    ${field.equalsExpression()}$suffix")
    }
    appendLine("$indentation            )")
}

private fun StringBuilder.appendContentHashCode(
    fields: List<KotlinDataField>,
    indentation: String
) {
    appendLine("$indentation    override fun hashCode(): kotlin.Int {")
    appendLine("$indentation        var result = ${fields.first().hashCodeExpression()}")
    fields.drop(1).forEach { field ->
        appendLine("$indentation        result = 31 * result + ${field.hashCodeExpression()}")
    }
    appendLine("$indentation        return result")
    appendLine("$indentation    }")
}

private fun KotlinDataField.equalsExpression(): String =
    when {
        !isByteArray() -> "$name == other.$name"
        type.expandedType.nullable -> "$name?.contentEquals(other.$name) ?: (other.$name == null)"
        else -> "$name.contentEquals(other.$name)"
    }

private fun KotlinDataField.hashCodeExpression(): String =
    when {
        isByteArray() && type.expandedType.nullable -> "($name?.contentHashCode() ?: 0)"
        isByteArray() -> "$name.contentHashCode()"
        type.expandedType.nullable -> "($name?.hashCode() ?: 0)"
        else -> "$name.hashCode()"
    }

private fun KotlinDataField.isByteArray(): Boolean = type.expandsTo("kotlin.ByteArray")
