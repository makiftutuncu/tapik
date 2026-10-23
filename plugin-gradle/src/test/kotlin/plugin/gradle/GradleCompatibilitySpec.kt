package dev.akif.tapik.plugin.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.TaskOutcome

class GradleCompatibilitySpec : FunSpec({
    listOf("9.1.0", "9.5.0").forEach { gradleVersion ->
        test("generate with Gradle $gradleVersion, Java 25, and Kotlin 2.4.10") {
            val fixture = CacheableGenerationFixture.create()

            val first = fixture.run(gradleVersion)
            first.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS

            val second = fixture.run(gradleVersion)
            second.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.UP_TO_DATE
            second.output shouldContain "Reusing configuration cache."
        }
    }
})
