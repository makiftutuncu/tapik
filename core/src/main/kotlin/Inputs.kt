package dev.akif.tapik

/** A typed endpoint input definition. */
sealed interface Input

/** An endpoint accepting no request body input. */
data object NoInput : Input

/** The input value for an endpoint accepting no request body. */
val noInput: NoInput = NoInput

/**
 * A request input containing one or more body alternatives. The request body is optional when [bodies] ends with
 * [NoBody].
 *
 * @property bodies media representations in declaration order.
 * @property documentation human-readable request-body documentation.
 */
@ConsistentCopyVisibility
data class BodyInput<out B : Bodies> internal constructor(
    /** Media representations in declaration order. */
    val bodies: B,
    /** Human-readable request-body documentation. */
    val documentation: RequestBodyDocumentation = RequestBodyDocumentation()
) : Input

/**
 * Attaches one required request [body] to this inputless draft endpoint.
 *
 * @param body body media type and format.
 * @return this endpoint with a required request body.
 */
fun <P : Paths, Q : Queries, H : Headers, O : Outputs, Value : Any>
    Endpoint<P, Q, H, NoInput, O, Draft>.input(
        body: Body<Value>
    ): Endpoint<P, Q, H, BodyInput<Bodies1<Body<Value>>>, O, Draft> = input(body, description = null)

/**
 * Attaches one required request [body] with its [description] to this inputless draft endpoint.
 *
 * @param body body media type and format.
 * @param description optional documentation for the complete request body.
 * @return this endpoint with a required request body.
 * @throws IllegalArgumentException when [description] is present but blank.
 */
fun <P : Paths, Q : Queries, H : Headers, O : Outputs, Value : Any>
    Endpoint<P, Q, H, NoInput, O, Draft>.input(
        body: Body<Value>,
        description: String?
    ): Endpoint<P, Q, H, BodyInput<Bodies1<Body<Value>>>, O, Draft> = input(bodiesOf(body), description)

/**
 * Attaches request [bodies] to this inputless draft endpoint.
 *
 * The group may end with [noBody] to allow a bodyless request.
 *
 * @param bodies body alternatives for the request.
 * @return this endpoint with the supplied request body alternatives.
 * @throws IllegalArgumentException when media types are duplicated or [noBody] is not the final, unique alternative.
 */
fun <P : Paths, Q : Queries, H : Headers, O : Outputs, B : Bodies>
    Endpoint<P, Q, H, NoInput, O, Draft>.input(
        bodies: B
    ): Endpoint<P, Q, H, BodyInput<B>, O, Draft> = input(bodies, description = null)

/**
 * Attaches request [bodies] with their shared [description] to this inputless draft endpoint.
 *
 * The group may end with [noBody] to allow a bodyless request.
 *
 * @param bodies body alternatives for the request.
 * @param description optional documentation for the complete request body.
 * @return this endpoint with the supplied request body alternatives.
 * @throws IllegalArgumentException when [description] is blank or the body alternatives are invalid.
 */
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

/**
 * Replaces this draft endpoint's request-body description.
 *
 * @param description new request-body description.
 * @return this endpoint with the updated request-body documentation.
 * @throws IllegalArgumentException when [description] is blank.
 */
fun <P : Paths, Q : Queries, H : Headers, B : Bodies, O : Outputs>
    Endpoint<P, Q, H, BodyInput<B>, O, Draft>.requestBodyDescription(
        description: String
    ): Endpoint<P, Q, H, BodyInput<B>, O, Draft> =
        copy(input = input.copy(documentation = input.documentation.copy(description = description)))
