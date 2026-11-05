package dev.akif.tapik.plugin.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.TaskOutcome

class GradleCompatibilitySpec : FunSpec({
    listOf("9.1.0", "9.8.0").forEach { gradleVersion ->
        test("generate with Gradle $gradleVersion, Java 25, and Kotlin 2.4.10") {
            val fixture = CacheableGenerationFixture.create()

            val first = fixture.run(gradleVersion)
            first.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS

            val second = fixture.run(gradleVersion)
            second.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.UP_TO_DATE
            second.output shouldContain "Reusing configuration cache."
        }
    }

    test("reject an unsupported Kotlin version after successful generation") {
        val fixture = CacheableGenerationFixture.create()
        val first = fixture.run()
        first.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS
        val generated = fixture.generatedFiles()

        fixture.configure { configuration ->
            configuration.copy(kotlinInputVersion = "2.4.0")
        }
        val failure = fixture.runAndFail()

        failure.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.FAILED
        failure.output shouldContain "tapik Gradle generation supports Kotlin 2.4.10; detected Kotlin 2.4.0"
        fixture.generatedFiles() shouldBe generated
    }

    test("reject an unsupported project type after successful generation") {
        val fixture = CacheableGenerationFixture.create()
        val first = fixture.run()
        first.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS
        val generated = fixture.generatedFiles()

        fixture.configure { configuration ->
            configuration.copy(projectTypeInput = "Kotlin Multiplatform")
        }
        val failure = fixture.runAndFail()

        failure.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.FAILED
        failure.output shouldContain
            "tapik Gradle generation supports Kotlin/JVM projects only; detected Kotlin Multiplatform"
        fixture.generatedFiles() shouldBe generated
    }
})
