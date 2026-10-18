package dev.akif.tapik.plugin.gradle

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import javax.inject.Inject

/** Type-safe, lazy configuration for tapik generation in a Gradle project. */
abstract class TapikExtension @Inject constructor(private val objects: ObjectFactory) {
    /** Root directory beneath which tapik materializes generated artifacts. */
    abstract val outputRoot: DirectoryProperty

    /** Explicit source of compiled API contracts. */
    abstract val mode: Property<Mode>

    private val executions: NamedDomainObjectContainer<TapikExecution> =
        objects.domainObjectContainer(TapikExecution::class.java) { name ->
            error("Use a typed tapik target method to create execution '$name'")
        }

    internal val orderedExecutions: ListProperty<TapikExecution> =
        objects.listProperty(TapikExecution::class.java).convention(emptyList())

    /** Selects the explicit [mode] used to load compiled API contracts. */
    fun mode(mode: Mode) = selectMode(mode)

    /** Sets the generated artifact root to [directory]. */
    fun outputRoot(directory: Directory) = outputRoot.set(directory)

    /** Sets the generated artifact root from a lazy [directory] provider. */
    fun outputRoot(directory: Provider<Directory>) = outputRoot.set(directory)

    /** Registers the default OpenAPI execution. */
    fun openApi(action: Action<in OpenApiExecution>) = openApi("openapi", action)

    /** Registers an OpenAPI execution named [name]. */
    fun openApi(name: String, action: Action<in OpenApiExecution>) {
        register(name, objects.newInstance(OpenApiExecution::class.java, name), action)
    }

    /** Registers the default Spring RestClient execution. */
    fun springRestClient(action: Action<in SpringRestClientExecution>) =
        springRestClient("spring-restclient", action)

    /** Registers a Spring RestClient execution named [name]. */
    fun springRestClient(name: String, action: Action<in SpringRestClientExecution>) {
        register(name, objects.newInstance(SpringRestClientExecution::class.java, name), action)
    }

    /** Registers the default Spring WebMVC execution. */
    fun springWebMvc(action: Action<in SpringWebMvcExecution>) = springWebMvc("spring-webmvc", action)

    /** Registers a Spring WebMVC execution named [name]. */
    fun springWebMvc(name: String, action: Action<in SpringWebMvcExecution>) {
        register(name, objects.newInstance(SpringWebMvcExecution::class.java, name), action)
    }

    /** Registers a custom service-loaded [targetId] under the execution [name]. */
    fun target(name: String, targetId: String, action: Action<in CustomTargetExecution>) {
        require(targetId.isNotBlank()) { "tapik target IDs must not be blank" }
        register(name, objects.newInstance(CustomTargetExecution::class.java, name, targetId), action)
    }

    private fun selectMode(requested: Mode) {
        val selected = mode.orNull
        require(selected == null || selected == requested) {
            "tapik generation mode is already $selected and cannot be changed to $requested"
        }
        mode.set(requested)
    }

    private fun <Execution : TapikExecution> register(
        name: String,
        execution: Execution,
        action: Action<in Execution>
    ) {
        require(name.isNotBlank()) { "tapik execution names must not be blank" }
        require(executions.findByName(name) == null) { "tapik execution name '$name' is duplicated" }
        action.execute(execution)
        executions.add(execution)
        orderedExecutions.add(execution)
    }
}
