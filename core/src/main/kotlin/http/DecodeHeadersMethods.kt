// This file is auto-generated. Do not edit manually as your changes will be lost.
package dev.akif.tapik.http

import arrow.core.EitherNel
import arrow.core.leftNel
import arrow.core.raise.either
import arrow.core.right
import dev.akif.tapik.tuples.*

fun <H> decodeHeaders0(): Tuple0<List<H>> =
   Tuple0()

fun <H: Any, H1: H> decodeHeaders1(
    headers: Map<String, List<String>>,
    header1: Header<H1>
): EitherNel<String, Tuple1<List<H>, List<H1>>> =
    either {
        Tuple1(
            decodeHeader(headers, header1).bind()
        )
    }

fun <H: Any, H1: H, H2: H> decodeHeaders2(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>
): EitherNel<String, Tuple2<List<H>, List<H1>, List<H2>>> =
    either {
        Tuple2(
            decodeHeader(headers, header1).bind(),
            decodeHeader(headers, header2).bind()
        )
    }

fun <H: Any, H1: H, H2: H, H3: H> decodeHeaders3(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>
): EitherNel<String, Tuple3<List<H>, List<H1>, List<H2>, List<H3>>> =
    either {
        Tuple3(
            decodeHeader(headers, header1).bind(),
            decodeHeader(headers, header2).bind(),
            decodeHeader(headers, header3).bind()
        )
    }

fun <H: Any, H1: H, H2: H, H3: H, H4: H> decodeHeaders4(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>
): EitherNel<String, Tuple4<List<H>, List<H1>, List<H2>, List<H3>, List<H4>>> =
    either {
        Tuple4(
            decodeHeader(headers, header1).bind(),
            decodeHeader(headers, header2).bind(),
            decodeHeader(headers, header3).bind(),
            decodeHeader(headers, header4).bind()
        )
    }

fun <H: Any, H1: H, H2: H, H3: H, H4: H, H5: H> decodeHeaders5(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>
): EitherNel<String, Tuple5<List<H>, List<H1>, List<H2>, List<H3>, List<H4>, List<H5>>> =
    either {
        Tuple5(
            decodeHeader(headers, header1).bind(),
            decodeHeader(headers, header2).bind(),
            decodeHeader(headers, header3).bind(),
            decodeHeader(headers, header4).bind(),
            decodeHeader(headers, header5).bind()
        )
    }

fun <H: Any, H1: H, H2: H, H3: H, H4: H, H5: H, H6: H> decodeHeaders6(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>,
    header6: Header<H6>
): EitherNel<String, Tuple6<List<H>, List<H1>, List<H2>, List<H3>, List<H4>, List<H5>, List<H6>>> =
    either {
        Tuple6(
            decodeHeader(headers, header1).bind(),
            decodeHeader(headers, header2).bind(),
            decodeHeader(headers, header3).bind(),
            decodeHeader(headers, header4).bind(),
            decodeHeader(headers, header5).bind(),
            decodeHeader(headers, header6).bind()
        )
    }

fun <H: Any, H1: H, H2: H, H3: H, H4: H, H5: H, H6: H, H7: H> decodeHeaders7(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>,
    header6: Header<H6>,
    header7: Header<H7>
): EitherNel<String, Tuple7<List<H>, List<H1>, List<H2>, List<H3>, List<H4>, List<H5>, List<H6>, List<H7>>> =
    either {
        Tuple7(
            decodeHeader(headers, header1).bind(),
            decodeHeader(headers, header2).bind(),
            decodeHeader(headers, header3).bind(),
            decodeHeader(headers, header4).bind(),
            decodeHeader(headers, header5).bind(),
            decodeHeader(headers, header6).bind(),
            decodeHeader(headers, header7).bind()
        )
    }

