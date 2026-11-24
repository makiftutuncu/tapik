package dev.akif.tapik.target.openapi

/**
 * A request body and its media representations.
 *
 * @property description optional detailed description.
 * @property required whether callers must provide a body.
 */
class OpenApiRequestBody(
    /** Optional detailed description. */
    val description: String? = null,
    /** Whether callers must provide a body. */
    val required: Boolean,
    content: Map<String, OpenApiMediaType>
) {
    /** Unmodifiable snapshot of representations keyed by media type in declaration order. */
    val content: Map<String, OpenApiMediaType> = content.snapshotMap()

    /** Returns [description] for destructuring. */
    operator fun component1(): String? = description

    /** Returns [required] for destructuring. */
    operator fun component2(): Boolean = required

    /** Returns [content] for destructuring. */
    operator fun component3(): Map<String, OpenApiMediaType> = content

    /** Returns a copy, snapshotting structural collection inputs. */
    fun copy(
        description: String? = this.description,
        required: Boolean = this.required,
        content: Map<String, OpenApiMediaType> = this.content
    ): OpenApiRequestBody = OpenApiRequestBody(description, required, content)

    /** Compares this value with [other]. */
    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is OpenApiRequestBody &&
                description == other.description &&
                required == other.required &&
                content == other.content)

    /** Returns a hash code consistent with [equals]. */
    override fun hashCode(): Int {
        var result = description.hashCode()
        result = 31 * result + required.hashCode()
        result = 31 * result + content.hashCode()
        return result
    }

    /** Returns a string representation of this value. */
    override fun toString(): String =
        "OpenApiRequestBody(description=$description, required=$required, content=$content)"
}
