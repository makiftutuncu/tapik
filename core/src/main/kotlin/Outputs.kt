package dev.akif.tapik

/** A typed endpoint output alternative. */
sealed interface OutputAlternative

/**
 * One endpoint output alternative.
 *
 * @param M status-matcher type.
 * @param B body-alternative tuple type.
 * @param H output-header tuple type.
 * @property matcher statuses represented by this output.
 * @property bodies alternative body representations.
 * @property headers headers produced with this output.
 */
@ConsistentCopyVisibility
data class Output<out M : StatusMatcher, out B : Bodies, out H : Headers> internal constructor(
    val matcher: M,
    val bodies: B,
    val headers: H
) : OutputAlternative

/** An ordered, non-empty heterogeneous tuple of endpoint output alternatives. */
typealias Outputs = NonEmptyTuple<OutputAlternative>

/** An output tuple with one alternative. */
typealias Outputs1<Output1> = Tuple1<OutputAlternative, Output1>

/** An output tuple with two alternatives. */
typealias Outputs2<Output1, Output2> = Tuple2<OutputAlternative, Output1, Output2>

/** An output tuple with three alternatives. */
typealias Outputs3<Output1, Output2, Output3> = Tuple3<OutputAlternative, Output1, Output2, Output3>

/** An output tuple with four alternatives. */
typealias Outputs4<Output1, Output2, Output3, Output4> =
    Tuple4<OutputAlternative, Output1, Output2, Output3, Output4>

/** An output tuple with five alternatives. */
typealias Outputs5<Output1, Output2, Output3, Output4, Output5> =
    Tuple5<OutputAlternative, Output1, Output2, Output3, Output4, Output5>

/** An output tuple with six alternatives. */
typealias Outputs6<Output1, Output2, Output3, Output4, Output5, Output6> =
    Tuple6<OutputAlternative, Output1, Output2, Output3, Output4, Output5, Output6>

/** An output tuple with seven alternatives. */
typealias Outputs7<Output1, Output2, Output3, Output4, Output5, Output6, Output7> =
    Tuple7<OutputAlternative, Output1, Output2, Output3, Output4, Output5, Output6, Output7>

/** An output tuple with eight alternatives. */
typealias Outputs8<Output1, Output2, Output3, Output4, Output5, Output6, Output7, Output8> =
    Tuple8<OutputAlternative, Output1, Output2, Output3, Output4, Output5, Output6, Output7, Output8>

private val emptyOkOutput: Output<ExactStatus, Bodies1<NoBody>, Headers0> =
    Output(ExactStatus(Status.Ok), Bodies1(noBody), noHeaders)

/** The implicit empty `200 OK` output used before any explicit output is declared. */
data object DefaultOutput : Outputs {
    override val values: List<OutputAlternative> = listOf(emptyOkOutput)
}

/** Combines this exact status with one required [body]. */
infix fun <Value : Any> Status.with(
    body: Body<Value>
): Output<ExactStatus, Bodies1<Body<Value>>, Headers0> = ExactStatus(this) with body

/** Combines this exact status with an explicitly absent body. */
infix fun Status.with(noBody: NoBody): Output<ExactStatus, Bodies1<NoBody>, Headers0> =
    ExactStatus(this) with noBody

/** Combines this exact status with alternative [bodies]. */
infix fun <B : Bodies> Status.with(bodies: B): Output<ExactStatus, B, Headers0> =
    ExactStatus(this) with bodies

/** Combines this status matcher with one required [body]. */
infix fun <M : StatusMatcher, Value : Any> M.with(
    body: Body<Value>
): Output<M, Bodies1<Body<Value>>, Headers0> = with(bodiesOf(body))

/** Combines this status matcher with an explicitly absent body. */
infix fun <M : StatusMatcher> M.with(noBody: NoBody): Output<M, Bodies1<NoBody>, Headers0> =
    with(Bodies1(noBody))

/** Combines this status matcher with alternative [bodies]. */
infix fun <M : StatusMatcher, B : Bodies> M.with(bodies: B): Output<M, B, Headers0> =
    Output(this, validatedBodies(bodies), noHeaders)

