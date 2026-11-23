package dev.akif.tapik.common.plugin

import dev.akif.tapik.Api
import dev.akif.tapik.Endpoint
import dev.akif.tapik.Ready

/**
 * A runtime API paired with the compiled types of its endpoints.
 *
 * @property value runtime API value.
 */
class CompiledApi(
    /** Runtime API value. */
    val value: Api,
    endpoints: List<CompiledEndpoint>
) {
    /** Endpoints in runtime declaration order. */
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

    /** Compares this value with [other]. */
    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is CompiledApi &&
                value == other.value &&
                endpoints == other.endpoints)

    /** Returns a hash code consistent with [equals]. */
    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + endpoints.hashCode()
        return result
    }

    /** Returns a string representation of this value. */
    override fun toString(): String =
        "CompiledApi(value=$value, endpoints=$endpoints)"
}

/**
 * A runtime endpoint paired with its delegated property's compiled Kotlin type.
 *
 * @property value runtime endpoint value.
 * @property type exact compiled property type.
 */
class CompiledEndpoint internal constructor(
    /** Runtime endpoint value. */
    val value: Endpoint<*, *, *, *, *, Ready>,
    /** Exact compiled property type. */
    val type: KotlinType,
    sourcePropertyPath: List<String>
) {
    /** Delegated property names leading from the selected root API to [value]. */
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

    /** Compares this value with [other]. */
    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is CompiledEndpoint &&
                value == other.value &&
                type == other.type &&
                propertyPath == other.propertyPath)

    /** Returns a hash code consistent with [equals]. */
    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + propertyPath.hashCode()
        return result
    }

    /** Returns a string representation of this value. */
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
