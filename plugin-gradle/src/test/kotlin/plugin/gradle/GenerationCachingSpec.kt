package dev.akif.tapik.plugin.gradle

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.TaskOutcome

class GenerationCachingSpec : FunSpec({
    test("reuse configuration and task state then restore and relocate generated output from cache") {
        val fixture = CacheableGenerationFixture.create()

        val first = fixture.run()
        first.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS
        first.output shouldContain "Configuration cache entry stored."
        fixture.generatedFiles() shouldContain "documentation/selected.json"
        fixture.generatedFiles() shouldContain "documentation/nested.txt"

        val unchanged = fixture.run()
        unchanged.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.UP_TO_DATE
        unchanged.output shouldContain "Reusing configuration cache."

        fixture.deleteOutput()
        val restored = fixture.run()
        restored.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.FROM_CACHE
        restored.output shouldContain "Reusing configuration cache."

        val relocated = fixture.relocate()
        val relocatedResult = relocated.run()
        relocatedResult.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.FROM_CACHE
        relocated.generatedFiles() shouldBe fixture.generatedFiles()
    }

    test("invalidate generation for every declared input and reuse content when only output location changes") {
        val fixture = CacheableGenerationFixture.create()
        fixture.run()

        fixture.updateContractBytecode("changed")
        fixture.run().task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS

        fixture.updateRuntimeDependency("changed")
        fixture.run().task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS

        fixture.updateTargetArtifact("changed")
        fixture.run().task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS

        fixture.configure { configuration -> configuration.copy(apiId = "Books") }
        fixture.run().task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS

        fixture.configure { configuration -> configuration.copy(nestedOwner = "documentation") }
        fixture.run().task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS

        fixture.configure { configuration -> configuration.copy(openApiFirst = false) }
        fixture.run().task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS

        fixture.configure { configuration -> configuration.copy(pluginInputVersion = "${cacheFixtureVersionForTests}-fixture") }
        fixture.run().task(":tapikGenerate")?.outcome shouldBe TaskOutcome.SUCCESS

        fixture.configure { configuration -> configuration.copy(outputRoot = "build/alternate/tapik") }
        fixture.run().task(":tapikGenerate")?.outcome shouldBe TaskOutcome.FROM_CACHE
        fixture.generatedFiles() shouldContain "documentation/selected.json"

        val unchanged = fixture.run()
        unchanged.task(":tapikGenerate")?.outcome shouldBe TaskOutcome.UP_TO_DATE
        unchanged.output shouldContain "Reusing configuration cache."
    }
})

private val cacheFixtureVersionForTests: String =
    requireNotNull(System.getProperty("tapik.version")) { "Missing tapik.version" }
