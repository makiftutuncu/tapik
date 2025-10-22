package dev.akif.tapik.http

/**
 * Describes a decoded HTTP response together with its status code.
 *
 * @param B type of the decoded body payload.
 * @property status status code returned by the server.
 */
sealed interface Response<out B> {
    val status: Status
}

/**
 * Response without a body.
 *
 */
data class ResponseWithoutBody0(
    override val status: Status
) : Response<Nothing>

/**
 * Response without a body carrying one header collection.
 *
 * @property header1 decoded values of the first header definition.
 */
data class ResponseWithoutBody1<H1>(
    override val status: Status,
    val header1: List<H1>
) : Response<Nothing>

/**
 * Response without a body carrying 2 header collections.
 *
 * @property header1 decoded values of the first header definition.
 * @property header2 decoded values of the second header definition.
 */
data class ResponseWithoutBody2<H1, H2>(
    override val status: Status,
    val header1: List<H1>,
    val header2: List<H2>
) : Response<Nothing>

/**
 * Response without a body carrying 3 header collections.
 *
 * @property header1 decoded values of the first header definition.
 * @property header2 decoded values of the second header definition.
 * @property header3 decoded values of the third header definition.
 */
data class ResponseWithoutBody3<H1, H2, H3>(
    override val status: Status,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>
) : Response<Nothing>

/**
 * Response without a body carrying 4 header collections.
 *
 * @property header1 decoded values of the first header definition.
 * @property header2 decoded values of the second header definition.
 * @property header3 decoded values of the third header definition.
 * @property header4 decoded values of the 4th header definition.
 */
data class ResponseWithoutBody4<H1, H2, H3, H4>(
    override val status: Status,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>
) : Response<Nothing>

/**
 * Response without a body carrying 5 header collections.
 *
 * @property header1 decoded values of the first header definition.
 * @property header2 decoded values of the second header definition.
 * @property header3 decoded values of the third header definition.
 * @property header4 decoded values of the 4th header definition.
 * @property header5 decoded values of the 5th header definition.
 */
data class ResponseWithoutBody5<H1, H2, H3, H4, H5>(
    override val status: Status,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>,
    val header5: List<H5>
) : Response<Nothing>

/**
 * Response without a body carrying 6 header collections.
 *
 * @property header1 decoded values of the first header definition.
 * @property header2 decoded values of the second header definition.
 * @property header3 decoded values of the third header definition.
 * @property header4 decoded values of the 4th header definition.
 * @property header5 decoded values of the 5th header definition.
 * @property header6 decoded values of the 6th header definition.
 */
data class ResponseWithoutBody6<H1, H2, H3, H4, H5, H6>(
    override val status: Status,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>,
    val header5: List<H5>,
    val header6: List<H6>
) : Response<Nothing>

/**
 * Response without a body carrying 7 header collections.
 *
 * @property header1 decoded values of the first header definition.
 * @property header2 decoded values of the second header definition.
 * @property header3 decoded values of the third header definition.
 * @property header4 decoded values of the 4th header definition.
 * @property header5 decoded values of the 5th header definition.
 * @property header6 decoded values of the 6th header definition.
 * @property header7 decoded values of the 7th header definition.
 */
data class ResponseWithoutBody7<H1, H2, H3, H4, H5, H6, H7>(
    override val status: Status,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>,
    val header5: List<H5>,
    val header6: List<H6>,
    val header7: List<H7>
) : Response<Nothing>

/**
 * Response without a body carrying 8 header collections.
 *
 * @property header1 decoded values of the first header definition.
 * @property header2 decoded values of the second header definition.
 * @property header3 decoded values of the third header definition.
 * @property header4 decoded values of the 4th header definition.
 * @property header5 decoded values of the 5th header definition.
 * @property header6 decoded values of the 6th header definition.
 * @property header7 decoded values of the 7th header definition.
 * @property header8 decoded values of the 8th header definition.
 */
