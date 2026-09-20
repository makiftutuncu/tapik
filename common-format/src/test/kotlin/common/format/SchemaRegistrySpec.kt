package dev.akif.tapik.common.format

import dev.akif.tapik.ScalarSchema
import dev.akif.tapik.SchemaType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class SchemaRegistrySpec : FunSpec({
    test("add and replace providers immutably") {
        val empty = SchemaRegistry<String, Int>()
        val first = empty.withProvider("value") { ScalarSchema(SchemaType.STRING, it.toString()) }
        val second = first.withProvider("value") { ScalarSchema(SchemaType.INTEGER, it.toString()) }

        empty.schema("value", 1) shouldBe null
        first.schema("value", 1) shouldBe ScalarSchema(SchemaType.STRING, "1")
        second.schema("value", 2) shouldBe ScalarSchema(SchemaType.INTEGER, "2")
    }
})
