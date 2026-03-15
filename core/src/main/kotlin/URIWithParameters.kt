@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik
import kotlin.jvm.JvmName

/**
 * URI template paired with the typed parameters it captures.
 *
 * The left side stores literal path segments. The right side keeps path variables and query
 * parameters in declaration order so endpoint builders and generators can derive method signatures
 * from the same value that describes the route.
 */
typealias URIWithParameters<P> = Pair<List<String>, P>

/** URI template with no captured parameters. */
typealias URIWithParameters0 = URIWithParameters<Parameters0>

/** URI template with one captured parameter. */
typealias URIWithParameters1<P1> = URIWithParameters<Parameters1<P1>>

/** URI template with two captured parameters. */
typealias URIWithParameters2<P1, P2> = URIWithParameters<Parameters2<P1, P2>>

/** URI template with three captured parameters. */
typealias URIWithParameters3<P1, P2, P3> = URIWithParameters<Parameters3<P1, P2, P3>>

/** URI template with four captured parameters. */
typealias URIWithParameters4<P1, P2, P3, P4> = URIWithParameters<Parameters4<P1, P2, P3, P4>>

/** URI template with five captured parameters. */
typealias URIWithParameters5<P1, P2, P3, P4, P5> = URIWithParameters<Parameters5<P1, P2, P3, P4, P5>>

/** URI template with six captured parameters. */
typealias URIWithParameters6<P1, P2, P3, P4, P5, P6> = URIWithParameters<Parameters6<P1, P2, P3, P4, P5, P6>>

/** URI template with seven captured parameters. */
typealias URIWithParameters7<P1, P2, P3, P4, P5, P6, P7> = URIWithParameters<Parameters7<P1, P2, P3, P4, P5, P6, P7>>

/** URI template with eight captured parameters. */
typealias URIWithParameters8<P1, P2, P3, P4, P5, P6, P7, P8> = URIWithParameters<Parameters8<P1, P2, P3, P4, P5, P6, P7, P8>>

/** URI template with nine captured parameters. */
typealias URIWithParameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9> = URIWithParameters<Parameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9>>

/** URI template with ten captured parameters. */
typealias URIWithParameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> = URIWithParameters<Parameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10>>

/** Starts a URI template from two literal path segments. */
operator fun String.div(segment: String): URIWithParameters0 = listOf(this, segment) to emptyParameters()

/** Starts a URI template with a literal segment followed by a path placeholder. */
operator fun <P : Any> String.div(variable: PathVariable<P>): URIWithParameters1<P> =
    listOf(this, "{${variable.name}}") to parametersOf(variable)

/** Appends another literal path segment without changing the captured parameter tuple. */
operator fun <P : Parameters> URIWithParameters<P>.div(segment: String): URIWithParameters<P> =
    (first + segment) to second

/** Appends one more path placeholder to the template. */
@JvmName("uriWithParameters0DivVariable")
operator fun <P1 : Any> URIWithParameters0.div(variable: PathVariable<P1>): URIWithParameters1<P1> {
    val parameter: Parameter<P1> = variable
    return (first + "{${variable.name}}") to (second + parameter)
}

/** Appends one more path placeholder to the template. */
@JvmName("uriWithParameters1DivVariable")
operator fun <P1 : Any, P2 : Any> URIWithParameters1<P1>.div(variable: PathVariable<P2>): URIWithParameters2<P1, P2> {
    val parameter: Parameter<P2> = variable
    return (first + "{${variable.name}}") to (second + parameter)
}

/** Appends one more path placeholder to the template. */
@JvmName("uriWithParameters2DivVariable")
operator fun <P1 : Any, P2 : Any, P3 : Any> URIWithParameters2<P1, P2>.div(
    variable: PathVariable<P3>
): URIWithParameters3<P1, P2, P3> {
    val parameter: Parameter<P3> = variable
    return (first + "{${variable.name}}") to (second + parameter)
}

/** Appends one more path placeholder to the template. */
@JvmName("uriWithParameters3DivVariable")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any> URIWithParameters3<P1, P2, P3>.div(
    variable: PathVariable<P4>
): URIWithParameters4<P1, P2, P3, P4> {
    val parameter: Parameter<P4> = variable
    return (first + "{${variable.name}}") to (second + parameter)
}

/** Appends one more path placeholder to the template. */
@JvmName("uriWithParameters4DivVariable")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any> URIWithParameters4<P1, P2, P3, P4>.div(
    variable: PathVariable<P5>
): URIWithParameters5<P1, P2, P3, P4, P5> {
    val parameter: Parameter<P5> = variable
    return (first + "{${variable.name}}") to (second + parameter)
}

/** Appends one more path placeholder to the template. */
@JvmName("uriWithParameters5DivVariable")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any> URIWithParameters5<P1, P2, P3, P4, P5>.div(
    variable: PathVariable<P6>
): URIWithParameters6<P1, P2, P3, P4, P5, P6> {
    val parameter: Parameter<P6> = variable
    return (first + "{${variable.name}}") to (second + parameter)
}

