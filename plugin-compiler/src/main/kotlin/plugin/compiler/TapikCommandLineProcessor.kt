package dev.akif.tapik.plugin.compiler

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

@OptIn(ExperimentalCompilerApi::class)
internal class TapikCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String = "dev.akif.tapik"

    override val pluginOptions: Collection<CliOption> =
        listOf(INCREMENTAL_REGISTRY_OPTION, REGISTRY_INDEX_OPTION, SOURCE_ROOT_OPTION)

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration
    ) {
        when (option.optionName) {
            INCREMENTAL_REGISTRY_OPTION.optionName ->
                configuration.put(INCREMENTAL_REGISTRY, value.toBooleanStrict())
            REGISTRY_INDEX_OPTION.optionName -> configuration.put(REGISTRY_INDEX, value)
            SOURCE_ROOT_OPTION.optionName -> configuration.add(SOURCE_ROOTS, value)
            else -> error("Unknown tapik compiler option '${option.optionName}'")
        }
    }
}

internal val INCREMENTAL_REGISTRY: CompilerConfigurationKey<Boolean> =
    CompilerConfigurationKey.create("retain unchanged tapik API registry entries")

internal val REGISTRY_INDEX: CompilerConfigurationKey<String> =
    CompilerConfigurationKey.create("tapik API registry index path")

internal val SOURCE_ROOTS: CompilerConfigurationKey<List<String>> =
    CompilerConfigurationKey.create("tapik Kotlin source roots")

private val INCREMENTAL_REGISTRY_OPTION =
    CliOption(
        optionName = "incrementalRegistry",
        valueDescription = "<true|false>",
        description = "Whether tapik retains registry entries from unchanged active sources",
        required = false,
        allowMultipleOccurrences = false
    )

private val REGISTRY_INDEX_OPTION =
    CliOption(
        optionName = "registryIndex",
        valueDescription = "<path>",
        description = "Persistent tapik API registry index",
        required = false,
        allowMultipleOccurrences = false
    )

private val SOURCE_ROOT_OPTION =
    CliOption(
        optionName = "sourceRoot",
        valueDescription = "<path>",
        description = "Declared Kotlin source root inspected by tapik",
        required = false,
        allowMultipleOccurrences = true
    )
