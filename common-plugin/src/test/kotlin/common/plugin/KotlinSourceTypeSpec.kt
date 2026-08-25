package dev.akif.tapik.common.plugin

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class KotlinSourceTypeSpec : FunSpec({
    test("preserve alias spelling and expanded identity separately") {
        val type =
            KotlinType(
                classifier = KotlinClassClassifier("kotlin.ByteArray"),
                abbreviation = KotlinType(KotlinTypeAliasClassifier("example.BinaryContent"))
            )

        val rendered = type.toKotlinSourceType("Download body")

        rendered.source shouldBe "example.BinaryContent"
        rendered.expandsTo("kotlin.ByteArray") shouldBe true
        rendered.asNullable().run {
            source shouldBe "example.BinaryContent?"
            expandsTo("kotlin.ByteArray") shouldBe true
            expandedType.nullable shouldBe true
            expandedType.abbreviation?.nullable shouldBe true
        }
    }

    test("render outer types and generic projections without flattening") {
        val outer =
            KotlinType(
                classifier = KotlinClassClassifier("example.Container"),
                arguments =
                    listOf(
                        KotlinTypedProjection(
                            KotlinVariance.INVARIANT,
                            KotlinType(KotlinClassClassifier("kotlin.String"))
                        )
                    )
            )
        val inner =
            KotlinType(
                classifier = KotlinClassClassifier("example.Container.Value"),
                arguments =
                    listOf(
                        KotlinStarProjection,
                        KotlinTypedProjection(
                            KotlinVariance.OUT,
                            KotlinType(KotlinClassClassifier("kotlin.Int"), nullable = true)
                        )
                    ),
                outerType = outer
            )

        inner.toKotlinSourceType("Nested value").source shouldBe
            "example.Container<kotlin.String>.Value<*, out kotlin.Int?>"
    }

    test("reject source shapes that cannot be reproduced") {
        val unsupported =
            listOf(
                "flexible Kotlin type" to
                    KotlinType(
                        classifier = KotlinClassClassifier("kotlin.String"),
                        flexibleUpperBound =
                            KotlinFlexibleTypeUpperBound(
                                type = KotlinType(KotlinClassClassifier("kotlin.String"), nullable = true),
                                flexibilityId = "kotlin.jvm.PlatformType"
                            )
                    ),
                "definitely-non-null Kotlin type" to
                    KotlinType(
                        classifier = KotlinTypeParameterClassifier(0),
                        definitelyNonNull = true
                    ),
                "unresolved Kotlin type parameter 1" to KotlinType(KotlinTypeParameterClassifier(1))
            )

        unsupported.forEach { (reason, type) ->
            val failure = shouldThrow<IllegalArgumentException> {
                type.toKotlinSourceType("Books.download response body")
            }

            requireNotNull(failure.message) shouldContain "Books.download response body"
            failure.message shouldContain reason
        }
    }
})
