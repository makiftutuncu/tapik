package dev.akif.tapik.http

sealed interface Response<out B> {
    val status: Status
}

data class ResponseWithoutBody0(
    override val status: Status
) : Response<Nothing>

data class ResponseWithoutBody1<H1>(
    override val status: Status,
    val header1: List<H1>
) : Response<Nothing>

data class ResponseWithoutBody2<H1, H2>(
    override val status: Status,
    val header1: List<H1>,
    val header2: List<H2>
) : Response<Nothing>

data class ResponseWithoutBody3<H1, H2, H3>(
    override val status: Status,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>
) : Response<Nothing>

data class ResponseWithoutBody4<H1, H2, H3, H4>(
    override val status: Status,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>
) : Response<Nothing>

data class ResponseWithoutBody5<H1, H2, H3, H4, H5>(
    override val status: Status,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>,
    val header5: List<H5>
) : Response<Nothing>

data class ResponseWithoutBody6<H1, H2, H3, H4, H5, H6>(
    override val status: Status,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>,
    val header5: List<H5>,
    val header6: List<H6>
) : Response<Nothing>

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

data class Response0<B>(
    override val status: Status,
    val body: B
) : Response<B>

data class Response1<B, H1>(
    override val status: Status,
    val body: B,
    val header1: List<H1>
) : Response<B>

data class Response2<B, H1, H2>(
    override val status: Status,
    val body: B,
    val header1: List<H1>,
    val header2: List<H2>
) : Response<B>

data class Response3<B, H1, H2, H3>(
    override val status: Status,
    val body: B,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>
) : Response<B>

data class Response4<B, H1, H2, H3, H4>(
    override val status: Status,
    val body: B,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>
) : Response<B>

data class Response5<B, H1, H2, H3, H4, H5>(
    override val status: Status,
    val body: B,
    val header1: List<H1>,
    val header2: List<H2>,
    val header3: List<H3>,
    val header4: List<H4>,
    val header5: List<H5>
) : Response<B>

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
