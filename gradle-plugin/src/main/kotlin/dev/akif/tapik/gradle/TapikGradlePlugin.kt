package dev.akif.tapik.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class TapikGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension = target.extensions.create("tapik", TapikExtension::class.java)

        // Cacheable info task
        val tapikInfo = target.tasks.register<TapikInfoTask>("tapikInfo") {
            group = "tapik"
            description = "Prints Tapik plugin configuration"
            endpointPackages.set(extension.springRestClient.endpointPackages)
            outputPackage.set(extension.springRestClient.outputPackage)
        }

        // Cacheable generate task
        val tapikGenerate = target.tasks.register<TapikGenerateTask>("tapikGenerate") {
            group = "tapik"
            description = "Generates code using Tapik (spring-restclient)"
            endpointPackages.set(extension.springRestClient.endpointPackages)
            outputPackage.set(extension.springRestClient.outputPackage)
            outputDirectory.set(target.layout.buildDirectory.dir("tapik/generated"))
            sourceDirectory.set(target.layout.projectDirectory.dir("src/main/kotlin"))
            compiledClassesDirectory.set(target.layout.buildDirectory.dir("classes/kotlin/main"))
        }

        // Ensure generation runs after Kotlin compilation so we can analyze compiled bytecode
        target.plugins.withId("org.jetbrains.kotlin.jvm") {
            target.tasks.named("compileKotlin").configure { finalizedBy(tapikGenerate) }
        }
    }
}
