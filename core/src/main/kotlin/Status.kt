package dev.akif.tapik

/**
 * An HTTP response status code.
 *
 * Tapik accepts the complete HTTP status domain instead of limiting contracts to a closed enum of registered codes.
 * Named values in [Companion] provide the standard statuses used most often by endpoint definitions.
 *
 * @property code numeric status code between 100 and 599.
 * @throws IllegalArgumentException when [code] is outside the HTTP status domain.
 */
@JvmInline
value class Status(
    val code: Int
) {
    init {
        require(code in MIN_CODE..MAX_CODE) {
            "HTTP status code must be between $MIN_CODE and $MAX_CODE, but was $code"
        }
    }

    /** Frequently used standard HTTP status values. */
    companion object {
        private const val MIN_CODE: Int = 100
        private const val MAX_CODE: Int = 599

        /** `200 OK`. */
        val Ok: Status = Status(200)

        /** `201 Created`. */
        val Created: Status = Status(201)

        /** `204 No Content`. */
        val NoContent: Status = Status(204)

        /** `400 Bad Request`. */
        val BadRequest: Status = Status(400)

        /** `404 Not Found`. */
        val NotFound: Status = Status(404)

        /** `409 Conflict`. */
        val Conflict: Status = Status(409)

        /** `500 Internal Server Error`. */
        val InternalServerError: Status = Status(500)
    }
}
