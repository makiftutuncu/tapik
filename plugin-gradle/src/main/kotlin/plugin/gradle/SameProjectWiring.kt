package dev.akif.tapik.plugin.gradle

import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.language.jvm.tasks.ProcessResources

internal fun Project.wireSameProjectInputsAndResources(
    extension: TapikExtension,
    generation: TaskProvider<TapikGenerateTask>
) {
    pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
        val main = extensions.getByType(SourceSetContainer::class.java).named("main").get()
        val classes = tasks.named(main.classesTaskName)
        val processedResources =
            tasks.named(main.processResourcesTaskName, ProcessResources::class.java).map { task ->
                task.destinationDir
            }
        val runtimeDependencies = configurations.named(main.runtimeClasspathConfigurationName)
        val sameProjectClasspath =
            objects.fileCollection().from(
                main.output.classesDirs,
                processedResources,
                runtimeDependencies
            )
        val emptyClasspath = files()
        val sameProjectClasses =
            extension.mode.map<List<Any>> { selected ->
                if (selected == Mode.SAME_PROJECT) listOf(classes) else emptyList()
            }.orElse(emptyList())
        val sameProjectGeneration =
            extension.mode.map<List<Any>> { selected ->
                if (selected == Mode.SAME_PROJECT) listOf(generation) else emptyList()
            }.orElse(emptyList())

        generation.configure { task ->
            task.contractClasspath.from(
                extension.classpathForMode(Mode.SAME_PROJECT, sameProjectClasspath, emptyClasspath)
            )
            task.dependsOn(sameProjectClasses)
        }

        val unusedRoot = layout.buildDirectory.dir("tapik/unwired-main")
        val generatedResources =
            extension.outputDirectoryForMode(Mode.SAME_PROJECT, unusedRoot, "resources")
        main.output.dir(generatedResources)
        tasks.named("jar", Jar::class.java).configure { task ->
            task.dependsOn(sameProjectGeneration)
        }
    }
}
