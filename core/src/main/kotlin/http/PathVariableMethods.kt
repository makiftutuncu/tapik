@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik.http

operator fun String.div(segment: String): URIWithParameters<Parameters0> =
    listOf(this, segment) to Parameters0()

operator fun <P: Parameters> URIWithParameters<P>.div(segment: String): URIWithParameters<P> {
    val (uri, parameters) = this
    return (uri + segment) to parameters
}

@JvmName("addVariable1")
operator fun <P1: Any> URIWithParameters<Parameters0>.div(
    variable: PathVariable<P1>
): URIWithParameters<Parameters1<P1>> {
    val (uri, parameters) = this
    return (uri + "{${variable.name}}") to (parameters + variable)
}

@JvmName("addVariable2")
operator fun <P1: Any, P2: Any> URIWithParameters<Parameters1<P1>>.div(
    variable: PathVariable<P2>
): URIWithParameters<Parameters2<P1, P2>> {
    val (uri, parameters) = this
    return (uri + "{${variable.name}}") to (parameters + variable)
}

@JvmName("addVariable3")
operator fun <P1: Any, P2: Any, P3: Any> URIWithParameters<Parameters2<P1, P2>>.div(
    variable: PathVariable<P3>
): URIWithParameters<Parameters3<P1, P2, P3>> {
    val (uri, parameters) = this
    return (uri + "{${variable.name}}") to (parameters + variable)
}

@JvmName("addVariable4")
operator fun <P1: Any, P2: Any, P3: Any, P4: Any> URIWithParameters<Parameters3<P1, P2, P3>>.div(
    variable: PathVariable<P4>
): URIWithParameters<Parameters4<P1, P2, P3, P4>> {
    val (uri, parameters) = this
    return (uri + "{${variable.name}}") to (parameters + variable)
}

@JvmName("addVariable5")
operator fun <P1: Any, P2: Any, P3: Any, P4: Any, P5: Any> URIWithParameters<Parameters4<P1, P2, P3, P4>>.div(
    variable: PathVariable<P5>
): URIWithParameters<Parameters5<P1, P2, P3, P4, P5>> {
    val (uri, parameters) = this
    return (uri + "{${variable.name}}") to (parameters + variable)
}

@JvmName("addVariable6")
operator fun <P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any> URIWithParameters<Parameters5<P1, P2, P3, P4, P5>>.div(
    variable: PathVariable<P6>
): URIWithParameters<Parameters6<P1, P2, P3, P4, P5, P6>> {
    val (uri, parameters) = this
    return (uri + "{${variable.name}}") to (parameters + variable)
}

@JvmName("addVariable7")
operator fun <P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any, P7: Any> URIWithParameters<Parameters6<P1, P2, P3, P4, P5, P6>>.div(
    variable: PathVariable<P7>
): URIWithParameters<Parameters7<P1, P2, P3, P4, P5, P6, P7>> {
    val (uri, parameters) = this
    return (uri + "{${variable.name}}") to (parameters + variable)
}

@JvmName("addVariable8")
operator fun <P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any, P7: Any, P8: Any> URIWithParameters<Parameters7<P1, P2, P3, P4, P5, P6, P7>>.div(
    variable: PathVariable<P8>
): URIWithParameters<Parameters8<P1, P2, P3, P4, P5, P6, P7, P8>> {
    val (uri, parameters) = this
    return (uri + "{${variable.name}}") to (parameters + variable)
}

@JvmName("addVariable9")
operator fun <P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any, P7: Any, P8: Any, P9: Any> URIWithParameters<Parameters8<P1, P2, P3, P4, P5, P6, P7, P8>>.div(
    variable: PathVariable<P9>
): URIWithParameters<Parameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9>> {
    val (uri, parameters) = this
    return (uri + "{${variable.name}}") to (parameters + variable)
}

@JvmName("addVariable10")
operator fun <P1: Any, P2: Any, P3: Any, P4: Any, P5: Any, P6: Any, P7: Any, P8: Any, P9: Any, P10: Any> URIWithParameters<Parameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9>>.div(
    variable: PathVariable<P10>
): URIWithParameters<Parameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10>> {
    val (uri, parameters) = this
    return (uri + "{${variable.name}}") to (parameters + variable)
}
