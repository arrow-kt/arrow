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


import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.fail
import io.kotlintest.matchers.shouldBe
import arrow.UnitSpec
import org.junit.runner.RunWith


@RunWith(KTestJUnitRunner::class)
class TryTest : UnitSpec() {

    val success = Try { "10".toInt() }
    val failure = Try { "NaN".toInt() }

    init {

        "show" {
            val problem = success.flatMap { x -> failure.map { y -> x / y } }
            when (problem) {
                is Success -> fail("This should not be possible")
                is Failure -> println(problem)
            }
        }

        "get" {
            10 shouldBe success()
            try {
                failure()
                fail("")
            } catch (e: Exception) {
                (e is NumberFormatException) shouldBe true
            }
        }

        "getOrElse" {
            success.getOrElse { 5 } shouldBe 10
            failure.getOrElse { 5 } shouldBe 5
        }

        "orElse" {
            success.orElse { Success(5) }.get() shouldBe 10
            failure.orElse { Success(5) }.get() shouldBe 5
        }

        "`foreach with side effect (applied on Success)`" {
            var wasInside = false
            success.foreach { wasInside = true }
            wasInside shouldBe true
        }

        "`foreach with side effect (applied on Failure)`" {
            var wasInside = false
            failure.foreach { wasInside = true }
            wasInside shouldBe false
        }

        "`foreach with exception thrown inside (applied on Success)`" {
            try {
                success.foreach { throw RuntimeException("thrown inside") }
            } catch (e: Throwable) {
                e.message shouldBe "thrown inside"
            }
        }

        "`foreach with exception thrown inside (applied on Failure)`" {
            failure.foreach { throw RuntimeException("thrown inside") }
            // and no exception should be thrown
        }

        "`onEach with side effect (applied on Success)`" {
            var wasInside = false
            success.onEach { wasInside = true }
            wasInside shouldBe true
        }

        "`onEach with side effect (applied on Failure)`" {
            var wasInside = false
            failure.onEach { wasInside = true }
            wasInside shouldBe false
        }

        "`onEach with exception thrown inside (applied on Success)`" {
            try {
                success.onEach { throw RuntimeException("thrown inside") }.get()
            } catch (e: Throwable) {
                e.message shouldBe "thrown inside"
            }
        }

        "`onEach with exception thrown inside (applied on Failure)`" {
            try {
                failure.onEach { throw RuntimeException("thrown inside") }.get()
            } catch (e: Throwable) {
                e.javaClass shouldBe NumberFormatException::class.java
            }
        }

        "`onEach with change of carried value (applied on Success)`" {
            val result = success.onEach { it * 2 }.get()
            result shouldBe 10
        }

        "`onEach with change of carried value (applied on Failure)`" {
            try {
                failure.onEach { it * 2 }.get()
            } catch (e: Throwable) {
                e.javaClass shouldBe NumberFormatException::class.java
            }
        }

        "flatMap" {
            success.flatMap { Success(it * 2) }.get() shouldBe 20
            (failure.flatMap { Success(it * 2) }.isFailure()) shouldBe true
        }

        "map" {
            success.map { it * 2 }.get() shouldBe 20
            (failure.map { it * 2 }.isFailure()) shouldBe true
        }

        "exists" {
            (success.exists { it > 5 }) shouldBe true
            (failure.exists { it > 5 }) shouldBe false
        }

        "filter" {
            (success.filter { it > 5 }.isSuccess()) shouldBe true
            (success.filter { it < 5 }.isFailure()) shouldBe true
            (failure.filter { it > 5 }.isSuccess()) shouldBe false
        }

        "rescue" {
            success.rescue { Success(5) }.get() shouldBe 10
            failure.rescue { Success(5) }.get() shouldBe 5
        }

        "handle" {
            success.handle { 5 }.get() shouldBe 10
            failure.handle { 5 }.get() shouldBe 5
        }

        "onSuccessAndOnFailure" {
            success.onSuccess { it shouldBe 10 }
                    .onFailure { fail("") }
            failure.onSuccess { fail("") }
                    .onFailure { }
        }

        "toOption" {
            (success.toOption().isDefined()) shouldBe true
            (failure.toOption().isEmpty()) shouldBe true
        }

        "toDisjunction" {
            (success.toDisjunction().isRight()) shouldBe true
            (failure.toDisjunction().isLeft()) shouldBe true
        }

        "failed" {
            success.failed().onFailure { (it is UnsupportedOperationException) shouldBe true }
            failure.failed().onSuccess { (it is NumberFormatException) shouldBe true }
        }

        "transform" {
            success.transform({ Try { it.toString() } }) { Try { "NaN" } }.get() shouldBe "10"
            failure.transform({ Try { it.toString() } }) { Try { "NaN" } }.get() shouldBe "NaN"
        }

        "fold" {
            success.fold(Int::toString) { "NaN" } shouldBe "10"
            success.fold({ throw RuntimeException("Fire($it)!!") }) { "NaN" } shouldBe "NaN"
            failure.fold(Int::toString) { "NaN" } shouldBe "NaN"
        }

        "flatten" {
            (Try { success }.flatten().isSuccess()) shouldBe true
            (Try { failure }.flatten().isFailure()) shouldBe true
            (Try<Try<Int>> { throw RuntimeException("") }.flatten().isFailure()) shouldBe true
        }
    }
}