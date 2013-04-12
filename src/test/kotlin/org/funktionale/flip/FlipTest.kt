package org.funktionale.flip

import org.testng.annotations.Test
import org.testng.Assert.*
import org.funktionale.currying.*

public class FlipTest {
    [Test] fun testFliping() {
        val getXtimes = {(message: String, x: Int) ->
            val builder = StringBuilder()
            (1..x).forEach { builder.append(message) }
            builder.toString()
        }

        assertEquals(getXtimes("foo", 3), "foofoofoo")

        val curriedAndFliped = getXtimes.curried().flip()

        assertEquals(curriedAndFliped(3)("foo"), "foofoofoo")
    }
}