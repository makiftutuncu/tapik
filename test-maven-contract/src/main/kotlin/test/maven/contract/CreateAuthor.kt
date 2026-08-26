package dev.akif.tapik.test.maven.contract

import kotlinx.serialization.Serializable

/** Input accepted when creating an author in the cross-target conformance fixture. */
@Serializable
data class CreateAuthor(
    val name: String
)
