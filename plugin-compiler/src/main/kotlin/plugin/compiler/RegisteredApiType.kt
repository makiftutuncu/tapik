package dev.akif.tapik.plugin.compiler

internal data class RegisteredApiType(
    val internalName: String,
    val instantiation: ApiInstantiation
)

internal enum class ApiInstantiation {
    OBJECT,
    CONSTRUCTOR
}
