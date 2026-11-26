package dev.akif.tapik.target.openapi

import dev.akif.tapik.Method

/**
 * Operations sharing one URI path template.
 *
 * @param operations operations keyed by HTTP method in declaration order.
 */
class OpenApiPathItem(
    operations: Map<Method, OpenApiOperation>
) {
    /** Unmodifiable snapshot of operations keyed by HTTP method in declaration order. */
    val operations: Map<Method, OpenApiOperation> = operations.snapshotMap()

    /** Returns [operations] for destructuring. */
    operator fun component1(): Map<Method, OpenApiOperation> = operations

    /** Returns a copy, snapshotting structural collection inputs. */
    fun copy(
        operations: Map<Method, OpenApiOperation> = this.operations
    ): OpenApiPathItem = OpenApiPathItem(operations)

    /** Compares this value with [other]. */
    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is OpenApiPathItem &&
                operations == other.operations)

    /** Returns a hash code consistent with [equals]. */
    override fun hashCode(): Int = operations.hashCode()

    /** Returns a string representation of this value. */
    override fun toString(): String =
        "OpenApiPathItem(operations=$operations)"
}
