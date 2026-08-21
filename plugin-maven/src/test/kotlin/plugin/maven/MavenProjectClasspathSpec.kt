package dev.akif.tapik.plugin.maven

import dev.akif.tapik.Api
import dev.akif.tapik.ApiRegistry
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
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

        ProjectApis.use(project.generationClasspath(), MavenProjectClasspathSpec::class.java.classLoader) { apis ->
            apis.map(Api::id) shouldContain "RuntimeOnly"
        }
    }
})

class RuntimeOnlyApiRegistry : ApiRegistry {
    override val apis: List<Api> = listOf(object : Api("RuntimeOnly") {})
}
