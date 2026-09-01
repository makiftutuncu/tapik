package dev.akif.tapik.format.kotlinx

import dev.akif.tapik.*
import dev.akif.tapik.common.format.ByteArrayFormatConformanceCase
import dev.akif.tapik.common.format.SchemaDerivationException
import dev.akif.tapik.common.format.includeByteArrayFormatConformance
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

class PolymorphicJsonFormatSpec : FunSpec({
    includeByteArrayFormatConformance(
        ByteArrayFormatConformanceCase(
            name = "sealed Kotlin serialization JSON",
            format = { jsonFormat<LibraryItem>(libraryJson) },
            value = LibraryItem.Book("The Dispossessed"),
            encoded = "{\"kind\":\"book\",\"title\":\"The Dispossessed\"}",
            schema = libraryItemSchema
        )
    )

    includeByteArrayFormatConformance(
        ByteArrayFormatConformanceCase(
            name = "open Kotlin serialization JSON",
            format = { jsonFormat<LibraryEvent>(libraryEventJson) },
            value = LibraryEvent.Borrowed("book-1"),
            encoded = "{\"event\":\"borrowed\",\"bookId\":\"book-1\"}",
            schema = libraryEventSchema
        )
    )

    test("retain recursive polymorphic values as references") {
        val schema = jsonFormat<LibraryNode>().schema.shouldBeInstanceOf<UnionSchema>()
        val branch = schema.alternatives.first().shouldBeInstanceOf<ObjectSchema>()
        val children = branch.properties.getValue("children").schema.shouldBeInstanceOf<ArraySchema>()

        children.items shouldBe ReferenceSchema("library-node")
    }

    @OptIn(ExperimentalSerializationApi::class)
    test("fail format construction for unbounded or unsupported polymorphism") {
        val defaulted =
            Json {
                serializersModule =
                    SerializersModule {
                        polymorphic(LibraryEvent::class) {
                            subclass(LibraryEvent.Borrowed::class)
                            defaultDeserializer { LibraryEvent.Returned.serializer() }
                        }
                    }
            }
        val arrays =
            Json {
                serializersModule = libraryEventModule
                useArrayPolymorphism = true
            }
        val none =
            Json {
                serializersModule = libraryEventModule
                classDiscriminatorMode = ClassDiscriminatorMode.NONE
            }
        val allObjects =
            Json {
                serializersModule = libraryEventModule
                classDiscriminatorMode = ClassDiscriminatorMode.ALL_JSON_OBJECTS
            }

        shouldThrow<SchemaDerivationException> { jsonFormat<LibraryEvent>() }
        shouldThrow<SchemaDerivationException> { jsonFormat<LibraryEvent>(defaulted) }
        shouldThrow<SchemaDerivationException> { jsonFormat<LibraryEvent>(arrays) }
        shouldThrow<SchemaDerivationException> { jsonFormat<LibraryEvent>(none) }
        shouldThrow<SchemaDerivationException> { jsonFormat<LibraryEvent>(allObjects) }
        shouldThrow<SchemaDerivationException> { jsonFormat<PlainItem>(allObjects) }
        shouldThrow<SchemaDerivationException> { jsonFormat<ConflictingItem>() }
    }
})

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@SerialName("library-item")
@JsonClassDiscriminator("kind")
private sealed interface LibraryItem {
    @Serializable
    @SerialName("book")
    data class Book(
        val title: String
    ) : LibraryItem

    @Serializable
    @SerialName("magazine")
    data class Magazine(
        val issue: Int
    ) : LibraryItem
}

private interface LibraryEvent {
    @Serializable
    @SerialName("borrowed")
    data class Borrowed(
        val bookId: String
    ) : LibraryEvent

    @Serializable
    @SerialName("returned")
    data class Returned(
        val bookId: String
    ) : LibraryEvent
}

@Serializable
@SerialName("library-node")
private sealed interface LibraryNode {
    @Serializable
    @SerialName("branch")
    data class Branch(
        val children: List<LibraryNode>
    ) : LibraryNode

    @Serializable
    @SerialName("leaf")
    data class Leaf(
        val value: String
    ) : LibraryNode
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("kind")
private sealed interface ConflictingItem {
    @Serializable
    data class Book(
        val kind: String
    ) : ConflictingItem
}

@Serializable
private data class PlainItem(
    val value: String
)

private val libraryJson = Json { classDiscriminator = "ignored" }

private val libraryEventModule =
    SerializersModule {
        polymorphic(LibraryEvent::class) {
            subclass(LibraryEvent.Returned::class)
            subclass(LibraryEvent.Borrowed::class)
        }
    }

private val libraryEventJson =
    Json {
        serializersModule = libraryEventModule
        classDiscriminator = "event"
    }

private val libraryItemSchema =
    UnionSchema(
        alternatives =
            listOf(
                polymorphicObject("book", "kind", "title", ScalarSchema(SchemaType.STRING)),
                polymorphicObject("magazine", "kind", "issue", ScalarSchema(SchemaType.INTEGER, "int32"))
            ),
        discriminator = discriminator("kind", "book", "magazine"),
        name = "library-item"
    )

private val libraryEventSchema =
    UnionSchema(
        alternatives =
            listOf(
                polymorphicObject("borrowed", "event", "bookId", ScalarSchema(SchemaType.STRING)),
                polymorphicObject("returned", "event", "bookId", ScalarSchema(SchemaType.STRING))
            ),
        discriminator = discriminator("event", "borrowed", "returned"),
        name = "dev.akif.tapik.format.kotlinx.LibraryEvent"
    )

private fun polymorphicObject(
    name: String,
    discriminator: String,
    property: String,
    propertySchema: Schema
): ObjectSchema =
    ObjectSchema(
        properties =
            linkedMapOf(
                discriminator to SchemaProperty(EnumSchema(listOf(name)), required = true),
                property to SchemaProperty(propertySchema, required = true)
            ),
        name = name
    )

private fun discriminator(
    propertyName: String,
    first: String,
    second: String
): SchemaDiscriminator =
    SchemaDiscriminator(
        propertyName = propertyName,
        mapping =
            linkedMapOf(
                first to ReferenceSchema(first),
                second to ReferenceSchema(second)
            )
    )
