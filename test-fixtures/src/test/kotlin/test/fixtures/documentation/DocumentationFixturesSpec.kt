package dev.akif.tapik.test.fixtures.documentation

import dev.akif.tapik.BodyInput
import dev.akif.tapik.Output
import dev.akif.tapik.id
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class DocumentationFixturesSpec : FunSpec({
    test("preserve API identity and URI parameter structure") {
        val authors = AuthorRoutes()
        val assets = AssetRoutes()

        authors.endpoints.map { endpoint -> endpoint.id } shouldContainExactly
            listOf("AuthorRoutes.list", "AuthorRoutes.get")
        authors.get.uri.toString() shouldBe "/authors/{authorId}"
        assets.search.uri.toString() shouldBe "/assets?name={name}&page={page}&ownerId={ownerId}"
        assets.download.uri.toString() shouldBe "/assets/{*path}"
    }

    test("preserve request and output alternatives") {
        val request = BookRequests().create
        val input = request.input as BodyInput<*>
        val response = BookResponses().create

        request.headers.values.map { header -> header.name } shouldContainExactly
            listOf("X-Request-Id", "X-Source", "X-Trace")
        input.bodies.values.size shouldBe 3
        input.documentation.description shouldBe "A book represented as standard or vendor JSON."
        response.outputs.values.size shouldBe 3
        (response.outputs.values.first() as Output<*, *, *>).documentation.description shouldBe "The created book."
    }

    test("preserve contract documentation and tag set") {
        val create = DocumentedBooks().create

        create.documentation.summary shouldBe "Create a book"
        create.documentation.description shouldBe "Adds a book to the library."
        create.tags shouldBe setOf("books", "write")
        create.headers._1.documentation.description shouldBe "Correlates the request across services."
    }
})
