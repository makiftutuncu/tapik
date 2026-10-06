package dev.akif.tapik.test.fixtures.documentation

import dev.akif.tapik.ObjectSchema
import dev.akif.tapik.ScalarSchema
import dev.akif.tapik.SchemaType
import dev.akif.tapik.format.jackson.JacksonSchemaRegistry
import dev.akif.tapik.format.jackson.jsonBody
import dev.akif.tapik.test.fixtures.library.Book
import dev.akif.tapik.test.fixtures.library.BookId
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.net.URI

class JacksonFormatExamplesSpec : FunSpec({
    test("configure recursive Jackson schemas") {
        val mapper = jacksonObjectMapper()

        // tag::jackson-schema-registry[]
        val registry = JacksonSchemaRegistry.Default
            .withSchema<BookId>(ScalarSchema(SchemaType.STRING, name = "BookId"))
            .withSchema(URI::class.java, ScalarSchema(SchemaType.STRING, format = "uri"))

        val bookBody = jsonBody<Book>(format = mapper, registry = registry)
        // end::jackson-schema-registry[]

        val schema = bookBody.format.schema.shouldBeInstanceOf<ObjectSchema>()
        schema.properties.getValue("id").schema shouldBe
            ScalarSchema(SchemaType.STRING, name = "BookId")
    }
})