/** Attaches [headers] after this output's status and bodies. */
infix fun <M : StatusMatcher, B : Bodies, H : NonEmptyTuple<Header<*, *>>> Output<M, B, Headers0>.with(
    headers: H
): Output<M, B, H> = Output(matcher, bodies, validatedHeaders(headers))

private fun <P : Paths, Q : Queries, H : Headers, I : Input, O : Outputs>
    Endpoint<P, Q, H, I, *, Draft>.withOutputs(
        outputs: O
    ): Endpoint<P, Q, H, I, O, Draft> =
        Endpoint(
            method = method,
            uri = uri,
            headers = headers,
            input = input,
            outputs = outputs,
            documentation = documentation,
            tags = tags,
            state = state
        )

private fun Outputs.requireUnambiguous(output: Output<*, *, *>) {
    val existing = values.filterIsInstance<Output<*, *, *>>()
    val overlap =
        (100..599).firstOrNull { code ->
            val status = Status(code)
            existing.any { alternative ->
                alternative.matcher.matchesForValidation(status) &&
                    output.matcher.matchesForValidation(status)
            }
        }
    require(overlap == null) { "Output status matchers overlap at status $overlap" }
}

private fun StatusMatcher.matchesForValidation(status: Status): Boolean =
    try {
        matches(status)
    } catch (cause: Exception) {
        throw IllegalArgumentException(
            "Status matcher '${validationDescription()}' failed while evaluating status ${status.code}",
            cause
        )
    }

private fun StatusMatcher.validationDescription(): String =
    when (this) {
        is ExactStatus -> "exact status ${status.code}"
        is StatusSet -> "status set ${statuses.joinToString { status -> status.code.toString() }}"
        is StatusRange -> "status range $range"
        is CustomStatus -> description
    }

/** Replaces the implicit default with the first explicit [output]. */
fun <P : Paths, Q : Queries, H : Headers, I : Input, OutputType : Output<*, *, *>>
    Endpoint<P, Q, H, I, DefaultOutput, Draft>.output(
        output: OutputType
    ): Endpoint<P, Q, H, I, Outputs1<OutputType>, Draft> = withOutputs(Outputs1(output))

/** Appends a second explicit [output]. */
@JvmName("endpointWithOneOutputOutput")
fun <P : Paths, Q : Queries, H : Headers, I : Input, Output1 : Output<*, *, *>, OutputType : Output<*, *, *>>
    Endpoint<P, Q, H, I, Outputs1<Output1>, Draft>.output(
        output: OutputType
    ): Endpoint<P, Q, H, I, Outputs2<Output1, OutputType>, Draft> {
        outputs.requireUnambiguous(output)
        return withOutputs(Outputs2(outputs._1, output))
    }

/** Appends a third explicit [output]. */
@JvmName("endpointWithTwoOutputsOutput")
fun <
    P : Paths,
    Q : Queries,
    H : Headers,
    I : Input,
    Output1 : Output<*, *, *>,
    Output2 : Output<*, *, *>,
    OutputType : Output<*, *, *>
> Endpoint<P, Q, H, I, Outputs2<Output1, Output2>, Draft>.output(
    output: OutputType
): Endpoint<P, Q, H, I, Outputs3<Output1, Output2, OutputType>, Draft> {
    outputs.requireUnambiguous(output)
    return withOutputs(Outputs3(outputs._1, outputs._2, output))
}

/** Appends a fourth explicit [output]. */
@JvmName("endpointWithThreeOutputsOutput")
fun <
    P : Paths,
    Q : Queries,
    H : Headers,
    I : Input,
    Output1 : Output<*, *, *>,
    Output2 : Output<*, *, *>,
    Output3 : Output<*, *, *>,
    OutputType : Output<*, *, *>
