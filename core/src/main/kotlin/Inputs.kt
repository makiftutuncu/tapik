package dev.akif.tapik

/** A typed endpoint input definition. */
sealed interface Input

/** An endpoint accepting no request body input. */
data object NoInput : Input

/** The input value for an endpoint accepting no request body. */
val noInput: NoInput = NoInput

/**
 * A request input containing one or more body alternatives.
 *
 * @property bodies media representations in declaration order.
 * @property documentation human-readable request-body documentation.
 */
@ConsistentCopyVisibility
data class BodyInput<out B : Bodies> internal constructor(
    val bodies: B,
    val documentation: RequestBodyDocumentation = RequestBodyDocumentation()
) : Input

/** Attaches one request [body] to this inputless draft endpoint. */
fun <P : Paths, Q : Queries, H : Headers, O : Outputs, Value : Any>
    Endpoint<P, Q, H, NoInput, O, Draft>.input(
        body: Body<Value>
    ): Endpoint<P, Q, H, BodyInput<Bodies1<Body<Value>>>, O, Draft> = input(body, description = null)

/** Attaches one request [body] with its [description] to this inputless draft endpoint. */
fun <P : Paths, Q : Queries, H : Headers, O : Outputs, Value : Any>
    Endpoint<P, Q, H, NoInput, O, Draft>.input(
        body: Body<Value>,
        description: String?
    ): Endpoint<P, Q, H, BodyInput<Bodies1<Body<Value>>>, O, Draft> = input(bodiesOf(body), description)

/** Attaches request [bodies] to this inputless draft endpoint. */
fun <P : Paths, Q : Queries, H : Headers, O : Outputs, B : Bodies>
    Endpoint<P, Q, H, NoInput, O, Draft>.input(
        bodies: B
    ): Endpoint<P, Q, H, BodyInput<B>, O, Draft> = input(bodies, description = null)

/** Attaches request [bodies] with their shared [description] to this inputless draft endpoint. */
fun <P : Paths, Q : Queries, H : Headers, O : Outputs, B : Bodies>
    Endpoint<P, Q, H, NoInput, O, Draft>.input(
        bodies: B,
        description: String?
    ): Endpoint<P, Q, H, BodyInput<B>, O, Draft> {
        val validBodies = validatedBodies(bodies)
        return Endpoint(
            method = method,
            uri = uri,
            headers = headers,
            input = BodyInput(validBodies, RequestBodyDocumentation(description)),
            outputs = outputs,
            documentation = documentation,
            tags = tags,
            state = state
        )
    }

/** Replaces this draft endpoint's request-body description. */
fun <P : Paths, Q : Queries, H : Headers, B : Bodies, O : Outputs>
    Endpoint<P, Q, H, BodyInput<B>, O, Draft>.requestBodyDescription(
        description: String
    ): Endpoint<P, Q, H, BodyInput<B>, O, Draft> =
        copy(input = input.copy(documentation = input.documentation.copy(description = description)))
