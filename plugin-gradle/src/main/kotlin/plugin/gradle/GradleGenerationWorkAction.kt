package dev.akif.tapik.plugin.gradle

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters

internal interface GradleGenerationWorkParameters : WorkParameters {
    val contractClasspath: ConfigurableFileCollection
    val executionName: Property<String>
    val targetId: Property<String>
    val configuration: MapProperty<String, GradleConfigurationValue>
    val includeApis: SetProperty<String>
    val excludeApis: SetProperty<String>
    val resultFile: RegularFileProperty
}

internal abstract class GradleGenerationWorkAction : WorkAction<GradleGenerationWorkParameters> {
    final override fun execute() {
        try {
            val result =
                GradleGenerationLoader.generate(
                    contractClasspath = parameters.contractClasspath.files.map { file -> file.toPath() },
                    targetId = parameters.targetId.get(),
                    configuration = parameters.configuration.get(),
                    includeApis = parameters.includeApis.get(),
                    excludeApis = parameters.excludeApis.get(),
                    targetClassLoader = javaClass.classLoader
                )
            GradleGenerationResultCodec.write(parameters.resultFile.get().asFile.toPath(), result)
        } catch (failure: Exception) {
            throw IllegalStateException(
                "tapik execution '${parameters.executionName.get()}' for target '${parameters.targetId.get()}' failed",
                failure
            )
        }
    }
}
