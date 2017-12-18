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

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.fail
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import kategory.UnitSpec
import org.funktionale.either.Disjunction.Left
import org.funktionale.either.Disjunction.Right
import org.funktionale.option.Option
import org.junit.runner.RunWith

fun fail(): Nothing = fail("")

@RunWith(KTestJUnitRunner::class)
class DisjunctionTest : UnitSpec() {

    private val left = Disjunction.left(5)
    private val right = Disjunction.right("kotlin")

    init {

        "get" {
            left.swap().get() shouldBe 5
            right.get() shouldBe "kotlin"
        }

        //[Test(expectedExceptions = array(javaClass<NoSuchElementException>()))]

        "getWithException" {
            try {
                right.swap().get() shouldBe 5
                fail()
            } catch(e: Exception) {
                //expected
            }
            try {
                left.get() shouldBe "kotlin"
                fail()
            } catch(e: Exception) {
                //Expected
            }
        }

        "forEach" {
            left.swap().forEach {
                it * 2 shouldBe 10
            }

            right.forEach {
                it.length shouldBe 6
            }
        }

        "getOrElse" {
            left.swap().getOrElse { 2 } shouldBe 5
            left.getOrElse { "java" } shouldBe "java"
        }

        "exists" {
            left.swap().exists { it == 5 } shouldBe true
            left.exists { it == "kotlin" } shouldBe false
        }

        "flatMap" {
            left.swap().flatMap { Left<String, Int>(it.toString()) }.swap().get() shouldBe "5"
            right.flatMap { Right<String, Int>(it.length) }.get() shouldBe 6
        }

        "map" {
            left.swap().map(Int::toString).get() shouldBe  "5"
            right.map { it.length }.get() shouldBe 6
        }

        "filter" {
            left.swap().filter { it == 5 }.get().get() shouldBe 5
            left.swap().filter { it == 6 } shouldBe Option.None
            right.filter { it.startsWith('k') }.get().get() shouldBe "kotlin"
            right.filter { it.startsWith('j') } shouldBe Option.None
        }

        "toList" {
            left.swap().toList() shouldBe listOf(5)
            left.toList() shouldBe listOf<Int>()
        }

        "toOption" {
            left.swap().toOption().get() shouldBe 5
            left.toOption() shouldBe Option.None
        }

        "fold" {
            left.fold(Int::toString, { it }) shouldBe  "5"
        }

        "swap" {
            left.swap().get() shouldBe 5
            right.swap().swap().get() shouldBe "kotlin"
        }

        "components" {
            val (aInt, aNullString) = left
            aInt shouldNotBe null

            val (aNullInt, aString) = right
            aString shouldNotBe null
        }

        "merge" {
            left.merge() shouldBe 5
            right.merge() shouldBe "kotlin"
        }

        "disjunctionTry" {
            val e: Disjunction<Throwable, Nothing> = disjunctionTry {
                throw RuntimeException()
            }
            e.isLeft() shouldBe true
        }

        "sequential" {
            fun parseInts(ints: List<String>): Disjunction<Throwable, List<Int>> {
                return ints.map { disjunctionTry { it.toInt() } }.disjunctionSequential()
            }

            parseInts(listOf("1", "2", "3")) shouldBe Right<Exception, List<Int>>(listOf(1, 2, 3))
            (parseInts(listOf("1", "foo", "3")) is Left) shouldBe true
        }

    }
}

