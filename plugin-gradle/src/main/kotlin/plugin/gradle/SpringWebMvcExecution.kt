package dev.akif.tapik.plugin.gradle

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import javax.inject.Inject

/** Typed configuration for one Spring WebMVC generation execution. */
abstract class SpringWebMvcExecution @Inject constructor(name: String) :
    TapikExecution(name, "spring-webmvc") {
    /** Package prefix for generated server declarations. */
    @get:Input
    abstract val packageName: Property<String>

    /** Suffix for generated server interface names. */
    @get:Input
    abstract val serverSuffix: Property<String>

    /** Suffix for generated Spring controller names. */
    @get:Input
    abstract val controllerSuffix: Property<String>

    init {
        packageName.convention("dev.akif.tapik.generated")
        serverSuffix.convention("Server")
        controllerSuffix.convention("GeneratedController")
    }

    /** Sets the generated [packageName]. */
    fun packageName(packageName: String) = this.packageName.set(packageName)

    /** Sets the generated [serverSuffix]. */
    fun serverSuffix(serverSuffix: String) = this.serverSuffix.set(serverSuffix)

    /** Sets the generated [controllerSuffix]. */
    fun controllerSuffix(controllerSuffix: String) = this.controllerSuffix.set(controllerSuffix)

    internal override fun validateConfiguration() {
        super.validateConfiguration()
        require(packageName.get().isNotBlank()) { "tapik generated package name must not be blank" }
        require(serverSuffix.get().isNotBlank()) { "tapik server suffix must not be blank" }
        require(controllerSuffix.get().isNotBlank()) { "tapik controller suffix must not be blank" }
    }
}
