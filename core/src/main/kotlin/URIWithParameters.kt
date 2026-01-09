@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

import kotlin.jvm.JvmName

/**
 * Concatenates the given [segment] to this base path segment while building a URI template.
 *
 * @receiver base path segment.
 * @param segment literal segment appended to the path.
 * @return URI template that tracks the accumulated path.
 */
operator fun String.div(segment: String): URIWithParameters0 = listOf(this, segment) to emptyParameters()

/**
 * Appends the [variable] placeholder to this base path segment while building a URI template.
 *
 * @param P type of the path variable.
 * @receiver base path segment.
 * @param variable path variable placeholder appended to the path.
 * @return URI template capturing the supplied [variable].
 */
operator fun <P : Any> String.div(variable: PathVariable<P>): URIWithParameters1<P> =
    listOf(this, "{${variable.name}}") to parametersOf(variable)

/**
 * Adds a literal [segment] to the path portion of this URI template.
 *
 * @receiver URI template described by literal segments and captured parameters.
 * @param segment literal segment appended to the template.
 * @return updated URI template retaining previously captured parameters.
 */
operator fun <P : Parameters> URIWithParameters<P>.div(segment: String): URIWithParameters<P> =
    (first + segment) to second

/**
 * Appends the given [variable] placeholder to the path portion of this URI template.
 *
 * @param P1 type of the path variable.
 * @receiver URI template described by literal segments and captured parameters.
 * @param variable path variable placeholder appended to the template.
 * @return updated URI template capturing the additional [variable].
 */
@JvmName("uriWithParameters0DivVariable")
operator fun <P1 : Any> URIWithParameters0.div(variable: PathVariable<P1>): URIWithParameters1<P1> {
    val parameter: Parameter<P1> = variable
    return (first + "{${variable.name}}") to (second + parameter)
}

/**
 * Appends the given [variable] placeholder to the path portion of this URI template.
 *
 * @param P1 type of the first path/query parameter.
 * @param P2 type of the new path variable.
 * @receiver URI template described by literal segments and captured parameters.
 * @param variable path variable placeholder appended to the template.
 * @return updated URI template capturing the additional [variable].
 */
@JvmName("uriWithParameters1DivVariable")
operator fun <P1 : Any, P2 : Any> URIWithParameters1<P1>.div(variable: PathVariable<P2>): URIWithParameters2<P1, P2> {
    val parameter: Parameter<P2> = variable
    return (first + "{${variable.name}}") to (second + parameter)
}

/**
 * Appends the given [variable] placeholder to the path portion of this URI template.
 *
 * @param P1 type of the first path/query parameter.
 * @param P2 type of the second path/query parameter.
 * @param P3 type of the new path variable.
 * @receiver URI template described by literal segments and captured parameters.
 * @param variable path variable placeholder appended to the template.
 * @return updated URI template capturing the additional [variable].
 */
@JvmName("uriWithParameters2DivVariable")
operator fun <P1 : Any, P2 : Any, P3 : Any> URIWithParameters2<P1, P2>.div(
    variable: PathVariable<P3>
): URIWithParameters3<P1, P2, P3> {
    val parameter: Parameter<P3> = variable
    return (first + "{${variable.name}}") to (second + parameter)
}

/**
 * Appends the given [variable] placeholder to the path portion of this URI template.
 *
 * @param P1 type of the first path/query parameter.
 * @param P2 type of the second path/query parameter.
 * @param P3 type of the third path/query parameter.
 * @param P4 type of the new path variable.
 * @receiver URI template described by literal segments and captured parameters.
 * @param variable path variable placeholder appended to the template.
 * @return updated URI template capturing the additional [variable].
 */
@JvmName("uriWithParameters3DivVariable")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any> URIWithParameters3<P1, P2, P3>.div(
    variable: PathVariable<P4>
): URIWithParameters4<P1, P2, P3, P4> {
    val parameter: Parameter<P4> = variable
    return (first + "{${variable.name}}") to (second + parameter)
}

/**
 * Appends the given [variable] placeholder to the path portion of this URI template.
 *
 * @param P1 type of the first path/query parameter.
 * @param P2 type of the second path/query parameter.
 * @param P3 type of the third path/query parameter.
 * @param P4 type of the fourth path/query parameter.
 * @param P5 type of the new path variable.
 * @receiver URI template described by literal segments and captured parameters.
 * @param variable path variable placeholder appended to the template.
 * @return updated URI template capturing the additional [variable].
 */
