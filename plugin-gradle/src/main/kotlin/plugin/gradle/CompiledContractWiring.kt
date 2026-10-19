package dev.akif.tapik.plugin.gradle

import org.gradle.api.Project
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

internal fun Project.wireCompiledContractOutputs(
    extension: TapikExtension,
    generation: TaskProvider<TapikGenerateTask>
) {
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        val unusedRoot = layout.buildDirectory.dir("tapik/unwired-main")
        val generatedSources =
            extension.directoryForMode(Mode.COMPILED_CONTRACT, generation, unusedRoot, "sources")
        val generatedResources =
            extension.directoryForMode(Mode.COMPILED_CONTRACT, generation, unusedRoot, "resources")

        extensions.getByType(KotlinJvmProjectExtension::class.java).sourceSets.named("main") { sourceSet ->
            sourceSet.kotlin.srcDir(generatedSources)
        }
        extensions.getByType(SourceSetContainer::class.java).named("main") { sourceSet ->
            sourceSet.resources.srcDir(generatedResources)
        }
    }
}
