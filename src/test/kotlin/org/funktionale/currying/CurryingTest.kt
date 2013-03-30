package org.funktionale.currying

import org.testng.annotations.Test
import org.testng.Assert.*

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 29/03/13
 * Time: 22:26
 */
public class CurryingTest {
    [Test] fun testCurrying() {
        val sum2ints = {(x: Int, y: Int)-> x + y }
        val curried = sum2ints.curried()
        assertEquals(curried(2)(4), 6)
        val add5 = curried(5)
        assertEquals(add5(7), 12)
    }
}