fun <H: Any, H1: H, H2: H, H3: H, H4: H, H5: H, H6: H, H7: H, H8: H> decodeHeaders8(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>,
    header6: Header<H6>,
    header7: Header<H7>,
    header8: Header<H8>
): EitherNel<String, Tuple8<List<H>, List<H1>, List<H2>, List<H3>, List<H4>, List<H5>, List<H6>, List<H7>, List<H8>>> =
    either {
        Tuple8(
            decodeHeader(headers, header1).bind(),
            decodeHeader(headers, header2).bind(),
            decodeHeader(headers, header3).bind(),
            decodeHeader(headers, header4).bind(),
            decodeHeader(headers, header5).bind(),
            decodeHeader(headers, header6).bind(),
            decodeHeader(headers, header7).bind(),
            decodeHeader(headers, header8).bind()
        )
    }

fun <H: Any, H1: H, H2: H, H3: H, H4: H, H5: H, H6: H, H7: H, H8: H, H9: H> decodeHeaders9(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>,
    header6: Header<H6>,
    header7: Header<H7>,
    header8: Header<H8>,
    header9: Header<H9>
): EitherNel<String, Tuple9<List<H>, List<H1>, List<H2>, List<H3>, List<H4>, List<H5>, List<H6>, List<H7>, List<H8>, List<H9>>> =
    either {
        Tuple9(
            decodeHeader(headers, header1).bind(),
            decodeHeader(headers, header2).bind(),
            decodeHeader(headers, header3).bind(),
            decodeHeader(headers, header4).bind(),
            decodeHeader(headers, header5).bind(),
            decodeHeader(headers, header6).bind(),
            decodeHeader(headers, header7).bind(),
            decodeHeader(headers, header8).bind(),
            decodeHeader(headers, header9).bind()
        )
    }

fun <H: Any, H1: H, H2: H, H3: H, H4: H, H5: H, H6: H, H7: H, H8: H, H9: H, H10: H> decodeHeaders10(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>,
    header6: Header<H6>,
    header7: Header<H7>,
    header8: Header<H8>,
    header9: Header<H9>,
    header10: Header<H10>
): EitherNel<String, Tuple10<List<H>, List<H1>, List<H2>, List<H3>, List<H4>, List<H5>, List<H6>, List<H7>, List<H8>, List<H9>, List<H10>>> =
    either {
        Tuple10(
            decodeHeader(headers, header1).bind(),
            decodeHeader(headers, header2).bind(),
            decodeHeader(headers, header3).bind(),
            decodeHeader(headers, header4).bind(),
            decodeHeader(headers, header5).bind(),
            decodeHeader(headers, header6).bind(),
            decodeHeader(headers, header7).bind(),
            decodeHeader(headers, header8).bind(),
            decodeHeader(headers, header9).bind(),
            decodeHeader(headers, header10).bind()
        )
    }

fun <H: Any, H1: H, H2: H, H3: H, H4: H, H5: H, H6: H, H7: H, H8: H, H9: H, H10: H, H11: H> decodeHeaders11(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>,
    header6: Header<H6>,
    header7: Header<H7>,
    header8: Header<H8>,
    header9: Header<H9>,
    header10: Header<H10>,
    header11: Header<H11>
): EitherNel<String, Tuple11<List<H>, List<H1>, List<H2>, List<H3>, List<H4>, List<H5>, List<H6>, List<H7>, List<H8>, List<H9>, List<H10>, List<H11>>> =
    either {
        Tuple11(
            decodeHeader(headers, header1).bind(),
            decodeHeader(headers, header2).bind(),
            decodeHeader(headers, header3).bind(),
            decodeHeader(headers, header4).bind(),
            decodeHeader(headers, header5).bind(),
            decodeHeader(headers, header6).bind(),
            decodeHeader(headers, header7).bind(),
            decodeHeader(headers, header8).bind(),
            decodeHeader(headers, header9).bind(),
            decodeHeader(headers, header10).bind(),
            decodeHeader(headers, header11).bind()
        )
    }