data class ResponseWithoutBody8<H1, H2, H3, H4, H5, H6, H7, H8>(
    override val status: Status,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>,
    val header5: List<H5>,
    val header6: List<H6>,
    val header7: List<H7>,
    val header8: List<H8>
) : Response<Nothing>

/**
 * Response without a body carrying 9 header collections.
 *
 * @property header1 decoded values of the first header definition.
 * @property header2 decoded values of the second header definition.
 * @property header3 decoded values of the third header definition.
 * @property header4 decoded values of the 4th header definition.
 * @property header5 decoded values of the 5th header definition.
 * @property header6 decoded values of the 6th header definition.
 * @property header7 decoded values of the 7th header definition.
 * @property header8 decoded values of the 8th header definition.
 * @property header9 decoded values of the 9th header definition.
 */
data class ResponseWithoutBody9<H1, H2, H3, H4, H5, H6, H7, H8, H9>(
    override val status: Status,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>,
    val header5: List<H5>,
    val header6: List<H6>,
    val header7: List<H7>,
    val header8: List<H8>,
    val header9: List<H9>
) : Response<Nothing>

/**
 * Response without a body carrying 10 header collections.
 *
 * @property header1 decoded values of the first header definition.
 * @property header2 decoded values of the second header definition.
 * @property header3 decoded values of the third header definition.
 * @property header4 decoded values of the 4th header definition.
 * @property header5 decoded values of the 5th header definition.
 * @property header6 decoded values of the 6th header definition.
 * @property header7 decoded values of the 7th header definition.
 * @property header8 decoded values of the 8th header definition.
 * @property header9 decoded values of the 9th header definition.
 * @property header10 decoded values of the 10th header definition.
 */
data class ResponseWithoutBody10<H1, H2, H3, H4, H5, H6, H7, H8, H9, H10>(
    override val status: Status,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>,
    val header5: List<H5>,
    val header6: List<H6>,
    val header7: List<H7>,
    val header8: List<H8>,
    val header9: List<H9>,
    val header10: List<H10>
) : Response<Nothing>

/**
 * Response carrying a body.
 *
 * @property body decoded response body.
 */
data class Response0<B>(
    override val status: Status,
    val body: B
) : Response<B>

/**
 * Response carrying a body and one header collection.
 *
 * @property body decoded response body.
 * @property header1 decoded values of the first header definition.
 */
data class Response1<B, H1>(
    override val status: Status,
    val body: B,
    val header1: List<H1>
) : Response<B>

/**
 * Response carrying a body and 2 header collections.
 *
 * @property body decoded response body.
 * @property header1 decoded values of the first header definition.
 * @property header2 decoded values of the second header definition.
 */
data class Response2<B, H1, H2>(
    override val status: Status,
    val body: B,
    val header1: List<H1>,
    val header2: List<H2>
) : Response<B>

/**
 * Response carrying a body and 3 header collections.
 *
 * @property body decoded response body.
 * @property header1 decoded values of the first header definition.
 * @property header2 decoded values of the second header definition.
 * @property header3 decoded values of the third header definition.
 */
data class Response3<B, H1, H2, H3>(
    override val status: Status,
    val body: B,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>
) : Response<B>

/**
 * Response carrying a body and 4 header collections.
 *
 * @property body decoded response body.
 * @property header1 decoded values of the first header definition.
 * @property header2 decoded values of the second header definition.
 * @property header3 decoded values of the third header definition.
 * @property header4 decoded values of the 4th header definition.
 */
data class Response4<B, H1, H2, H3, H4>(
    override val status: Status,
    val body: B,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>
) : Response<B>

/**
 * Response carrying a body and 5 header collections.
 *
 * @property body decoded response body.
 * @property header1 decoded values of the first header definition.
 * @property header2 decoded values of the second header definition.
 * @property header3 decoded values of the third header definition.
 * @property header4 decoded values of the 4th header definition.
 * @property header5 decoded values of the 5th header definition.
 */
data class Response5<B, H1, H2, H3, H4, H5>(
    override val status: Status,
    val body: B,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>,
    val header5: List<H5>
) : Response<B>

