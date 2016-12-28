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

package org.funktionale.either

import org.funktionale.either.Disjunction.Left
import org.funktionale.either.Disjunction.Right
import org.funktionale.option.Option
import org.testng.Assert.*
import org.testng.annotations.Test


class DisjunctionTest {


    val left = Disjunction.left(5)
    val right = Disjunction.right("kotlin")

    @Test fun get() {
        assertEquals(left.swap().get(), 5)
        assertEquals(right.get(), "kotlin")
    }

    //[Test(expectedExceptions = array(javaClass<NoSuchElementException>()))]

    @Test fun getWithException() {
        try {
            assertEquals(right.swap().get(), 5)
            fail()
        } catch(e: Exception) {
            //expected
        }
        try {
            assertEquals(left.get(), "kotlin")
            fail()
        } catch(e: Exception) {
            //Expected
        }
    }

    @Test fun forEach() {
        left.swap().forEach {
            assertEquals(it * 2, 10)
        }

        right.forEach {
            assertEquals(it.length, 6)
        }
    }

    @Test fun getOrElse() {
        assertEquals(left.swap().getOrElse { 2 }, 5)
        assertEquals(left.getOrElse { "java" }, "java")
    }

    @Test fun exists() {
        assertTrue(left.swap().exists { it == 5 })
        assertFalse(left.exists { it == "kotlin" })
    }

    @Test fun flatMap() {
        assertEquals(left.swap().flatMap { Left<String, Int>(it.toString()) }.swap().get(), "5")
        assertEquals(right.flatMap { Right<String, Int>(it.length) }.get(), 6)
    }

    @Test fun map() {
        assertEquals(left.swap().map(Int::toString).get(), "5")
        assertEquals(right.map { it.length }.get(), 6)
    }

    @Test fun filter() {
        assertEquals(left.swap().filter { it == 5 }.get().get(), 5)
        assertEquals(left.swap().filter { it == 6 }, Option.None)
        assertEquals(right.filter { it.startsWith('k') }.get().get(), "kotlin")
        assertEquals(right.filter { it.startsWith('j') }, Option.None)
    }

    @Test fun toList() {
        assertEquals(left.swap().toList(), listOf(5))
        assertEquals(left.toList(), listOf<Int>())
    }

    @Test fun toOption() {
        assertEquals(left.swap().toOption().get(), 5)
        assertEquals(left.toOption(), Option.None)
    }

    @Test fun fold() {
        assertEquals(left.fold(Int::toString, { it }), "5")
    }

    @Test fun swap() {
        assertEquals(left.swap().get(), 5)
        assertEquals(right.swap().swap().get(), "kotlin")
    }

    @Test fun components() {
        val (aInt, aNullString) = left
        assertNotNull(aInt)
        assertNull(aNullString)

        val (aNullInt, aString) = right
        assertNull(aNullInt)
        assertNotNull(aString)
    }

    @Test fun merge() {
        assertEquals(left.merge(), 5)
        assertEquals(right.merge(), "kotlin")
    }

    @Test fun disjunctionTry() {
        val e: Disjunction<Exception, Nothing> = disjunctionTry {
            throw RuntimeException()
        }
        assertTrue(e.isLeft())
    }

    @Test fun sequential() {
        fun parseInts(ints: List<String>): Disjunction<Exception, List<Int>> {
            return ints.map { disjunctionTry { it.toInt() } }.disjunctionSequential()
        }

        assertEquals(parseInts(listOf("1", "2", "3")), Right<Exception, List<Int>>(listOf(1, 2, 3)))
        assertTrue(parseInts(listOf("1", "foo", "3")) is Left)
    }
}
