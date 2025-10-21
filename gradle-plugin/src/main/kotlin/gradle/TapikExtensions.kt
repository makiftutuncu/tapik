package dev.akif.tapik.gradle

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import javax.inject.Inject

open class SpringRestClientExtension @Inject constructor(objects: ObjectFactory) {
    val endpointPackages: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())

    fun endpointPackages(vararg packages: String) {
        this.endpointPackages.set(packages.toList())
    }
}

open class TapikExtension @Inject constructor(private val objects: ObjectFactory) {
    val springRestClient: SpringRestClientExtension = objects.newInstance(SpringRestClientExtension::class.java)

    fun springRestClient(configure: Action<in SpringRestClientExtension>) {
        configure.execute(springRestClient)
    }
}
