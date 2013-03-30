package org.funktionale.partials

import org.testng.annotations.Test
import org.testng.Assert.*

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 29/03/13
 * Time: 21:35
 */
public class PartialsTest {
    [Test] fun partially() {
        val sum5ints = {(a: Int, b: Int, c: Int, d: Int, e: Int) -> a + b + c + d + e }

        val sum4intsTo10 = sum5ints.partially5(10)

        val sum3intsTo15 = sum4intsTo10.partially4(5)

        val sum2intsTo17 = sum3intsTo15.partially3(2)

        assertEquals(sum2intsTo17(1, 2), 20)

        val prefixAndPostfix = {(prefix: String, x: String, postfix: String) -> "${prefix}${x}${postfix}" }

        val helloX = prefixAndPostfix.partially1("Hello, ").partially2("!")

        assertEquals(helloX("funKTionale"), "Hello, funKTionale!")
    }
}