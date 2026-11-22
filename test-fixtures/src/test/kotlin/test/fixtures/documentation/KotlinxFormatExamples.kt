package dev.akif.tapik.test.fixtures.documentation

import dev.akif.tapik.ObjectSchema
import dev.akif.tapik.ScalarSchema
import dev.akif.tapik.SchemaType
import dev.akif.tapik.format.kotlinx.KotlinxSchemaRegistry
import dev.akif.tapik.format.kotlinx.jsonBody
import dev.akif.tapik.test.fixtures.library.Book
import dev.akif.tapik.test.fixtures.library.BookId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// tag::serializable-model[]
@Serializable
internal data class BookMessage(val text: String)
// end::serializable-model[]

class KotlinxFormatExamplesSpec : FunSpec({
    test("configure recursive Kotlin serialization schemas") {
        // tag::kotlinx-schema-registry[]
        val registry = KotlinxSchemaRegistry.Default
            .withSchema<BookId>(ScalarSchema(SchemaType.STRING, name = "BookId"))

        val bookBody = jsonBody<Book>(format = Json.Default, registry = registry)
        // end::kotlinx-schema-registry[]

        val schema = bookBody.format.schema.shouldBeInstanceOf<ObjectSchema>()
        schema.properties.getValue("id").schema shouldBe
            ScalarSchema(SchemaType.STRING, name = "BookId")
    }
})