@JvmName("uriWithParameters4DivVariable")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any> URIWithParameters4<P1, P2, P3, P4>.div(
    variable: PathVariable<P5>
): URIWithParameters5<P1, P2, P3, P4, P5> {
    val parameter: Parameter<P5> = variable
    return (first + "{${variable.name}}") to (second + parameter)
}

/**
 * Appends the given [variable] placeholder to the path portion of this URI template.
 *
 * @param P1 type of the first path/query parameter.
 * @param P2 type of the second path/query parameter.
 * @param P3 type of the third path/query parameter.
 * @param P4 type of the fourth path/query parameter.
 * @param P5 type of the fifth path/query parameter.
 * @param P6 type of the new path variable.
 * @receiver URI template described by literal segments and captured parameters.
 * @param variable path variable placeholder appended to the template.
 * @return updated URI template capturing the additional [variable].
 */
@JvmName("uriWithParameters5DivVariable")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any> URIWithParameters5<P1, P2, P3, P4, P5>.div(
    variable: PathVariable<P6>
): URIWithParameters6<P1, P2, P3, P4, P5, P6> {
    val parameter: Parameter<P6> = variable
    return (first + "{${variable.name}}") to (second + parameter)
}

/**
 * Appends the given [variable] placeholder to the path portion of this URI template.
 *
 * @param P1 type of the first path/query parameter.
 * @param P2 type of the second path/query parameter.
 * @param P3 type of the third path/query parameter.
 * @param P4 type of the fourth path/query parameter.
 * @param P5 type of the fifth path/query parameter.
 * @param P6 type of the sixth path/query parameter.
 * @param P7 type of the new path variable.
 * @receiver URI template described by literal segments and captured parameters.
 * @param variable path variable placeholder appended to the template.
 * @return updated URI template capturing the additional [variable].
 */
@JvmName("uriWithParameters6DivVariable")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any> URIWithParameters6<P1, P2, P3, P4, P5, P6>.div(
    variable: PathVariable<P7>
): URIWithParameters7<P1, P2, P3, P4, P5, P6, P7> {
    val parameter: Parameter<P7> = variable
    return (first + "{${variable.name}}") to (second + parameter)
}

/**
 * Appends the given [variable] placeholder to the path portion of this URI template.
 *
 * @param P1 type of the first path/query parameter.
 * @param P2 type of the second path/query parameter.
 * @param P3 type of the third path/query parameter.
 * @param P4 type of the fourth path/query parameter.
 * @param P5 type of the fifth path/query parameter.
 * @param P6 type of the sixth path/query parameter.
 * @param P7 type of the seventh path/query parameter.
 * @param P8 type of the new path variable.
 * @receiver URI template described by literal segments and captured parameters.
 * @param variable path variable placeholder appended to the template.
 * @return updated URI template capturing the additional [variable].
 */
@JvmName("uriWithParameters7DivVariable")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any> URIWithParameters7<P1, P2, P3, P4, P5, P6, P7>.div(
    variable: PathVariable<P8>
): URIWithParameters8<P1, P2, P3, P4, P5, P6, P7, P8> {
    val parameter: Parameter<P8> = variable
    return (first + "{${variable.name}}") to (second + parameter)
}

/**
 * Appends the given [variable] placeholder to the path portion of this URI template.
 *
 * @param P1 type of the first path/query parameter.
 * @param P2 type of the second path/query parameter.
 * @param P3 type of the third path/query parameter.
 * @param P4 type of the fourth path/query parameter.
 * @param P5 type of the fifth path/query parameter.
 * @param P6 type of the sixth path/query parameter.
 * @param P7 type of the seventh path/query parameter.
 * @param P8 type of the eighth path/query parameter.
 * @param P9 type of the new path variable.
 * @receiver URI template described by literal segments and captured parameters.
 * @param variable path variable placeholder appended to the template.
 * @return updated URI template capturing the additional [variable].
 */
@JvmName("uriWithParameters8DivVariable")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any, P9 : Any> URIWithParameters8<P1, P2, P3, P4, P5, P6, P7, P8>.div(
    variable: PathVariable<P9>
): URIWithParameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9> {
    val parameter: Parameter<P9> = variable
    return (first + "{${variable.name}}") to (second + parameter)
}

