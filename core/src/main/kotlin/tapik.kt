package dev.akif.tapik

abstract class Endpoint<out Parameters, out InputHeaders, out InputBody, out OutputHeaders, out OutputBodies> {
    internal abstract val id: String
    internal abstract val description: String?
    internal abstract val details: String?
    internal abstract val parameters: Parameters
    internal abstract val inputHeaders: InputHeaders
    internal abstract val inputBody: InputBody
    internal abstract val outputHeaders: OutputHeaders
    internal abstract val outputBodies: OutputBodies
}
