package dev.akif.tapik.common.plugin

import dev.akif.tapik.Api
import dev.akif.tapik.Endpoint
import dev.akif.tapik.Ready

/**
 * A runtime API paired with the compiled types of its endpoints.
 *
 * @property value runtime API value.
 * @property endpoints endpoints in runtime declaration order.
 */
data class CompiledApi(
    val value: Api,
    val endpoints: List<CompiledEndpoint>
)

/**
 * A runtime endpoint paired with its delegated property's compiled Kotlin type.
 *
 * @property value runtime endpoint value.
 * @property type exact compiled property type.
 */
data class CompiledEndpoint(
    val value: Endpoint<*, *, *, *, *, Ready>,
    val type: KotlinType
)
