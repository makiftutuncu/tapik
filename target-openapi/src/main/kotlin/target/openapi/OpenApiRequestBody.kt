package dev.akif.tapik.target.openapi

/**
 * A request body and its media representations.
 *
 * @property description optional detailed description.
 * @property required whether callers must provide a body.
 * @property content representations keyed by media type in declaration order.
 */
class OpenApiRequestBody(
    val description: String? = null,
    val required: Boolean,
    content: Map<String, OpenApiMediaType>
) {
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

    override fun equals(other: Any?): Boolean =
        this === other ||
            (other is OpenApiRequestBody &&
                description == other.description &&
                required == other.required &&
                content == other.content)

    override fun hashCode(): Int {
        var result = description.hashCode()
        result = 31 * result + required.hashCode()
        result = 31 * result + content.hashCode()
        return result
    }

    override fun toString(): String =
        "OpenApiRequestBody(description=$description, required=$required, content=$content)"
}
