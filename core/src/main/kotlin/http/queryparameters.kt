package dev.akif.tapik.http

import dev.akif.tapik.codec.*

fun <Q : Any> query(name: String, codec: StringCodec<Q>): QueryParameter<Q> = QueryParameter(name, codec)

@JvmName("plus1")
operator fun <Q1 : Any> URIWithParameters<Parameters0>.plus(parameter: QueryParameter<Q1>): URIWithParameters<Parameters1<Q1>> {
    val (uri, parameters) = this
    return uri to (parameters + parameter)
}

@JvmName("plus2")
operator fun <Q1 : Any, Q2 : Any> URIWithParameters<Parameters1<Q1>>.plus(parameter: QueryParameter<Q2>): URIWithParameters<Parameters2<Q1, Q2>> {
    val (uri, parameters) = this
    return uri to (parameters + parameter)
}

@JvmName("plus3")
operator fun <Q1 : Any, Q2 : Any, Q3 : Any> URIWithParameters<Parameters2<Q1, Q2>>.plus(parameter: QueryParameter<Q3>): URIWithParameters<Parameters3<Q1, Q2, Q3>> {
    val (uri, parameters) = this
    return uri to (parameters + parameter)
}

@JvmName("plus4")
operator fun <Q1 : Any, Q2 : Any, Q3 : Any, Q4 : Any> URIWithParameters<Parameters3<Q1, Q2, Q3>>.plus(parameter: QueryParameter<Q4>): URIWithParameters<Parameters4<Q1, Q2, Q3, Q4>> {
    val (uri, parameters) = this
    return uri to (parameters + parameter)
}

@JvmName("plus5")
operator fun <Q1 : Any, Q2 : Any, Q3 : Any, Q4 : Any, Q5 : Any> URIWithParameters<Parameters4<Q1, Q2, Q3, Q4>>.plus(parameter: QueryParameter<Q5>): URIWithParameters<Parameters5<Q1, Q2, Q3, Q4, Q5>> {
    val (uri, parameters) = this
    return uri to (parameters + parameter)
}

@JvmName("plus6")
operator fun <Q1 : Any, Q2 : Any, Q3 : Any, Q4 : Any, Q5 : Any, Q6 : Any> URIWithParameters<Parameters5<Q1, Q2, Q3, Q4, Q5>>.plus(parameter: QueryParameter<Q6>): URIWithParameters<Parameters6<Q1, Q2, Q3, Q4, Q5, Q6>> {
    val (uri, parameters) = this
    return uri to (parameters + parameter)
}

@JvmName("plus7")
operator fun <Q1 : Any, Q2 : Any, Q3 : Any, Q4 : Any, Q5 : Any, Q6 : Any, Q7 : Any> URIWithParameters<Parameters6<Q1, Q2, Q3, Q4, Q5, Q6>>.plus(parameter: QueryParameter<Q7>): URIWithParameters<Parameters7<Q1, Q2, Q3, Q4, Q5, Q6, Q7>> {
    val (uri, parameters) = this
    return uri to (parameters + parameter)
}

@JvmName("plus8")
operator fun <Q1 : Any, Q2 : Any, Q3 : Any, Q4 : Any, Q5 : Any, Q6 : Any, Q7 : Any, Q8 : Any> URIWithParameters<Parameters7<Q1, Q2, Q3, Q4, Q5, Q6, Q7>>.plus(parameter: QueryParameter<Q8>): URIWithParameters<Parameters8<Q1, Q2, Q3, Q4, Q5, Q6, Q7, Q8>> {
    val (uri, parameters) = this
    return uri to (parameters + parameter)
}

@JvmName("plus9")
operator fun <Q1 : Any, Q2 : Any, Q3 : Any, Q4 : Any, Q5 : Any, Q6 : Any, Q7 : Any, Q8 : Any, Q9 : Any> URIWithParameters<Parameters8<Q1, Q2, Q3, Q4, Q5, Q6, Q7, Q8>>.plus(parameter: QueryParameter<Q9>): URIWithParameters<Parameters9<Q1, Q2, Q3, Q4, Q5, Q6, Q7, Q8, Q9>> {
    val (uri, parameters) = this
    return uri to (parameters + parameter)
}

@JvmName("plus10")
operator fun <Q1 : Any, Q2 : Any, Q3 : Any, Q4 : Any, Q5 : Any, Q6 : Any, Q7 : Any, Q8 : Any, Q9 : Any, Q10 : Any> URIWithParameters<Parameters9<Q1, Q2, Q3, Q4, Q5, Q6, Q7, Q8, Q9>>.plus(parameter: QueryParameter<Q10>): URIWithParameters<Parameters10<Q1, Q2, Q3, Q4, Q5, Q6, Q7, Q8, Q9, Q10>> {
    val (uri, parameters) = this
    return uri to (parameters + parameter)
}
