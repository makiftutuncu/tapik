package dev.akif.tapik.common.plugin

/**
 * Returns a stable per-API namespace beneath [packagePrefix] for the JVM [apiBinaryName].
 *
 * Names are relative to [packagePrefix]'s parent package. External APIs retain their full binary identity beneath
 * the reserved `_external` namespace; a one-segment prefix has an empty source root and needs no external marker.
 * Every binary-name segment preserves ASCII letters and digits and encodes other UTF-16 code units as `_hhhh`.
 * Invalid resulting Kotlin identifiers receive `_k_`. Encoding underscores prevents collisions with escape sequences;
 * preserving binary nesting separators distinguishes nested classes from similarly named top-level classes.
 * This mapping is independent of API selection and is part of generated-source compatibility.
 */
fun generatedApiPackage(packagePrefix: String, apiBinaryName: String): String {
    require(packagePrefix.split('.').all(String::isKotlinIdentifier)) { "Invalid generated package prefix '$packagePrefix'" }
    require(apiBinaryName.split('.').all(String::isNotEmpty)) { "Invalid API binary name '$apiBinaryName'" }
    val sourceRoot = packagePrefix.substringBeforeLast('.', "")
    val external = sourceRoot.isNotEmpty() && !apiBinaryName.startsWith("$sourceRoot.")
    val relativeName = if (sourceRoot.isEmpty() || external) apiBinaryName else apiBinaryName.removePrefix("$sourceRoot.")
    val namespace = if (external) "$packagePrefix._external" else packagePrefix
    return relativeName.split('.').joinToString(separator = ".", prefix = "$namespace.") { segment ->
        val encoded = buildString {
            segment.forEach { character ->
                if (character in 'a'..'z' || character in 'A'..'Z' || character in '0'..'9') {
                    append(character)
                } else {
                    append('_')
                    append(character.code.toString(16).padStart(4, '0'))
                }
            }
        }
        if (encoded.isKotlinIdentifier()) encoded else "_k_$encoded"
    }
}
