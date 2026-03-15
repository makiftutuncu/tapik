package dev.akif.tapik

import kotlin.properties.ReadOnlyProperty

/**
 * Entry point for declaring endpoint sets with Tapik's DSL.
 *
 * Implement this interface, usually on a singleton object, to group related endpoints under one
 * type and gain access to the `endpoint` builders that capture each declaration as an
 * [HttpEndpoint].
 */
interface API {
    /**
     * Builds an endpoint immediately using the supplied [id].
     *
     * The [builder] runs against a fresh [HttpEndpointVerbBuildingContext], which forces the DSL to
     * choose the HTTP method and URI first and then refine the request and response shapes.
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
     * Declares an endpoint as a property delegate.
     *
     * This variant exists so endpoint definitions can stay close to the Kotlin property that names
     * them. The delegate reuses the property name as the endpoint [HttpEndpoint.id], which keeps the
     * declaration concise while still producing the same immutable endpoint contract.
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
