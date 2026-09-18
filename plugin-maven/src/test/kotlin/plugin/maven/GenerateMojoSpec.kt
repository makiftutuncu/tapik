package dev.akif.tapik.plugin.maven

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.apache.maven.model.Build
import org.apache.maven.plugin.MojoExecution
import org.apache.maven.plugin.MojoExecutionException
import org.apache.maven.plugin.descriptor.MojoDescriptor
import org.apache.maven.plugin.descriptor.PluginDescriptor
import org.apache.maven.plugin.logging.SystemStreamLog
import org.apache.maven.project.MavenProject
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.util.Collections
import java.util.Enumeration

class GenerateMojoSpec : FunSpec({
    test("generate-sources registers artifacts without compiling or synchronizing them") {
        val fixture = GenerateMojoFixture("generate-sources")
        fixture.mojo.execute()

        Files.exists(fixture.output.resolve(fixture.sourcePath)) shouldBe true
        fixture.project.compileSourceRoots.contains(fixture.output.toString()) shouldBe true
        fixture.project.resources.single().includes.single() shouldBe fixture.descriptorPath
        Files.exists(fixture.classes) shouldBe false
        Files.exists(fixture.workspace.resolve("target/tapik-generated-classes")) shouldBe false
    }

    test("process-classes compiles and synchronizes generated classes and resources") {
        val fixture = GenerateMojoFixture("process-classes")
        Files.createDirectories(fixture.classes)
        fixture.mojo.execute()

        Files.exists(fixture.classes.resolve(fixture.classPath)) shouldBe true
        Files.exists(fixture.classes.resolve(fixture.descriptorPath)) shouldBe true
        fixture.project.compileSourceRoots.contains(fixture.output.toString()) shouldBe true
    }

    test("reject unsupported and missing lifecycle phases before writing outputs") {
        listOf("process-sources", "compile", "test-compile", "package", "validate", null).forEach { phase ->
            val fixture = GenerateMojoFixture(phase)
            val failure = shouldThrow<MojoExecutionException> { fixture.mojo.execute() }
            failure.message shouldContain "generate-sources"
            failure.message shouldContain "process-classes"
            failure.message shouldContain (phase ?: "missing")
            Files.exists(fixture.output) shouldBe false
            Files.exists(fixture.classes) shouldBe false
        }
    }

    test("direct invocation reports missing main compilation before writing artifacts") {
        val fixture = GenerateMojoFixture(null, MojoExecution.Source.CLI)
        val failure = shouldThrow<MojoExecutionException> { fixture.mojo.execute() }
        failure.message shouldContain "Direct"
        failure.message shouldContain "does not run"
        failure.message shouldContain "compile"
        Files.exists(fixture.output) shouldBe false
    }

    test("direct execution uses late generation even when the selected execution has an early binding") {
        listOf(null, "generate-sources").forEach { phase ->
            val fixture = GenerateMojoFixture(phase, MojoExecution.Source.CLI)
            Files.createDirectories(fixture.classes)
            fixture.mojo.execute()

            Files.exists(fixture.classes.resolve(fixture.classPath)) shouldBe true
            Files.exists(fixture.classes.resolve(fixture.descriptorPath)) shouldBe true
            fixture.messages.first() shouldContain "does not run user compilation"
            fixture.messages.first() shouldContain "may be stale"
        }
    }

    test("direct generation without compiled APIs fails before touching existing output") {
        val output = Files.createTempDirectory("tapik-direct-no-apis-")
        val sentinel = Files.writeString(output.resolve("existing.txt"), "unchanged")
        val withoutRegistries = object : ClassLoader(GenerateMojoSpec::class.java.classLoader) {
            override fun getResources(name: String): Enumeration<URL> =
                if (name == "META-INF/services/dev.akif.tapik.ApiRegistry") {
                    Collections.emptyEnumeration()
                } else {
                    super.getResources(name)
                }
        }

        val failure = shouldThrow<IllegalArgumentException> {
            MavenGenerator().generate(
                classpath = emptyList(),
                targetId = "openapi",
                targetConfiguration = emptyMap(),
                outputDirectory = output,
                executionId = "direct-test",
                parentClassLoader = withoutRegistries,
                pluginVersion = "0.6.0",
                projectTapikVersions = emptySet(),
                mode = MavenGenerationMode.DIRECT
            )
        }
        failure.message shouldContain "Direct"
        failure.message shouldContain "no APIs in compiled registries"
        failure.message shouldContain "does not run user compilation"
        Files.readString(sentinel) shouldBe "unchanged"
        Files.list(output).use { it.count() } shouldBe 1L
    }
})

private class GenerateMojoFixture(phase: String?, source: MojoExecution.Source = MojoExecution.Source.LIFECYCLE) {
    val workspace = Files.createTempDirectory("tapik-mojo-lifecycle-").toAbsolutePath()
    val output = workspace.resolve("target/generated")
    val classes = workspace.resolve("target/classes")
    val messages = mutableListOf<String>()
    val sourcePath = "dev/akif/tapik/generated/test/fixtures/library/Authors/AuthorsServer.kt"
    val classPath = sourcePath.removeSuffix(".kt") + ".class"
    val descriptorPath = "META-INF/tapik/spring/webmvc/" +
        "dev.akif.tapik.generated.test.fixtures.library.Authors.AuthorsGeneratedController.properties"
    val project = object : MavenProject() {
        override fun getCompileClasspathElements(): List<String> =
            listOf(classes.toString()) + System.getProperty("java.class.path").split(File.pathSeparator)

        override fun getRuntimeClasspathElements(): List<String> = compileClasspathElements
    }.apply {
        artifactId = "lifecycle-fixture"
        file = workspace.resolve("pom.xml").toFile()
        build = Build().apply {
            directory = workspace.resolve("target").toString()
            outputDirectory = classes.toString()
        }
        artifacts = emptySet()
    }
    val mojo = GenerateMojo().apply {
        log = object : SystemStreamLog() {
            override fun info(content: CharSequence) {
                messages.add(content.toString())
            }
        }
        inject("project", project)
        inject("mojoExecution", MojoExecution(
            MojoDescriptor().apply { pluginDescriptor = PluginDescriptor().apply { version = "0.6.0" } },
            "lifecycle-test",
            source
        ).apply { lifecyclePhase = phase })
        inject("target", "spring-webmvc")
        inject("outputDirectory", output.toFile())
        inject("includeApis", setOf("Authors"))
    }
}

private fun GenerateMojo.inject(name: String, value: Any) {
    GenerateMojo::class.java.getDeclaredField(name).apply { isAccessible = true }.set(this, value)
}
