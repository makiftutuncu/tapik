package dev.akif.tapik.target.openapi

import dev.akif.tapik.Method
import dev.akif.tapik.ScalarSchema
import dev.akif.tapik.SchemaType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.*

class OpenApiSnapshotSpec : FunSpec({
    test("published components snapshot the internal registry without freezing its builder") {
        val registry = SchemaRegistry(OpenApiComponentNaming.Simple)
        registry.schema(ScalarSchema(SchemaType.STRING, name = "First"))
        val components = OpenApiComponents(registry.components)

        registry.schema(ScalarSchema(SchemaType.INTEGER, name = "Second"))

        registry.components.keys.toList() shouldBe listOf("First", "Second")
        components.schemas.keys.toList() shouldBe listOf("First")
        runCatching { (components.schemas as MutableMap).clear() }
        components.schemas.keys.toList() shouldBe listOf("First")
    }

    test("snapshot all schema list keywords on construction and copy") {
        val strings = mutableListOf("string")
        val schemas = mutableListOf(OpenApiSchema(reference = "#/components/schemas/Book"))
        val schema = OpenApiSchema(types = strings, enumValues = strings, required = strings, anyOf = schemas, oneOf = schemas)
        val copy = schema.copy(types = strings, enumValues = strings, required = strings, anyOf = schemas, oneOf = schemas)
        strings.clear()
        schemas.clear()
        listOf(schema, copy).forEach {
            listOf(it.types, it.enumValues, it.required, it.component2(), it.component4(), it.component7()).forEach { values ->
                runCatching { (values as MutableList).clear() }
                values shouldBe listOf("string")
            }
            listOf(it.anyOf, it.oneOf, it.component10(), it.component11()).forEach { values ->
                runCatching { (values as MutableList).clear() }
                values.size shouldBe 1
            }
        }
        schema shouldBe copy
    }

    test("snapshot schema component and discriminator maps including copy and map views") {
        val schemas = linkedMapOf("Book" to OpenApiSchema(types = listOf("object")))
        val schema = OpenApiSchema(properties = schemas)
        val schemaCopy = schema.copy(properties = schemas)
        val components = OpenApiComponents(schemas)
        val componentCopy = components.copy(schemas = schemas)
        val mappings = linkedMapOf("book" to "#/components/schemas/Book")
        val discriminator = OpenApiDiscriminator("kind", mappings)
        val discriminatorCopy = discriminator.copy(mapping = mappings)
        schemas.clear()
        mappings.clear()
        listOf(schema.properties, schemaCopy.component6(), components.schemas, componentCopy.component1()).forEach {
            assertProtectedMap(it, "Book")
        }
        listOf(discriminator.mapping, discriminatorCopy.component2()).forEach { assertProtectedMap(it, "book") }
        schema shouldBe schemaCopy
        components shouldBe componentCopy
        discriminator shouldBe discriminatorCopy
    }

    test("snapshot every document operation and response container including copies") {
        val media = linkedMapOf("application/json" to OpenApiMediaType(OpenApiSchema()))
        val headers = linkedMapOf("X-Id" to OpenApiHeader(required = true, deprecated = false, schema = OpenApiSchema()))
        val request = OpenApiRequestBody(required = true, content = media)
        val requestCopy = request.copy(content = media)
        val response = OpenApiResponse("OK", headers, media)
        val responseCopy = response.copy(headers = headers, content = media)
        val responses = linkedMapOf("200" to response)
        val tags = mutableListOf("books")
        val parameters = mutableListOf(OpenApiParameter("id", OpenApiParameterLocation.PATH, required = true, deprecated = false, schema = OpenApiSchema()))
        val operation = OpenApiOperation("Books.list", tags, null, null, parameters, request, responses)
        val operationCopy = operation.copy(tags = tags, parameters = parameters, responses = responses)
        val operations = linkedMapOf(Method.GET to operation)
        val item = OpenApiPathItem(operations)
        val itemCopy = item.copy(operations = operations)
        val paths = linkedMapOf("/books" to item)
        val document = OpenApiDocument(info = OpenApiInfo("Books", "1"), paths = paths)
        val documentCopy = document.copy(paths = paths)
        val hash = document.hashCode()
        media.clear()
        headers.clear()
        responses.clear()
        tags.clear()
        parameters.clear()
        operations.clear()
        paths.clear()
        listOf(request.content, requestCopy.component3(), response.content, responseCopy.component3()).forEach { assertProtectedMap(it, "application/json") }
        listOf(response.headers, responseCopy.component2()).forEach { assertProtectedMap(it, "X-Id") }
        listOf(operation, operationCopy).forEach {
            assertProtectedMap(it.component7(), "200")
            runCatching { (it.component2() as MutableList).clear() }
            runCatching { (it.component5() as MutableList).clear() }
            it.tags shouldBe listOf("books")
            it.parameters.map(OpenApiParameter::name) shouldBe listOf("id")
        }
        listOf(item.operations, itemCopy.component1()).forEach { assertProtectedMap(it, Method.GET) }
        listOf(document.paths, documentCopy.component3()).forEach { assertProtectedMap(it, "/books") }
        document shouldBe documentCopy
        document.hashCode() shouldBe hash
    }

    test("snapshot nested JSON containers used as documented defaults and constants") {
        val children = mutableListOf<JsonElement>(JsonPrimitive("book"))
        val properties = linkedMapOf<String, JsonElement>("items" to JsonArray(children))
        val json = JsonObject(properties)
        val schema = OpenApiSchema(defaultValue = json, constantValue = json)
        val copy = schema.copy(defaultValue = json, constantValue = json)
        children.clear()
        properties.clear()
        listOf(schema, copy).forEach {
            it.defaultValue.toString() shouldBe "{\"items\":[\"book\"]}"
            it.constantValue shouldBe it.defaultValue
        }
    }
})

private fun <Key, Value> assertProtectedMap(map: Map<Key, Value>, key: Key) {
    val expected = map.toMap()
    runCatching { (map as MutableMap).clear() }
    runCatching { (map.keys as MutableSet).clear() }
    runCatching { (map.values as MutableCollection).clear() }
    runCatching { (map.entries as MutableSet).clear() }
    runCatching { (map.entries.first() as MutableMap.MutableEntry).setValue(map.getValue(key)) }
    map shouldBe expected
    map.keys shouldBe setOf(key)
}
