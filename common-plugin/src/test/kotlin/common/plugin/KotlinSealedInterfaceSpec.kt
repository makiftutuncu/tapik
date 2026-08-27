package dev.akif.tapik.common.plugin

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class KotlinSealedInterfaceSpec : FunSpec({
    test("render data variants with byte-array value semantics") {
        val source =
            buildString {
                appendSealedInterface(
                    name = "Result",
                    variants =
                        listOf(
                            KotlinSealedVariant("Empty"),
                            KotlinSealedVariant(
                                name = "Found",
                                fields =
                                    listOf(
                                        KotlinDataField("value", sourceType("kotlin.String")),
                                        KotlinDataField("bytes", sourceType("example.Bytes?", "kotlin.ByteArray", true), "null")
                                    )
                            )
                        )
                )
            }

        source shouldBe
            """
            public sealed interface Result {
                public data object Empty : Result
                public data class Found(
                    public val value: kotlin.String,
                    public val bytes: example.Bytes? = null
                ) : Result {
                    override fun equals(other: kotlin.Any?): kotlin.Boolean =
                        this === other ||
                            (
                                other is Found &&
                                    value == other.value &&
                                    bytes?.contentEquals(other.bytes) ?: (other.bytes == null)
                            )

                    override fun hashCode(): kotlin.Int {
                        var result = value.hashCode()
                        result = 31 * result + (bytes?.contentHashCode() ?: 0)
                        return result
                    }
                }
            }
            """.trimIndent() + "\n"
    }
})

private fun sourceType(
    source: String,
    expandedName: String = source,
    nullable: Boolean = false
): KotlinSourceType =
    KotlinSourceType(
        source = source,
        expandedType = KotlinType(KotlinClassClassifier(expandedName), nullable = nullable)
    )
