package dev.akif.tapik.plugin.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.shouldBe
import java.nio.file.Files
import java.nio.file.Path

class GenerationFailureSpec : FunSpec({
    test("diagnose unknown targets empty contracts and empty selections") {
        val unknownTarget = generationProject()
        unknownTarget.writeBuild(
            """
            target('company', 'missing') {
            }
            """.trimIndent()
        )
        val unknown = unknownTarget.runAndFail("tapikGenerate")
        unknown.output shouldContain "tapik execution 'company' for target 'missing' failed"
        unknown.output shouldContain "Unknown generation target 'missing' on tapikTargetClasspath"
        unknown.output shouldContain "tapikTarget"

        val emptyContract = generationProject()
        emptyContract.writeBuild(
            tapikBlock =
                """
                openApi('docs') {
                    version('1.0.0')
                }
                """.trimIndent(),
            contractDependencies = ""
        )
        val empty = emptyContract.runAndFail("tapikGenerate")
        empty.output shouldContain "tapik execution 'docs' for target 'openapi' failed"
        empty.output shouldContain "No APIs were discovered on tapikContractClasspath"
        empty.output shouldContain "tapikContract"

        val emptySelection = generationProject()
        emptySelection.writeBuild(
            """
            openApi('docs') {
                excludeApis('Authors', 'Books', 'Catalog')
                version('1.0.0')
            }
            """.trimIndent()
        )
        val selection = emptySelection.runAndFail("tapikGenerate")
        selection.output shouldContain "tapik execution 'docs' for target 'openapi' failed"
        selection.output shouldContain "API selection must retain at least one API"
    }

    test("diagnose incompatible tapik dependencies before isolated generation") {
        val project = generationProject()
        project.publishTapikModule("tapik-incompatible", "9.9.9")
        project.writeBuild(
            tapikBlock =
                """
                openApi('docs') {
                    version('1.0.0')
                }
                """.trimIndent(),
            additionalDependencies = "tapikContract 'dev.akif:tapik-incompatible:9.9.9'"
        )

        val failure = project.runAndFail("tapikGenerate")

        failure.output shouldContain
            "tapik Gradle plugin $gradleTestTapikVersion is incompatible with resolved dependencies"
        failure.output shouldContain "tapikContractClasspath contains dev.akif:tapik-incompatible:9.9.9"
        failure.output shouldContain "Align every dev.akif:tapik-* dependency with the plugin version"
    }

    test("diagnose incompatible tapik project dependencies before isolated generation") {
        val project = generationProject()
        project.includeTapikProject("tapik-incompatible", "9.9.9")
        project.writeBuild(
            tapikBlock =
                """
                openApi('docs') {
                    version('1.0.0')
                }
                """.trimIndent(),
            additionalDependencies = "tapikContract project(':tapik-incompatible')"
        )

        val failure = project.runAndFail("tapikGenerate")

        failure.output shouldContain
            "tapik Gradle plugin $gradleTestTapikVersion is incompatible with resolved dependencies"
        failure.output shouldContain "tapikContractClasspath"
        failure.output shouldContain "dev.akif:tapik-incompatible:9.9.9"
        failure.output shouldContain "project :tapik-incompatible"
        project.generatedFiles() shouldBe emptyList()
    }

    test("frame target exceptions with their execution and target") {
        val descriptorRoot = Files.createTempDirectory("tapik-gradle-throwing-target-")
        val descriptor =
            descriptorRoot.resolve("META-INF/services/dev.akif.tapik.common.plugin.GenerationTargetRegistry")
        Files.createDirectories(descriptor.parent)
        Files.writeString(descriptor, ThrowingTargetRegistry::class.qualifiedName + "\n")
        val testClasses = Path.of(ThrowingTargetRegistry::class.java.protectionDomain.codeSource.location.toURI())
        val project = generationProject()
        project.writeBuild(
            tapikBlock =
                """
                target('explode', 'throwing') {
                }
                """.trimIndent(),
            additionalDependencies =
                "tapikTarget files('${descriptorRoot.gradleGroovyPath()}', '${testClasses.gradleGroovyPath()}')"
        )

        val failure = project.runAndFail("tapikGenerate")

        failure.output shouldContain "tapik execution 'explode' for target 'throwing' failed"
        failure.output shouldContain "target failure"
    }
})
