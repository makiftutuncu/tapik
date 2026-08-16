package dev.akif.tapik.plugin.maven

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import org.apache.maven.model.Plugin
import org.apache.maven.plugin.MojoExecution
import org.apache.maven.project.MavenProject

class KotlinMavenPluginExtensionSpec : FunSpec({
    val extension = TapikKotlinMavenPluginExtension()
    val project = MavenProject()
    val execution = MojoExecution(Plugin(), "compile", "compile")

    test("activate Tapik for every Kotlin Maven compilation") {
        extension.isApplicable(project, execution) shouldBe true
        extension.compilerPluginId shouldBe "dev.akif.tapik"
    }

    test("leave Tapik compiler configuration empty") {
        extension.getPluginOptions(project, execution).shouldBeEmpty()
    }
})
