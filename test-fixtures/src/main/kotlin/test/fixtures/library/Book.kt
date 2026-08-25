package dev.akif.tapik.test.fixtures.library

import kotlinx.serialization.Serializable

/** A library book with its [id], [title], and [authors]. */
@Serializable
data class Book(
    val id: BookId,
    val title: String,
    val authors: List<Author>
)

/** Request model for a new book with [title] and existing [authorIds]. */
@Serializable
data class CreateBook(
    val title: String,
    val authorIds: List<AuthorId>
)
