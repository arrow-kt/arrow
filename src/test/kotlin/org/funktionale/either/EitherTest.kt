/*
 * Copyright 2013 Mario Arias
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

package org.funktionale.either

import org.testng.annotations.Test
import org.testng.Assert.*
import org.funktionale.option.*

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 17/05/13
 * Time: 21:53
 */
public class EitherTest {

    val pair = 5 to "kotlin"
    val left = pair.toLeft()
    val right = pair.toRight()

    [Test] fun get() {
        assertEquals(left.left().get(), 5)
        assertEquals(right.right().get(), "kotlin")
    }

    //[Test(expectedExceptions = array(javaClass<NoSuchElementException>()))]

    [Test] fun getWithException() {
        try {
            assertEquals(right.left().get(), 5)
            fail()
        } catch(e: Exception) {
            //expected
        }
        try {
            assertEquals(left.right().get(), "kotlin")
            fail()
        } catch(e: Exception) {
            //Expected
        }
    }

    [Test] fun forEach() {
        assertEquals(left.left().forEach { it * 2 }, 10)
        assertEquals(right.right().forEach { it.length() }, 6)
    }

    [Test] fun getOrElse() {
        assertEquals(left.left().getOrElse { 2 }, 5)
        assertEquals(left.right().getOrElse { "java" }, "java")
    }

    [Test] fun exists() {
        assertTrue(left.left().exists { it == 5 })
        assertFalse(left.right().exists { it.equals("kotlin") })
    }

    [Test] fun flatMap() {
        assertEquals(left.left().flatMap { Left(it.toString()) }.left().get(), "5")
        assertEquals(right.right().flatMap { Right(it.length()) }.right().get(), 6)
    }

    [Test] fun map() {
        assertEquals(left.left().map { it.toString() }.left().get(), "5")
        assertEquals(right.right().map { it.length() }.right().get(), 6)
    }

    [Test] fun filter() {
        assertEquals(left.left().filter { it == 5 }.get().left().get(), 5)
        assertEquals(left.left().filter { it == 6 }, None<String>())
        assertEquals(right.right().filter { it.startsWith('k') }.get().right().get(), "kotlin")
        assertEquals(right.right().filter { it.startsWith('j') }, None<Int>())
    }

    [Test] fun toList() {
        assertEquals(left.left().toList(), listOf(5))
        assertEquals(left.right().toList(), listOf())
    }

    [Test] fun toOption() {
        assertEquals(left.left().toOption().get(), 5)
        assertEquals(left.right().toOption(), None<String>())
    }

    [Test] fun fold() {
        assertEquals(left.fold({ it.toString() }, { it }), "5")
    }

    [Test] fun swap() {
        assertEquals(left.swap().right().get(), 5)
        assertEquals(right.swap().left().get(), "kotlin")
    }

    [Test] fun components() {
        val (aInt, aNullString) = left
        assertNotNull(aInt)
        assertNull(aNullString)

        val (aNullInt, aString) = right
        assertNull(aNullInt)
        assertNotNull(aString)
    }

    [Test] fun merge() {
        assertEquals(left.merge(), 5)
        assertEquals(right.merge(), "kotlin")
    }

    [Test] fun either(){
        val e = either {
            throw RuntimeException()
        }
        assertTrue(e.isLeft())

    }
}