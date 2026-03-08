package dev.akif.tapik.plugin.gradle

import dev.akif.tapik.plugin.GeneratorConfiguration
import org.gradle.api.Action
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject

/**
 * Base type tracking whether a generator has been configured via the Gradle DSL.
 */
abstract class TapikGeneratorExtension @Inject constructor(objects: ObjectFactory) {
    private var configured: Boolean = false

    /**
     * Generator-specific override for import optimization.
     *
     * When unset, root-level [TapikExtension.optimizeImports] value is used.
     */
    val optimizeImports: Property<Boolean> = objects.property(Boolean::class.java)

    /** Prefix used for generated type names for this generator. */
    val namePrefix: Property<String> = objects.property(String::class.java).convention("")

    /** Suffix used for generated type names for this generator. */
    val nameSuffix: Property<String> = objects.property(String::class.java).convention("")

    internal fun markConfigured() {
        configured = true
    }

    internal fun isConfigured(): Boolean = configured

    /**
     * Enables/disables import optimization for this generator specifically.
     *
     * @param enabled `true` to optimize imports, `false` to keep fully-qualified references.
     */
    fun optimizeImports(enabled: Boolean) {
        optimizeImports.set(enabled)
    }

    /**
     * Sets a prefix for generated type names for this generator.
     *
     * @param prefix text prepended to generated type names.
     */
    fun namePrefix(prefix: String) {
        namePrefix.set(prefix)
    }

    /**
     * Sets a suffix for generated type names for this generator.
     *
     * @param suffix text appended to generated type names.
     */
    fun nameSuffix(suffix: String) {
        nameSuffix.set(suffix)
    }
}

/** Configures Spring RestClient code generation for Tapik endpoints. */
open class SpringRestClientExtension @Inject constructor(
    objects: ObjectFactory
) : TapikGeneratorExtension(objects) {
    init {
        nameSuffix.convention("Client")
    }
}

/** Configures Spring WebMVC controller generation for Tapik endpoints. */
open class SpringWebMvcExtension @Inject constructor(
    objects: ObjectFactory
) : TapikGeneratorExtension(objects) {
    init {
        nameSuffix.convention("Controller")
    }
}

/** Configures Markdown documentation generation for Tapik endpoints. */
open class MarkdownDocumentationExtension @Inject constructor(
    objects: ObjectFactory
) : TapikGeneratorExtension(objects)

/**
 * Root Gradle extension exposing Tapik-specific configuration surfaces.
 *
 * @property basePackage single base package whose compiled classes will be inspected for Tapik API implementations.
 * @property springRestClient nested DSL configuring the RestClient generator.
 * @property springWebMvc nested DSL configuring the WebMVC generator.
 * @property markdownDocumentation nested DSL configuring Markdown documentation generation.
 * @property optimizeImports default import optimization toggle for configured generators.
 */
open class TapikExtension @Inject constructor(objects: ObjectFactory) {
    /** Base package whose compiled classes will be inspected for Tapik endpoint declarations. */
    val basePackage: Property<String> = objects.property(String::class.java)

    /** Whether generated Kotlin sources should be post-processed to optimize imports. */
    val optimizeImports: Property<Boolean> = objects.property(Boolean::class.java).convention(true)

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
     * Enables/disables post-processing that optimizes imports in generated Kotlin sources.
     *
     * @param enabled `true` to optimize imports, `false` to keep fully-qualified references untouched.
     */
    fun optimizeImports(enabled: Boolean) {
        optimizeImports.set(enabled)
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

    /** Returns the configured base package, trimmed and defaulted to empty string when unset. */
    internal fun resolvedBasePackage(): String =
        basePackage.orNull?.trim().orEmpty()

    /** Returns the resolved root-level import optimization flag; defaults to `true`. */
    internal fun resolvedOptimizeImports(): Boolean = optimizeImports.orNull ?: true

    /**
     * Returns generator-specific configurations for all enabled generators.
     */
    internal fun resolvedGeneratorConfigurations(): Map<String, GeneratorConfiguration> =
        buildMap {
            if (springRestClient.isConfigured()) {
                put(
                    "spring-restclient",
                    GeneratorConfiguration(
                        optimizeImports = springRestClient.optimizeImports.orNull ?: resolvedOptimizeImports(),
                        namePrefix = springRestClient.namePrefix.orNull,
                        nameSuffix = springRestClient.nameSuffix.orNull
                    )
                )
            }
            if (springWebMvc.isConfigured()) {
                put(
                    "spring-webmvc",
                    GeneratorConfiguration(
                        optimizeImports = springWebMvc.optimizeImports.orNull ?: resolvedOptimizeImports(),
                        namePrefix = springWebMvc.namePrefix.orNull,
                        nameSuffix = springWebMvc.nameSuffix.orNull
                    )
                )
            }
            if (markdownDocumentation.isConfigured()) {
                put(
                    "markdown-docs",
                    GeneratorConfiguration(
                        optimizeImports = markdownDocumentation.optimizeImports.orNull ?: resolvedOptimizeImports(),
                        namePrefix = markdownDocumentation.namePrefix.orNull,
                        nameSuffix = markdownDocumentation.nameSuffix.orNull
                    )
                )
            }
        }
}
