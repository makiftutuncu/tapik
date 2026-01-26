package dev.akif.tapik.plugin.gradle

import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
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
 * @property basePackage single base package whose compiled classes will be inspected for Tapik API implementations.
 * @property springRestClient nested DSL configuring the RestClient generator.
 * @property springWebMvc nested DSL configuring the WebMVC generator.
 * @property markdownDocumentation nested DSL configuring Markdown documentation generation.
 */
open class TapikExtension @Inject constructor(objects: ObjectFactory) {
    /** Base package whose compiled classes will be inspected for Tapik endpoint declarations. */
    val basePackage: Property<String> = objects.property(String::class.java)

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

    /** Returns the configured base package, trimmed and defaulted to empty string when unset. */
    internal fun resolvedBasePackage(): String =
        basePackage.orNull?.trim().orEmpty()
}
