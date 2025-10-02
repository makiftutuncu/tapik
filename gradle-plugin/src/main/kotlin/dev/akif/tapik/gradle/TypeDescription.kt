package dev.akif.tapik.gradle

import kotlinx.serialization.Serializable

@Serializable
data class TypeDescription(
    val name: String?,
    val type: String,
    val arguments: List<TypeDescription>
) {
    override fun toString(): String =
        if (arguments.isEmpty()) {
            type
        } else {
            arguments.joinToString(prefix = "$type<", postfix = ">", separator = ", ") { it.toString() }
        }
}
