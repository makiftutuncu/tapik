package dev.akif.tapik

/** Appends the non-empty segments in an already-encoded [fragment]. */
operator fun <P : Paths, Q : Queries> Uri<P, Q>.div(fragment: String): Uri<P, Q> {
    val normalizedFragment = fragment.trim('/')

    require(normalizedFragment.isNotEmpty()) { "URI path fragment must contain at least one segment" }

    val newSegments = normalizedFragment.split('/')
    require(newSegments.none(String::isEmpty)) {
        "URI path fragment must not contain empty segments: '$fragment'"
    }

    return Uri(
        path = path + newSegments.map(PathSegment::Literal),
        pathVariables = pathVariables,
        queryParameters = queryParameters
    )
}

private fun <P : Paths, Q : Queries> Uri<*, Q>.append(
    variable: PathVariable<*>,
    paths: P
): Uri<P, Q> {
    require(pathVariables.values.none { it.name == variable.name }) {
        "Path variable '${variable.name}' is already defined"
    }

    return Uri(
        path = path + variable,
        pathVariables = paths,
        queryParameters = queryParameters
    )
}

/** Appends the first path [variable]. */
@JvmName("uriWithNoPathsDivPathVariable")
operator fun <Value : Any, Q : Queries> Uri<Paths0, Q>.div(
    variable: PathVariable<Value>
): Uri<Paths1<Value>, Q> = append(variable, Paths1(variable))

/** Appends a second path [variable]. */
@JvmName("uriWithOnePathDivPathVariable")
operator fun <Value1 : Any, Value : Any, Q : Queries> Uri<Paths1<Value1>, Q>.div(
    variable: PathVariable<Value>
): Uri<Paths2<Value1, Value>, Q> = append(variable, Paths2(pathVariables._1, variable))

/** Appends a third path [variable]. */
@JvmName("uriWithTwoPathsDivPathVariable")
operator fun <Value1 : Any, Value2 : Any, Value : Any, Q : Queries>
    Uri<Paths2<Value1, Value2>, Q>.div(
        variable: PathVariable<Value>
    ): Uri<Paths3<Value1, Value2, Value>, Q> =
        append(variable, Paths3(pathVariables._1, pathVariables._2, variable))

/** Appends a fourth path [variable]. */
@JvmName("uriWithThreePathsDivPathVariable")
operator fun <Value1 : Any, Value2 : Any, Value3 : Any, Value : Any, Q : Queries>
    Uri<Paths3<Value1, Value2, Value3>, Q>.div(
        variable: PathVariable<Value>
    ): Uri<Paths4<Value1, Value2, Value3, Value>, Q> =
        append(variable, Paths4(pathVariables._1, pathVariables._2, pathVariables._3, variable))

/** Appends a fifth path [variable]. */
@JvmName("uriWithFourPathsDivPathVariable")
operator fun <Value1 : Any, Value2 : Any, Value3 : Any, Value4 : Any, Value : Any, Q : Queries>
    Uri<Paths4<Value1, Value2, Value3, Value4>, Q>.div(
        variable: PathVariable<Value>
    ): Uri<Paths5<Value1, Value2, Value3, Value4, Value>, Q> =
        append(variable, Paths5(pathVariables._1, pathVariables._2, pathVariables._3, pathVariables._4, variable))

/** Appends a sixth path [variable]. */
@JvmName("uriWithFivePathsDivPathVariable")
operator fun <Value1 : Any, Value2 : Any, Value3 : Any, Value4 : Any, Value5 : Any, Value : Any, Q : Queries>
    Uri<Paths5<Value1, Value2, Value3, Value4, Value5>, Q>.div(
        variable: PathVariable<Value>
    ): Uri<Paths6<Value1, Value2, Value3, Value4, Value5, Value>, Q> =
        append(
            variable,
            Paths6(
                pathVariables._1,
                pathVariables._2,
                pathVariables._3,
                pathVariables._4,
                pathVariables._5,
                variable
            )
        )

/** Appends a seventh path [variable]. */
@JvmName("uriWithSixPathsDivPathVariable")
operator fun <
    Value1 : Any,
    Value2 : Any,
    Value3 : Any,
    Value4 : Any,
    Value5 : Any,
    Value6 : Any,
    Value : Any,
    Q : Queries
> Uri<Paths6<Value1, Value2, Value3, Value4, Value5, Value6>, Q>.div(
        variable: PathVariable<Value>
    ): Uri<Paths7<Value1, Value2, Value3, Value4, Value5, Value6, Value>, Q> =
        append(
            variable,
            Paths7(
                pathVariables._1,
                pathVariables._2,
                pathVariables._3,
                pathVariables._4,
                pathVariables._5,
                pathVariables._6,
                variable
            )
        )

/** Appends an eighth path [variable]. */
@JvmName("uriWithSevenPathsDivPathVariable")
operator fun <
    Value1 : Any,
    Value2 : Any,
    Value3 : Any,
    Value4 : Any,
    Value5 : Any,
    Value6 : Any,
    Value7 : Any,
    Value : Any,
    Q : Queries
> Uri<Paths7<Value1, Value2, Value3, Value4, Value5, Value6, Value7>, Q>.div(
    variable: PathVariable<Value>
): Uri<Paths8<Value1, Value2, Value3, Value4, Value5, Value6, Value7, Value>, Q> =
    append(
        variable,
        Paths8(
            pathVariables._1,
            pathVariables._2,
            pathVariables._3,
            pathVariables._4,
            pathVariables._5,
            pathVariables._6,
            pathVariables._7,
            variable
        )
    )
