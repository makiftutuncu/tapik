package dev.akif.tapik

abstract class Endpoint<Method, UriBuilder, Headers, Input, Outputs> {
    abstract val id: String
    abstract val description: String?
    abstract val details: String?
    abstract val method: Method
    abstract val uri: UriBuilder
    abstract val headers: Headers
    abstract val input: Input
    abstract val outputs: Outputs
}
