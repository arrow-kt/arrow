package org.funktionale.composition

import org.testng.Assert.assertEquals
import org.testng.annotations.Test
import java.util.Random

class ComposeTest {

    @Test
    fun `it should compose function correctly (andThen)`() {
        val potato = "potato"
        val ninja = "ninja"
        val get = { potato }
        val map = { word: String -> ninja + word }
        assertEquals(ninja + potato, (get andThen map)())
    }

    @Test
    fun `it should compose function correctly (forwardCompose)`() {
        val randomDigit = Random().nextInt()
        val get = { randomDigit }
        val pow = { i: Int -> i * i }
        assertEquals(randomDigit * randomDigit, (get forwardCompose pow)())
    }

}