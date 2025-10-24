package dev.akif.tapik.plugin.gradle

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import javax.inject.Inject

/**
 * Gradle extension configuring Spring RestClient code generation for Tapik endpoints.
 *
 * @property endpointPackages packages whose compiled classes should be scanned for endpoint definitions.
 */
open class SpringRestClientExtension @Inject constructor(objects: ObjectFactory) {
    val endpointPackages: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())

    /**
     * Replaces the set of packages that will be scanned for Tapik endpoints.
     *
     * @param packages packages containing `HttpEndpoint` declarations.
     */
    fun endpointPackages(vararg packages: String) {
        this.endpointPackages.set(packages.toList())
    }
}

/**
 * Root Gradle extension exposing Tapik-specific configuration surfaces.
 *
 * @property springRestClient nested extension that configures Spring RestClient code generation.
 */
open class TapikExtension @Inject constructor(objects: ObjectFactory) {
    val springRestClient: SpringRestClientExtension = objects.newInstance(SpringRestClientExtension::class.java)

    /**
     * Configures the [springRestClient] extension using a Gradle [Action].
     *
     * @param configure action applied to the nested [SpringRestClientExtension].
     */
    fun springRestClient(configure: Action<in SpringRestClientExtension>) {
        configure.execute(springRestClient)
    }
}
