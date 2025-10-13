package dev.akif.tapik.gradle

import kotlinx.serialization.Serializable

@Serializable
data class TypeDescription(
    val name: String?,
    val type: String,
    val arguments: List<TypeDescription>,
    val hasKnownValues: Boolean? = null,
    val required: Boolean? = null,
    val hasDefault: Boolean? = null
) {
    override fun toString(): String =
        if (arguments.isEmpty()) {
            type
        } else {
            arguments.joinToString(prefix = "$type<", postfix = ">", separator = ", ") { it.toString() }
        }
}
