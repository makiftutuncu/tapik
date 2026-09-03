package dev.akif.tapik.target.openapi

import dev.akif.tapik.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class OpenApiSchemaAuditSpec : FunSpec({
    test("retain named reference aliases and resolve their targets") {
        val schemas = SchemaRegistry(OpenApiComponentNaming.Simple)

        schemas.schema(ReferenceSchema("model.Value").named("model.Alias")).reference shouldBe
            "#/components/schemas/Alias"
        schemas.schema(ScalarSchema(SchemaType.STRING, name = "model.Value"))
        schemas.requireResolvedReferences()

        schemas.components.getValue("Alias").reference shouldBe "#/components/schemas/Value"
        schemas.components.getValue("Value").types shouldBe listOf("string")
    }

    test("normalize a reference alias with the same name without creating a self reference") {
        val schemas = SchemaRegistry(OpenApiComponentNaming.Simple)

        schemas.schema(ReferenceSchema("model.Value").named("Value")).reference shouldBe
            "#/components/schemas/Value"
        schemas.schema(ScalarSchema(SchemaType.STRING, name = "model.Value"))
        schemas.requireResolvedReferences()
        schemas.components.keys shouldBe setOf("Value")
        schemas.components.getValue("Value").reference shouldBe null
    }

    test("reject unresolved alias targets and conflicting alias definitions") {
        val schemas = SchemaRegistry(OpenApiComponentNaming.Simple)
        schemas.schema(ReferenceSchema("Missing").named("Alias"))
        shouldThrow<OpenApiGenerationException> { schemas.requireResolvedReferences() }
            .message shouldContain "Missing"
        shouldThrow<OpenApiGenerationException> { schemas.schema(ScalarSchema(SchemaType.STRING, name = "Alias")) }
            .message shouldContain "conflicting"
    }

    test("require a default mapping when a discriminator property is optional") {
        val schemas = SchemaRegistry(OpenApiComponentNaming.Simple)
        schemas.schema(
            UnionSchema(
                listOf(discriminatorObject("Required", required = true), discriminatorObject("Optional", required = false)),
                discriminator = SchemaDiscriminator("kind")
            )
        )

        shouldThrow<OpenApiGenerationException> { schemas.requireResolvedReferences() }.message.let { message ->
            message shouldContain "kind"
            message shouldContain "Optional"
            message shouldContain "defaultMapping"
        }
    }

    test("check required discriminator properties after resolving forward references and aliases") {
        val schemas = SchemaRegistry(OpenApiComponentNaming.Simple)
        val alias = ReferenceSchema("First").named("Alias")
        val union = schemas.schema(
            UnionSchema(
                listOf(alias, ReferenceSchema("Second")),
                discriminator = SchemaDiscriminator("kind", mapping = mapOf("first" to alias))
            )
        )
        schemas.schema(discriminatorObject("First", required = true))
        schemas.schema(discriminatorObject("Second", required = true))
        schemas.requireResolvedReferences()
        union.discriminator?.mapping shouldBe mapOf("first" to "#/components/schemas/Alias")
    }

    test("allow an optional discriminator with an explicit default mapping") {
        val schemas = SchemaRegistry(OpenApiComponentNaming.Simple)
        schemas.schema(
            UnionSchema(
                listOf(discriminatorObject("Required", required = true), discriminatorObject("Optional", required = false)),
                discriminator = SchemaDiscriminator("kind", defaultMapping = ReferenceSchema("Optional"))
            )
        )
        schemas.requireResolvedReferences()
    }
})

private fun discriminatorObject(name: String, required: Boolean): ObjectSchema =
    ObjectSchema(
        properties = mapOf("kind" to SchemaProperty(EnumSchema(listOf(name)), required = required)),
        name = name
    )
