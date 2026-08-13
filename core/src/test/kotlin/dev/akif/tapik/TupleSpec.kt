package dev.akif.tapik

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe

class TupleSpec : FunSpec({
    test("represent the empty tuple") {
        Tuple0.values.shouldBeEmpty()
    }

    test("append values in declaration order while retaining every concrete type") {
        val tuple: Tuple8<Element, First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth> =
            (Tuple0 and First) and Second and Third and Fourth and Fifth and Sixth and Seventh and Eighth

        tuple._1 shouldBe First
        tuple._2 shouldBe Second
        tuple._3 shouldBe Third
        tuple._4 shouldBe Fourth
        tuple._5 shouldBe Fifth
        tuple._6 shouldBe Sixth
        tuple._7 shouldBe Seventh
        tuple._8 shouldBe Eighth
        tuple.values shouldBe listOf(First, Second, Third, Fourth, Fifth, Sixth, Seventh, Eighth)
    }

    test("use structural value semantics") {
        ((Tuple0 and "first") and "second") shouldBe ((Tuple0 and "first") and "second")
    }
})

private sealed interface Element

private data object First : Element

private data object Second : Element

private data object Third : Element

private data object Fourth : Element

private data object Fifth : Element

private data object Sixth : Element

private data object Seventh : Element

private data object Eighth : Element
