// This file is auto-generated. Do not edit manually as your changes will be lost.
@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.http

sealed interface Response<out B> {
    val status: Status
}

data class ResponseWithoutBodyWithHeaders0(
    override val status: Status
): Response<Nothing>

data class ResponseWithoutBodyWithHeaders1<H1>(
    override val status: Status,
    val header1: List<H1>
): Response<Nothing>

data class ResponseWithoutBodyWithHeaders2<H1, H2>(
    override val status: Status,
    val header1: List<H1>,
    val header2: List<H2>
): Response<Nothing>

data class ResponseWithoutBodyWithHeaders3<H1, H2, H3>(
    override val status: Status,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>
): Response<Nothing>

data class ResponseWithoutBodyWithHeaders4<H1, H2, H3, H4>(
    override val status: Status,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>
): Response<Nothing>

data class ResponseWithoutBodyWithHeaders5<H1, H2, H3, H4, H5>(
    override val status: Status,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>,
    val header5: List<H5>
): Response<Nothing>

data class ResponseWithoutBodyWithHeaders6<H1, H2, H3, H4, H5, H6>(
    override val status: Status,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>,
    val header5: List<H5>,
    val header6: List<H6>
): Response<Nothing>

data class ResponseWithoutBodyWithHeaders7<H1, H2, H3, H4, H5, H6, H7>(
    override val status: Status,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>,
    val header5: List<H5>,
    val header6: List<H6>,
    val header7: List<H7>
): Response<Nothing>

data class ResponseWithoutBodyWithHeaders8<H1, H2, H3, H4, H5, H6, H7, H8>(
    override val status: Status,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>,
    val header5: List<H5>,
    val header6: List<H6>,
    val header7: List<H7>,
    val header8: List<H8>
): Response<Nothing>

data class ResponseWithoutBodyWithHeaders9<H1, H2, H3, H4, H5, H6, H7, H8, H9>(
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
): Response<Nothing>

data class ResponseWithoutBodyWithHeaders10<H1, H2, H3, H4, H5, H6, H7, H8, H9, H10>(
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
): Response<Nothing>

data class ResponseWithHeaders0<B>(
    override val status: Status,
    val body: B
): Response<B>

data class ResponseWithHeaders1<B, H1>(
    override val status: Status,
    val body: B,
    val header1: List<H1>
): Response<B>

data class ResponseWithHeaders2<B, H1, H2>(
    override val status: Status,
    val body: B,
    val header1: List<H1>,
    val header2: List<H2>
): Response<B>

data class ResponseWithHeaders3<B, H1, H2, H3>(
    override val status: Status,
    val body: B,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>
): Response<B>

data class ResponseWithHeaders4<B, H1, H2, H3, H4>(
    override val status: Status,
    val body: B,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>
): Response<B>

data class ResponseWithHeaders5<B, H1, H2, H3, H4, H5>(
    override val status: Status,
    val body: B,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>,
    val header5: List<H5>
): Response<B>

data class ResponseWithHeaders6<B, H1, H2, H3, H4, H5, H6>(
    override val status: Status,
    val body: B,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>,
    val header5: List<H5>,
    val header6: List<H6>
): Response<B>

data class ResponseWithHeaders7<B, H1, H2, H3, H4, H5, H6, H7>(
    override val status: Status,
    val body: B,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>,
    val header5: List<H5>,
    val header6: List<H6>,
    val header7: List<H7>
): Response<B>

data class ResponseWithHeaders8<B, H1, H2, H3, H4, H5, H6, H7, H8>(
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
): Response<B>

data class ResponseWithHeaders9<B, H1, H2, H3, H4, H5, H6, H7, H8, H9>(
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
): Response<B>

data class ResponseWithHeaders10<B, H1, H2, H3, H4, H5, H6, H7, H8, H9, H10>(
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
): Response<B>
