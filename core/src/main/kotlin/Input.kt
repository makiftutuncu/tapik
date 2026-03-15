@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

/**
 * Request contract for an endpoint.
 *
 * Tapik keeps headers and body together because they are configured by the same DSL stage and are
 * both needed to derive a client method signature or interpret an incoming request.
 */
data class Input<H : Headers, B : Body<*>>(
    /** Header contract applied to the request. */
    val headers: H,
    /** Body contract applied to the request. */
    val body: B
)
