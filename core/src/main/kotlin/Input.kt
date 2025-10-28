@file:Suppress("ktlint:standard:max-line-length")

package dev.akif.tapik

/**
 * Couples the request headers with the request body definition.
 *
 * @param headers tuple describing headers expected from the caller.
 * @param body body definition expected from the caller; defaults to [EmptyBody] when absent.
 */
data class Input<H : Headers, B : Body<*>>(
    val headers: H,
    val body: B
)
