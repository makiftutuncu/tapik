package dev.akif.tapik.plugin.maven

import dev.akif.tapik.Api
import dev.akif.tapik.ApiRegistry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import org.apache.maven.artifact.DefaultArtifact
import org.apache.maven.artifact.handler.DefaultArtifactHandler
import org.apache.maven.project.MavenProject
import java.nio.file.Files
import java.nio.file.Path

class MavenProjectClasspathSpec : FunSpec({
    test("combine compile and runtime classpaths deterministically") {
        val project =
            object : MavenProject() {
                override fun getCompileClasspathElements(): List<String> =
                    listOf("target/classes", "dependencies/compile.jar", "dependencies/../dependencies/shared.jar")

                override fun getRuntimeClasspathElements(): List<String> =
                    listOf("target/classes", "dependencies/shared.jar", "dependencies/runtime.jar")
            }

        project.generationClasspath() shouldContainExactly
            listOf(
                Path.of("target/classes").toAbsolutePath().normalize(),
                Path.of("dependencies/compile.jar").toAbsolutePath().normalize(),
                Path.of("dependencies/shared.jar").toAbsolutePath().normalize(),
                Path.of("dependencies/runtime.jar").toAbsolutePath().normalize()
            )
    }

    test("discover an API registry present only at runtime") {
        val runtime = Files.createTempDirectory("tapik-runtime-classpath-")
        val service = runtime.resolve("META-INF/services/dev.akif.tapik.ApiRegistry")
        Files.createDirectories(service.parent)
        Files.writeString(service, RuntimeOnlyApiRegistry::class.java.name)
        val project =
            object : MavenProject() {
                override fun getCompileClasspathElements(): List<String> = emptyList()

                override fun getRuntimeClasspathElements(): List<String> = listOf(runtime.toString())
            }

        ProjectApis.use(project.generationClasspath(), MavenProjectClasspathSpec::class.java.classLoader) { apis, _ ->
            apis.map(Api::id) shouldContain "RuntimeOnly"
        }
    }

    test("collect versions from tapik project dependencies") {
        val project = MavenProject()
        project.artifacts =
            setOf(
                artifact("dev.akif", "tapik-core", "0.6.0"),
                artifact("dev.akif", "tapik-format-kotlinx", "0.5.0"),
                artifact("dev.akif", "unrelated", "1.0.0"),
                artifact("example", "tapik-extension", "2.0.0")
            )

        project.tapikDependencyVersions() shouldContainExactly setOf("0.5.0", "0.6.0")
    }
})

private fun artifact(
    groupId: String,
    artifactId: String,
    version: String
) = DefaultArtifact(groupId, artifactId, version, "compile", "jar", null, DefaultArtifactHandler("jar"))

class RuntimeOnlyApiRegistry : ApiRegistry {
    override val apis: List<Api> = listOf(object : Api("RuntimeOnly") {})
}
