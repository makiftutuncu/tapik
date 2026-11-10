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
        apiDependencyClasspath: List<Path>,
        mode: Mode,
        targetClassLoader: ClassLoader
    ): GradleGenerationSession =
        GradleGenerationSession(apiDependencyClasspath, mode, targetClassLoader)

    fun generate(
        apiDependencyClasspath: List<Path>,
        mode: Mode,
        targetId: String,
        configuration: Map<String, GradleConfigurationValue>,
        includeApis: Set<String>,
        excludeApis: Set<String>,
        targetClassLoader: ClassLoader
    ): GenerationResult =
        open(apiDependencyClasspath, mode, targetClassLoader).use { session ->
            session.generate(targetId, configuration, includeApis, excludeApis)
        }
}

internal class GradleGenerationSession(
    apiDependencyClasspath: List<Path>,
    private val mode: Mode,
    targetClassLoader: ClassLoader
) : AutoCloseable {
    private val targets =
        ServiceProviderSession.runWith(targetClassLoader) { activeClassLoader ->
            loadTargets(activeClassLoader, TARGET_CLASSPATH)
        }
    private val available = targets.map(GenerationTarget::id)
    private val apiSession =
        ServiceProviderSession.open(
            classpath = apiDependencyClasspath,
            parent = targetClassLoader,
            serviceTypes = setOf(ApiRegistry::class.java, GenerationTargetRegistry::class.java)
        )
    private val apis =
        try {
            apiSession.run { apiClassLoader ->
                loadApis(apiClassLoader, mode.apiClasspathName())
                    .also { loaded -> require(loaded.isNotEmpty()) { mode.emptyApiCatalogMessage() } }
            }
        } catch (failure: Exception) {
            apiSession.close()
            throw failure
        }
    private val engine = GenerationEngine(targets)

    fun generate(
        targetId: String,
        configuration: Map<String, GradleConfigurationValue>,
        includeApis: Set<String>,
        excludeApis: Set<String>
    ): GenerationResult =
        apiSession.run { apiClassLoader ->
            if (targetId !in available) {
                diagnoseUnknownTarget(targetId, available, apiClassLoader)
            }
            engine.generate(
                targetId = targetId,
                apis = apis,
                configuration = configuration.toTargetConfiguration(),
                selection = ApiSelection(includes = includeApis, excludes = excludeApis)
            )
        }

    override fun close() {
        apiSession.close()
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
    apiClassLoader: ClassLoader
): Nothing {
    val misplaced =
        loadTargets(apiClassLoader, API_DEPENDENCY_CLASSPATH)
            .any { target -> target.id == targetId }
    if (misplaced) {
        error(
            "Generation target '$targetId' was found on $API_DEPENDENCY_CLASSPATH, but target artifacts belong in " +
                "tapikTarget rather than tapikApiDependency or application dependencies."
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
    if (classpath == API_DEPENDENCY_CLASSPATH) {
        " Generation target providers belong in tapikTarget rather than tapikApiDependency or application dependencies."
    } else {
        ""
    }

private fun apiLoadingFailure(classpath: String, cause: Throwable): IllegalStateException =
    IllegalStateException(
        "Failed to load API registry providers from $classpath. " +
            "Check compiler-generated registry declarations, API runtime dependencies, and tapik version " +
            "alignment.",
        cause
    )

private fun Mode.apiClasspathName(): String =
    when (this) {
        Mode.COMPILED_API_DEPENDENCY -> API_DEPENDENCY_CLASSPATH
        Mode.SAME_PROJECT -> SAME_PROJECT_CLASSPATH
    }

private fun Mode.emptyApiCatalogMessage(): String =
    when (this) {
        Mode.COMPILED_API_DEPENDENCY ->
            "No APIs were discovered on $API_DEPENDENCY_CLASSPATH. Add an API compiled with the tapik compiler " +
                "plugin to tapikApiDependency."
        Mode.SAME_PROJECT ->
            "No APIs were discovered on $SAME_PROJECT_CLASSPATH. Ensure the main compilation contains eligible " +
                "public concrete Api types and the tapik compiler plugin is active."
    }

private const val TARGET_CLASSPATH: String = "tapikTargetClasspath"
private const val API_DEPENDENCY_CLASSPATH: String = "tapikApiDependencyClasspath"
private const val SAME_PROJECT_CLASSPATH: String = "the same project's main compile and runtime classpaths"
