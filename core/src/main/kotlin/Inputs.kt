package dev.akif.tapik

/** A typed endpoint input definition. */
sealed interface Input

/** An endpoint accepting no request body input. */
data object NoInput : Input

/** The input value for an endpoint accepting no request body. */
val noInput: NoInput = NoInput

/** A request input containing one or more body alternatives. */
@ConsistentCopyVisibility
data class BodyInput<out B : Bodies> internal constructor(
    val bodies: B
) : Input

/** Attaches one request [body] to this inputless draft endpoint. */
fun <P : Paths, Q : Queries, H : Headers, O : Outputs, Value : Any>
    Endpoint<P, Q, H, NoInput, O, Draft>.input(
        body: Body<Value>
    ): Endpoint<P, Q, H, BodyInput<Bodies1<Body<Value>>>, O, Draft> = input(bodiesOf(body))

/** Attaches request [bodies] to this inputless draft endpoint. */
fun <P : Paths, Q : Queries, H : Headers, O : Outputs, B : Bodies>
    Endpoint<P, Q, H, NoInput, O, Draft>.input(
        bodies: B
    ): Endpoint<P, Q, H, BodyInput<B>, O, Draft> {
        require(bodies.values.isNotEmpty()) { "Request body alternatives must not be empty" }
        return Endpoint(
            method = method,
            uri = uri,
            headers = headers,
            input = BodyInput(bodies),
            outputs = outputs,
            documentation = documentation,
            tags = tags,
            state = state
        )
    }
