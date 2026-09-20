package dev.akif.tapik.common.format

import dev.akif.tapik.ScalarSchema
import dev.akif.tapik.SchemaType
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Instant
import java.time.LocalDate

class BuiltInSchemasSpec : FunSpec({
    test("resolve primitives and Java time values from one built-in catalog") {
        builtInSchema(Boolean::class.java) shouldBe ScalarSchema(SchemaType.BOOLEAN)
        builtInSchema("kotlin.Int") shouldBe ScalarSchema(SchemaType.INTEGER, "int32")
        builtInSchema(String::class.java) shouldBe ScalarSchema(SchemaType.STRING)
        builtInSchema(LocalDate::class.java) shouldBe ScalarSchema(SchemaType.STRING, "date")
        builtInSchema(Instant::class.java.name) shouldBe ScalarSchema(SchemaType.STRING, "date-time")
        builtInSchema("example.Unknown") shouldBe null
    }
})
