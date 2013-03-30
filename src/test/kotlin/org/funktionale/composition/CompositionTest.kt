package org.funktionale.composition

import org.testng.annotations.Test
import org.testng.Assert.*


public class CompositionTest {


    private val add5 = {(i: Int)-> i + 5 }
    private val multiplyBy2 = {(i: Int)-> i * 2 }

    [Test] fun testAndThen() {
        val add5andMultiplyBy2 = add5 andThen multiplyBy2
        assertEquals(add5andMultiplyBy2(2), 14)
    }

    [Test] fun testCompose() {
        val multiplyBy2andAdd5 = add5 compose multiplyBy2
        assertEquals(multiplyBy2andAdd5(2), 9)
    }
}