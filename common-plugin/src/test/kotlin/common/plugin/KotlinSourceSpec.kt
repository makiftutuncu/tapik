package dev.akif.tapik.common.plugin

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class KotlinSourceSpec : FunSpec({
    test("read compiled type arguments and Tapik tuple elements") {
        val string = KotlinType(KotlinClassClassifier("kotlin.String"))
        val integer = KotlinType(KotlinClassClassifier("kotlin.Int"))
        val tuple =
            KotlinType(
                classifier = KotlinClassClassifier("dev.akif.tapik.Tuple2"),
                arguments =
                    listOf(
                        string.invariant(),
                        string.invariant(),
                        integer.invariant()
                    )
            )

        tuple.argument(1, "Example tuple") shouldBe string
        tuple.tupleElements("Example tuple") shouldContainExactly listOf(string, integer)
        KotlinType(KotlinClassClassifier("dev.akif.tapik.Tuple0"))
            .tupleElements("Empty tuple") shouldBe emptyList()
        shouldThrow<IllegalArgumentException> {
            KotlinType(KotlinClassClassifier("example.Star"), listOf(KotlinStarProjection))
                .argument(0, "Star")
        }
    }

    test("sanitize and allocate Kotlin names") {
        "find-book".kotlinIdentifier("endpoint") shouldBe "findBook"
        "fun".isKotlinIdentifier() shouldBe false
        "fun".kotlinIdentifier("endpoint") shouldBe "`fun`"
        "fun".kotlinReferenceIdentifier() shouldBe "`fun`"
        "book-list".upperCamel() shouldBe "BookList"
        "API".lowerCamel("api") shouldBe "aPI"

        val used = mutableSetOf<String>()
        uniqueKotlinName("`fun`", used) shouldBe "`fun`"
        uniqueKotlinName("`fun`", used) shouldBe "fun2"
    }

    test("escape Kotlin string literals") {
        "quote=\" slash=\\ newline=\n dollar=${'$'}".kotlinString() shouldBe
            "\"quote=\\\" slash=\\\\ newline=\\n dollar=\\${'$'}\""
    }
})

private fun KotlinType.invariant(): KotlinTypedProjection =
    KotlinTypedProjection(KotlinVariance.INVARIANT, this)
