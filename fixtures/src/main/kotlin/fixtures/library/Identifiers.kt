package dev.akif.tapik.fixtures.library

import dev.akif.tapik.StringFormat
import dev.akif.tapik.format
import kotlinx.serialization.Serializable

/** A book identifier serialized as [value]. */
@JvmInline
@Serializable
value class BookId(
    val value: String
)

/** An author identifier serialized as [value]. */
@JvmInline
@Serializable
value class AuthorId(
    val value: String
)

/** A rental identifier serialized as [value]. */
@JvmInline
@Serializable
value class RentalId(
    val value: String
)

/** String representation of [BookId] used by URI parameters. */
val bookIdFormat: StringFormat<BookId> =
    format.string
        .transform(decode = ::BookId, encode = BookId::value)
        .named("BookId")

/** String representation of [AuthorId] used by URI parameters. */
val authorIdFormat: StringFormat<AuthorId> =
    format.string
        .transform(decode = ::AuthorId, encode = AuthorId::value)
        .named("AuthorId")

/** String representation of [RentalId] used by URI parameters. */
val rentalIdFormat: StringFormat<RentalId> =
    format.string
        .transform(decode = ::RentalId, encode = RentalId::value)
        .named("RentalId")
