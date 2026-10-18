package dev.akif.tapik.plugin.gradle

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import javax.inject.Inject

/** Typed configuration for one Spring RestClient generation execution. */
abstract class SpringRestClientExecution @Inject constructor(name: String) :
    TapikExecution(name, "spring-restclient") {
    /** Package prefix for generated client declarations. */
    @get:Input
    abstract val packageName: Property<String>

    /** Suffix for generated client interface names. */
    @get:Input
    abstract val clientSuffix: Property<String>

    init {
        packageName.convention("dev.akif.tapik.generated")
        clientSuffix.convention("Client")
    }

    /** Sets the generated [packageName]. */
    fun packageName(packageName: String) = this.packageName.set(packageName)

    /** Sets the generated [clientSuffix]. */
    fun clientSuffix(clientSuffix: String) = this.clientSuffix.set(clientSuffix)

    internal override fun validateConfiguration() {
        super.validateConfiguration()
        require(packageName.get().isNotBlank()) { "tapik generated package name must not be blank" }
        require(clientSuffix.get().isNotBlank()) { "tapik client suffix must not be blank" }
    }
}
