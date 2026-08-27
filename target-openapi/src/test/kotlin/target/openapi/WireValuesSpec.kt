package dev.akif.tapik.target.openapi

import dev.akif.tapik.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

class WireValuesSpec : FunSpec({
    test("encode default and fixed values through their wire formats") {
        val accessFormat =
            Format(
                codec =
                    Codec(
                        decoder = Decoder { representation ->
                            DecodeResult.Success(WireAccess.entries.first { it.wireValue == representation })
                        },
                        encoder = Encoder(WireAccess::wireValue)
                    ),
                schema = EnumSchema(listOf("public", "private"))
            )
        val pageFormat = format.int.transform(decode = ::Page, encode = Page::value)
        val nullablePageFormat = pageFormat.copy(schema = NullableSchema(pageFormat.schema))
        val api =
            object : Api("WireValues") {
                val list by
                    get(
                        root / "books" +
                            query("page", pageFormat).optional(Page(1)) +
                            query("pages", pageFormat).repeated().optional(listOf(Page(2), Page(3))) +
                            query("nullablePage", nullablePageFormat).optional(Page(4))
                    ).header(header("X-Access", accessFormat).fixed(WireAccess.PUBLIC))
            }

        val parameters =
            OpenApi.from(api, version = "1")
                .paths
                .getValue("/books")
                .operations
                .getValue(Method.GET)
                .parameters
                .associateBy(OpenApiParameter::name)

        parameters.getValue("page").schema.defaultValue?.jsonPrimitive?.content shouldBe "1"
        parameters.getValue("page").schema.defaultValue?.jsonPrimitive?.isString shouldBe false
        parameters.getValue("pages").schema.defaultValue?.jsonArray?.map { it.jsonPrimitive.content } shouldBe
            listOf("2", "3")
        parameters.getValue("pages").schema.defaultValue?.jsonArray?.all { !it.jsonPrimitive.isString } shouldBe true
        parameters.getValue("nullablePage").schema.defaultValue?.jsonPrimitive?.content shouldBe "4"
        parameters.getValue("X-Access").schema.constantValue?.jsonPrimitive?.content shouldBe "public"
    }
})

@JvmInline
private value class Page(
    val value: Int
)

private enum class WireAccess(
    val wireValue: String
) {
    PUBLIC("public"),
    PRIVATE("private")
}
