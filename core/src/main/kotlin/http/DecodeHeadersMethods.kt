@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.http

import arrow.core.Either.Companion.zipOrAccumulate
import arrow.core.EitherNel
import arrow.core.leftNel
import arrow.core.right
import dev.akif.tapik.*

fun decodeHeaders0(): Tuple0 = Tuple0

fun <H1 : Any> decodeHeaders1(
    headers: Map<String, List<String>>,
    header1: Header<H1>
): EitherNel<String, Tuple1<List<H1>>> = decodeHeader(headers, header1).map { Tuple1(it) }

fun <H1 : Any, H2 : Any> decodeHeaders2(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>
): EitherNel<String, Tuple2<List<H1>, List<H2>>> =
    zipOrAccumulate(
        decodeHeader(headers, header1),
        decodeHeader(headers, header2)
    ) { values1, values2 ->
        Tuple2(values1, values2)
    }

fun <H1 : Any, H2 : Any, H3 : Any> decodeHeaders3(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>
): EitherNel<String, Tuple3<List<H1>, List<H2>, List<H3>>> =
    zipOrAccumulate(
        decodeHeader(headers, header1),
        decodeHeader(headers, header2),
        decodeHeader(headers, header3)
    ) { values1, values2, values3 ->
        Tuple3(values1, values2, values3)
    }

fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any> decodeHeaders4(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>
): EitherNel<String, Tuple4<List<H1>, List<H2>, List<H3>, List<H4>>> =
    zipOrAccumulate(
        decodeHeader(headers, header1),
        decodeHeader(headers, header2),
        decodeHeader(headers, header3),
        decodeHeader(headers, header4)
    ) { values1, values2, values3, values4 ->
        Tuple4(values1, values2, values3, values4)
    }

fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any> decodeHeaders5(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>
): EitherNel<String, Tuple5<List<H1>, List<H2>, List<H3>, List<H4>, List<H5>>> =
    zipOrAccumulate(
        decodeHeader(headers, header1),
        decodeHeader(headers, header2),
        decodeHeader(headers, header3),
        decodeHeader(headers, header4),
        decodeHeader(headers, header5)
    ) { values1, values2, values3, values4, values5 ->
        Tuple5(values1, values2, values3, values4, values5)
    }

fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any> decodeHeaders6(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>,
    header6: Header<H6>
): EitherNel<String, Tuple6<List<H1>, List<H2>, List<H3>, List<H4>, List<H5>, List<H6>>> =
    zipOrAccumulate(
        decodeHeader(headers, header1),
        decodeHeader(headers, header2),
        decodeHeader(headers, header3),
        decodeHeader(headers, header4),
        decodeHeader(headers, header5),
        decodeHeader(headers, header6)
    ) { values1, values2, values3, values4, values5, values6 ->
        Tuple6(values1, values2, values3, values4, values5, values6)
    }

fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any> decodeHeaders7(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>,
    header6: Header<H6>,
    header7: Header<H7>
): EitherNel<String, Tuple7<List<H1>, List<H2>, List<H3>, List<H4>, List<H5>, List<H6>, List<H7>>> =
    zipOrAccumulate(
        decodeHeader(headers, header1),
        decodeHeader(headers, header2),
        decodeHeader(headers, header3),
        decodeHeader(headers, header4),
        decodeHeader(headers, header5),
        decodeHeader(headers, header6),
        decodeHeader(headers, header7)
    ) { values1, values2, values3, values4, values5, values6, values7 ->
        Tuple7(values1, values2, values3, values4, values5, values6, values7)
    }

fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any> decodeHeaders8(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>,
    header6: Header<H6>,
    header7: Header<H7>,
    header8: Header<H8>
): EitherNel<String, Tuple8<List<H1>, List<H2>, List<H3>, List<H4>, List<H5>, List<H6>, List<H7>, List<H8>>> =
    zipOrAccumulate(
        decodeHeader(headers, header1),
        decodeHeader(headers, header2),
        decodeHeader(headers, header3),
        decodeHeader(headers, header4),
        decodeHeader(headers, header5),
        decodeHeader(headers, header6),
        decodeHeader(headers, header7),
        decodeHeader(headers, header8)
    ) { values1, values2, values3, values4, values5, values6, values7, values8 ->
        Tuple8(values1, values2, values3, values4, values5, values6, values7, values8)
    }

fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any, H9 : Any> decodeHeaders9(
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
): EitherNel<String, Tuple9<List<H1>, List<H2>, List<H3>, List<H4>, List<H5>, List<H6>, List<H7>, List<H8>, List<H9>>> =
    zipOrAccumulate(
        decodeHeader(headers, header1),
        decodeHeader(headers, header2),
        decodeHeader(headers, header3),
        decodeHeader(headers, header4),
        decodeHeader(headers, header5),
        decodeHeader(headers, header6),
        decodeHeader(headers, header7),
        decodeHeader(headers, header8),
        decodeHeader(headers, header9)
    ) { values1, values2, values3, values4, values5, values6, values7, values8, values9 ->
        Tuple9(values1, values2, values3, values4, values5, values6, values7, values8, values9)
    }

fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any, H8 : Any, H9 : Any, H10 : Any> decodeHeaders10(
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
): EitherNel<String, Tuple10<List<H1>, List<H2>, List<H3>, List<H4>, List<H5>, List<H6>, List<H7>, List<H8>, List<H9>, List<H10>>> =
    zipOrAccumulate(
        decodeHeader(headers, header1),
        decodeHeader(headers, header2),
        decodeHeader(headers, header3),
        decodeHeader(headers, header4),
        decodeHeader(headers, header5),
        decodeHeader(headers, header6),
        decodeHeader(headers, header7),
        decodeHeader(headers, header8),
        decodeHeader(headers, header9),
        decodeHeader(headers, header10)
    ) { values1, values2, values3, values4, values5, values6, values7, values8, values9, values10 ->
        Tuple10(values1, values2, values3, values4, values5, values6, values7, values8, values9, values10)
    }

private fun <H : Any> decodeHeader(
    values: Map<String, List<String>>,
    header: Header<H>
): EitherNel<String, List<H>> =
    when (header) {
        is HeaderInput<H> -> {
            val values = values[header.name]
            if (values == null && header.required) {
                "Required header '${header.name}' is missing".leftNel()
            } else {
                val initial: EitherNel<String, List<H>> = emptyList<H>().right()
                values.orEmpty().fold(initial) { acc, raw ->
                    zipOrAccumulate(
                        acc,
                        header.codec.decode(raw)
                    ) { decodedValues, decoded ->
                        decodedValues + decoded
                    }
                }
            }
        }
        is HeaderValues<H> -> header.values.right()
    }
