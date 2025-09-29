package dev.akif.tapik.gradle

import kotlinx.serialization.Serializable

@Serializable
data class TypeDescription(
    val name: String,
    val arguments: List<TypeDescription> = emptyList()
) {
    override fun toString(): String =
        if (arguments.isEmpty()) {
            name
        } else {
            arguments.joinToString(prefix = "$name<", postfix = ">", separator = ", ")
        }
}
