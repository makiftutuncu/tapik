package dev.akif.tapik.plugin.gradle

/** Determines where tapik loads compiled API contracts from. */
enum class Mode {
    /** Load API contracts from the dedicated contract classpath. */
    COMPILED_CONTRACT,

    /** Load API contracts from the current project's compiled main output. */
    SAME_PROJECT
}
