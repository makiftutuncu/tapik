package dev.akif.tapik.plugin.gradle

import org.gradle.api.Project
import org.gradle.api.attributes.Usage
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

/** Entry point for tapik's Gradle integration. */
class TapikGradlePlugin : KotlinCompilerPluginSupportPlugin {
    /** Applies tapik to [target]. */
    override fun apply(target: Project) {
        val contract = target.declarableConfiguration("tapikContract")
        val contractClasspath = target.runtimeClasspathConfiguration("tapikContractClasspath", contract)
        val generationTargets = target.declarableConfiguration("tapikTarget")
        val targetClasspath = target.runtimeClasspathConfiguration("tapikTargetClasspath", generationTargets)
        listOf("openapi", "spring-restclient", "spring-webmvc").forEach { targetName ->
            generationTargets.dependencies.add(
                target.dependencies.create("dev.akif:tapik-target-$targetName:$tapikVersion")
            )
        }

        val extension =
            target.extensions.create(
                "tapik",
                TapikExtension::class.java,
                target.objects
            )
        extension.outputRoot.convention(target.layout.buildDirectory.dir("generated/tapik"))
        val emptyClasspath = target.files()
        val generation =
            target.tasks.register("tapikGenerate", TapikGenerateTask::class.java) { task ->
                task.group = "tapik"
                task.description = "Generates artifacts from tapik API contracts"
                task.mode.set(extension.mode)
                task.pluginVersion.set(tapikVersion)
                task.executions.set(extension.orderedExecutions)
                task.contractClasspath.from(
                    extension.classpathForMode(Mode.COMPILED_CONTRACT, contractClasspath, emptyClasspath)
                )
                task.targetClasspath.from(targetClasspath)
                task.outputRoot.set(extension.outputRoot)
                task.projectDirectory.set(target.layout.projectDirectory)
            }
        target.wireCompiledContractOutputs(extension, generation)
        target.wireSameProjectInputsAndResources(extension, generation)
    }

    /** Returns whether the tapik compiler plugin supports [kotlinCompilation]. */
    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean =
        kotlinCompilation.platformType == KotlinPlatformType.jvm

    /** Attaches tapik and its host-managed registry synchronization to [kotlinCompilation]. */
    override fun applyToCompilation(
        kotlinCompilation: KotlinCompilation<*>
    ): Provider<List<SubpluginOption>> {
        val project = kotlinCompilation.target.project
        val registryIndex =
            project.layout.projectDirectory.file(
                ".gradle/tapik/compiler/${kotlinCompilation.name}/registry.index"
            )
        return project.provider {
            buildList {
                add(SubpluginOption(key = "incrementalRegistry", value = "true"))
                add(SubpluginOption(key = "registryIndex", value = registryIndex.asFile.absolutePath))
                kotlinCompilation.allKotlinSourceSets
                    .flatMap { sourceSet -> sourceSet.kotlin.srcDirs }
                    .map { directory -> directory.absoluteFile.normalize() }
                    .distinct()
                    .forEach { directory ->
                        add(SubpluginOption(key = "sourceRoot", value = directory.path))
                    }
            }
        }
    }

    /** Kotlin compiler plugin ID used by tapik. */
    override fun getCompilerPluginId(): String = "dev.akif.tapik"

    /** Compiler plugin artifact matching this Gradle plugin's version. */
    override fun getPluginArtifact(): SubpluginArtifact =
        SubpluginArtifact(
            groupId = "dev.akif",
            artifactId = "tapik-plugin-compiler",
            version = tapikVersion
        )
}

private fun Project.declarableConfiguration(name: String): Configuration =
    configurations.create(name) { configuration ->
        configuration.isCanBeDeclared = true
        configuration.isCanBeResolved = false
        configuration.isCanBeConsumed = false
        configuration.description = "Dependencies declared for tapik ${name.removePrefix("tapik").lowercase()}"
    }

private fun Project.runtimeClasspathConfiguration(name: String, dependencies: Configuration): Configuration =
    configurations.create(name) { configuration ->
        configuration.isCanBeDeclared = false
        configuration.isCanBeResolved = true
        configuration.isCanBeConsumed = false
        configuration.extendsFrom(dependencies)
        configuration.attributes.attribute(
            Usage.USAGE_ATTRIBUTE,
            objects.named(Usage::class.java, Usage.JAVA_RUNTIME)
        )
    }
