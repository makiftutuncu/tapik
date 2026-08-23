package dev.akif.tapik.spring.restclient

internal fun uniqueName(
    requested: String,
    used: MutableSet<String>
): String {
    if (used.add(requested)) return requested
    val raw = requested.removeSurrounding("`")
    var suffix = 2
    while (!used.add(raw + suffix)) suffix++
    return raw + suffix
}
