package dev.akif.tapik.target.openapi

/**
 * A response with headers and media representations.
 *
 * @property description response description, always supplied by tapik even though OpenAPI 3.2 permits omission.
 */
class OpenApiResponse(
    /** Response description, always supplied by tapik even though OpenAPI 3.2 permits omission. */
    val description: String,
    headers: Map<String, OpenApiHeader>,
    content: Map<String, OpenApiMediaType>
) {
    /** Unmodifiable snapshot of response headers keyed by name in declaration order. */
    val headers: Map<String, OpenApiHeader> = headers.snapshotMap()

    /** Unmodifiable snapshot of representations keyed by media type in declaration order. */
    val content: Map<String, OpenApiMediaType> = content.snapshotMap()

    /** Returns [description] for destructuring. */
    operator fun component1(): String = description

    /** Returns [headers] for destructuring. */
    operator fun component2(): Map<String, OpenApiHeader> = headers

    /** Returns [content] for destructuring. */
    operator fun component3(): Map<String, OpenApiMediaType> = content

    /** Returns a copy, snapshotting structural collection inputs. */
    fun copy(
        description: String = this.description,
        headers: Map<String, OpenApiHeader> = this.headers,
        content: Map<String, OpenApiMediaType> = this.content
    ): OpenApiResponse = OpenApiResponse(description, headers, content)

    /** Compares this value with [other]. */
    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is OpenApiResponse &&
                description == other.description &&
                headers == other.headers &&
                content == other.content)

    /** Returns a hash code consistent with [equals]. */
    override fun hashCode(): Int {
        var result = description.hashCode()
        result = 31 * result + headers.hashCode()
        result = 31 * result + content.hashCode()
        return result
    }

    /** Returns a string representation of this value. */
    override fun toString(): String =
        "OpenApiResponse(description=$description, headers=$headers, content=$content)"
}
