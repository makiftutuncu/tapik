package dev.akif.tapik.plugin.gradle

import org.gradle.api.Project
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.language.jvm.tasks.ProcessResources
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

internal fun Project.wireSameProjectGeneration(
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
        val generatedSources =
            extension.directoryForMode(Mode.SAME_PROJECT, generation, unusedRoot, "sources")
        val generatedResources =
            extension.outputDirectoryForMode(Mode.SAME_PROJECT, unusedRoot, "resources")
        val kotlin = extensions.getByType(KotlinJvmProjectExtension::class.java)
        val mainCompilation = kotlin.target.compilations.getByName(KotlinCompilation.MAIN_COMPILATION_NAME)
        val generatedModuleName = "${project.name}_tapik_generated"
        val generatedCompilation = kotlin.target.compilations.create("tapikGenerated") { compilation ->
            compilation.defaultSourceSet.kotlin.setSrcDirs(listOf(generatedSources))
        }
        generatedCompilation.compileTaskProvider.configure { task ->
            (task as KotlinJvmCompile).apply {
                libraries.setFrom(main.output.classesDirs, processedResources, runtimeDependencies)
                compilerOptions.moduleName.set(generatedModuleName)
            }
        }

        mainCompilation.compileTaskProvider.configure { task ->
            task.doFirst {
                if (extension.mode.orNull == Mode.SAME_PROJECT) {
                    logger.lifecycle(
                        "tapik same-project generated declarations are unavailable to ordinary main source; " +
                            "move the API contract to a separately compiled project and use Mode.COMPILED_CONTRACT"
                    )
                }
            }
        }
        val generatedClasses = layout.buildDirectory.dir("classes/kotlin/tapikGenerated")
        main.output.dir(generatedClasses)
        main.output.dir(generatedResources)
        val sameProjectGeneratedCompilation =
            extension.mode.map<List<Any>> { selected ->
                if (selected == Mode.SAME_PROJECT) listOf(generatedCompilation.compileTaskProvider) else emptyList()
            }.orElse(emptyList())
        tasks.withType(JavaExec::class.java).configureEach { task ->
            task.dependsOn(sameProjectGeneratedCompilation)
        }
        tasks.withType(Test::class.java).configureEach { task ->
            task.dependsOn(sameProjectGeneratedCompilation)
        }
        tasks.named("jar", Jar::class.java).configure { task ->
            task.dependsOn(sameProjectGeneration, sameProjectGeneratedCompilation)
        }
    }
}
