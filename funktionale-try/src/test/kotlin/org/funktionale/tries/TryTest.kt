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

package org.funktionale.tries

import org.funktionale.tries.Try.Failure
import org.funktionale.tries.Try.Success
import org.testng.Assert.*
import org.testng.annotations.Test


class TryTest {
    val success = Try { "10".toInt() }
    val failure = Try { "NaN".toInt() }

    @Test fun show() {
        val problem = success.flatMap { x -> failure.map { y -> x / y } }
        when (problem) {
            is Success -> fail("This should not be possible")
            is Failure -> println(problem)
        }
    }

    @Test fun get() {
        assertEquals(10, success())
        try {
            failure()
            fail()
        } catch (e: Exception) {
            assertTrue(e is NumberFormatException)
        }
    }

    @Test fun getOrElse() {
        assertEquals(success.getOrElse { 5 }, 10)
        assertEquals(failure.getOrElse { 5 }, 5)
    }

    @Test fun orElse() {
        assertEquals(success.orElse { Success(5) }.get(), 10)
        assertEquals(failure.orElse { Success(5) }.get(), 5)
    }

    @Test fun foreach() {
        success.foreach { assertEquals(10, it) }
        failure.foreach { fail() }
    }

    @Test fun flatMap() {
        assertEquals(success.flatMap { Success(it * 2) }.get(), 20)
        assertTrue(failure.flatMap { Success(it * 2) }.isFailure())
    }

    @Test fun map() {
        assertEquals(success.map { it * 2 }.get(), 20)
        assertTrue(failure.map { it * 2 }.isFailure())
    }

    @Test fun exists() {
        assertTrue(success.exists { it > 5 })
        assertFalse(failure.exists { it > 5 })
    }

    @Test fun filter() {
        assertTrue(success.filter { it > 5 }.isSuccess())
        assertTrue(success.filter { it < 5 }.isFailure())
        assertFalse(failure.filter { it > 5 }.isSuccess())
    }

    @Test fun rescue() {
        assertEquals(success.rescue { Success(5) }.get(), 10)
        assertEquals(failure.rescue { Success(5) }.get(), 5)
    }

    @Test fun handle() {
        assertEquals(success.handle { 5 }.get(), 10)
        assertEquals(failure.handle { 5 }.get(), 5)
    }

    @Test fun onSuccessAndOnFailure() {
        success.onSuccess { assertEquals(it, 10) }
                .onFailure { fail() }
        failure.onSuccess { fail() }
                .onFailure { }
    }

    @Test fun toOption() {
        assertTrue(success.toOption().isDefined())
        assertTrue(failure.toOption().isEmpty())
    }

    @Test fun toDisjunction() {
        assertTrue(success.toDisjunction().isRight())
        assertTrue(failure.toDisjunction().isLeft())
    }

    @Test fun failed() {
        success.failed().onFailure { assertTrue(it is UnsupportedOperationException) }
        failure.failed().onSuccess { assertTrue(it is NumberFormatException) }
    }

    @Test fun transform() {
        assertEquals(success.transform({ Try { it.toString() } }) { Try { "NaN" } }.get(), "10")
        assertEquals(failure.transform({ Try { it.toString() } }) { Try { "NaN" } }.get(), "NaN")
    }

    @Test fun fold() {
        assertEquals(success.fold(Int::toString) { "NaN" }, "10")
        assertEquals(success.fold({ throw RuntimeException("Fire($it)!!") }) { "NaN" }, "NaN")
        assertEquals(failure.fold(Int::toString) { "NaN" }, "NaN")
    }

    @Test fun flatten() {
        assertTrue(Try { success }.flatten().isSuccess())
        assertTrue(Try { failure }.flatten().isFailure())
        assertTrue(Try<Try<Int>> { throw RuntimeException("") }.flatten().isFailure())
    }
}