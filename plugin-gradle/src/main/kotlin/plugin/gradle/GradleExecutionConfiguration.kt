package dev.akif.tapik.plugin.gradle

import java.util.Locale

internal fun TapikExecution.generationConfiguration(): Map<String, GradleConfigurationValue> =
    when (this) {
        is OpenApiExecution ->
            linkedMapOf<String, GradleConfigurationValue>().apply {
                put("version", version.get().scalar())
                put("format", format.get().name.lowercase(Locale.ROOT).scalar())
                put("pretty", pretty.get().toString().scalar())
                put("componentNaming", componentNaming.get().name.lowercase(Locale.ROOT).scalar())
                output.orNull?.let { value -> put("output", value.scalar()) }
            }

        is SpringRestClientExecution ->
            linkedMapOf(
                "packageName" to packageName.get().scalar(),
                "clientSuffix" to clientSuffix.get().scalar()
            )

        is SpringWebMvcExecution ->
            linkedMapOf(
                "packageName" to packageName.get().scalar(),
                "serverSuffix" to serverSuffix.get().scalar(),
                "controllerSuffix" to controllerSuffix.get().scalar()
            )

        is CustomTargetExecution -> configuration.get()
        else -> error("Unsupported tapik Gradle execution type '${javaClass.name}'")
    }

private fun String.scalar(): ScalarGradleConfigurationValue = ScalarGradleConfigurationValue(this)
