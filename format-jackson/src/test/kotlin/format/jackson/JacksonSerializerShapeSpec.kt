package dev.akif.tapik.format.jackson

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.annotation.JsonCreator
import dev.akif.tapik.*
import dev.akif.tapik.common.format.SchemaDerivationException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import tools.jackson.core.JsonGenerator
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueSerializer
import tools.jackson.databind.annotation.JsonSerialize
import tools.jackson.databind.module.SimpleModule
import tools.jackson.databind.ser.std.ToStringSerializer
import tools.jackson.module.kotlin.jacksonObjectMapper
import tools.jackson.module.kotlin.jsonMapper
import tools.jackson.module.kotlin.kotlinModule
import java.time.LocalDate
import kotlin.reflect.typeOf

class JacksonSerializerShapeSpec : FunSpec({
    test("reject JsonValue wrappers instead of inventing object schemas") {
        jacksonObjectMapper().writeValueAsString(WireId("book-1")) shouldBe "\"book-1\""
        shouldThrow<SchemaDerivationException> { jsonFormat<WireId>() }
    }

    test("reject property and content serializers with property context") {
        val mapper = jacksonObjectMapper()
        mapper.writeValueAsString(PropertyOverride("book")) shouldBe "{\"value\":4}"
        mapper.writeValueAsString(ContentOverride(listOf("book"))) shouldBe "{\"values\":[4]}"
        shouldThrow<SchemaDerivationException> { jsonFormat<PropertyOverride>() }.message shouldContain "value"
        shouldThrow<SchemaDerivationException> { jsonFormat<ContentOverride>() }.message shouldContain "values"
    }

    test("reject class serializers") {
        jacksonObjectMapper().writeValueAsString(ClassOverride("book")) shouldBe "4"
        shouldThrow<SchemaDerivationException> { jsonFormat<ClassOverride>() }
    }

    test("reject registered serializers at root and nested positions") {
        val mapper = jsonMapper {
            addModule(kotlinModule())
            addModule(SimpleModule().addSerializer(ModuleValue::class.java, ModuleValueSerializer()))
        }
        mapper.writeValueAsString(ModuleValue("book")) shouldBe "\"book\""
        shouldThrow<SchemaDerivationException> { jsonFormat<ModuleValue>(mapper) }
        shouldThrow<SchemaDerivationException> { jsonFormat<List<ModuleValue>>(mapper) }
        shouldThrow<SchemaDerivationException> { jsonFormat<ModuleContainer>(mapper) }
    }

    test("reject registered scalar serializers before primitive schema shortcuts") {
        val mapper = jsonMapper {
            addModule(kotlinModule())
            addModule(
                SimpleModule()
                    .addSerializer(String::class.java, StringLengthSerializer())
                    .addSerializer(LocalDate::class.java, LocalDateEpochDaySerializer())
            )
        }
        mapper.writeValueAsString("book") shouldBe "4"
        mapper.writeValueAsString(LocalDate.ofEpochDay(1)) shouldBe "1"
        shouldThrow<SchemaDerivationException> { jsonFormat<String>(mapper) }
        shouldThrow<SchemaDerivationException> { jsonFormat<List<String>>(mapper) }
        shouldThrow<SchemaDerivationException> { jsonFormat<LocalDate>(mapper) }
        shouldThrow<SchemaDerivationException> { jsonFormat<List<LocalDate>>(mapper) }
    }

    test("honor serializer overrides supplied through mixins") {
        val mapper = jsonMapper {
            addModule(kotlinModule())
            addMixIn(ModuleValue::class.java, ModuleValueMixin::class.java)
        }
        mapper.writeValueAsString(ModuleValue("book")) shouldBe "\"book\""
        shouldThrow<SchemaDerivationException> { jsonFormat<ModuleValue>(mapper) }.message shouldContain "explicit schema"
    }

    test("reject a standard serializer registered for an incompatible Kotlin shape") {
        val mapper = jsonMapper {
            addModule(kotlinModule())
            addModule(SimpleModule().addSerializer(ModuleValue::class.java, ToStringSerializer.instance))
        }
        mapper.readTree(mapper.writeValueAsBytes(ModuleValue("book"))).isString shouldBe true
        shouldThrow<SchemaDerivationException> { jsonFormat<ModuleValue>(mapper) }
    }

    test("explicit schemas describe JsonValue wire output without changing the codec") {
        val mapper = jacksonObjectMapper()
        val schema = ScalarSchema(SchemaType.STRING, name = "WireId")
        val format = jsonFormat<WireId>(mapper, schema)
        val value = WireId("book-1")

        format.schema shouldBe schema
        mapper.readTree(format.encode(value)).isString shouldBe true
        format.decode(format.encode(value)) shouldBe DecodeResult.Success(value)
        jsonBody<WireId>(mapper, schema = schema).format.schema shouldBe schema
        jsonFormat<WireId>(mapper, typeOf<WireId>(), schema).schema shouldBe schema
        shouldThrow<SchemaDerivationException> { jsonFormat<WireId>(mapper) }
    }

    test("explicit object and scalar schemas agree with custom serializer output") {
        val mapper = jacksonObjectMapper()
        val objectSchema = ObjectSchema(mapOf("value" to SchemaProperty(ScalarSchema(SchemaType.INTEGER), required = true)))
        val propertyFormat = jsonFormat<PropertyOverride>(mapper, objectSchema)
        val encoded = mapper.readTree(propertyFormat.encode(PropertyOverride("book")))
        propertyFormat.schema shouldBe objectSchema
        encoded.isObject shouldBe true
        encoded.get("value").isIntegralNumber shouldBe true

        val classFormat = jsonFormat<ClassOverride>(schema = ScalarSchema(SchemaType.INTEGER))
        mapper.readTree(classFormat.encode(ClassOverride("book"))).isIntegralNumber shouldBe true
    }

    test("explicit schemas never pollute the inferred format cache") {
        val mapper = jacksonObjectMapper()
        val inferred = jsonFormat<ModuleValue>(mapper)
        inferred.schema.shouldBeInstanceOf<ObjectSchema>()
        val first = inferred.schema.named("First")
        val second = inferred.schema.named("Second")
        jsonFormat<ModuleValue>(mapper, first).schema shouldBe first
        jsonFormat<ModuleValue>(mapper, second).schema shouldBe second
        jsonFormat<ModuleValue>(mapper) shouldBeSameInstanceAs inferred
    }

    test("retain exhaustive enum wire-value derivation") {
        val format = jsonFormat<WireGenre>()
        format.schema.shouldBeInstanceOf<EnumSchema>().values shouldBe listOf("fiction", "history")
        WireGenre.entries.forEach { value ->
            jacksonObjectMapper().readTree(format.encode(value)).stringValue() shouldBe value.wireValue
        }
    }
})

