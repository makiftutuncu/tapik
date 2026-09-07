package dev.akif.tapik.common.plugin

import dev.akif.tapik.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SnapshotSpec : FunSpec({
    test("snapshot generation requests including copies") {
        val api = object : Api("Books") {}
        val apis = mutableListOf<Api>(api)
        val request = GenerationRequest(apis)
        val copy = request.copy(apis = apis)
        apis.clear()
        listOf(request, copy).forEach {
            runCatching { (it.component1() as MutableList).clear() }
            it.apis shouldBe listOf(api)
        }
        request shouldBe copy
    }

    test("catalog getters cannot replace or remove discovered providers") {
        val api = object : Api("Books") {}
        val otherApi = object : Api("Authors") {}
        val registry = object : ApiRegistry { override val apis = mutableListOf<Api>(api, otherApi) }
        val catalog = ApiCatalog.from(listOf(registry))
        registry.apis.clear()
        runCatching { (catalog.apis as MutableList).clear() }
        catalog.apis shouldBe listOf(otherApi, api)

        val target = object : GenerationTarget {
            override val id = "test"
            override fun generate(request: GenerationRequest) = GenerationResult(emptyList())
        }
        val otherTarget = object : GenerationTarget {
            override val id = "another"
            override fun generate(request: GenerationRequest) = GenerationResult(emptyList())
        }
        val targets = object : GenerationTargetRegistry { override val targets = mutableListOf<GenerationTarget>(target, otherTarget) }
        val targetCatalog = GenerationTargetCatalog.from(listOf(targets))
        targets.targets.clear()
        runCatching { (targetCatalog.targets as MutableList).clear() }
        targetCatalog.targets shouldBe listOf(otherTarget, target)
    }

    test("snapshot compiled endpoint paths types and API lists including copies") {
        val api = object : Api("Books") { val list by get(root) }
        val arguments = mutableListOf<KotlinTypeProjection>(KotlinStarProjection)
        val type = KotlinType(KotlinClassClassifier("List"), arguments)
        val typeCopy = type.copy(arguments = arguments)
        val path = mutableListOf("list")
        val endpoint = CompiledEndpoint(api.list, type, path)
        val endpointCopy = endpoint.copy(sourcePropertyPath = path)
        val endpoints = mutableListOf(endpoint)
        val compiled = CompiledApi(api, endpoints)
        val compiledCopy = compiled.copy(endpoints = endpoints)
        val hash = endpoint.hashCode()
        arguments.clear()
        path.clear()
        endpoints.clear()
        listOf(type, typeCopy).forEach {
            runCatching { (it.component2() as MutableList).clear() }
            it.arguments shouldBe listOf(KotlinStarProjection)
        }
        listOf(endpoint, endpointCopy).forEach {
            runCatching { (it.propertyPath as MutableList).clear() }
            it.propertyPath shouldBe listOf("list")
            it.hashCode() shouldBe hash
        }
        listOf(compiled, compiledCopy).forEach {
            runCatching { (it.component2() as MutableList).clear() }
            it.endpoints shouldBe listOf(endpoint)
        }
        endpoint shouldBe endpointCopy
    }

    test("snapshot generated sealed variant fields including copies") {
        val field = KotlinDataField("id", KotlinType(KotlinClassClassifier("kotlin.String")).toKotlinSourceType("test"))
        val fields = mutableListOf(field)
        val variant = KotlinSealedVariant("Found", fields)
        val copy = variant.copy(fields = fields)
        fields.clear()
        listOf(variant, copy).forEach {
            runCatching { (it.component2() as MutableList).clear() }
            it.fields shouldBe listOf(field)
        }
    }

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
