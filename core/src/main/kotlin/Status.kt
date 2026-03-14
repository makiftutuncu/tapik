package dev.akif.tapik

/** Enumerates the known HTTP status codes as defined by RFC 9110 and the IANA registry.
 * @property code numeric HTTP status code backing the enum entry.
 */
enum class Status(
    /** Numeric HTTP status code backing the enum entry. */
    val code: Int
) {
    /*
     * Status codes are defined by RFC 9110 and the IANA HTTP Status Code Registry:
     * - https://www.rfc-editor.org/rfc/rfc9110.html#name-status-codes
     * - https://www.iana.org/assignments/http-status-codes/http-status-codes.xhtml
     */
    Continue(100),
    SwitchingProtocols(101),
    Processing(102),
    EarlyHints(103),
    Ok(200),
    Created(201),
    Accepted(202),
    NonAuthoritativeInformation(203),
    NoContent(204),
    ResetContent(205),
    PartialContent(206),
    MultiStatus(207),
    AlreadyReported(208),
    ImUsed(226),
    MultipleChoices(300),
    MovedPermanently(301),
    Found(302),
    SeeOther(303),
    NotModified(304),
    UseProxy(305),
    Unused(306),
    TemporaryRedirect(307),
    PermanentRedirect(308),
    BadRequest(400),
    Unauthorized(401),
    PaymentRequired(402),
    Forbidden(403),
    NotFound(404),
    MethodNotAllowed(405),
    NotAcceptable(406),
    ProxyAuthenticationRequired(407),
    RequestTimeout(408),
    Conflict(409),
    Gone(410),
    LengthRequired(411),
    PreconditionFailed(412),
    ContentTooLarge(413),
    UriTooLong(414),
    UnsupportedMediaType(415),
    RangeNotSatisfiable(416),
    ExpectationFailed(417),
    ImATeapot(418),
    MisdirectedRequest(421),
    UnprocessableContent(422),
    Locked(423),
    FailedDependency(424),
    TooEarly(425),
    UpgradeRequired(426),
    PreconditionRequired(428),
    TooManyRequests(429),
    RequestHeaderFieldsTooLarge(431),
    UnavailableForLegalReasons(451),
    InternalServerError(500),
    NotImplemented(501),
    BadGateway(502),
    ServiceUnavailable(503),
    GatewayTimeout(504),
    HttpVersionNotSupported(505),
    VariantAlsoNegotiates(506),
    InsufficientStorage(507),
    LoopDetected(508),
    NotExtended(510),
    NetworkAuthenticationRequired(511);

    /** Helpers for resolving [Status] instances. */
    companion object {
        private val codesToStatuses = entries.associateBy { it.code }

        /**
         * Looks up the [Status] that corresponds to [code], throwing if the code is unknown.
         *
         * @param code numeric status code to resolve.
         * @return the matching [Status] entry.
         * @throws IllegalArgumentException when the code is not recognised by Tapik.
         * @see Status.entries
         */
        fun of(code: Int): Status = requireNotNull(codesToStatuses[code]) { "Unknown status code: $code" }
    }
}
