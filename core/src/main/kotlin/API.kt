package dev.akif.tapik

import kotlin.properties.ReadOnlyProperty

/**
 * Entry point for the tapik endpoint DSL.
 *
 * Implement this interface (typically on a singleton object) to gain access to the `endpoint` builders.
 * This keeps endpoint construction constrained and discoverable (e.g., `Users.list`, `Products.get`).
 */
interface API {
    /**
     * Builds an [HttpEndpoint] with an explicit [id] using the DSL.
     *
     * @param P tuple capturing path and query parameters.
     * @param I inbound input definition for headers and body.
     * @param O outbound response definitions.
     * @param id unique identifier to assign to the endpoint (usually the property name).
     * @param description optional short description shown in generated documentation.
     * @param details optional long-form documentation such as business rules or references.
     * @param builder DSL block invoked on a fresh [HttpEndpointVerbBuildingContext].
     * @return fully materialised [HttpEndpoint] built from the provided [builder].
     */
    fun <P : Parameters, I : Input<*, *>, O : Outputs> endpoint(
        id: String,
        description: String? = null,
        details: String? = null,
        builder: HttpEndpointVerbBuildingContext.() -> HttpEndpointBuildingContext<P, I, O>
    ): HttpEndpoint<P, I, O> {
        val ctx = HttpEndpointVerbBuildingContext(id, description, details).builder()
        return HttpEndpoint(
            id = ctx.id,
            description = ctx.description,
            details = ctx.details,
            method = ctx.method,
            path = ctx.path,
            parameters = ctx.parameters,
            input = ctx.input,
            outputs = ctx.outputs
        )
    }

    /**
     * Declares an HTTP endpoint builder that captures property metadata at declaration time.
     *
     * The returned delegate instantiates a [HttpEndpointVerbBuildingContext] so the caller can choose
     * the verb and URI together, then chain header and body builders before materialising a [HttpEndpoint].
     *
     * @param T owner type that will receive the property.
     * @param P tuple capturing path and query parameters.
     * @param I inbound input definition for headers and body.
     * @param O outbound response definitions.
     * @param description optional short description shown in generated documentation.
     * @param details optional long-form documentation such as business rules or references.
     * @param builder DSL block building the endpoint structure using [HttpEndpointVerbBuildingContext].
     * @return property delegate that records endpoint metadata using the owning property name and produces [HttpEndpoint].
     * @see HttpEndpoint
     */
    fun <T, P : Parameters, I : Input<*, *>, O : Outputs> endpoint(
        description: String? = null,
        details: String? = null,
        builder: HttpEndpointVerbBuildingContext.() -> HttpEndpointBuildingContext<P, I, O>
    ): ReadOnlyProperty<T, HttpEndpoint<P, I, O>> =
        ReadOnlyProperty { _, property ->
            endpoint(
                id = property.name,
                description = description,
                details = details,
                builder = builder
            )
        }
}
