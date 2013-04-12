package org.funktionale.utils

import org.testng.annotations.Test
import org.testng.Assert.*

public class UtilTest {

    private val add5 = {(i: Int) -> i + 5 }
    private val multiplyBy2 = {(i: Int)-> i * 2 }

    private fun applyTwoFunctions(i: Int, firstFunction: (Int) -> Int, secondFunction: (Int) -> Int): Int {
        val x = firstFunction(i)
        return secondFunction(x)
    }

    [Test] fun testIdentity() {

        assertEquals(applyTwoFunctions(2, add5, multiplyBy2), 14)

        assertEquals(applyTwoFunctions(2, add5, identity), 7)

        assertEquals(applyTwoFunctions(2, identity, identity), 2)
    }

    [Test] fun testConstant() {

        assertEquals(applyTwoFunctions(2, add5, constant(1)), 1)

        val list = arrayListOf("foo", "bar", "baz")

        assertEquals(list.map(constant(7)), arrayListOf(7, 7, 7))
    }
}