package dev.akif.tapik.fixtures.library

import kotlinx.serialization.Serializable

/** An author represented by [id] and [name]. */
@Serializable
data class Author(
    val id: AuthorId,
    val name: String
)
