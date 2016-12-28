/*
 * Copyright 2013 - 2016 Mario Arias
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.funktionale.utils

import org.testng.Assert.assertEquals
import org.testng.annotations.Test

class UtilTest {

    private val add5 = { i: Int -> i + 5 }
    private val multiplyBy2 = { i: Int -> i * 2 }

    private fun applyTwoFunctions(i: Int, firstFunction: (Int) -> Int, secondFunction: (Int) -> Int): Int {
        val x = firstFunction(i)
        return secondFunction(x)
    }

    @Test fun testIdentity() {

        assertEquals(applyTwoFunctions(2, add5, multiplyBy2), 14)

        assertEquals(applyTwoFunctions(2, add5, identity()), 7)

        assertEquals(applyTwoFunctions(2, identity(), identity()), 2)
    }

    @Test fun testConstant() {

        assertEquals(applyTwoFunctions(2, add5, constant(1)), 1)

        val list = arrayListOf("foo", "bar", "baz")

        assertEquals(list.map(constant(7)), arrayListOf(7, 7, 7))
    }

    val Greeter.receive: SetterOperation<String, String>
        get() {
            return SetterOperationImpl { k, v ->
                this.receiveHello(k, v)
            }
        }

    val Greeter.sayHello: GetterOperation<String, String>
        get() {
            return GetterOperationImpl { k ->
                this.sayHelloTo(k)
            }
        }

    @Test fun testGetterAndSetterOperations() {
        val greeter = Greeter()

        //Test Setter
        greeter.receive["Hola"] = "Mario"
        assertEquals("Hola from Mario", greeter.getReceivedHello())
        assertEquals("Hello Mario", greeter.sayHello["Mario"])

    }
}


class Greeter {

    var hello: String? = null
    var name: String? = null

    fun receiveHello(hello: String, name: String) {
        this.hello = hello
        this.name = name
    }

    fun getReceivedHello(): String {
        return "$hello from $name"
    }

    fun sayHelloTo(name: String): String {
        return "Hello $name"
    }


}

