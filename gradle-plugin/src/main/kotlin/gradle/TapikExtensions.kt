package dev.akif.tapik.plugin.gradle

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import javax.inject.Inject

/**
 * Base type tracking whether a generator has been configured via the Gradle DSL.
 */
abstract class TapikGeneratorExtension {
    private var configured: Boolean = false

    internal fun markConfigured() {
        configured = true
    }

    internal fun isConfigured(): Boolean = configured
}

/** Configures Spring RestClient code generation for Tapik endpoints. */
open class SpringRestClientExtension @Inject constructor() : TapikGeneratorExtension()

/** Configures Spring WebMVC controller generation for Tapik endpoints. */
open class SpringWebMvcExtension @Inject constructor() : TapikGeneratorExtension()

/** Configures Markdown documentation generation for Tapik endpoints. */
open class MarkdownDocumentationExtension @Inject constructor() : TapikGeneratorExtension()

/**
 * Root Gradle extension exposing Tapik-specific configuration surfaces.
 *
 * @property endpointPackages packages whose compiled classes will be inspected for Tapik endpoint declarations.
 * @property springRestClient nested DSL configuring the RestClient generator.
 * @property springWebMvc nested DSL configuring the WebMVC generator.
 * @property documentation nested DSL configuring Markdown documentation generation.
 */
open class TapikExtension @Inject constructor(objects: ObjectFactory) {
    /** Packages whose compiled classes will be inspected for Tapik endpoint declarations. */
    val endpointPackages: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())

    /** Nested extension configuring Spring RestClient code generation. */
    val springRestClient: SpringRestClientExtension = objects.newInstance(SpringRestClientExtension::class.java)

    /** Nested extension configuring Spring WebMVC controller generation. */
    val springWebMvc: SpringWebMvcExtension = objects.newInstance(SpringWebMvcExtension::class.java)

    /** Nested extension configuring Markdown documentation generation. */
    val markdownDocumentation: MarkdownDocumentationExtension =
        objects.newInstance(MarkdownDocumentationExtension::class.java)

    /**
     * Replaces the set of endpoint packages that will be scanned.
     *
     * @param packages fully-qualified package names containing Tapik endpoints.
     */
    fun endpointPackages(vararg packages: String) {
        endpointPackages.set(packages.toList())
    }

    /**
     * Replaces the set of endpoint packages that will be scanned.
     *
     * @param packages fully-qualified package names containing Tapik endpoints.
     */
    fun endpointPackages(packages: Iterable<String>) {
        endpointPackages.set(packages.toList())
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
     * @return `Unit`.
     */
    fun markdownDocumentation(configure: Action<in MarkdownDocumentationExtension>) {
        markdownDocumentation.markConfigured()
        configure.execute(markdownDocumentation)
    }

    /**
     * Resolves the set of generator identifiers that should run.
     *
     * @return identifiers matching SPI implementations present on the classpath.
     */
    internal fun configuredGeneratorIds(): Set<String> = buildSet {
        if (springRestClient.isConfigured()) add("spring-restclient")
        if (springWebMvc.isConfigured()) add("spring-webmvc")
        if (markdownDocumentation.isConfigured()) add("markdown-docs")
    }

    /**
     * Returns the distinct set of endpoint packages configured at the top level.
     */
    internal fun resolvedEndpointPackages(): List<String> =
        endpointPackages.orNull.orEmpty().map(String::trim).filter { it.isNotEmpty() }.distinct()
}
