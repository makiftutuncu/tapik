package dev.akif.tapik

/** Standard HTTP methods supported by tapik's endpoint DSL. */
enum class Method {
    /** Requests the current representation of a resource. */
    GET,

    /** Requests the headers for a resource without its response body. */
    HEAD,

    /** Submits data for processing by the target resource. */
    POST,

    /** Creates or replaces the target resource's representation. */
    PUT,

    /** Applies changes to the target resource. */
    PATCH,

    /** Removes the target resource. */
    DELETE,

    /** Establishes a tunnel to the target server. */
    CONNECT,

    /** Requests the communication options supported by the target resource. */
    OPTIONS,

    /** Requests a diagnostic loop-back of the received request. */
    TRACE,

    /** Requests a representation using query content. */
    QUERY
}
