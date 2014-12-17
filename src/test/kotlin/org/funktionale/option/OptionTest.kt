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

package org.funktionale.option

import org.testng.annotations.Test
import org.testng.Assert.*

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 17/05/13
 * Time: 13:39
 */

public class OptionTest {

    fun getSome(): Option<String> = "kotlin".toOption()

    fun getNone(): Option<String> = null.toOption()

    [Test] fun option() {

        val option = getSome()
        when (option) {
            is Some<String> -> {
                assertEquals(option.get(), "kotlin")
            }
            else -> fail()//Same as None never happen
        }

        val otherOption = getNone()

        when (otherOption) {
            is Some<String> -> fail()
            else -> assertEquals(otherOption, None<String>())
        }

    }

    [Test] fun getOrElse() {
        assertEquals(getSome().getOrElse { "java" }, "kotlin")
        assertEquals(getNone().getOrElse { "java" }, "java")
    }

    [Test] fun orNull() {
        assertNotNull(getSome().orNull())
        assertNull(getNone().orNull())
    }

    [Test] fun map() {
        assertEquals(getSome().map { it.toUpperCase() }.get(), "KOTLIN")
        assertEquals(getNone().map { it.toUpperCase() }, None<String>())
    }

    [Test] fun fold() {
        assertEquals(getSome().fold({ 0 }) { it.length() }, 6)
        assertEquals(getNone().fold({ 0 }) { it.length() }, 0)
    }

    [Test] fun flatMap() {
        assertEquals(getSome().flatMap<String> { Some(it.toUpperCase()) }.get(), "KOTLIN")
        assertEquals(getNone().flatMap<String> { Some(it.toUpperCase()) }, None<String>())
    }

    [Test] fun filter() {
        assertEquals(getSome().filter { it.equals("java") }, None<String>())
        assertEquals(getNone().filter { it.equals("java") }, None<String>())
        assertEquals(getSome().filter { it.startsWith('k') }.get(), "kotlin")
    }

    [Test] fun filterNot() {
        assertEquals(getSome().filterNot { it.equals("java") }.get(), "kotlin")
        assertEquals(getNone().filterNot { it.equals("java") }, None<String>())
        assertEquals(getSome().filterNot { it.startsWith('k') }, None<String>())
    }

    [Test] fun exists() {
        assertTrue(getSome().exists { it.startsWith('k') })
        assertFalse(getNone().exists { it.startsWith('k') })

    }

    [Test] fun forEach() {
        getSome().forEach {
            assertEquals(it, "kotlin")
        }

        getNone().forEach {
            fail()
        }
    }

    [Test] fun orElse() {
        assertEquals(getSome().orElse { Some("java") }.get(), "kotlin")
        assertEquals(getNone().orElse { Some("java") }.get(), "java")
    }

    [Test] fun toList() {
        assertEquals(getSome().toList(), listOf("kotlin"))
        assertEquals(getNone().toList(), listOf())
    }

    [Test] fun toRight() {
        assertTrue(getSome().toRight { 0 }.isRight())
        assertFalse(getNone().toRight { 0 }.isRight())
    }

    [Test] fun toLeft() {
        assertTrue(getSome().toLeft { 0 }.isLeft())
        assertFalse(getNone().toLeft { 0 }.isLeft())
    }

    [Test] fun getAsOption() {
        val map = mapOf(1 to "uno", 2 to "dos", 4 to null)
        assertEquals(map.option[1], Some("uno"))
        assertEquals(map.option[3], None<String>())
        assertEquals(map.option[4], None<String>())
    }

    [Test] fun firstOption() {
        val l = listOf(1, 2, 3, 4, 5, 6)
        assertEquals(l.firstOption(), Some(1))
        assertEquals(l.firstOption { it > 2 }, Some(3))
    }

}