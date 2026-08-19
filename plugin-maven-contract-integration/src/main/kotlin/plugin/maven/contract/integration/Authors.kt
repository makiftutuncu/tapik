package dev.akif.tapik.plugin.maven.contract.integration

import dev.akif.tapik.*
import dev.akif.tapik.format.kotlinx.jsonBody
import kotlinx.serialization.Serializable

/** Author representation published by the contract integration fixture. */
@Serializable
data class Author(
    val id: String,
    val name: String
)

/** Author operations published for cross-module generation tests. */
class Authors : Api() {
    private val requestId = header.string("X-Request-Id")

    val list by
        get(root / "authors" + query.string("name").optional())
            .header(requestId)
            .output(Status.Ok with jsonBody<List<Author>>())
}
