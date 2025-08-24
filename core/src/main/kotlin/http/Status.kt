package dev.akif.tapik.http

enum class Status(val code: Int) {
    OK(200),
    CREATED(201),
    NO_CONTENT(204),
    BAD_REQUEST(400),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500);

    companion object {
        private val codesToStatuses = entries.associateBy { it.code }

        fun of(code: Int): Status = requireNotNull(codesToStatuses[code]) { "Unknown status code: $code" }
    }
}