fun <H: Any, H1: H, H2: H, H3: H, H4: H, H5: H, H6: H, H7: H, H8: H, H9: H, H10: H, H11: H, H12: H> decodeHeaders12(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>,
    header6: Header<H6>,
    header7: Header<H7>,
    header8: Header<H8>,
    header9: Header<H9>,
    header10: Header<H10>,
    header11: Header<H11>,
    header12: Header<H12>
): EitherNel<String, Tuple12<List<H>, List<H1>, List<H2>, List<H3>, List<H4>, List<H5>, List<H6>, List<H7>, List<H8>, List<H9>, List<H10>, List<H11>, List<H12>>> =
    either {
        Tuple12(
            decodeHeader(headers, header1).bind(),
            decodeHeader(headers, header2).bind(),
            decodeHeader(headers, header3).bind(),
            decodeHeader(headers, header4).bind(),
            decodeHeader(headers, header5).bind(),
            decodeHeader(headers, header6).bind(),
            decodeHeader(headers, header7).bind(),
            decodeHeader(headers, header8).bind(),
            decodeHeader(headers, header9).bind(),
            decodeHeader(headers, header10).bind(),
            decodeHeader(headers, header11).bind(),
            decodeHeader(headers, header12).bind()
        )
    }

fun <H: Any, H1: H, H2: H, H3: H, H4: H, H5: H, H6: H, H7: H, H8: H, H9: H, H10: H, H11: H, H12: H, H13: H> decodeHeaders13(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>,
    header6: Header<H6>,
    header7: Header<H7>,
    header8: Header<H8>,
    header9: Header<H9>,
    header10: Header<H10>,
    header11: Header<H11>,
    header12: Header<H12>,
    header13: Header<H13>
): EitherNel<String, Tuple13<List<H>, List<H1>, List<H2>, List<H3>, List<H4>, List<H5>, List<H6>, List<H7>, List<H8>, List<H9>, List<H10>, List<H11>, List<H12>, List<H13>>> =
    either {
        Tuple13(
            decodeHeader(headers, header1).bind(),
            decodeHeader(headers, header2).bind(),
            decodeHeader(headers, header3).bind(),
            decodeHeader(headers, header4).bind(),
            decodeHeader(headers, header5).bind(),
            decodeHeader(headers, header6).bind(),
            decodeHeader(headers, header7).bind(),
            decodeHeader(headers, header8).bind(),
            decodeHeader(headers, header9).bind(),
            decodeHeader(headers, header10).bind(),
            decodeHeader(headers, header11).bind(),
            decodeHeader(headers, header12).bind(),
            decodeHeader(headers, header13).bind()
        )
    }

fun <H: Any, H1: H, H2: H, H3: H, H4: H, H5: H, H6: H, H7: H, H8: H, H9: H, H10: H, H11: H, H12: H, H13: H, H14: H> decodeHeaders14(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>,
    header6: Header<H6>,
    header7: Header<H7>,
    header8: Header<H8>,
    header9: Header<H9>,
    header10: Header<H10>,
    header11: Header<H11>,
    header12: Header<H12>,
    header13: Header<H13>,
    header14: Header<H14>
): EitherNel<String, Tuple14<List<H>, List<H1>, List<H2>, List<H3>, List<H4>, List<H5>, List<H6>, List<H7>, List<H8>, List<H9>, List<H10>, List<H11>, List<H12>, List<H13>, List<H14>>> =
    either {
        Tuple14(
            decodeHeader(headers, header1).bind(),
            decodeHeader(headers, header2).bind(),
            decodeHeader(headers, header3).bind(),
            decodeHeader(headers, header4).bind(),
            decodeHeader(headers, header5).bind(),
            decodeHeader(headers, header6).bind(),
            decodeHeader(headers, header7).bind(),
            decodeHeader(headers, header8).bind(),
            decodeHeader(headers, header9).bind(),
            decodeHeader(headers, header10).bind(),
            decodeHeader(headers, header11).bind(),
            decodeHeader(headers, header12).bind(),
            decodeHeader(headers, header13).bind(),
            decodeHeader(headers, header14).bind()
        )
    }

