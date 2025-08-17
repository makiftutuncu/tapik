package dev.akif.tapik

abstract class Endpoint<Method, UriBuilder, Headers, Input, Outputs> {
    internal abstract val method: Method
    internal abstract val uri: UriBuilder
    internal abstract val headers: Headers
    internal abstract val input: Input
    internal abstract val outputs: Outputs
}
