package dev.akif.tapik

import kotlin.test.Test
import kotlin.test.assertEquals

class OneOfTest {
    @Test
    fun `select handles OneOf2 options`() {
        val option1: OneOf2.Option1<String> = OneOf2.Option1("first")
        val option2: OneOf2.Option2<String> = OneOf2.Option2("second")

        val result1 = option1.select({ value -> "1:$value" }, { value -> "2:$value" })
        val result2 = option2.select({ value -> "1:$value" }, { value -> "2:$value" })

        assertEquals("1:first", result1)
        assertEquals("2:second", result2)
    }

    @Test
    fun `select handles OneOf3 options`() {
        val option1: OneOf3.Option1<Int> = OneOf3.Option1(1)
        val option2: OneOf3.Option2<Int> = OneOf3.Option2(2)
        val option3: OneOf3.Option3<Int> = OneOf3.Option3(3)

        val result1 = option1.select({ "1:$it" }, { "2:$it" }, { "3:$it" })
        val result2 = option2.select({ "1:$it" }, { "2:$it" }, { "3:$it" })
        val result3 = option3.select({ "1:$it" }, { "2:$it" }, { "3:$it" })

        assertEquals("1:1", result1)
        assertEquals("2:2", result2)
        assertEquals("3:3", result3)
    }

    @Test
    fun `select handles OneOf5 options`() {
        val invocations =
            mutableListOf<String>().apply {
                OneOf5.Option1("one").select(
                    {
                        add("1:$it")
                        1
                    },
                    {
                        add("2:$it")
                        2
                    },
                    {
                        add("3:$it")
                        3
                    },
                    {
                        add("4:$it")
                        4
                    },
                    {
                        add("5:$it")
                        5
                    }
                )
                OneOf5.Option3("three").select(
                    {
                        add("1:$it")
                        1
                    },
                    {
                        add("2:$it")
                        2
                    },
                    {
                        add("3:$it")
                        3
                    },
                    {
                        add("4:$it")
                        4
                    },
                    {
                        add("5:$it")
                        5
                    }
                )
                OneOf5.Option5("five").select(
                    {
                        add("1:$it")
                        1
                    },
                    {
                        add("2:$it")
                        2
                    },
                    {
                        add("3:$it")
                        3
                    },
                    {
                        add("4:$it")
                        4
                    },
                    {
                        add("5:$it")
                        5
                    }
                )
            }

        assertEquals(listOf("1:one", "3:three", "5:five"), invocations)
    }
}
