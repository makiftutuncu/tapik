package dev.akif.tapik.format.kotlinx

import dev.akif.tapik.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDate

class KotlinxSchemaRegistrySpec : FunSpec({
    test("derive standard Java time schemas when serializers identify the types") {
        val dateFormat = jsonFormat(Json.Default, LocalDateSerializer)
        val instantFormat = jsonFormat(Json.Default, InstantSerializer)
        val date = LocalDate.parse("2026-09-20")
        val instant = Instant.parse("2026-09-20T12:34:56Z")

        dateFormat.schema shouldBe ScalarSchema(SchemaType.STRING, "date")
        instantFormat.schema shouldBe ScalarSchema(SchemaType.STRING, "date-time")
        dateFormat.decode(dateFormat.encode(date)) shouldBe DecodeResult.Success(date)
        instantFormat.decode(instantFormat.encode(instant)) shouldBe DecodeResult.Success(instant)

        deriveSchema(ListSerializer(LocalDateSerializer.nullable)) shouldBe
            ArraySchema(NullableSchema(ScalarSchema(SchemaType.STRING, "date")))
        deriveSchema(MapSerializer(String.serializer(), InstantSerializer)) shouldBe
            MapSchema(
                ScalarSchema(SchemaType.STRING),
                ScalarSchema(SchemaType.STRING, "date-time")
            )

        val objectSchema = jsonFormat<TemporalValues>().schema.shouldBeInstanceOf<ObjectSchema>()
        objectSchema.properties.getValue("date").schema shouldBe ScalarSchema(SchemaType.STRING, "date")
        objectSchema.properties.getValue("instant").schema shouldBe ScalarSchema(SchemaType.STRING, "date-time")
    }

    test("apply fixed custom schemas recursively and retain nullability") {
        val wireIdSchema = ScalarSchema(SchemaType.STRING, "wire-id", "WireId")
        val registry = KotlinxSchemaRegistry.Default.withSchema<RegisteredWireId>(wireIdSchema)
        val format = jsonFormat<RegisteredValues>(registry = registry)
        val schema = format.schema.shouldBeInstanceOf<ObjectSchema>()

        schema.properties.getValue("id").schema shouldBe wireIdSchema
        schema.properties.getValue("optionalId").schema shouldBe NullableSchema(wireIdSchema)
        schema.properties.getValue("ids").schema shouldBe ArraySchema(wireIdSchema)
        schema.properties.getValue("idsByName").schema shouldBe
            MapSchema(ScalarSchema(SchemaType.STRING), wireIdSchema)
        jsonBody<RegisteredValues>(registry = registry).format.schema shouldBe schema

        val value =
            RegisteredValues(
                id = RegisteredWireId("book-1"),
                optionalId = null,
                ids = listOf(RegisteredWireId("book-2")),
                idsByName = mapOf("featured" to RegisteredWireId("book-3"))
            )
        format.decode(format.encode(value)) shouldBe DecodeResult.Success(value)
    }

    test("invoke schema providers with the resolved descriptor") {
        val serializer = Page.serializer(RegisteredWireIdSerializer)
        var providedSerialName: String? = null
        val registry =
            KotlinxSchemaRegistry.Default.withSchemaProvider(serializer) { descriptor ->
                providedSerialName = descriptor.serialName
                ScalarSchema(SchemaType.STRING, "page")
            }

        jsonFormat(Json.Default, serializer, registry = registry).schema shouldBe
            ScalarSchema(SchemaType.STRING, "page")
        providedSerialName shouldBe serializer.descriptor.serialName
    }

    test("isolate cached formats between schema registries") {
        val firstSchema = ScalarSchema(SchemaType.STRING, name = "FirstWireId")
        val secondSchema = ScalarSchema(SchemaType.STRING, name = "SecondWireId")
        val first = KotlinxSchemaRegistry.Default.withSchema<RegisteredWireId>(firstSchema)
        val second = KotlinxSchemaRegistry.Default.withSchema<RegisteredWireId>(secondSchema)

        val firstFormat = jsonFormat<RegisteredWireId>(registry = first)
        val secondFormat = jsonFormat<RegisteredWireId>(registry = second)

        firstFormat.schema shouldBe firstSchema
        secondFormat.schema shouldBe secondSchema
        firstFormat shouldNotBeSameInstanceAs secondFormat
        jsonFormat<RegisteredWireId>(registry = first) shouldBeSameInstanceAs firstFormat
        jsonFormat<RegisteredWireId>(
            registry = KotlinxSchemaRegistry().withSchema<RegisteredWireId>(firstSchema)
        )
            .shouldNotBeSameInstanceAs(firstFormat)
        deriveSchema(RegisteredWireIdSerializer, first) shouldBe firstSchema

        val explicitRoot = ScalarSchema(SchemaType.INTEGER)
        jsonFormat<RegisteredWireId>(schema = explicitRoot, registry = first).schema shouldBe explicitRoot
    }
})

@Serializable
private data class TemporalValues(
    @Serializable(with = LocalDateSerializer::class)
    val date: LocalDate,
    @Serializable(with = InstantSerializer::class)
    val instant: Instant
)

@Serializable(with = RegisteredWireIdSerializer::class)
private data class RegisteredWireId(
    val value: String
)

private object RegisteredWireIdSerializer : StringValueSerializer<RegisteredWireId>(
    serialName = RegisteredWireId::class.qualifiedName!!,
    decode = ::RegisteredWireId,
    encode = RegisteredWireId::value
)

@Serializable
private data class RegisteredValues(
    val id: RegisteredWireId,
    val optionalId: RegisteredWireId?,
    val ids: List<RegisteredWireId>,
    val idsByName: Map<String, RegisteredWireId>
)

@Serializable
private data class Page<Value>(
    val values: List<Value>
)

private object LocalDateSerializer : StringValueSerializer<LocalDate>(
    serialName = LocalDate::class.java.name,
    decode = LocalDate::parse,
    encode = LocalDate::toString
)

private object InstantSerializer : StringValueSerializer<Instant>(
    serialName = Instant::class.java.name,
    decode = Instant::parse,
    encode = Instant::toString
)

private abstract class StringValueSerializer<Value : Any>(
    serialName: String,
    private val decode: (String) -> Value,
    private val encode: (Value) -> String
) : KSerializer<Value> {
    final override val descriptor = PrimitiveSerialDescriptor(serialName, PrimitiveKind.STRING)

    final override fun deserialize(decoder: Decoder): Value = decode(decoder.decodeString())

    final override fun serialize(encoder: Encoder, value: Value) {
        encoder.encodeString(encode(value))
    }
}
