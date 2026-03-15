@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

/**
 * Staged endpoint builder state.
 *
 * Each DSL step returns a new instance with a more specific [input] or [outputs] type, which is
 * how Tapik keeps the fluent builder immutable while still preserving the exact request and
 * response shape at compile time.
 */
data class HttpEndpointBuildingContext<P : Parameters, I : Input<*, *>, O : Outputs>(
    internal val id: String,
    internal val description: String?,
    internal val details: String?,
    internal val method: Method,
    internal val path: List<String>,
    internal val parameters: P,
    internal val input: I,
    internal val outputs: O
) {
    internal fun <I1 : Input<*, *>, O1 : Outputs> modifiedAs(
        input: I1,
        outputs: O1
    ): HttpEndpointBuildingContext<P, I1, O1> =
        HttpEndpointBuildingContext(
            id = id,
            description = description,
            details = details,
            method = method,
            path = path,
            parameters = parameters,
            input = input,
            outputs = outputs
        )
}
