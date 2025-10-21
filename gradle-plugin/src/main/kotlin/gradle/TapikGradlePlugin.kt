package dev.akif.tapik.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

/**
 * Registers Tapik-specific Gradle tasks and DSL extensions for client generation.
 */
class TapikGradlePlugin : Plugin<Project> {
    /**
     * Installs the Tapik Gradle extension and wires the generation task into the project.
     *
     * @param target Gradle project receiving the plugin.
     */
    override fun apply(target: Project) {
        val extension = target.extensions.create("tapik", TapikExtension::class.java)

        val generatedSources = target.layout.buildDirectory.dir("generated/sources/tapik/main/kotlin")

        val tapikGenerate = target.tasks.register<TapikGenerateTask>("tapikGenerate") {
            group = "tapik"
            description = "Generates Tapik outputs"
            endpointPackages.set(extension.springRestClient.endpointPackages)
            outputDirectory.set(target.layout.buildDirectory.dir("generated"))
            sourceDirectory.set(target.layout.projectDirectory.dir("src/main/kotlin"))
            compiledClassesDirectory.set(target.layout.buildDirectory.dir("classes/kotlin/main"))
            generatedSourcesDirectory.set(generatedSources)
            additionalClassDirectories.set(collectClassDirectories(target))
            runtimeClasspath.from(target.configurations.getByName("runtimeClasspath"))
        }

        tapikGenerate.configure {
            val upstreamClasses = target.rootProject.subprojects
                .filter { it != target }
                .mapNotNull { it.tasks.findByName("classes") }
            dependsOn(upstreamClasses)
            dependsOn(target.tasks.named("classes"))
        }

        target.plugins.withId("org.jetbrains.kotlin.jvm") {
            target.extensions.findByName("kotlin")?.let { kotlinExtension ->
                val sourceSets = kotlinExtension::class.java.getMethod("getSourceSets").invoke(kotlinExtension)
                val getByName = sourceSets::class.java.getMethod("getByName", String::class.java)
                val mainSourceSet = getByName.invoke(sourceSets, "main")
                val kotlin = mainSourceSet::class.java.methods.first { it.name == "getKotlin" }.invoke(mainSourceSet) as org.gradle.api.file.SourceDirectorySet
                kotlin.srcDir(generatedSources)
            }
        }
    }
}

/**
 * Collects class directories from the current project and its siblings so endpoint scanning can
 * resolve cross-project dependencies.
 *
 * @param project project requesting classpath augmentation.
 * @return distinct list of absolute class directory paths.
 */
private fun collectClassDirectories(project: Project): List<String> = buildList {
    project.rootProject.allprojects.forEach { subproject ->
        add(subproject.layout.buildDirectory.dir("classes/kotlin/main").get().asFile.absolutePath)
        add(subproject.layout.buildDirectory.dir("classes/java/main").get().asFile.absolutePath)
    }
}.distinct()
