package dev.akif.tapik.plugin.gradle

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import javax.inject.Inject

/** Typed configuration for one Spring RestClient generation execution. */
abstract class SpringRestClientExecution @Inject constructor(name: String) :
    TapikExecution(name, "spring-restclient") {
    /** Package prefix for generated client declarations. */
    @get:Input
    @get:Optional
    abstract val packageName: Property<String>

    /** Suffix for generated client interface names. */
    @get:Input
    @get:Optional
    abstract val clientSuffix: Property<String>

    /** Sets the generated [packageName]. */
    fun packageName(packageName: String) = this.packageName.set(packageName)

    /** Sets the generated [clientSuffix]. */
    fun clientSuffix(clientSuffix: String) = this.clientSuffix.set(clientSuffix)
}
