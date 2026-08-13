package dev.akif.tapik

/**
 * An HTTP request method retained by its concrete subtype where OpenAPI defines a standard operation field.
 *
 * @property token case-sensitive method token sent on the wire.
 */
sealed interface Method {
    val token: String

    /** The standard `GET` method. */
    data object Get : Method {
        override val token: String = "GET"
    }

    /** The standard `PUT` method. */
    data object Put : Method {
        override val token: String = "PUT"
    }

    /** The standard `POST` method. */
    data object Post : Method {
        override val token: String = "POST"
    }

    /** The standard `DELETE` method. */
    data object Delete : Method {
        override val token: String = "DELETE"
    }

    /** The standard `OPTIONS` method. */
    data object Options : Method {
        override val token: String = "OPTIONS"
    }

    /** The standard `HEAD` method. */
    data object Head : Method {
        override val token: String = "HEAD"
    }

    /** The standard `PATCH` method. */
    data object Patch : Method {
        override val token: String = "PATCH"
    }

    /** The standard `TRACE` method. */
    data object Trace : Method {
        override val token: String = "TRACE"
    }

    /** The standard OpenAPI 3.2 `QUERY` method. */
    data object Query : Method {
        override val token: String = "QUERY"
    }

    /**
     * A valid HTTP method represented by OpenAPI 3.2 `additionalOperations`.
     *
     * @throws IllegalArgumentException when [token] is not an HTTP token or duplicates a standard method.
     */
    data class Custom(
        override val token: String
    ) : Method {
        init {
            require(token.isHttpToken()) { "HTTP method must be a non-empty RFC 9110 token, but was '$token'" }
            require(token !in STANDARD_METHOD_TOKENS) {
                "HTTP method '$token' is standard and must use its distinct Method type"
            }
        }
    }
}

private val STANDARD_METHOD_TOKENS: Set<String> =
    setOf("GET", "PUT", "POST", "DELETE", "OPTIONS", "HEAD", "PATCH", "TRACE", "QUERY")

private const val HTTP_TOKEN_PUNCTUATION: String = "!#$%&'*+-.^_`|~"

private fun String.isHttpToken(): Boolean =
    isNotEmpty() && all { character ->
        character in 'A'..'Z' ||
            character in 'a'..'z' ||
            character in '0'..'9' ||
            character in HTTP_TOKEN_PUNCTUATION
    }
