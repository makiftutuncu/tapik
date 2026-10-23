package dev.akif.tapik.plugin.gradle

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import javax.tools.ToolProvider
import kotlin.io.path.exists

internal class CacheableGenerationFixture private constructor(
    private val root: Path,
    private val buildCache: Path,
    private val targetDescriptor: Path,
    private val targetClasses: Path
) {
    var configuration: CacheableGenerationConfiguration = CacheableGenerationConfiguration()
        private set

    val outputRoot: Path
        get() = root.resolve(configuration.outputRoot ?: "build/generated/tapik")

    fun configure(transform: (CacheableGenerationConfiguration) -> CacheableGenerationConfiguration) {
        configuration = transform(configuration)
        writeBuild()
    }

    fun updateContractBytecode(value: String) {
        rewriteJarWithMarker(root.resolve("inputs/contract.jar"), "ContractMarker", value)
    }

    fun updateRuntimeDependency(value: String) {
        rewriteJarWithMarker(root.resolve("inputs/runtime.jar"), "RuntimeMarker", value)
    }

    fun updateTargetArtifact(value: String) {
        rewriteJarWithMarker(root.resolve("inputs/target.jar"), "TargetMarker", value)
    }

    fun deleteOutput() {
        deleteRecursively(outputRoot)
    }

    fun generatedFiles(): List<String> {
        if (!outputRoot.exists()) return emptyList()
        return Files.walk(outputRoot).use { paths ->
            paths.filter(Files::isRegularFile)
                .map { path -> outputRoot.relativize(path).toString().replace('\\', '/') }
                .filter { path -> path != ".tapik-ownership" }
                .sorted()
                .toList()
        }
    }

    fun relocate(): CacheableGenerationFixture {
        val relocatedRoot = Files.createTempDirectory("tapik-gradle-cache-relocated-")
        Files.walk(root).use { paths ->
            paths.forEach { source ->
                val relative = root.relativize(source)
                if (relative.nameCount == 0 || relative.startsWith("build") || relative.startsWith(".gradle")) return@forEach
                val destination = relocatedRoot.resolve(relative)
                if (Files.isDirectory(source)) Files.createDirectories(destination)
                else Files.copy(source, destination)
            }
        }
        return CacheableGenerationFixture(relocatedRoot, buildCache, targetDescriptor, targetClasses).also {
            it.configuration = configuration
        }
    }

    fun run(gradleVersion: String? = null, vararg tasks: String = arrayOf("tapikGenerate")): BuildResult {
        val runner =
            GradleRunner.create()
                .withProjectDir(root.toFile())
                .withArguments(*tasks, "--configuration-cache", "--build-cache", "--stacktrace")
                .withPluginClasspath()
        if (gradleVersion != null) runner.withGradleVersion(gradleVersion)
        return runner.build()
    }

    private fun writeBuild() {
        val openApi =
            """
            openApi('docs') {
                includeApis('${configuration.apiId}')
                version('1.0.0')
                format(OpenApiFormat.JSON)
                output('selected.json')
            }
            """.trimIndent()
        val nested =
            """
            target('metadata', 'nested-configuration') {
                includeApis('${configuration.apiId}')
                list('formats', 'json', 'yaml')
                objectValue('metadata') { metadata ->
                    metadata.value('owner', '${configuration.nestedOwner}')
                    metadata.objectValue('release') { release ->
                        release.value('stable', true)
                    }
                }
            }
            """.trimIndent()
        val executions = if (configuration.openApiFirst) "$openApi\n$nested" else "$nested\n$openApi"
        val outputRoot =
            configuration.outputRoot?.let { path -> "outputRoot(layout.projectDirectory.dir('$path'))" }.orEmpty()
        Files.writeString(
            root.resolve("build.gradle"),
            """
            import dev.akif.tapik.plugin.gradle.Mode
            import dev.akif.tapik.plugin.gradle.OpenApiFormat

            plugins {
                id 'org.jetbrains.kotlin.jvm'
                id 'dev.akif.tapik.plugin.gradle'
            }

            repositories {
                maven { url = uri('${cacheFixtureRepository.toUri()}') }
                mavenCentral()
            }

            dependencies {
                tapikContract files('inputs/contract.jar')
                tapikContract files('inputs/runtime.jar')
                tapikContract 'dev.akif:tapik-core:$cacheFixtureVersion'
                tapikContract 'dev.akif:tapik-format-jackson:$cacheFixtureVersion'
                tapikTarget files('inputs/target.jar')
                tapikTarget files('${targetDescriptor.groovyPath()}', '${targetClasses.groovyPath()}')
            }

            tapik {
                mode(Mode.COMPILED_CONTRACT)
                $outputRoot
            ${executions.prependIndent("    ")}
            }

            kotlin {
                jvmToolchain(25)
            }

            tasks.named('tapikGenerate') {
                pluginVersion.set('${configuration.pluginInputVersion}')
            }
            """.trimIndent() + "\n"
        )
    }

    companion object {
        fun create(sharedBuildCache: Path = Files.createTempDirectory("tapik-gradle-build-cache-")): CacheableGenerationFixture {
            val root = Files.createTempDirectory("tapik-gradle-cacheable-")
            val descriptorRoot = Files.createTempDirectory("tapik-gradle-cache-target-")
            val descriptor =
                descriptorRoot.resolve("META-INF/services/dev.akif.tapik.common.plugin.GenerationTargetRegistry")
            Files.createDirectories(descriptor.parent)
            Files.writeString(descriptor, NestedConfigurationTargetRegistry::class.qualifiedName + "\n")
            val testClasses =
                Path.of(NestedConfigurationTargetRegistry::class.java.protectionDomain.codeSource.location.toURI())
            Files.writeString(
                root.resolve("settings.gradle"),
                """
                buildCache {
                    local {
                        directory = file('${sharedBuildCache.groovyPath()}')
                    }
                }

                rootProject.name = 'cacheable-generation'
                """.trimIndent() + "\n"
            )
            val inputs = root.resolve("inputs")
            Files.createDirectories(inputs)
            Files.copy(cacheFixtureContractJar, inputs.resolve("contract.jar"))
            rewriteJarWithMarker(inputs.resolve("contract.jar"), "ContractMarker", "initial")
            rewriteJarWithMarker(inputs.resolve("runtime.jar"), "RuntimeMarker", "initial")
            rewriteJarWithMarker(inputs.resolve("target.jar"), "TargetMarker", "initial")
            return CacheableGenerationFixture(root, sharedBuildCache, descriptorRoot, testClasses).also {
                it.writeBuild()
            }
        }
    }
}

