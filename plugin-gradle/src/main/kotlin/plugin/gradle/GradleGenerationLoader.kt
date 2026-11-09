package dev.akif.tapik.plugin.gradle

import dev.akif.tapik.Api
import dev.akif.tapik.ApiRegistry
import dev.akif.tapik.common.plugin.ApiCatalog
import dev.akif.tapik.common.plugin.ApiSelection
import dev.akif.tapik.common.plugin.GenerationEngine
import dev.akif.tapik.common.plugin.GenerationResult
import dev.akif.tapik.common.plugin.GenerationTarget
import dev.akif.tapik.common.plugin.GenerationTargetCatalog
import dev.akif.tapik.common.plugin.GenerationTargetRegistry
import dev.akif.tapik.common.plugin.ServiceProviderSession
import java.nio.file.Path
import java.util.ServiceConfigurationError

internal object GradleGenerationLoader {
    fun open(
        contractClasspath: List<Path>,
        mode: Mode,
        targetClassLoader: ClassLoader
    ): GradleGenerationSession =
        GradleGenerationSession(contractClasspath, mode, targetClassLoader)

    fun generate(
        contractClasspath: List<Path>,
        mode: Mode,
        targetId: String,
        configuration: Map<String, GradleConfigurationValue>,
        includeApis: Set<String>,
        excludeApis: Set<String>,
        targetClassLoader: ClassLoader
    ): GenerationResult =
        open(contractClasspath, mode, targetClassLoader).use { session ->
            session.generate(targetId, configuration, includeApis, excludeApis)
        }
}

internal class GradleGenerationSession(
    contractClasspath: List<Path>,
    private val mode: Mode,
    targetClassLoader: ClassLoader
) : AutoCloseable {
    private val targets =
        ServiceProviderSession.runWith(targetClassLoader) { activeClassLoader ->
            loadTargets(activeClassLoader, TARGET_CLASSPATH)
        }
    private val available = targets.map(GenerationTarget::id)
    private val contractSession =
        ServiceProviderSession.open(
            classpath = contractClasspath,
            parent = targetClassLoader,
            serviceTypes = setOf(ApiRegistry::class.java, GenerationTargetRegistry::class.java)
        )
    private val apis =
        try {
            contractSession.run { contractClassLoader ->
                loadApis(contractClassLoader, mode.apiClasspathName())
                    .also { loaded -> require(loaded.isNotEmpty()) { mode.emptyApiCatalogMessage() } }
            }
        } catch (failure: Exception) {
            contractSession.close()
            throw failure
        }
    private val engine = GenerationEngine(targets)

    fun generate(
        targetId: String,
        configuration: Map<String, GradleConfigurationValue>,
        includeApis: Set<String>,
        excludeApis: Set<String>
    ): GenerationResult =
        contractSession.run { contractClassLoader ->
            if (targetId !in available) {
                diagnoseUnknownTarget(targetId, available, contractClassLoader)
            }
            engine.generate(
                targetId = targetId,
                apis = apis,
                configuration = configuration.toTargetConfiguration(),
                selection = ApiSelection(includes = includeApis, excludes = excludeApis)
            )
        }

    override fun close() {
        contractSession.close()
    }
}

private fun loadTargets(classLoader: ClassLoader, classpath: String): List<GenerationTarget> =
    try {
        GenerationTargetCatalog.load(classLoader).targets
    } catch (cause: ServiceConfigurationError) {
        throw targetLoadingFailure(classpath, cause)
    } catch (cause: LinkageError) {
        throw targetLoadingFailure(classpath, cause)
    } catch (cause: IllegalArgumentException) {
        throw IllegalStateException(
            "Invalid generation target catalog on $classpath: ${cause.message}." + targetPlacementGuidance(classpath),
            cause
        )
    }

private fun loadApis(classLoader: ClassLoader, classpath: String): List<Api> =
    try {
        ApiCatalog.load(classLoader).apis
    } catch (cause: ServiceConfigurationError) {
        throw apiLoadingFailure(classpath, cause)
    } catch (cause: LinkageError) {
        throw apiLoadingFailure(classpath, cause)
    } catch (cause: IllegalArgumentException) {
        throw IllegalStateException("Invalid API catalog on $classpath: ${cause.message}", cause)
    }

private fun diagnoseUnknownTarget(
    targetId: String,
    available: List<String>,
    contractClassLoader: ClassLoader
): Nothing {
    val misplaced =
        loadTargets(contractClassLoader, CONTRACT_CLASSPATH)
            .any { target -> target.id == targetId }
    if (misplaced) {
        error(
            "Generation target '$targetId' was found on $CONTRACT_CLASSPATH, but target artifacts belong in " +
                "tapikTarget rather than tapikContract or application dependencies."
        )
    }
    throw IllegalArgumentException(
        "Unknown generation target '$targetId' on $TARGET_CLASSPATH. " +
            "Available targets: ${available.sorted().joinToString().ifEmpty { "none" }}. " +
            "Add custom target artifacts to tapikTarget."
    )
}

private fun targetLoadingFailure(classpath: String, cause: Throwable): IllegalStateException =
    IllegalStateException(
        "Failed to load generation target providers from $classpath. " +
            "Check META-INF/services declarations, target runtime dependencies, and tapik version alignment." +
            targetPlacementGuidance(classpath),
        cause
    )

private fun targetPlacementGuidance(classpath: String): String =
    if (classpath == CONTRACT_CLASSPATH) {
        " Generation target providers belong in tapikTarget rather than tapikContract or application dependencies."
    } else {
        ""
    }

private fun apiLoadingFailure(classpath: String, cause: Throwable): IllegalStateException =
    IllegalStateException(
        "Failed to load API registry providers from $classpath. " +
            "Check compiler-generated registry declarations, contract runtime dependencies, and tapik version " +
            "alignment.",
        cause
    )

private fun Mode.apiClasspathName(): String =
    when (this) {
        Mode.COMPILED_CONTRACT -> CONTRACT_CLASSPATH
        Mode.SAME_PROJECT -> SAME_PROJECT_CLASSPATH
    }

private fun Mode.emptyApiCatalogMessage(): String =
    when (this) {
        Mode.COMPILED_CONTRACT ->
            "No APIs were discovered on $CONTRACT_CLASSPATH. Add a contract compiled with the tapik compiler " +
                "plugin to tapikContract."
        Mode.SAME_PROJECT ->
            "No APIs were discovered on $SAME_PROJECT_CLASSPATH. Ensure the main compilation contains eligible " +
                "public concrete Api types and the tapik compiler plugin is active."
    }

private const val TARGET_CLASSPATH: String = "tapikTargetClasspath"
private const val CONTRACT_CLASSPATH: String = "tapikContractClasspath"
private const val SAME_PROJECT_CLASSPATH: String = "the same project's main compile and runtime classpaths"