/**
 * Response carrying a body and 6 header collections.
 *
 * @property body decoded response body.
 * @property header1 decoded values of the first header definition.
 * @property header2 decoded values of the second header definition.
 * @property header3 decoded values of the third header definition.
 * @property header4 decoded values of the 4th header definition.
 * @property header5 decoded values of the 5th header definition.
 * @property header6 decoded values of the 6th header definition.
 */
data class Response6<B, H1, H2, H3, H4, H5, H6>(
    override val status: Status,
    val body: B,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>,
    val header5: List<H5>,
    val header6: List<H6>
) : Response<B>

/**
 * Response carrying a body and 7 header collections.
 *
 * @property body decoded response body.
 * @property header1 decoded values of the first header definition.
 * @property header2 decoded values of the second header definition.
 * @property header3 decoded values of the third header definition.
 * @property header4 decoded values of the 4th header definition.
 * @property header5 decoded values of the 5th header definition.
 * @property header6 decoded values of the 6th header definition.
 * @property header7 decoded values of the 7th header definition.
 */
data class Response7<B, H1, H2, H3, H4, H5, H6, H7>(
    override val status: Status,
    val body: B,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>,
    val header5: List<H5>,
    val header6: List<H6>,
    val header7: List<H7>
) : Response<B>

/**
 * Response carrying a body and 8 header collections.
 *
 * @property body decoded response body.
 * @property header1 decoded values of the first header definition.
 * @property header2 decoded values of the second header definition.
 * @property header3 decoded values of the third header definition.
 * @property header4 decoded values of the 4th header definition.
 * @property header5 decoded values of the 5th header definition.
 * @property header6 decoded values of the 6th header definition.
 * @property header7 decoded values of the 7th header definition.
 * @property header8 decoded values of the 8th header definition.
 */
data class Response8<B, H1, H2, H3, H4, H5, H6, H7, H8>(
    override val status: Status,
    val body: B,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>,
    val header5: List<H5>,
    val header6: List<H6>,
    val header7: List<H7>,
    val header8: List<H8>
) : Response<B>

/**
 * Response carrying a body and 9 header collections.
 *
 * @property body decoded response body.
 * @property header1 decoded values of the first header definition.
 * @property header2 decoded values of the second header definition.
 * @property header3 decoded values of the third header definition.
 * @property header4 decoded values of the 4th header definition.
 * @property header5 decoded values of the 5th header definition.
 * @property header6 decoded values of the 6th header definition.
 * @property header7 decoded values of the 7th header definition.
 * @property header8 decoded values of the 8th header definition.
 * @property header9 decoded values of the 9th header definition.
 */
data class Response9<B, H1, H2, H3, H4, H5, H6, H7, H8, H9>(
    override val status: Status,
    val body: B,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>,
    val header5: List<H5>,
    val header6: List<H6>,
    val header7: List<H7>,
    val header8: List<H8>,
    val header9: List<H9>
) : Response<B>

/**
 * Response carrying a body and 10 header collections.
 *
 * @property body decoded response body.
 * @property header1 decoded values of the first header definition.
 * @property header2 decoded values of the second header definition.
 * @property header3 decoded values of the third header definition.
 * @property header4 decoded values of the 4th header definition.
 * @property header5 decoded values of the 5th header definition.
 * @property header6 decoded values of the 6th header definition.
 * @property header7 decoded values of the 7th header definition.
 * @property header8 decoded values of the 8th header definition.
 * @property header9 decoded values of the 9th header definition.
 * @property header10 decoded values of the 10th header definition.
 */
data class Response10<B, H1, H2, H3, H4, H5, H6, H7, H8, H9, H10>(
    override val status: Status,
    val body: B,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>,
    val header5: List<H5>,
    val header6: List<H6>,
    val header7: List<H7>,
    val header8: List<H8>,
    val header9: List<H9>,
    val header10: List<H10>
) : Response<B>
