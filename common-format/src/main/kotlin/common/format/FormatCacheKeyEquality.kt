package dev.akif.tapik.common.format

/** Selects how a [WeakFormatCache] compares its type keys. */
enum class FormatCacheKeyEquality {
    /** Keys match only when they are the same object. */
    IDENTITY,

    /** Keys match according to [Any.equals]. */
    STRUCTURAL
}
