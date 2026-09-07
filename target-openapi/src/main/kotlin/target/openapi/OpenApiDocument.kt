package dev.akif.tapik.target.openapi

/**
 * The OpenAPI document subset emitted by tapik.
 *
 * @property specificationVersion OpenAPI specification version used by the document.
 * @property info identifying information about the API document.
 * @property paths operations grouped by URI path template in declaration order.
 * @property components reusable components referenced by operations.
 */
class OpenApiDocument(
    val specificationVersion: String = "3.2.0",
    val info: OpenApiInfo,
    paths: Map<String, OpenApiPathItem>,
    val components: OpenApiComponents = OpenApiComponents()
) {
    val paths: Map<String, OpenApiPathItem> = paths.snapshotMap()

    /** Returns [specificationVersion] for destructuring. */
    operator fun component1(): String = specificationVersion

    /** Returns [info] for destructuring. */
    operator fun component2(): OpenApiInfo = info

    /** Returns [paths] for destructuring. */
    operator fun component3(): Map<String, OpenApiPathItem> = paths

    /** Returns [components] for destructuring. */
    operator fun component4(): OpenApiComponents = components

    /** Returns a copy, snapshotting structural collection inputs. */
    fun copy(
        specificationVersion: String = this.specificationVersion,
        info: OpenApiInfo = this.info,
        paths: Map<String, OpenApiPathItem> = this.paths,
        components: OpenApiComponents = this.components
    ): OpenApiDocument = OpenApiDocument(specificationVersion, info, paths, components)

    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is OpenApiDocument &&
                specificationVersion == other.specificationVersion &&
                info == other.info &&
                paths == other.paths &&
                components == other.components)

    override fun hashCode(): Int {
        var result = specificationVersion.hashCode()
        result = 31 * result + info.hashCode()
        result = 31 * result + paths.hashCode()
        result = 31 * result + components.hashCode()
        return result
    }

    override fun toString(): String =
        "OpenApiDocument(specificationVersion=$specificationVersion, info=$info, paths=$paths, components=$components)"
}
