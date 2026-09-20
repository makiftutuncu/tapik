package dev.akif.tapik.format.jackson

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import dev.akif.tapik.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import tools.jackson.module.kotlin.jacksonObjectMapper
import java.net.URI
import java.time.*
import kotlin.reflect.typeOf

class JacksonSchemaRegistrySpec : FunSpec({
    test("derive standard Java time schemas at root and nested positions") {
        val dateFormat = jsonFormat<LocalDate>()
        val instantFormat = jsonFormat<Instant>()
        val date = LocalDate.parse("2026-09-20")
        val instant = Instant.parse("2026-09-20T12:34:56Z")

        dateFormat.schema shouldBe ScalarSchema(SchemaType.STRING, "date")
        instantFormat.schema shouldBe ScalarSchema(SchemaType.STRING, "date-time")
        dateFormat.decode(dateFormat.encode(date)) shouldBe DecodeResult.Success(date)
        instantFormat.decode(instantFormat.encode(instant)) shouldBe DecodeResult.Success(instant)

        val schema = jsonFormat<TemporalValues>().schema.shouldBeInstanceOf<ObjectSchema>()
        schema.properties.getValue("date").schema shouldBe ScalarSchema(SchemaType.STRING, "date")
        schema.properties.getValue("instant").schema shouldBe ScalarSchema(SchemaType.STRING, "date-time")
        schema.properties.getValue("localTime").schema shouldBe ScalarSchema(SchemaType.STRING, "time-local")
        schema.properties.getValue("localDateTime").schema shouldBe
            ScalarSchema(SchemaType.STRING, "date-time-local")
        schema.properties.getValue("offsetTime").schema shouldBe ScalarSchema(SchemaType.STRING, "time")
        schema.properties.getValue("offsetDateTime").schema shouldBe ScalarSchema(SchemaType.STRING, "date-time")
        schema.properties.getValue("zonedDateTime").schema shouldBe ScalarSchema(SchemaType.STRING, "date-time")
        schema.properties.getValue("duration").schema shouldBe ScalarSchema(SchemaType.STRING, "duration")
        schema.properties.getValue("period").schema shouldBe ScalarSchema(SchemaType.STRING, "duration")
        schema.properties.getValue("optionalDates").schema shouldBe
            ArraySchema(NullableSchema(ScalarSchema(SchemaType.STRING, "date")))
        schema.properties.getValue("instantsByName").schema shouldBe
            MapSchema(
                keys = ScalarSchema(SchemaType.STRING),
                values = ScalarSchema(SchemaType.STRING, "date-time")
            )
    }

    test("apply fixed custom schemas recursively and retain nullability") {
        val wireIdSchema = ScalarSchema(SchemaType.STRING, "wire-id", "WireId")
        val uriSchema = ScalarSchema(SchemaType.STRING, "uri")
        val registry =
            JacksonSchemaRegistry.Default
                .withSchema<RegisteredWireId>(wireIdSchema)
                .withSchema(URI::class.java, uriSchema)

        val format = jsonFormat<RegisteredValues>(registry = registry)
        val schema = format.schema.shouldBeInstanceOf<ObjectSchema>()

        schema.properties.getValue("id").schema shouldBe wireIdSchema
        schema.properties.getValue("optionalId").schema shouldBe NullableSchema(wireIdSchema)
        schema.properties.getValue("ids").schema shouldBe ArraySchema(wireIdSchema)
        schema.properties.getValue("idsByName").schema shouldBe
            MapSchema(ScalarSchema(SchemaType.STRING), wireIdSchema)
        schema.properties.getValue("homepage").schema shouldBe uriSchema
        jsonBody<RegisteredValues>(registry = registry).format.schema shouldBe schema

        val value =
            RegisteredValues(
                id = RegisteredWireId("book-1"),
                optionalId = null,
                ids = listOf(RegisteredWireId("book-2")),
                idsByName = mapOf("featured" to RegisteredWireId("book-3")),
                homepage = URI("https://tapik.akif.dev")
            )
        format.decode(format.encode(value)) shouldBe DecodeResult.Success(value)
    }

    test("invoke schema providers with the resolved generic Java type") {
        val registry =
            JacksonSchemaRegistry.Default.withSchemaProvider(Page::class) { type ->
                ScalarSchema(
                    type = SchemaType.STRING,
                    format = type.containedType(0).rawClass.simpleName.lowercase()
                )
            }

        jsonFormat<Page<RegisteredWireId>>(registry = registry).schema shouldBe
            ScalarSchema(SchemaType.STRING, "registeredwireid")
    }

    test("isolate cached formats between schema registries") {
        val mapper = jacksonObjectMapper()
        val firstSchema = ScalarSchema(SchemaType.STRING, name = "FirstWireId")
        val secondSchema = ScalarSchema(SchemaType.STRING, name = "SecondWireId")
        val first = JacksonSchemaRegistry.Default.withSchema<RegisteredWireId>(firstSchema)
        val second = JacksonSchemaRegistry.Default.withSchema<RegisteredWireId>(secondSchema)

        val firstFormat = jsonFormat<RegisteredWireId>(mapper, registry = first)
        val secondFormat = jsonFormat<RegisteredWireId>(mapper, registry = second)

        firstFormat.schema shouldBe firstSchema
        secondFormat.schema shouldBe secondSchema
        firstFormat shouldNotBeSameInstanceAs secondFormat
        jsonFormat<RegisteredWireId>(mapper, registry = first) shouldBeSameInstanceAs firstFormat
        jsonFormat<RegisteredWireId>(
            mapper,
            registry = JacksonSchemaRegistry().withSchema<RegisteredWireId>(firstSchema)
        )
            .shouldNotBeSameInstanceAs(firstFormat)
        deriveSchema(mapper, typeOf<RegisteredWireId>(), first) shouldBe firstSchema

        val explicitRoot = ScalarSchema(SchemaType.INTEGER)
        jsonFormat<RegisteredWireId>(mapper, schema = explicitRoot, registry = first).schema shouldBe explicitRoot
    }
})

private data class TemporalValues(
    val date: LocalDate,
    val instant: Instant,
    val localTime: LocalTime,
    val localDateTime: LocalDateTime,
    val offsetTime: OffsetTime,
    val offsetDateTime: OffsetDateTime,
    val zonedDateTime: ZonedDateTime,
    val duration: Duration,
    val period: Period,
    val optionalDates: List<LocalDate?>,
    val instantsByName: Map<String, Instant>
)

private data class RegisteredWireId @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(
    @get:JsonValue
    val value: String
)

private data class RegisteredValues(
    val id: RegisteredWireId,
    val optionalId: RegisteredWireId?,
    val ids: List<RegisteredWireId>,
    val idsByName: Map<String, RegisteredWireId>,
    val homepage: URI
)

private data class Page<Value>(
    val values: List<Value>
)
