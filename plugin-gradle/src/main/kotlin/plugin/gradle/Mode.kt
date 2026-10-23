package dev.akif.tapik.plugin.gradle

/**
 * Determines where tapik loads compiled API contracts and when generation runs in the Kotlin/JVM lifecycle.
 *
 * A project must select one mode before running `tapikGenerate`. Applying the plugin without running generation does
 * not require a mode.
 */
enum class Mode {
    /**
     * Load API contracts from `tapikContract` dependencies.
     *
     * Generated Kotlin and resources become ordinary main inputs, so generation runs before main compilation and
     * resource processing. Add a contract separately to an ordinary dependency configuration when application source
     * also imports its declarations.
     */
    COMPILED_CONTRACT,

    /**
     * Load API contracts from the current project's compiled main output and runtime classpath.
     *
     * `tapikGenerate` runs after main classes. Generated Kotlin is compiled separately and packaged for downstream
     * consumers, but is not visible to ordinary main source in the same project.
     */
    SAME_PROJECT
}
