package org.funktionale.pipe

import org.junit.Assert.assertEquals
import org.junit.Test


class pipeTest {


    private val values = listOf(1, "String", 10.2)
    private val intFunctions = listOf({ x: Int -> x }, { x: Int -> x * x })

    fun <T> identity(value: T): T = value

    @Test fun testPipe() {

        values.forEach {
            assertEquals(it pipe { identity(it) }, it)
        }

        intFunctions.forEach {
            assertEquals(it(2), 2 pipe it)
        }
    }
}