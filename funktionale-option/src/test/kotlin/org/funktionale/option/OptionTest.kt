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

package org.funktionale.option

import org.funktionale.option.Option.None
import org.funktionale.option.Option.Some
import org.testng.Assert.*
import org.testng.annotations.Test

class OptionTest {

    val some: Option<String> = "kotlin".toOption()
    val none: Option<String> = Option.empty()

    @Test fun option() {

        val option = some
        when (option) {
            is Some<String> -> {
                assertEquals(option.get(), "kotlin")
            }
            is None -> fail()
        }

        val otherOption = none

        when (otherOption) {
            is Some<String> -> fail()
            is None -> assertEquals(otherOption, None)
        }

    }

    @Test fun getOrElse() {
        assertEquals(some.getOrElse { "java" }, "kotlin")
        assertEquals(none.getOrElse { "java" }, "java")
    }

    @Test fun orNull() {
        assertNotNull(some.orNull())
        assertNull(none.orNull())
    }

    @Test fun map() {
        assertEquals(some.map(String::toUpperCase).get(), "KOTLIN")
        assertEquals(none.map(String::toUpperCase), None)

        assertEquals(some.map(Some(12)) { name, version -> "${name.toUpperCase()} M$version" }.get(), "KOTLIN M12")
        assertEquals(none.map(Some(12)) { name, version -> "${name.toUpperCase()} M$version" }, None)
    }

    @Test fun fold() {
        assertEquals(some.fold({ 0 }) { it.length }, 6)
        assertEquals(none.fold({ 0 }) { it.length }, 0)
    }

    @Test fun flatMap() {
        assertEquals(some.flatMap { Some(it.toUpperCase()) }.get(), "KOTLIN")
        assertEquals(none.flatMap { Some(it.toUpperCase()) }, None)
    }

    @Test fun filter() {
        assertEquals(some.filter { it == "java" }, None)
        assertEquals(none.filter { it == "java" }, None)
        assertEquals(some.filter { it.startsWith('k') }.get(), "kotlin")
    }

    @Test fun filterNot() {
        assertEquals(some.filterNot { it == "java" }.get(), "kotlin")
        assertEquals(none.filterNot { it == "java" }, None)
        assertEquals(some.filterNot { it.startsWith('k') }, None)
    }

    @Test fun exists() {
        assertTrue(some.exists { it.startsWith('k') })
        assertFalse(none.exists { it.startsWith('k') })

    }

    @Test fun forEach() {
        some.forEach {
            assertEquals(it, "kotlin")
        }

        none.forEach {
            fail()
        }
    }

    @Test fun orElse() {
        assertEquals(some.orElse { Some("java") }.get(), "kotlin")
        assertEquals(none.orElse { Some("java") }.get(), "java")
    }

    @Test fun toList() {
        assertEquals(some.toList(), listOf("kotlin"))
        assertEquals(none.toList(), listOf<String>())
    }


    @Test fun getAsOption() {
        val map = mapOf(1 to "uno", 2 to "dos", 4 to null)
        assertEquals(map.option[1], Some("uno"))
        assertEquals(map.option[3], None)
        assertEquals(map.option[4], None)
    }

    @Test fun firstOption() {
        val l = listOf(1, 2, 3, 4, 5, 6)
        assertEquals(l.firstOption(), Some(1))
        assertEquals(l.firstOption { it > 2 }, Some(3))
    }

    @Test fun optionBody() {
        assertEquals(optionTry { "1".toInt() }, Some(1))
        assertEquals(optionTry { "foo".toInt() }, None)
    }

    @Test fun sequential() {
        fun parseInts(ints: List<String>): Option<List<Int>> {
            return ints.map { optionTry { it.toInt() } }.optionSequential()
        }

        assertEquals(parseInts(listOf("1", "2", "3")), Some(listOf(1, 2, 3)))
        assertEquals(parseInts(listOf("1", "foo", "3")), None)
    }

    @Test fun and() {
        val x = Some(2)
        val y = Some("Foo")
        assertEquals(x and y, Some("Foo"))
        assertEquals(x and None, None)
        assertEquals(None and x, None)
        assertEquals(None and None, None)

    }

    @Test fun or() {
        val x = Some(2)
        val y = Some(100)
        assertEquals(x or y, Some(2))
        assertEquals(x or None, Some(2))
        assertEquals(None or x, Some(2))
        assertEquals(None or None, None)

    }

}