/**
 * Appends the given [variable] placeholder to the path portion of this URI template.
 *
 * @param P1 type of the first path/query parameter.
 * @param P2 type of the second path/query parameter.
 * @param P3 type of the third path/query parameter.
 * @param P4 type of the fourth path/query parameter.
 * @param P5 type of the fifth path/query parameter.
 * @param P6 type of the sixth path/query parameter.
 * @param P7 type of the seventh path/query parameter.
 * @param P8 type of the eighth path/query parameter.
 * @param P9 type of the ninth path/query parameter.
 * @param P10 type of the new path variable.
 * @receiver URI template described by literal segments and captured parameters.
 * @param variable path variable placeholder appended to the template.
 * @return updated URI template capturing the additional [variable].
 */
@JvmName("uriWithParameters9DivVariable")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any, P9 : Any, P10 : Any> URIWithParameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9>.div(
    variable: PathVariable<P10>
): URIWithParameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> {
    val parameter: Parameter<P10> = variable
    return (first + "{${variable.name}}") to (second + parameter)
}

/**
 * Appends the given query [parameter] to this URI template.
 *
 * @param P1 type of the new query parameter.
 * @receiver URI template described by literal segments and captured parameters.
 * @param parameter query parameter appended to the template.
 * @return updated URI template capturing the additional [parameter].
 */
@JvmName("uriWithParameters0PlusParameter")
operator fun <P1 : Any> URIWithParameters0.plus(parameter: QueryParameter<P1>): URIWithParameters1<P1> {
    val param: Parameter<P1> = parameter
    return first to (second + param)
}

/**
 * Appends the given query [parameter] to this URI template.
 *
 * @param P1 type of the first path/query parameter.
 * @param P2 type of the new query parameter.
 * @receiver URI template described by literal segments and captured parameters.
 * @param parameter query parameter appended to the template.
 * @return updated URI template capturing the additional [parameter].
 */
@JvmName("uriWithParameters1PlusParameter")
operator fun <P1 : Any, P2 : Any> URIWithParameters1<P1>.plus(
    parameter: QueryParameter<P2>
): URIWithParameters2<P1, P2> {
    val param: Parameter<P2> = parameter
    return first to (second + param)
}

/**
 * Appends the given query [parameter] to this URI template.
 *
 * @param P1 type of the first path/query parameter.
 * @param P2 type of the second path/query parameter.
 * @param P3 type of the new query parameter.
 * @receiver URI template described by literal segments and captured parameters.
 * @param parameter query parameter appended to the template.
 * @return updated URI template capturing the additional [parameter].
 */
@JvmName("uriWithParameters2PlusParameter")
operator fun <P1 : Any, P2 : Any, P3 : Any> URIWithParameters2<P1, P2>.plus(
    parameter: QueryParameter<P3>
): URIWithParameters3<P1, P2, P3> {
    val param: Parameter<P3> = parameter
    return first to (second + param)
}

/**
 * Appends the given query [parameter] to this URI template.
 *
 * @param P1 type of the first path/query parameter.
 * @param P2 type of the second path/query parameter.
 * @param P3 type of the third path/query parameter.
 * @param P4 type of the new query parameter.
 * @receiver URI template described by literal segments and captured parameters.
 * @param parameter query parameter appended to the template.
 * @return updated URI template capturing the additional [parameter].
 */
@JvmName("uriWithParameters3PlusParameter")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any> URIWithParameters3<P1, P2, P3>.plus(
    parameter: QueryParameter<P4>
): URIWithParameters4<P1, P2, P3, P4> {
    val param: Parameter<P4> = parameter
    return first to (second + param)
}

/**
 * Appends the given query [parameter] to this URI template.
 *
 * @param P1 type of the first path/query parameter.
 * @param P2 type of the second path/query parameter.
 * @param P3 type of the third path/query parameter.
 * @param P4 type of the fourth path/query parameter.
 * @param P5 type of the new query parameter.
 * @receiver URI template described by literal segments and captured parameters.
 * @param parameter query parameter appended to the template.
 * @return updated URI template capturing the additional [parameter].
 */
@JvmName("uriWithParameters4PlusParameter")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any> URIWithParameters4<P1, P2, P3, P4>.plus(
    parameter: QueryParameter<P5>
): URIWithParameters5<P1, P2, P3, P4, P5> {
    val param: Parameter<P5> = parameter
    return first to (second + param)
}

/**
 * Appends the given query [parameter] to this URI template.
 *
 * @param P1 type of the first path/query parameter.
 * @param P2 type of the second path/query parameter.
 * @param P3 type of the third path/query parameter.
 * @param P4 type of the fourth path/query parameter.
 * @param P5 type of the fifth path/query parameter.
 * @param P6 type of the new query parameter.
 * @receiver URI template described by literal segments and captured parameters.
 * @param parameter query parameter appended to the template.
 * @return updated URI template capturing the additional [parameter].
 */
