package dev.akif.tapik.plugin.gradle

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import javax.inject.Inject

/** Typed configuration for one Spring WebMVC generation execution. */
abstract class SpringWebMvcExecution @Inject constructor(name: String) :
    TapikExecution(name, "spring-webmvc") {
    /** Package prefix for generated server declarations. */
    @get:Input
    @get:Optional
    abstract val packageName: Property<String>

    /** Suffix for generated server interface names. */
    @get:Input
    @get:Optional
    abstract val serverSuffix: Property<String>

    /** Suffix for generated Spring controller names. */
    @get:Input
    @get:Optional
    abstract val controllerSuffix: Property<String>

    /** Sets the generated [packageName]. */
    fun packageName(packageName: String) = this.packageName.set(packageName)

    /** Sets the generated [serverSuffix]. */
    fun serverSuffix(serverSuffix: String) = this.serverSuffix.set(serverSuffix)

    /** Sets the generated [controllerSuffix]. */
    fun controllerSuffix(controllerSuffix: String) = this.controllerSuffix.set(controllerSuffix)
}
