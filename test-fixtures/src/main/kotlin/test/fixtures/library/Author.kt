package dev.akif.tapik.test.fixtures.library

import kotlinx.serialization.Serializable

/** An author represented by [id] and [name]. */
@Serializable
data class Author(
    val id: AuthorId,
    val name: String
)

/** Request model for a new author with [name]. */
@Serializable
data class CreateAuthor(
    val name: String
)
