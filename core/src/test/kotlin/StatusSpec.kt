package dev.akif.tapik

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class StatusSpec : FunSpec({
    test("retain valid custom HTTP status codes with value equality") {
        Status(100) shouldBe Status(100)
        Status(299).code shouldBe 299
        Status(599).code shouldBe 599
        (Status(299) with noBody).matcher.matches(Status(299)) shouldBe true
    }

    test("provide every standard HTTP status as a named value") {
        val statuses =
            linkedMapOf(
                100 to Status.Continue,
                101 to Status.SwitchingProtocols,
                102 to Status.Processing,
                103 to Status.EarlyHints,
                200 to Status.Ok,
                201 to Status.Created,
                202 to Status.Accepted,
                203 to Status.NonAuthoritativeInformation,
                204 to Status.NoContent,
                205 to Status.ResetContent,
                206 to Status.PartialContent,
                207 to Status.MultiStatus,
                208 to Status.AlreadyReported,
                226 to Status.ImUsed,
                300 to Status.MultipleChoices,
                301 to Status.MovedPermanently,
                302 to Status.Found,
                303 to Status.SeeOther,
                304 to Status.NotModified,
                305 to Status.UseProxy,
                306 to Status.Unused,
                307 to Status.TemporaryRedirect,
                308 to Status.PermanentRedirect,
                400 to Status.BadRequest,
                401 to Status.Unauthorized,
                402 to Status.PaymentRequired,
                403 to Status.Forbidden,
                404 to Status.NotFound,
                405 to Status.MethodNotAllowed,
                406 to Status.NotAcceptable,
                407 to Status.ProxyAuthenticationRequired,
                408 to Status.RequestTimeout,
                409 to Status.Conflict,
                410 to Status.Gone,
                411 to Status.LengthRequired,
                412 to Status.PreconditionFailed,
                413 to Status.ContentTooLarge,
                414 to Status.UriTooLong,
                415 to Status.UnsupportedMediaType,
                416 to Status.RangeNotSatisfiable,
                417 to Status.ExpectationFailed,
                418 to Status.ImATeapot,
                421 to Status.MisdirectedRequest,
                422 to Status.UnprocessableContent,
                423 to Status.Locked,
                424 to Status.FailedDependency,
                425 to Status.TooEarly,
                426 to Status.UpgradeRequired,
                428 to Status.PreconditionRequired,
                429 to Status.TooManyRequests,
                431 to Status.RequestHeaderFieldsTooLarge,
                451 to Status.UnavailableForLegalReasons,
                500 to Status.InternalServerError,
                501 to Status.NotImplemented,
                502 to Status.BadGateway,
                503 to Status.ServiceUnavailable,
                504 to Status.GatewayTimeout,
                505 to Status.HttpVersionNotSupported,
                506 to Status.VariantAlsoNegotiates,
                507 to Status.InsufficientStorage,
                508 to Status.LoopDetected,
                510 to Status.NotExtended,
                511 to Status.NetworkAuthenticationRequired
            )

        statuses.forEach { (code, status) -> status shouldBe Status(code) }
        statuses.keys shouldBe standardStatusCodes
        statuses.values.forEach { status -> requireNotNull(status.standardDescription) }
        Status.ImATeapot.standardDescription shouldBe "I'm a Teapot"
        Status.NotExtended.standardDescription shouldBe "Not Extended"
        Status(104).standardDescription shouldBe null
        Status(299).standardDescription shouldBe null
    }

    test("reject codes immediately outside the HTTP status domain") {
        shouldThrow<IllegalArgumentException> { Status(99) }
        shouldThrow<IllegalArgumentException> { Status(600) }
    }
})

private val standardStatusCodes: Set<Int> =
    setOf(
        100, 101, 102, 103,
        200, 201, 202, 203, 204, 205, 206, 207, 208, 226,
        300, 301, 302, 303, 304, 305, 306, 307, 308,
        400, 401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417, 418,
        421, 422, 423, 424, 425, 426, 428, 429, 431, 451,
        500, 501, 502, 503, 504, 505, 506, 507, 508, 510, 511
    )
