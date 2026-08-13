package dev.akif.tapik

/** A typed endpoint input definition. */
sealed interface Input

/** An endpoint accepting no request body input. */
data object NoInput : Input

/** The input value for an endpoint accepting no request body. */
val noInput: NoInput = NoInput
