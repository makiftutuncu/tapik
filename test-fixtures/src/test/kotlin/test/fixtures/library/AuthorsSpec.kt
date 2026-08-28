package dev.akif.tapik.test.fixtures.library

import dev.akif.tapik.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class AuthorsSpec : FunSpec({
    test("declare author endpoints in source order") {
        Authors.endpoints.map { it.id } shouldContainExactly
            listOf("Authors.list", "Authors.get", "Authors.create")
        Authors.endpoints.map { it.method } shouldContainExactly
            listOf(Method.GET, Method.GET, Method.POST)
        Authors.endpoints.map { it.uri.toString() } shouldContainExactly
            listOf("/authors?name={name}&page={page}", "/authors/{authorId}", "/authors")
    }

    test("exercise author request and response features") {
        Authors.list.outputs._1.matcher.matches(Status.Ok) shouldBe true
        Authors.get.uri.paths.values.single().format shouldBe authorIdFormat
        Authors.get.outputs.values.size shouldBe 2
        Authors.create.input.shouldBeInstanceOf<BodyInput<*>>()
        Authors.create.outputs.values.size shouldBe 2
        Authors.create.tags shouldBe setOf("authors")
    }
})
