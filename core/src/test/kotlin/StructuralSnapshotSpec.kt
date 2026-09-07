package dev.akif.tapik

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs

class StructuralSnapshotSpec : FunSpec({
    test("failure construction and copy snapshot errors and protect destructuring") {
        val error = DecodeError("invalid")
        val errors = mutableListOf(error)
        val failure = DecodeResult.Failure(errors)
        val copy = failure.copy(errors = errors)
        val hash = failure.hashCode()
        errors.clear()
        listOf(failure, copy).forEach {
            runCatching { (it.component1() as MutableList).clear() }
            it.errors shouldBe listOf(error)
            it.errors.single() shouldBeSameInstanceAs error
            it.hashCode() shouldBe hash
        }
        failure shouldBe copy
    }

    test("URI construction copy and destructuring retain ordered segments") {
        val segments = mutableListOf<PathSegment>(PathSegment.Literal("books"), PathSegment.Literal("recent"))
        val uri = Uri(segments, Paths0, Queries0)
        val copy = uri.copy(segments = segments)
        segments.clear()
        listOf(uri, copy).forEach {
            runCatching { (it.component1() as MutableList).clear() }
            it.toString() shouldBe "/books/recent"
        }
        uri shouldBe copy
    }

    test("endpoint tags snapshot every construction and copy boundary") {
        val tags = linkedSetOf("books", "read")
        val api = object : Api("Books") { val list by get(root, tags = tags) }
        val endpoint = api.list.copy(tags = tags)
        val hash = endpoint.hashCode()
        tags.clear()
        runCatching { (endpoint.component7() as MutableSet).clear() }
        runCatching { (api.list.tags as MutableSet).clear() }
        endpoint.tags shouldBe setOf("books", "read")
        api.list.tags shouldBe endpoint.tags
        endpoint.hashCode() shouldBe hash
    }

    test("status sets preserve matching and set equality after attempted mutation") {
        val statuses = linkedSetOf(Status.Ok, Status.Created)
        val matcher = StatusSet(statuses)
        val copy = matcher.copy(statuses = statuses)
        statuses.clear()
        listOf(matcher, copy).forEach {
            runCatching { (it.component1() as MutableSet).clear() }
            it.matches(Status.Ok) shouldBe true
            it.statuses shouldBe setOf(Status.Created, Status.Ok)
        }
        matcher shouldBe copy
    }

    test("domain values are retained by identity rather than copied") {
        val value = mutableListOf("owned by caller")
        DecodeResult.Success(value).value shouldBeSameInstanceAs value
        Tuple1<List<String>, List<String>>(value).values.single() shouldBeSameInstanceAs value
    }

    test("fresh API and tuple views cannot mutate their source structure") {
        val api = object : Api("Books") {
            val list by get(root)
            val create by post(root)
        }
        val endpoints = api.endpoints.toList()
        runCatching { (api.endpoints as MutableList).clear() }
        api.endpoints shouldBe endpoints
        val tuple = (Tuple0 and "first") and "second"
        runCatching { (tuple.values as MutableList)[0] = "changed" }
        tuple.values shouldBe listOf("first", "second")
        tuple._1 shouldBe "first"
    }
})