internal data class WireId @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(@get:JsonValue val value: String)

internal data class PropertyOverride(@get:JsonSerialize(using = StringLengthSerializer::class) val value: String)

internal data class ContentOverride(@get:JsonSerialize(contentUsing = StringLengthSerializer::class) val values: List<String>)

@JsonSerialize(using = ClassOverrideSerializer::class)
internal data class ClassOverride(val value: String)

internal data class ModuleValue(val value: String)

internal data class ModuleContainer(val value: ModuleValue)

@JsonSerialize(using = ModuleValueSerializer::class)
internal abstract class ModuleValueMixin

internal enum class WireGenre(@get:JsonValue val wireValue: String) {
    FICTION("fiction"), HISTORY("history")
}

internal class StringLengthSerializer : ValueSerializer<String>() {
    override fun serialize(value: String, generator: JsonGenerator, context: SerializationContext) {
        generator.writeNumber(value.length)
    }
}

internal class LocalDateEpochDaySerializer : ValueSerializer<LocalDate>() {
    override fun serialize(value: LocalDate, generator: JsonGenerator, context: SerializationContext) {
        generator.writeNumber(value.toEpochDay())
    }
}

internal class ClassOverrideSerializer : ValueSerializer<ClassOverride>() {
    override fun serialize(value: ClassOverride, generator: JsonGenerator, context: SerializationContext) {
        generator.writeNumber(value.value.length)
    }
}

internal class ModuleValueSerializer : ValueSerializer<ModuleValue>() {
    override fun serialize(value: ModuleValue, generator: JsonGenerator, context: SerializationContext) {
        generator.writeString(value.value)
    }
}
