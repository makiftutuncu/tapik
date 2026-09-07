package dev.akif.tapik.target.openapi

import dev.akif.tapik.Method

/**
 * Operations sharing one URI path template.
 *
 * @property operations operations keyed by HTTP method in declaration order.
 */
class OpenApiPathItem(
    operations: Map<Method, OpenApiOperation>
) {
    val operations: Map<Method, OpenApiOperation> = operations.snapshotMap()

    /** Returns [operations] for destructuring. */
    operator fun component1(): Map<Method, OpenApiOperation> = operations

    /** Returns a copy, snapshotting structural collection inputs. */
    fun copy(
        operations: Map<Method, OpenApiOperation> = this.operations
    ): OpenApiPathItem = OpenApiPathItem(operations)

    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is OpenApiPathItem &&
                operations == other.operations)

    override fun hashCode(): Int = operations.hashCode()

    override fun toString(): String =
        "OpenApiPathItem(operations=$operations)"
}
