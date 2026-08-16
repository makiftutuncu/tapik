package dev.akif.tapik.plugin.core

/** The role of a generated artifact in its host build. */
enum class ArtifactKind {
    /** Source code compiled by the host project. */
    SOURCE,

    /** Runtime resource packaged by the host project. */
    RESOURCE,

    /** Documentation published or attached by the host project. */
    DOCUMENTATION
}

/**
 * One text artifact returned by a generation target.
 *
 * @property relativePath normalized, forward-slash path beneath the execution output directory.
 * @property mediaType media type of [content].
 * @property kind role of the artifact in the host build.
 * @property content complete textual content.
 * @throws IllegalArgumentException when [relativePath] is blank, absolute, or not normalized.
 */
data class GeneratedArtifact(
    val relativePath: String,
    val mediaType: String,
    val kind: ArtifactKind,
    val content: String
) {
    init {
        require(relativePath.isNotBlank()) { "Generated artifact path must not be blank" }
        require(!relativePath.startsWith('/') && !WINDOWS_ABSOLUTE_PATH.containsMatchIn(relativePath)) {
            "Generated artifact path must be relative: '$relativePath'"
        }
        require('\\' !in relativePath) {
            "Generated artifact path must use forward slashes: '$relativePath'"
        }
        val segments = relativePath.split('/')
        require(
            segments.none(String::isBlank) &&
                segments.none { segment -> segment == "." || segment == ".." }
        ) {
            "Generated artifact path must be normalized: '$relativePath'"
        }
        require(mediaType.isNotBlank()) { "Generated artifact media type must not be blank" }
    }
}

private val WINDOWS_ABSOLUTE_PATH: Regex = Regex("^[A-Za-z]:/")

/**
 * Artifacts returned by one target execution.
 *
 * @property artifacts generated artifacts in deterministic order.
 * @throws IllegalArgumentException when two artifacts have the same relative path.
 */
data class GenerationResult(
    val artifacts: List<GeneratedArtifact>
) {
    init {
        val paths = artifacts.map(GeneratedArtifact::relativePath)
        require(paths.distinct().size == paths.size) { "Generated artifact paths must be unique" }
    }
}
