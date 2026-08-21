package dev.akif.tapik.plugin.core

import dev.akif.tapik.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

private data class Item(
    val name: String
)

private val itemsFormat: ByteArrayFormat<List<Item?>> =
    Format(
        codec =
            Codec(
                decoder = Decoder { DecodeResult.Success(emptyList()) },
                encoder = Encoder { byteArrayOf() }
            ),
        schema = ArraySchema(NullableSchema(ObjectSchema(emptyMap(), name = "Item")))
    )

private abstract class BaseApi : Api() {
    val inherited by get(root / "inherited")
}

private class TypedApi : BaseApi() {
    val items by
        get(root / "items")
            .output(Status.Ok with body(MediaType.Json, itemsFormat))
}

private class InaccessibleApi : Api() {
    internal val hidden by get(root / "hidden")
}

class CompiledApiReaderSpec : FunSpec({
    test("pair runtime endpoints with their exact compiled Kotlin types") {
        val compiled = CompiledApiReader.read(TypedApi())

        compiled.endpoints.map { endpoint -> endpoint.value.id } shouldContainExactly
            listOf("TypedApi.inherited", "TypedApi.items")

        val endpointType = compiled.endpoints[1].type
        endpointType.classifier shouldBe KotlinClassClassifier("dev.akif.tapik.Endpoint")

        val outputs = endpointType.argumentType(4)
        outputs.classifier shouldBe KotlinClassClassifier("dev.akif.tapik.Tuple1")

        val output = outputs.argumentType(1)
        val bodies = output.argumentType(1)
        val body = bodies.argumentType(1)
        val list = body.argumentType(0)
        list.classifier shouldBe KotlinClassClassifier("kotlin.collections.List")

        val item = list.argumentType(0)
        item.classifier shouldBe KotlinClassClassifier("dev.akif.tapik.plugin.core.Item")
        item.nullable shouldBe true
    }

    test("retain source type aliases as abbreviations") {
        val endpointType = CompiledApiReader.read(TypedApi()).endpoints[1].type
        val outputs = endpointType.argumentType(4)

        outputs.abbreviation?.classifier shouldBe KotlinTypeAliasClassifier("dev.akif.tapik.Outputs1")
    }

    test("reject endpoint properties that generated targets cannot access") {
        val failure = shouldThrow<CompiledApiInspectionException> {
            CompiledApiReader.read(InaccessibleApi())
        }

        requireNotNull(failure.message) shouldContain
            "Endpoint property 'InaccessibleApi.hidden' must be public for generated targets"
    }
})

private fun KotlinType.argumentType(index: Int): KotlinType =
    (arguments[index] as KotlinTypedProjection).type