fun <H: Any, H1: H, H2: H, H3: H, H4: H, H5: H, H6: H, H7: H, H8: H, H9: H, H10: H, H11: H, H12: H, H13: H, H14: H, H15: H> decodeHeaders15(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>,
    header6: Header<H6>,
    header7: Header<H7>,
    header8: Header<H8>,
    header9: Header<H9>,
    header10: Header<H10>,
    header11: Header<H11>,
    header12: Header<H12>,
    header13: Header<H13>,
    header14: Header<H14>,
    header15: Header<H15>
): EitherNel<String, Tuple15<List<H>, List<H1>, List<H2>, List<H3>, List<H4>, List<H5>, List<H6>, List<H7>, List<H8>, List<H9>, List<H10>, List<H11>, List<H12>, List<H13>, List<H14>, List<H15>>> =
    either {
        Tuple15(
            decodeHeader(headers, header1).bind(),
            decodeHeader(headers, header2).bind(),
            decodeHeader(headers, header3).bind(),
            decodeHeader(headers, header4).bind(),
            decodeHeader(headers, header5).bind(),
            decodeHeader(headers, header6).bind(),
            decodeHeader(headers, header7).bind(),
            decodeHeader(headers, header8).bind(),
            decodeHeader(headers, header9).bind(),
            decodeHeader(headers, header10).bind(),
            decodeHeader(headers, header11).bind(),
            decodeHeader(headers, header12).bind(),
            decodeHeader(headers, header13).bind(),
            decodeHeader(headers, header14).bind(),
            decodeHeader(headers, header15).bind()
        )
    }

fun <H: Any, H1: H, H2: H, H3: H, H4: H, H5: H, H6: H, H7: H, H8: H, H9: H, H10: H, H11: H, H12: H, H13: H, H14: H, H15: H, H16: H> decodeHeaders16(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>,
    header6: Header<H6>,
    header7: Header<H7>,
    header8: Header<H8>,
    header9: Header<H9>,
    header10: Header<H10>,
    header11: Header<H11>,
    header12: Header<H12>,
    header13: Header<H13>,
    header14: Header<H14>,
    header15: Header<H15>,
    header16: Header<H16>
): EitherNel<String, Tuple16<List<H>, List<H1>, List<H2>, List<H3>, List<H4>, List<H5>, List<H6>, List<H7>, List<H8>, List<H9>, List<H10>, List<H11>, List<H12>, List<H13>, List<H14>, List<H15>, List<H16>>> =
    either {
        Tuple16(
            decodeHeader(headers, header1).bind(),
            decodeHeader(headers, header2).bind(),
            decodeHeader(headers, header3).bind(),
            decodeHeader(headers, header4).bind(),
            decodeHeader(headers, header5).bind(),
            decodeHeader(headers, header6).bind(),
            decodeHeader(headers, header7).bind(),
            decodeHeader(headers, header8).bind(),
            decodeHeader(headers, header9).bind(),
            decodeHeader(headers, header10).bind(),
            decodeHeader(headers, header11).bind(),
            decodeHeader(headers, header12).bind(),
            decodeHeader(headers, header13).bind(),
            decodeHeader(headers, header14).bind(),
            decodeHeader(headers, header15).bind(),
            decodeHeader(headers, header16).bind()
        )
    }

private fun <H: Any> decodeHeader(
   headers: Map<String, List<String>>,
   header: Header<H>
): EitherNel<String, List<H>> =
    when (header) {
        is HeaderInput<H> -> {
            val values = headers[header.name]
            if (values == null && header.required) {
                "Required header '${header.name}' is missing".leftNel()
            } else {
                val initial: EitherNel<String, List<H>> = emptyList<H>().right()
                values.orEmpty().fold(initial) { results, value ->
                    header.codec.decode(value).fold(
                        { errors -> results.mapLeft { it + errors } },
                        { h -> results.map { it + h } }
                    )
                }
            }
        }
        is HeaderValues<H> -> header.values.right()
    }