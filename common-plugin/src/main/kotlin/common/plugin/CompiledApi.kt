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
class CompiledApi(
    val value: Api,
    endpoints: List<CompiledEndpoint>
) {
    val endpoints: List<CompiledEndpoint> = endpoints.snapshotList()

    /** Returns [value] for destructuring. */
    operator fun component1(): Api = value

    /** Returns [endpoints] for destructuring. */
    operator fun component2(): List<CompiledEndpoint> = endpoints

    /** Returns a copy, snapshotting structural collection inputs. */
    fun copy(
        value: Api = this.value,
        endpoints: List<CompiledEndpoint> = this.endpoints
    ): CompiledApi = CompiledApi(value, endpoints)

    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is CompiledApi &&
                value == other.value &&
                endpoints == other.endpoints)

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + endpoints.hashCode()
        return result
    }

    override fun toString(): String =
        "CompiledApi(value=$value, endpoints=$endpoints)"
}

/**
 * A runtime endpoint paired with its delegated property's compiled Kotlin type.
 *
 * @property value runtime endpoint value.
 * @property type exact compiled property type.
 * @property propertyPath delegated property names leading from the selected root API to [value].
 */
class CompiledEndpoint internal constructor(
    val value: Endpoint<*, *, *, *, *, Ready>,
    val type: KotlinType,
    sourcePropertyPath: List<String>
) {
    val propertyPath: List<String> = sourcePropertyPath.snapshotList().also { path ->
        require(path.isNotEmpty()) { "Compiled endpoint property path must not be empty" }
    }

    /** Returns [value] for destructuring. */
    operator fun component1(): Endpoint<*, *, *, *, *, Ready> = value

    /** Returns [type] for destructuring. */
    operator fun component2(): KotlinType = type

    /** Returns a copy, snapshotting structural collection inputs. */
    internal fun copy(
        value: Endpoint<*, *, *, *, *, Ready> = this.value,
        type: KotlinType = this.type,
        sourcePropertyPath: List<String> = this.propertyPath
    ): CompiledEndpoint = CompiledEndpoint(value, type, sourcePropertyPath)

    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is CompiledEndpoint &&
                value == other.value &&
                type == other.type &&
                propertyPath == other.propertyPath)

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + propertyPath.hashCode()
        return result
    }

    override fun toString(): String =
        "CompiledEndpoint(value=$value, type=$type, sourcePropertyPath=$propertyPath)"
}

/** Returns a statically typed Kotlin access from [rootExpression] through this endpoint's property path. */
fun CompiledEndpoint.kotlinPropertyAccess(rootExpression: String): String =
    propertyPath.joinToString(separator = ".", prefix = "$rootExpression.") { property ->
        property.kotlinReferenceIdentifier()
    }

/** Returns the property path as one source-name input while retaining every nesting segment. */
fun CompiledEndpoint.kotlinNameSource(): String = propertyPath.joinToString("-")
