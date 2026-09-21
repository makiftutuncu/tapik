package dev.akif.tapik

/**
 * An HTTP response status code.
 *
 * tapik accepts the complete HTTP status domain instead of limiting contracts to a closed enum of registered codes.
 * Named values in [Companion] provide the stable standard HTTP status catalog without preventing custom codes.
 *
 * @property code numeric status code between 100 and 599.
 * @throws IllegalArgumentException when [code] is outside the HTTP status domain.
 */
data class Status(
    val code: Int
) {
    init {
        require(code in MIN_CODE..MAX_CODE) {
            "HTTP status code must be between $MIN_CODE and $MAX_CODE, but was $code"
        }
    }

    /** The registered standard description for [code], or `null` when this is a custom or temporary status. */
    val standardDescription: String?
        get() = standardDescriptions[code]

    /** Standard HTTP status values grouped by status class. */
    companion object {
        private const val MIN_CODE: Int = 100
        private const val MAX_CODE: Int = 599
        private val standardDescriptions: MutableMap<Int, String> = mutableMapOf()

        // Informational

        /** `100 Continue`. */
        val Continue: Status = standard(100, "Continue")

        /** `101 Switching Protocols`. */
        val SwitchingProtocols: Status = standard(101, "Switching Protocols")

        /** `102 Processing`. */
        val Processing: Status = standard(102, "Processing")

        /** `103 Early Hints`. */
        val EarlyHints: Status = standard(103, "Early Hints")

        // Success

        /** `200 OK`. */
        val Ok: Status = standard(200, "OK")

        /** `201 Created`. */
        val Created: Status = standard(201, "Created")

        /** `202 Accepted`. */
        val Accepted: Status = standard(202, "Accepted")

        /** `203 Non-Authoritative Information`. */
        val NonAuthoritativeInformation: Status = standard(203, "Non-Authoritative Information")

        /** `204 No Content`. */
        val NoContent: Status = standard(204, "No Content")

        /** `205 Reset Content`. */
        val ResetContent: Status = standard(205, "Reset Content")

        /** `206 Partial Content`. */
        val PartialContent: Status = standard(206, "Partial Content")

        /** `207 Multi-Status`. */
        val MultiStatus: Status = standard(207, "Multi-Status")

        /** `208 Already Reported`. */
        val AlreadyReported: Status = standard(208, "Already Reported")

        /** `226 IM Used`. */
        val ImUsed: Status = standard(226, "IM Used")

        // Redirection

        /** `300 Multiple Choices`. */
        val MultipleChoices: Status = standard(300, "Multiple Choices")

        /** `301 Moved Permanently`. */
        val MovedPermanently: Status = standard(301, "Moved Permanently")

        /** `302 Found`. */
        val Found: Status = standard(302, "Found")

        /** `303 See Other`. */
        val SeeOther: Status = standard(303, "See Other")

        /** `304 Not Modified`. */
        val NotModified: Status = standard(304, "Not Modified")

        /** `305 Use Proxy`. */
        val UseProxy: Status = standard(305, "Use Proxy")

        /** `306 (Unused)`. */
        val Unused: Status = standard(306, "(Unused)")

        /** `307 Temporary Redirect`. */
        val TemporaryRedirect: Status = standard(307, "Temporary Redirect")

        /** `308 Permanent Redirect`. */
        val PermanentRedirect: Status = standard(308, "Permanent Redirect")

        // Client errors

        /** `400 Bad Request`. */
        val BadRequest: Status = standard(400, "Bad Request")

        /** `401 Unauthorized`. */
        val Unauthorized: Status = standard(401, "Unauthorized")

        /** `402 Payment Required`. */
        val PaymentRequired: Status = standard(402, "Payment Required")

        /** `403 Forbidden`. */
        val Forbidden: Status = standard(403, "Forbidden")

        /** `404 Not Found`. */
        val NotFound: Status = standard(404, "Not Found")

        /** `405 Method Not Allowed`. */
        val MethodNotAllowed: Status = standard(405, "Method Not Allowed")

        /** `406 Not Acceptable`. */
        val NotAcceptable: Status = standard(406, "Not Acceptable")

        /** `407 Proxy Authentication Required`. */
        val ProxyAuthenticationRequired: Status = standard(407, "Proxy Authentication Required")

        /** `408 Request Timeout`. */
        val RequestTimeout: Status = standard(408, "Request Timeout")

        /** `409 Conflict`. */
        val Conflict: Status = standard(409, "Conflict")

        /** `410 Gone`. */
        val Gone: Status = standard(410, "Gone")

        /** `411 Length Required`. */
        val LengthRequired: Status = standard(411, "Length Required")

        /** `412 Precondition Failed`. */
        val PreconditionFailed: Status = standard(412, "Precondition Failed")

        /** `413 Content Too Large`. */
        val ContentTooLarge: Status = standard(413, "Content Too Large")

        /** `414 URI Too Long`. */
        val UriTooLong: Status = standard(414, "URI Too Long")

        /** `415 Unsupported Media Type`. */
        val UnsupportedMediaType: Status = standard(415, "Unsupported Media Type")

        /** `416 Range Not Satisfiable`. */
        val RangeNotSatisfiable: Status = standard(416, "Range Not Satisfiable")

        /** `417 Expectation Failed`. */
        val ExpectationFailed: Status = standard(417, "Expectation Failed")

        /** `418 I'm a Teapot`, retained as the conventional name for the registry's unused code. */
        val ImATeapot: Status = standard(418, "I'm a Teapot")

        /** `421 Misdirected Request`. */
        val MisdirectedRequest: Status = standard(421, "Misdirected Request")

        /** `422 Unprocessable Content`. */
        val UnprocessableContent: Status = standard(422, "Unprocessable Content")

        /** `423 Locked`. */
        val Locked: Status = standard(423, "Locked")

        /** `424 Failed Dependency`. */
        val FailedDependency: Status = standard(424, "Failed Dependency")

        /** `425 Too Early`. */
        val TooEarly: Status = standard(425, "Too Early")

        /** `426 Upgrade Required`. */
        val UpgradeRequired: Status = standard(426, "Upgrade Required")

        /** `428 Precondition Required`. */
        val PreconditionRequired: Status = standard(428, "Precondition Required")

        /** `429 Too Many Requests`. */
        val TooManyRequests: Status = standard(429, "Too Many Requests")

        /** `431 Request Header Fields Too Large`. */
        val RequestHeaderFieldsTooLarge: Status = standard(431, "Request Header Fields Too Large")

        /** `451 Unavailable For Legal Reasons`. */
        val UnavailableForLegalReasons: Status = standard(451, "Unavailable For Legal Reasons")

        // Server errors

        /** `500 Internal Server Error`. */
        val InternalServerError: Status = standard(500, "Internal Server Error")

        /** `501 Not Implemented`. */
        val NotImplemented: Status = standard(501, "Not Implemented")

        /** `502 Bad Gateway`. */
        val BadGateway: Status = standard(502, "Bad Gateway")

        /** `503 Service Unavailable`. */
        val ServiceUnavailable: Status = standard(503, "Service Unavailable")

        /** `504 Gateway Timeout`. */
        val GatewayTimeout: Status = standard(504, "Gateway Timeout")

        /** `505 HTTP Version Not Supported`. */
        val HttpVersionNotSupported: Status = standard(505, "HTTP Version Not Supported")

        /** `506 Variant Also Negotiates`. */
        val VariantAlsoNegotiates: Status = standard(506, "Variant Also Negotiates")

        /** `507 Insufficient Storage`. */
        val InsufficientStorage: Status = standard(507, "Insufficient Storage")

        /** `508 Loop Detected`. */
        val LoopDetected: Status = standard(508, "Loop Detected")

        /** `510 Not Extended`, retained although the registration is obsolete. */
        val NotExtended: Status = standard(510, "Not Extended")

        /** `511 Network Authentication Required`. */
        val NetworkAuthenticationRequired: Status = standard(511, "Network Authentication Required")

        private fun standard(
            code: Int,
            description: String
        ): Status = Status(code).also { standardDescriptions[code] = description }
    }
}
