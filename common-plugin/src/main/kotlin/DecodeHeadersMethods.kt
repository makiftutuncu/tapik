@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

import arrow.core.Either.Companion.zipOrAccumulate
import arrow.core.EitherNel
import arrow.core.leftNel
import arrow.core.right

/** Returns the empty header tuple for inputs that declare no headers. */
fun decodeHeaders0(): HeaderValues0 = HeaderValues0

/** Decodes one declared header from a raw HTTP header map. */
fun <H1 : Any> decodeHeaders1(
    headers: Map<String, List<String>>,
    header1: Header<H1>
): EitherNel<String, HeaderValues1<H1>> = decodeHeader(headers, header1).map { headerValuesOf(it) }

/** Decodes two declared headers and accumulates validation errors across both definitions. */
fun <H1 : Any, H2 : Any> decodeHeaders2(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>
): EitherNel<String, HeaderValues2<H1, H2>> =
    zipOrAccumulate(
        decodeHeader(headers, header1),
        decodeHeader(headers, header2)
    ) { values1, values2 ->
        headerValuesOf(values1, values2)
    }

/** Decodes three declared headers and accumulates validation errors across all three definitions. */
fun <H1 : Any, H2 : Any, H3 : Any> decodeHeaders3(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>
): EitherNel<String, HeaderValues3<H1, H2, H3>> =
    zipOrAccumulate(
        decodeHeader(headers, header1),
        decodeHeader(headers, header2),
        decodeHeader(headers, header3)
    ) { values1, values2, values3 ->
        headerValuesOf(values1, values2, values3)
    }

/** Decodes four declared headers and accumulates validation errors across all four definitions. */
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any> decodeHeaders4(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>
): EitherNel<String, HeaderValues4<H1, H2, H3, H4>> =
    zipOrAccumulate(
        decodeHeader(headers, header1),
        decodeHeader(headers, header2),
        decodeHeader(headers, header3),
        decodeHeader(headers, header4)
    ) { values1, values2, values3, values4 ->
        headerValuesOf(values1, values2, values3, values4)
    }

/** Decodes five declared headers and accumulates validation errors across all five definitions. */
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any> decodeHeaders5(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>
): EitherNel<String, HeaderValues5<H1, H2, H3, H4, H5>> =
    zipOrAccumulate(
        decodeHeader(headers, header1),
        decodeHeader(headers, header2),
        decodeHeader(headers, header3),
        decodeHeader(headers, header4),
        decodeHeader(headers, header5)
    ) { values1, values2, values3, values4, values5 ->
        headerValuesOf(values1, values2, values3, values4, values5)
    }

/** Decodes six declared headers and accumulates validation errors across all six definitions. */
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any> decodeHeaders6(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>,
    header6: Header<H6>
): EitherNel<String, HeaderValues6<H1, H2, H3, H4, H5, H6>> =
    zipOrAccumulate(
        decodeHeader(headers, header1),
        decodeHeader(headers, header2),
        decodeHeader(headers, header3),
        decodeHeader(headers, header4),
        decodeHeader(headers, header5),
        decodeHeader(headers, header6)
    ) { values1, values2, values3, values4, values5, values6 ->
        headerValuesOf(values1, values2, values3, values4, values5, values6)
    }

/** Decodes seven declared headers and accumulates validation errors across all seven definitions. */
fun <H1 : Any, H2 : Any, H3 : Any, H4 : Any, H5 : Any, H6 : Any, H7 : Any> decodeHeaders7(
    headers: Map<String, List<String>>,
    header1: Header<H1>,
    header2: Header<H2>,
    header3: Header<H3>,
    header4: Header<H4>,
    header5: Header<H5>,
    header6: Header<H6>,
    header7: Header<H7>
): EitherNel<String, HeaderValues7<H1, H2, H3, H4, H5, H6, H7>> =
    zipOrAccumulate(
        decodeHeader(headers, header1),
        decodeHeader(headers, header2),
        decodeHeader(headers, header3),
        decodeHeader(headers, header4),
        decodeHeader(headers, header5),
        decodeHeader(headers, header6),
        decodeHeader(headers, header7)
    ) { values1, values2, values3, values4, values5, values6, values7 ->
        headerValuesOf(values1, values2, values3, values4, values5, values6, values7)
    }

/** Decodes eight declared headers and accumulates validation errors across all eight definitions. */
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
): EitherNel<String, HeaderValues8<H1, H2, H3, H4, H5, H6, H7, H8>> =
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
        headerValuesOf(values1, values2, values3, values4, values5, values6, values7, values8)
    }

/** Decodes nine declared headers and accumulates validation errors across all nine definitions. */
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
): EitherNel<String, HeaderValues9<H1, H2, H3, H4, H5, H6, H7, H8, H9>> =
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
        headerValuesOf(values1, values2, values3, values4, values5, values6, values7, values8, values9)
    }

/** Decodes ten declared headers and accumulates validation errors across all ten definitions. */
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
): EitherNel<String, HeaderValues10<H1, H2, H3, H4, H5, H6, H7, H8, H9, H10>> =
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
        headerValuesOf(values1, values2, values3, values4, values5, values6, values7, values8, values9, values10)
    }

private fun <H : Any> decodeHeader(
    values: Map<String, List<String>>,
    header: Header<H>
): EitherNel<String, HeaderValues<H>> =
    when (header) {
        is HeaderInput<H> -> {
            val headerValues = values[header.name]
            if (headerValues == null && header.required) {
                "Required header '${header.name}' is missing".leftNel()
            } else {
                val initial: EitherNel<String, List<H>> = emptyList<H>().right()
                headerValues
                    .orEmpty()
                    .fold(initial) { acc, raw ->
                        zipOrAccumulate(
                            acc,
                            header.codec.decode(raw)
                        ) { decodedValues, decoded ->
                            decodedValues + decoded
                        }
                    }.map { decodedValues ->
                        HeaderValues(header.name, header.codec, decodedValues)
                    }
            }
        }

        is HeaderValues<H> -> header.right()
    }
