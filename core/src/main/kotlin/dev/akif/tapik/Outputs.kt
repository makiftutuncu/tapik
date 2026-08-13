package dev.akif.tapik

/** A typed collection of endpoint output alternatives. */
sealed interface Outputs

/** The implicit empty `200 OK` output used before any explicit output is declared. */
data object DefaultOutputs : Outputs