@JvmName("uriWithParameters5PlusParameter")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any> URIWithParameters5<P1, P2, P3, P4, P5>.plus(
    parameter: QueryParameter<P6>
): URIWithParameters6<P1, P2, P3, P4, P5, P6> {
    val param: Parameter<P6> = parameter
    return first to (second + param)
}

/**
 * Appends the given query [parameter] to this URI template.
 *
 * @param P1 type of the first path/query parameter.
 * @param P2 type of the second path/query parameter.
 * @param P3 type of the third path/query parameter.
 * @param P4 type of the fourth path/query parameter.
 * @param P5 type of the fifth path/query parameter.
 * @param P6 type of the sixth path/query parameter.
 * @param P7 type of the new query parameter.
 * @receiver URI template described by literal segments and captured parameters.
 * @param parameter query parameter appended to the template.
 * @return updated URI template capturing the additional [parameter].
 */
@JvmName("uriWithParameters6PlusParameter")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any> URIWithParameters6<P1, P2, P3, P4, P5, P6>.plus(
    parameter: QueryParameter<P7>
): URIWithParameters7<P1, P2, P3, P4, P5, P6, P7> {
    val param: Parameter<P7> = parameter
    return first to (second + param)
}

/**
 * Appends the given query [parameter] to this URI template.
 *
 * @param P1 type of the first path/query parameter.
 * @param P2 type of the second path/query parameter.
 * @param P3 type of the third path/query parameter.
 * @param P4 type of the fourth path/query parameter.
 * @param P5 type of the fifth path/query parameter.
 * @param P6 type of the sixth path/query parameter.
 * @param P7 type of the seventh path/query parameter.
 * @param P8 type of the new query parameter.
 * @receiver URI template described by literal segments and captured parameters.
 * @param parameter query parameter appended to the template.
 * @return updated URI template capturing the additional [parameter].
 */
@JvmName("uriWithParameters7PlusParameter")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any> URIWithParameters7<P1, P2, P3, P4, P5, P6, P7>.plus(
    parameter: QueryParameter<P8>
): URIWithParameters8<P1, P2, P3, P4, P5, P6, P7, P8> {
    val param: Parameter<P8> = parameter
    return first to (second + param)
}

/**
 * Appends the given query [parameter] to this URI template.
 *
 * @param P1 type of the first path/query parameter.
 * @param P2 type of the second path/query parameter.
 * @param P3 type of the third path/query parameter.
 * @param P4 type of the fourth path/query parameter.
 * @param P5 type of the fifth path/query parameter.
 * @param P6 type of the sixth path/query parameter.
 * @param P7 type of the seventh path/query parameter.
 * @param P8 type of the eighth path/query parameter.
 * @param P9 type of the new query parameter.
 * @receiver URI template described by literal segments and captured parameters.
 * @param parameter query parameter appended to the template.
 * @return updated URI template capturing the additional [parameter].
 */
@JvmName("uriWithParameters8PlusParameter")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any, P9 : Any> URIWithParameters8<P1, P2, P3, P4, P5, P6, P7, P8>.plus(
    parameter: QueryParameter<P9>
): URIWithParameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9> {
    val param: Parameter<P9> = parameter
    return first to (second + param)
}

/**
 * Appends the given query [parameter] to this URI template.
 *
 * @param P1 type of the first path/query parameter.
 * @param P2 type of the second path/query parameter.
 * @param P3 type of the third path/query parameter.
 * @param P4 type of the fourth path/query parameter.
 * @param P5 type of the fifth path/query parameter.
 * @param P6 type of the sixth path/query parameter.
 * @param P7 type of the seventh path/query parameter.
 * @param P8 type of the eighth path/query parameter.
 * @param P9 type of the ninth path/query parameter.
 * @param P10 type of the new query parameter.
 * @receiver URI template described by literal segments and captured parameters.
 * @param parameter query parameter appended to the template.
 * @return updated URI template capturing the additional [parameter].
 */
@JvmName("uriWithParameters9PlusParameter")
operator fun <P1 : Any, P2 : Any, P3 : Any, P4 : Any, P5 : Any, P6 : Any, P7 : Any, P8 : Any, P9 : Any, P10 : Any> URIWithParameters9<P1, P2, P3, P4, P5, P6, P7, P8, P9>.plus(
    parameter: QueryParameter<P10>
): URIWithParameters10<P1, P2, P3, P4, P5, P6, P7, P8, P9, P10> {
    val param: Parameter<P10> = parameter
    return first to (second + param)
}
