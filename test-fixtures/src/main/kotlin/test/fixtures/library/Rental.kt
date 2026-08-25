package dev.akif.tapik.test.fixtures.library

import kotlinx.serialization.Serializable

/** Lifecycle state of a book rental. */
@Serializable
enum class RentalStatus {
    ACTIVE,
    RETURNED
}

/** A rental connecting [bookId] to [borrower] until the encoded [dueDate]. */
@Serializable
data class Rental(
    val id: RentalId,
    val bookId: BookId,
    val borrower: String,
    val dueDate: String,
    val status: RentalStatus
)
