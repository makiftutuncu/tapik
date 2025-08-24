package dev.akif.tapik

abstract class Endpoint<out Parameters, out Input, out Outputs> {
    internal abstract val id: String
    internal abstract val description: String?
    internal abstract val details: String?
    internal abstract val parameters: Parameters
    internal abstract val input: Input
    internal abstract val outputs: Outputs
}
