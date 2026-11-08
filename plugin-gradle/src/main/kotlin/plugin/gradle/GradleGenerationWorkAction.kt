package dev.akif.tapik.plugin.gradle

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import java.io.Serializable

internal interface GradleGenerationWorkParameters : WorkParameters {
    val contractClasspath: ConfigurableFileCollection
    val mode: Property<Mode>
    val executions: ListProperty<GradleGenerationWorkExecution>
    val resultDirectory: DirectoryProperty
}

internal abstract class GradleGenerationWorkAction : WorkAction<GradleGenerationWorkParameters> {
    final override fun execute() {
        val executions = parameters.executions.get()
        val session =
            try {
                GradleGenerationLoader.open(
                    contractClasspath = parameters.contractClasspath.files.map { file -> file.toPath() },
                    mode = parameters.mode.get(),
                    targetClassLoader = javaClass.classLoader
                )
            } catch (failure: Exception) {
                throw executions.first().failure(failure)
            }
        session.use {
            executions.forEach { execution ->
                try {
                    val result =
                        session.generate(
                            targetId = execution.targetId,
                            configuration = execution.configuration,
                            includeApis = execution.includeApis,
                            excludeApis = execution.excludeApis
                        )
                    val resultFile = parameters.resultDirectory.file(execution.resultFileName).get().asFile.toPath()
                    GradleGenerationResultCodec.write(resultFile, result)
                } catch (failure: Exception) {
                    throw execution.failure(failure)
                }
            }
        }
    }
}

internal data class GradleGenerationWorkExecution(
    val executionName: String,
    val targetId: String,
    val configuration: Map<String, GradleConfigurationValue>,
    val includeApis: Set<String>,
    val excludeApis: Set<String>,
    val resultFileName: String
) : Serializable

private fun GradleGenerationWorkExecution.failure(cause: Exception): IllegalStateException =
    IllegalStateException(
        "tapik execution '$executionName' for target '$targetId' failed",
        cause
    )
