package dev.akif.tapik.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@CacheableTask
abstract class TapikGenerateTask : DefaultTask() {
    @get:Input
    abstract val endpointPackages: ListProperty<String>

    @get:Input
    abstract val outputPackage: Property<String>

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val sourceDirectory: DirectoryProperty

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val compiledClassesDirectory: DirectoryProperty

    @TaskAction
    fun generate() {
        val outDir = outputDirectory.get().asFile
        outDir.mkdirs()

        val pkg = outputPackage.orNull ?: ""
        val pkgs = endpointPackages.orNull ?: emptyList()
        logger.lifecycle("[tapik] Analyzing HttpEndpoint types in packages: $pkgs")

        // Resolve Kotlin source root from configured property
        val sourceRoot: File = sourceDirectory.get().asFile
        if (!sourceRoot.exists()) {
            logger.lifecycle("[tapik] No Kotlin sources found at ${sourceRoot.absolutePath}")
            return
        }

        // Use bytecode analysis to get actual resolved types from compiled classes
        val bytecodeTypeAnalyzer = BytecodeTypeAnalyzer()
        val compiledClassesDir = compiledClassesDirectory.get().asFile
        val resolvedEndpoints = try {
            if (compiledClassesDir.exists()) {
                bytecodeTypeAnalyzer.analyzeEndpoints(pkgs, compiledClassesDir, logger)
            } else {
                logger.warn("[tapik] No compiled classes found at ${compiledClassesDir.absolutePath}")
                emptyList()
            }
        } catch (e: Exception) {
            logger.error("[tapik] Bytecode analysis failed", e)
            emptyList()
        }

        if (resolvedEndpoints.isEmpty()) {
            logger.lifecycle("[tapik] No HttpEndpoint properties found in scope")
            return
        }

        // Filter out synthetic methods and simplify type names
        val filteredEndpoints = resolvedEndpoints.filter { endpoint ->
            !endpoint.name.contains("$") && !endpoint.name.startsWith("_") &&
            !endpoint.name.contains("delegate") && !endpoint.name.contains("lambda")
        }.map { endpoint ->
            endpoint.copy(fullType = bytecodeTypeAnalyzer.simplifyTapikTypes(endpoint.fullType))
        }

        logger.lifecycle("[tapik] Resolved ${filteredEndpoints.size} HttpEndpoint properties (filtered from ${resolvedEndpoints.size}):")
        filteredEndpoints.forEach { endpoint ->
            val type = if (endpoint.isDelegated) "delegated" else "explicit"
            logger.lifecycle("[tapik] ${endpoint.name} (${endpoint.packageName}) [$type] -> ${endpoint.fullType}")
        }
        writeJsonOutput(filteredEndpoints, outDir, pkg, pkgs, logger)
    }

    private fun writeJsonOutput(
        endpoints: List<ResolvedEndpointInfo>,
        outputDir: File,
        outputPackage: String,
        endpointPackages: List<String>,
        logger: org.gradle.api.logging.Logger
    ) {
        val report = TapikReport(
            outputPackage = outputPackage,
            endpointPackages = endpointPackages,
            endpoints = endpoints.map { endpoint ->
                TapikEndpoint(
                    name = endpoint.name,
                    packageName = endpoint.packageName,
                    fullType = endpoint.fullType,
                    file = endpoint.file,
                    isDelegated = endpoint.isDelegated
                )
            }
        )

        val json = Json { prettyPrint = true }
        val jsonOutput = json.encodeToString(report)

        val outputFile = outputDir.resolve("tapik-endpoints.json")
        outputFile.writeText(jsonOutput)

        logger.lifecycle("[tapik] JSON report written to: ${outputFile.absolutePath}")
    }
}

@Serializable
data class TapikReport(
    val outputPackage: String,
    val endpointPackages: List<String>,
    val endpoints: List<TapikEndpoint>
)

@Serializable
data class TapikEndpoint(
    val name: String,
    val packageName: String,
    val fullType: String,
    val file: String,
    val isDelegated: Boolean
)