internal data class CacheableGenerationConfiguration(
    val apiId: String = "Catalog",
    val nestedOwner: String = "platform",
    val openApiFirst: Boolean = true,
    val pluginInputVersion: String = cacheFixtureVersion,
    val outputRoot: String? = null
)

private fun rewriteJarWithMarker(path: Path, className: String, value: String) {
    val entries = linkedMapOf<String, ByteArray>()
    if (path.exists()) {
        JarFile(path.toFile()).use { jar ->
            jar.entries().asSequence().filterNot(JarEntry::isDirectory).forEach { entry ->
                entries[entry.name] = jar.getInputStream(entry).readAllBytes()
            }
        }
    }
    entries["dev/akif/tapik/cache/$className.class"] = compileMarkerClass(className, value)
    val replacement = Files.createTempFile(path.parent, "tapik-cache-", ".jar")
    JarOutputStream(Files.newOutputStream(replacement)).use { jar ->
        entries.forEach { (name, bytes) ->
            jar.putNextEntry(JarEntry(name).apply { time = 0L })
            jar.write(bytes)
            jar.closeEntry()
        }
    }
    Files.move(replacement, path, java.nio.file.StandardCopyOption.REPLACE_EXISTING)
}

private fun compileMarkerClass(className: String, value: String): ByteArray {
    val root = Files.createTempDirectory("tapik-gradle-marker-")
    return try {
        val source = root.resolve("$className.java")
        Files.writeString(
            source,
            "package dev.akif.tapik.cache; public final class $className { " +
                "public static String value() { return \"$value\"; } }\n"
        )
        val result = ToolProvider.getSystemJavaCompiler().run(null, null, null, "-d", root.toString(), source.toString())
        check(result == 0) { "Could not compile cache marker $className" }
        Files.readAllBytes(root.resolve("dev/akif/tapik/cache/$className.class"))
    } finally {
        deleteRecursively(root)
    }
}

private fun deleteRecursively(root: Path) {
    if (!root.exists()) return
    Files.walk(root).use { paths -> paths.sorted(Comparator.reverseOrder()).forEach(Files::delete) }
}

private fun Path.groovyPath(): String = toString().replace("'", "\\'")

private val cacheFixtureRepository: Path =
    Path.of(requireNotNull(System.getProperty("tapik.repository")) { "Missing tapik.repository" })
private val cacheFixtureVersion: String =
    requireNotNull(System.getProperty("tapik.version")) { "Missing tapik.version" }
private val cacheFixtureContractJar: Path =
    cacheFixtureRepository.resolve(
        "dev/akif/tapik-example-contract/$cacheFixtureVersion/" +
            "tapik-example-contract-$cacheFixtureVersion.jar"
    )
