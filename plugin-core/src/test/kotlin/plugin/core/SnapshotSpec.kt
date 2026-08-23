package dev.akif.tapik.plugin.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SnapshotSpec : FunSpec({
    test("snapshot configuration collections") {
        val children = mutableListOf<ConfigurationValue>(ScalarConfigurationValue("one"))
        val objectValues = linkedMapOf<String, ConfigurationValue>("children" to ListConfigurationValue(children))
        val rootValues = linkedMapOf<String, ConfigurationValue>("object" to ObjectConfigurationValue(objectValues))
        val list = ListConfigurationValue(children)
        val objectValue = ObjectConfigurationValue(objectValues)
        val configuration = TargetConfiguration(rootValues)

        children.clear()
        objectValues.clear()
        rootValues.clear()
        runCatching { (list.values as MutableList).clear() }
        runCatching { (objectValue.values as MutableMap).clear() }
        runCatching { (configuration.values as MutableMap).clear() }

        list.values shouldBe listOf(ScalarConfigurationValue("one"))
        objectValue.values.keys shouldBe setOf("children")
        configuration.values.keys shouldBe setOf("object")
    }

    test("snapshot generation result artifacts") {
        val artifacts =
            mutableListOf(
                GeneratedArtifact("result.txt", "text/plain", ArtifactKind.RESOURCE, "result")
            )
        val result = GenerationResult(artifacts)

        artifacts.clear()
        runCatching { (result.artifacts as MutableList).clear() }

        result.artifacts.map(GeneratedArtifact::relativePath) shouldBe listOf("result.txt")
    }
})
