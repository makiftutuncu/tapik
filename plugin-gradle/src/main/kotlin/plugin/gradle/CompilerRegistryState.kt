package dev.akif.tapik.plugin.gradle

internal fun compilerRegistryIndexPath(
    targetIdentity: String,
    compilationIdentity: String
): String =
    ".gradle/tapik/compiler/" +
        "${targetIdentity.registryIdentitySegment()}/" +
        "${compilationIdentity.registryIdentitySegment()}/" +
        "registry.index"

internal fun compilerRegistrySnapshotDirectoryPath(
    targetIdentity: String,
    compilationIdentity: String
): String =
    "tapik/compiler/" +
        "${targetIdentity.registryIdentitySegment()}/" +
        "${compilationIdentity.registryIdentitySegment()}/" +
        "snapshot"

internal fun String.registryIdentitySegment(): String =
    buildString {
        append("id-")
        toByteArray(Charsets.UTF_8).forEach { byte ->
            val value = byte.toInt() and 0xff
            val retained =
                value in 'a'.code..'z'.code ||
                    value in '0'.code..'9'.code ||
                    value == '-'.code ||
                    value == '_'.code
            if (retained) {
                append(value.toChar())
            } else {
                append('%')
                append(HEX_DIGITS[value ushr 4])
                append(HEX_DIGITS[value and 0x0f])
            }
        }
    }

private const val HEX_DIGITS = "0123456789ABCDEF"
