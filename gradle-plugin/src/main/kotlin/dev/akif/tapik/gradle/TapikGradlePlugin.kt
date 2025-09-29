package dev.akif.tapik.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class TapikGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("tapik", TapikExtension::class.java)

        val generatedSources = target.layout.buildDirectory.dir("generated/sources/tapik/main/kotlin")

        val tapikGenerate = target.tasks.register<TapikGenerateTask>("tapikGenerate") {
            group = "tapik"
            description = "Generates Tapik outputs"
            endpointPackages.set(extension.springRestClient.endpointPackages)
            outputPackage.set(extension.springRestClient.outputPackage)
            outputDirectory.set(target.layout.buildDirectory.dir("generated"))
            sourceDirectory.set(target.layout.projectDirectory.dir("src/main/kotlin"))
            compiledClassesDirectory.set(target.layout.buildDirectory.dir("classes/kotlin/main"))
            generatedSourcesDirectory.set(generatedSources)
        }

        target.plugins.withId("org.jetbrains.kotlin.jvm") {
            target.tasks.named("compileKotlin").configure { finalizedBy(tapikGenerate) }
            tapikGenerate.configure { dependsOn(target.tasks.named("compileKotlin")) }

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
