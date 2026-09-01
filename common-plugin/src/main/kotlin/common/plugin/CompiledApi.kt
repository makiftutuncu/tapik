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
 * @property propertyPath delegated property names leading from the selected root API to [value].
 */
@ConsistentCopyVisibility
data class CompiledEndpoint internal constructor(
    val value: Endpoint<*, *, *, *, *, Ready>,
    val type: KotlinType,
    private val sourcePropertyPath: List<String>
) {
    val propertyPath: List<String> =
        sourcePropertyPath.snapshotList().also { path ->
            require(path.isNotEmpty()) { "Compiled endpoint property path must not be empty" }
        }
}

/** Returns a statically typed Kotlin access from [rootExpression] through this endpoint's property path. */
fun CompiledEndpoint.kotlinPropertyAccess(rootExpression: String): String =
    propertyPath.joinToString(separator = ".", prefix = "$rootExpression.") { property ->
        property.kotlinReferenceIdentifier()
    }

/** Returns the property path as one source-name input while retaining every nesting segment. */
fun CompiledEndpoint.kotlinNameSource(): String = propertyPath.joinToString("-")
