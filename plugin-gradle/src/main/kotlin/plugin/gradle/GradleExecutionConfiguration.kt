package dev.akif.tapik.plugin.gradle

import java.util.Locale

internal fun TapikExecution.generationConfiguration(): Map<String, GradleConfigurationValue> =
    when (this) {
        is OpenApiExecution ->
            linkedMapOf<String, GradleConfigurationValue>().apply {
                put("version", version.get().scalar())
                format.orNull?.let { value -> put("format", value.name.lowercase(Locale.ROOT).scalar()) }
                pretty.orNull?.let { value -> put("pretty", value.toString().scalar()) }
                componentNaming.orNull?.let { value ->
                    put("componentNaming", value.name.lowercase(Locale.ROOT).scalar())
                }
                output.orNull?.let { value -> put("output", value.scalar()) }
            }

        is SpringRestClientExecution ->
            linkedMapOf<String, GradleConfigurationValue>().apply {
                packageName.orNull?.let { value -> put("packageName", value.scalar()) }
                clientSuffix.orNull?.let { value -> put("clientSuffix", value.scalar()) }
            }

        is SpringWebMvcExecution ->
            linkedMapOf<String, GradleConfigurationValue>().apply {
                packageName.orNull?.let { value -> put("packageName", value.scalar()) }
                serverSuffix.orNull?.let { value -> put("serverSuffix", value.scalar()) }
                controllerSuffix.orNull?.let { value -> put("controllerSuffix", value.scalar()) }
            }

        is CustomTargetExecution -> configuration.get()
        else -> error("Unsupported tapik Gradle execution type '${javaClass.name}'")
    }

private fun String.scalar(): ScalarGradleConfigurationValue = ScalarGradleConfigurationValue(this)
