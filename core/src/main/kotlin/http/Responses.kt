// This file is auto-generated. Do not edit manually as your changes will be lost.
package dev.akif.tapik.http

sealed interface Response<out B> {
    val status: Status
}

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

data class ResponseWithHeaders11<B, H1, H2, H3, H4, H5, H6, H7, H8, H9, H10, H11>(
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
    val header10: List<H10>,
    val header11: List<H11>
): Response<B>

data class ResponseWithHeaders12<B, H1, H2, H3, H4, H5, H6, H7, H8, H9, H10, H11, H12>(
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
    val header10: List<H10>,
    val header11: List<H11>,
    val header12: List<H12>
): Response<B>

data class ResponseWithHeaders13<B, H1, H2, H3, H4, H5, H6, H7, H8, H9, H10, H11, H12, H13>(
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
    val header10: List<H10>,
    val header11: List<H11>,
    val header12: List<H12>,
    val header13: List<H13>
): Response<B>

data class ResponseWithHeaders14<B, H1, H2, H3, H4, H5, H6, H7, H8, H9, H10, H11, H12, H13, H14>(
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
    val header10: List<H10>,
    val header11: List<H11>,
    val header12: List<H12>,
    val header13: List<H13>,
    val header14: List<H14>
): Response<B>

data class ResponseWithHeaders15<B, H1, H2, H3, H4, H5, H6, H7, H8, H9, H10, H11, H12, H13, H14, H15>(
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
    val header10: List<H10>,
    val header11: List<H11>,
    val header12: List<H12>,
    val header13: List<H13>,
    val header14: List<H14>,
    val header15: List<H15>
): Response<B>

data class ResponseWithHeaders16<B, H1, H2, H3, H4, H5, H6, H7, H8, H9, H10, H11, H12, H13, H14, H15, H16>(
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
    val header10: List<H10>,
    val header11: List<H11>,
    val header12: List<H12>,
    val header13: List<H13>,
    val header14: List<H14>,
    val header15: List<H15>,
    val header16: List<H16>
): Response<B>
