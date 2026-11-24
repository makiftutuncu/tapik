package dev.akif.tapik.common.plugin

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
 * @property sharingKey stable identity allowing several executions to own the same path, or `null` for exclusive
 * ownership.
 * @throws IllegalArgumentException when [relativePath] is blank, absolute, or not normalized, or when [mediaType] or
 * [sharingKey] is blank.
 */
data class GeneratedArtifact(
    /** Normalized, forward-slash path beneath the execution output directory. */
    val relativePath: String,
    /** Media type of [content]. */
    val mediaType: String,
    /** Role of the artifact in the host build. */
    val kind: ArtifactKind,
    /** Complete textual content. */
    val content: String,
    /** Stable identity allowing several executions to own the same path, or `null` for exclusive ownership. */
    val sharingKey: String? = null
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
        require(sharingKey == null || sharingKey.isNotBlank()) { "Generated artifact sharing key must not be blank" }
    }
}

private val WINDOWS_ABSOLUTE_PATH: Regex = Regex("^[A-Za-z]:/")

/**
 * Artifacts returned by one target execution.
 *
 * @throws IllegalArgumentException when two artifacts have the same relative path.
 */
class GenerationResult(
    artifacts: List<GeneratedArtifact>
) {
    /** Unmodifiable snapshot of generated artifacts in the order returned by the target. */
    val artifacts: List<GeneratedArtifact> = artifacts.snapshotList()

    init {
        val paths = artifacts.map(GeneratedArtifact::relativePath)
        require(paths.distinct().size == paths.size) { "Generated artifact paths must be unique" }
    }

    /** Returns [artifacts] for destructuring. */
    operator fun component1(): List<GeneratedArtifact> = artifacts

    /** Returns a copy, snapshotting [artifacts]. */
    fun copy(artifacts: List<GeneratedArtifact> = this.artifacts): GenerationResult =
        GenerationResult(artifacts)

    /** Compares this value with [other]. */
    override fun equals(other: Any?): Boolean =
        this === other || other is GenerationResult && artifacts == other.artifacts

    /** Returns a hash code consistent with [equals]. */
    override fun hashCode(): Int = artifacts.hashCode()

    /** Returns a string representation of this value. */
    override fun toString(): String = "GenerationResult(artifacts=$artifacts)"
}