/** Appends one more path placeholder to the template. */
@JvmName("uriWithParameters6DivVariable")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any> URIWithParameters6<P1, P2, P3, P4, P5, P6>.div(
    variable: PathVariable<P7>
): URIWithParameters7<P1, P2, P3, P4, P5, P6, P7> {
    val parameter: Parameter<P7> = variable
    return (first + "{${variable.name}}") to (second + parameter)
}

/** Appends one more path placeholder to the template. */
@JvmName("uriWithParameters7DivVariable")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any> URIWithParameters7<P1, P2, P3, P4, P5, P6, P7>.div(
    variable: PathVariable<P8>
): URIWithParameters8<P1, P2, P3, P4, P5, P6, P7, P8> {
    val parameter: Parameter<P8> = variable
    return (first + "{${variable.name}}") to (second + parameter)
}

/** Appends one more path placeholder to the template. */
@JvmName("uriWithParameters8DivVariable")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any, P9 : Any> URIWithParameters8<P1, P2, P3, P4, P5, P6, P7, P8>.div(
    variable: PathVariable<P9>
): URIWithParameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9> {
    val parameter: Parameter<P9> = variable
    return (first + "{${variable.name}}") to (second + parameter)
}

/** Appends one more path placeholder to the template. */
@JvmName("uriWithParameters9DivVariable")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any, P9 : Any, P10 : Any> URIWithParameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9>.div(
    variable: PathVariable<P10>
): URIWithParameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> {
    val parameter: Parameter<P10> = variable
    return (first + "{${variable.name}}") to (second + parameter)
}

/** Appends a query parameter while leaving the literal path unchanged. */
@JvmName("uriWithParameters0PlusParameter")
operator fun <P1 : Any> URIWithParameters0.plus(parameter: QueryParameter<P1>): URIWithParameters1<P1> {
    val param: Parameter<P1> = parameter
    return first to (second + param)
}

/** Appends a query parameter while leaving the literal path unchanged. */
@JvmName("uriWithParameters1PlusParameter")
operator fun <P1 : Any, P2 : Any> URIWithParameters1<P1>.plus(
    parameter: QueryParameter<P2>
): URIWithParameters2<P1, P2> {
    val param: Parameter<P2> = parameter
    return first to (second + param)
}

/** Appends a query parameter while leaving the literal path unchanged. */
@JvmName("uriWithParameters2PlusParameter")
operator fun <P1 : Any, P2 : Any, P3 : Any> URIWithParameters2<P1, P2>.plus(
    parameter: QueryParameter<P3>
): URIWithParameters3<P1, P2, P3> {
    val param: Parameter<P3> = parameter
    return first to (second + param)
}

/** Appends a query parameter while leaving the literal path unchanged. */
@JvmName("uriWithParameters3PlusParameter")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any> URIWithParameters3<P1, P2, P3>.plus(
    parameter: QueryParameter<P4>
): URIWithParameters4<P1, P2, P3, P4> {
    val param: Parameter<P4> = parameter
    return first to (second + param)
}

/** Appends a query parameter while leaving the literal path unchanged. */
@JvmName("uriWithParameters4PlusParameter")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any> URIWithParameters4<P1, P2, P3, P4>.plus(
    parameter: QueryParameter<P5>
): URIWithParameters5<P1, P2, P3, P4, P5> {
    val param: Parameter<P5> = parameter
    return first to (second + param)
}

/** Appends a query parameter while leaving the literal path unchanged. */
@JvmName("uriWithParameters5PlusParameter")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any> URIWithParameters5<P1, P2, P3, P4, P5>.plus(
    parameter: QueryParameter<P6>
): URIWithParameters6<P1, P2, P3, P4, P5, P6> {
    val param: Parameter<P6> = parameter
    return first to (second + param)
}

/** Appends a query parameter while leaving the literal path unchanged. */
@JvmName("uriWithParameters6PlusParameter")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any> URIWithParameters6<P1, P2, P3, P4, P5, P6>.plus(
    parameter: QueryParameter<P7>
): URIWithParameters7<P1, P2, P3, P4, P5, P6, P7> {
    val param: Parameter<P7> = parameter
    return first to (second + param)
}

/** Appends a query parameter while leaving the literal path unchanged. */
@JvmName("uriWithParameters7PlusParameter")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any> URIWithParameters7<P1, P2, P3, P4, P5, P6, P7>.plus(
    parameter: QueryParameter<P8>
): URIWithParameters8<P1, P2, P3, P4, P5, P6, P7, P8> {
    val param: Parameter<P8> = parameter
    return first to (second + param)
}

/** Appends a query parameter while leaving the literal path unchanged. */
@JvmName("uriWithParameters8PlusParameter")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any, P9 : Any> URIWithParameters8<P1, P2, P3, P4, P5, P6, P7, P8>.plus(
    parameter: QueryParameter<P9>
): URIWithParameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9> {
    val param: Parameter<P9> = parameter
    return first to (second + param)
}

/** Appends a query parameter while leaving the literal path unchanged. */
@JvmName("uriWithParameters9PlusParameter")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any, P9 : Any, P10 : Any> URIWithParameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9>.plus(
    parameter: QueryParameter<P10>
): URIWithParameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> {
    val param: Parameter<P10> = parameter
    return first to (second + param)
}
