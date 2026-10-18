package dev.akif.tapik.plugin.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

/** Aggregates configured tapik generation executions behind one Gradle output owner. */
@CacheableTask
abstract class TapikGenerateTask : DefaultTask() {
    /** Explicit source of compiled API contracts. */
    @get:Optional
    @get:org.gradle.api.tasks.Input
    abstract val mode: Property<Mode>

    /** Generation executions in declaration order. */
    @get:Nested
    abstract val executions: ListProperty<TapikExecution>

    /** Root directory owned by this task. */
    @get:OutputDirectory
    abstract val outputRoot: DirectoryProperty

    /** Validates the complete lazy configuration before generation begins. */
    @TaskAction
    internal fun validateConfiguration() {
        try {
            require(executions.get().isNotEmpty()) { "tapik generation requires at least one execution" }
            require(mode.isPresent) {
                "tapik generation requires mode(Mode.COMPILED_CONTRACT) or mode(Mode.SAME_PROJECT)"
            }
            executions.get().forEach(TapikExecution::validateConfiguration)
        } catch (failure: IllegalArgumentException) {
            throw GradleException(failure.message ?: "Invalid tapik generation configuration", failure)
        }
    }
}
