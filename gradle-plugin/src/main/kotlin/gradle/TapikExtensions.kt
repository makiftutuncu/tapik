package dev.akif.tapik.plugin.gradle

import dev.akif.tapik.plugin.GeneratorConfiguration
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * Base type tracking whether a generator has been configured via the Gradle DSL.
 */
abstract class TapikGeneratorExtension @Inject constructor() {
    private var configured: Boolean = false

    internal fun markConfigured() {
        configured = true
    }

    internal fun isConfigured(): Boolean = configured
}

/** Configures Spring RestClient code generation for Tapik endpoints. */
open class SpringRestClientExtension @Inject constructor(
    objects: ObjectFactory
) : TapikGeneratorExtension() {
    /** Suffix appended to aggregate and nested client interfaces. */
    val clientSuffix: Property<String> = objects.property(String::class.java).convention("Client")

    /**
     * Sets the suffix appended to aggregate and nested client interfaces.
     *
     * @param suffix client suffix.
     */
    fun clientSuffix(suffix: String) {
        clientSuffix.set(suffix)
    }
}

/** Configures Spring WebMVC controller generation for Tapik endpoints. */
open class SpringWebMvcExtension @Inject constructor(
    objects: ObjectFactory
) : TapikGeneratorExtension() {
    /** Suffix appended to aggregate and nested server interfaces. */
    val serverSuffix: Property<String> = objects.property(String::class.java).convention("Server")

    /**
     * Sets the suffix appended to aggregate and nested server interfaces.
     *
     * @param suffix server suffix.
     */
    fun serverSuffix(suffix: String) {
        serverSuffix.set(suffix)
    }
}

/** Configures Markdown documentation generation for Tapik endpoints. */
open class MarkdownDocumentationExtension @Inject constructor(
    objects: ObjectFactory
) : TapikGeneratorExtension()

/**
 * Root Gradle extension exposing Tapik-specific configuration surfaces.
 *
 * @property basePackage single base package whose compiled classes will be inspected for Tapik API implementations.
 * @property generatedPackageName package segment appended to source packages for generated Kotlin sources.
 * @property endpointsSuffix suffix appended to the source-level enclosing endpoints interface.
 * @property springRestClient nested DSL configuring the RestClient generator.
 * @property springWebMvc nested DSL configuring the WebMVC generator.
 * @property markdownDocumentation nested DSL configuring Markdown documentation generation.
 */
open class TapikExtension @Inject constructor(objects: ObjectFactory) {
    /** Base package whose compiled classes will be inspected for Tapik endpoint declarations. */
    val basePackage: Property<String> = objects.property(String::class.java)

    /** Package segment appended to source packages for generated Kotlin sources. */
    val generatedPackageName: Property<String> = objects.property(String::class.java).convention("generated")

    /** Suffix appended to the source-level enclosing endpoints interface. */
    val endpointsSuffix: Property<String> = objects.property(String::class.java).convention("Endpoints")

    /** Nested extension configuring Spring RestClient code generation. */
    val springRestClient: SpringRestClientExtension = objects.newInstance(SpringRestClientExtension::class.java)

    /** Nested extension configuring Spring WebMVC controller generation. */
    val springWebMvc: SpringWebMvcExtension = objects.newInstance(SpringWebMvcExtension::class.java)

    /** Nested extension configuring Markdown documentation generation. */
    val markdownDocumentation: MarkdownDocumentationExtension =
        objects.newInstance(MarkdownDocumentationExtension::class.java)

    /**
     * Sets the base package that will be scanned for Tapik endpoints.
     *
     * @param pkg fully-qualified base package containing Tapik API implementations.
     */
    fun basePackage(pkg: String) {
        basePackage.set(pkg)
    }

    /**
     * Sets the package segment appended to source packages for generated Kotlin sources.
     *
     * @param pkg generated package segment.
     */
    fun generatedPackageName(pkg: String) {
        generatedPackageName.set(pkg)
    }

    /**
     * Sets the suffix appended to the source-level enclosing endpoints interface.
     *
     * @param suffix endpoints container suffix.
     */
    fun endpointsSuffix(suffix: String) {
        endpointsSuffix.set(suffix)
    }

    /**
     * Configures the [springRestClient] extension using a Gradle [Action].
     */
    fun springRestClient(configure: Action<in SpringRestClientExtension>) {
        springRestClient.markConfigured()
        configure.execute(springRestClient)
    }

    /**
     * Configures the [springWebMvc] extension using a Gradle [Action].
     */
    fun springWebMvc(configure: Action<in SpringWebMvcExtension>) {
        springWebMvc.markConfigured()
        configure.execute(springWebMvc)
    }

    /**
     * Configures the [MarkdownDocumentationExtension] responsible for Markdown output generation.
     *
     * @param configure action applied to the nested [MarkdownDocumentationExtension].
     */
    fun markdownDocumentation(configure: Action<in MarkdownDocumentationExtension>) {
        markdownDocumentation.markConfigured()
        configure.execute(markdownDocumentation)
    }

    /** Returns the configured base package, trimmed and defaulted to empty string when unset. */
    internal fun resolvedBasePackage(): String =
        basePackage.orNull?.trim().orEmpty()

    /**
     * Returns generator-specific configurations for all enabled generators.
     */
    internal fun resolvedGeneratorConfigurations(): Map<String, GeneratorConfiguration> =
        buildMap {
            if (springRestClient.isConfigured()) {
                put(
                    "spring-restclient",
                    GeneratorConfiguration(
                        clientSuffix = springRestClient.clientSuffix.get(),
                        serverSuffix = "Server"
                    )
                )
            }
            if (springWebMvc.isConfigured()) {
                put(
                    "spring-webmvc",
                    GeneratorConfiguration(
                        clientSuffix = "Client",
                        serverSuffix = springWebMvc.serverSuffix.get()
                    )
                )
            }
            if (markdownDocumentation.isConfigured()) {
                put(
                    "markdown-docs",
                    GeneratorConfiguration()
                )
            }
        }
}
