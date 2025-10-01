package dev.akif.tapik.gradle

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import javax.inject.Inject

open class SpringRestClientExtension @Inject constructor(objects: ObjectFactory) {
    val endpointPackages: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())
    val useContextReceivers: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

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
