package dev.akif.tapik.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class TapikInfoTask : DefaultTask() {
    @get:Input
    abstract val endpointPackages: ListProperty<String>

    @get:Input
    abstract val outputPackage: Property<String>

    @TaskAction
    fun printInfo() {
        val apis = endpointPackages.orNull ?: emptyList()
        val pkg = outputPackage.orNull ?: ""
        logger.lifecycle("tapik.springRestClient.endpointPackages=$apis")
        logger.lifecycle("tapik.springRestClient.outputPackage=$pkg")
    }
}
