package dev.akif.tapik.plugin.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.config.kotlinSourceRoots
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import java.nio.file.Path

/** Registers tapik's JVM API-registry generation with the Kotlin compiler. */
@OptIn(ExperimentalCompilerApi::class)
class TapikCompilerPluginRegistrar : CompilerPluginRegistrar() {
    override val pluginId: String = "dev.akif.tapik"

    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val messages = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        val outputDirectory = configuration[JVMConfigurationKeys.OUTPUT_DIRECTORY] ?: return
        val configuredSourceRoots = configuration.getList(SOURCE_ROOTS).map(Path::of)
        IrGenerationExtension.registerExtension(
            ApiRegistryGenerationExtension(
                outputDirectory = outputDirectory.toPath(),
                messages = messages,
                incremental =
                    configuration[INCREMENTAL_REGISTRY]
                        ?: configuration.getBoolean(CommonConfigurationKeys.INCREMENTAL_COMPILATION),
                registryIndex = configuration[REGISTRY_INDEX]?.let(Path::of),
                sourceRoots =
                    configuredSourceRoots.ifEmpty {
                        configuration.kotlinSourceRoots.map { sourceRoot -> Path.of(sourceRoot.path) }
                    }
            )
        )
    }
}
