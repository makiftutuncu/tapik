package dev.akif.tapik

import kotlin.test.Test
import kotlin.test.assertEquals

class TupleTest {
    @Test
    fun `plus builds tuple chain`() {
        val tuple1 = Tuple0 + "first"
        val tuple2 = tuple1 + 2

        assertEquals("first", tuple1.item1)
        assertEquals(listOf("first"), tuple1.toList<String>())
        assertEquals(2, tuple2.item2)
        assertEquals(listOf("first", 2), tuple2.toList<Any>())
    }

    @Test
    fun `toList validates element types`() {
        val tuple = Tuple2("first", 2)

        assertEquals(listOf("first"), tuple.toList<String>())
        assertEquals(listOf(2), tuple.toList<Int>())
    }
}