> Endpoint<P, Q, H, I, Outputs3<Output1, Output2, Output3>, Draft>.output(
    output: OutputType
): Endpoint<P, Q, H, I, Outputs4<Output1, Output2, Output3, OutputType>, Draft> {
    outputs.requireUnambiguous(output)
    return withOutputs(Outputs4(outputs._1, outputs._2, outputs._3, output))
}

/** Appends a fifth explicit [output]. */
@JvmName("endpointWithFourOutputsOutput")
fun <
    P : Paths,
    Q : Queries,
    H : Headers,
    I : Input,
    Output1 : Output<*, *, *>,
    Output2 : Output<*, *, *>,
    Output3 : Output<*, *, *>,
    Output4 : Output<*, *, *>,
    OutputType : Output<*, *, *>
> Endpoint<P, Q, H, I, Outputs4<Output1, Output2, Output3, Output4>, Draft>.output(
    output: OutputType
): Endpoint<P, Q, H, I, Outputs5<Output1, Output2, Output3, Output4, OutputType>, Draft> {
    outputs.requireUnambiguous(output)
    return withOutputs(Outputs5(outputs._1, outputs._2, outputs._3, outputs._4, output))
}

/** Appends a sixth explicit [output]. */
@JvmName("endpointWithFiveOutputsOutput")
fun <
    P : Paths,
    Q : Queries,
    H : Headers,
    I : Input,
    Output1 : Output<*, *, *>,
    Output2 : Output<*, *, *>,
    Output3 : Output<*, *, *>,
    Output4 : Output<*, *, *>,
    Output5 : Output<*, *, *>,
    OutputType : Output<*, *, *>
> Endpoint<P, Q, H, I, Outputs5<Output1, Output2, Output3, Output4, Output5>, Draft>.output(
    output: OutputType
): Endpoint<P, Q, H, I, Outputs6<Output1, Output2, Output3, Output4, Output5, OutputType>, Draft> {
    outputs.requireUnambiguous(output)
    return withOutputs(Outputs6(outputs._1, outputs._2, outputs._3, outputs._4, outputs._5, output))
}

/** Appends a seventh explicit [output]. */
@JvmName("endpointWithSixOutputsOutput")
fun <
    P : Paths,
    Q : Queries,
    H : Headers,
    I : Input,
    Output1 : Output<*, *, *>,
    Output2 : Output<*, *, *>,
    Output3 : Output<*, *, *>,
    Output4 : Output<*, *, *>,
    Output5 : Output<*, *, *>,
    Output6 : Output<*, *, *>,
    OutputType : Output<*, *, *>
> Endpoint<P, Q, H, I, Outputs6<Output1, Output2, Output3, Output4, Output5, Output6>, Draft>.output(
    output: OutputType
): Endpoint<P, Q, H, I, Outputs7<Output1, Output2, Output3, Output4, Output5, Output6, OutputType>, Draft> {
    outputs.requireUnambiguous(output)
    return withOutputs(
        Outputs7(outputs._1, outputs._2, outputs._3, outputs._4, outputs._5, outputs._6, output)
    )
}

/** Appends an eighth explicit [output]. */
@JvmName("endpointWithSevenOutputsOutput")
fun <
    P : Paths,
    Q : Queries,
    H : Headers,
    I : Input,
    Output1 : Output<*, *, *>,
    Output2 : Output<*, *, *>,
    Output3 : Output<*, *, *>,
    Output4 : Output<*, *, *>,
    Output5 : Output<*, *, *>,
    Output6 : Output<*, *, *>,
    Output7 : Output<*, *, *>,
    OutputType : Output<*, *, *>
> Endpoint<P, Q, H, I, Outputs7<Output1, Output2, Output3, Output4, Output5, Output6, Output7>, Draft>.output(
    output: OutputType
): Endpoint<P, Q, H, I, Outputs8<Output1, Output2, Output3, Output4, Output5, Output6, Output7, OutputType>, Draft> {
    outputs.requireUnambiguous(output)
    return withOutputs(
        Outputs8(
            outputs._1,
            outputs._2,
            outputs._3,
            outputs._4,
            outputs._5,
            outputs._6,
            outputs._7,
            output
        )
    )
}
