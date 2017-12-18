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
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import kategory.UnitSpec
import org.funktionale.either.Either.Left
import org.funktionale.either.Either.Right
import org.funktionale.option.Option
import org.funktionale.option.Option.None
import org.funktionale.option.toOption
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class EitherTest : UnitSpec() {

    val pair = 5 to "kotlin"
    val left = pair.toLeft()
    val right = pair.toRight()
    
    init {

        "get" {
            left.left().get() shouldBe 5
            right.right().get() shouldBe "kotlin"
        }

        //[Test(expectedExceptions = array(javaClass<NoSuchElementException>()))]

        "getWithException" {
            try {
                right.left().get() shouldBe 5
                fail()
            } catch(e: Exception) {
                //expected
            }
            try {
                left.right().get() shouldBe "kotlin"
                fail()
            } catch(e: Exception) {
                //Expected
            }
        }

        "forEach" {
            left.left().forEach {
                it * 2 shouldBe 10
            }

            right.right().forEach {
                it.length shouldBe 6
            }
        }

        "getOrElse" {
            left.left().getOrElse { 2 } shouldBe 5
            left.right().getOrElse { "java" } shouldBe "java"
        }

        "exists" {
            left.left().exists { it == 5 } shouldBe true
            left.right().exists { it == "kotlin" } shouldBe false
        }

        "flatMap" {
            left.left().flatMap { Left<String, Int>(it.toString()) }.left().get() shouldBe "5"
            right.right().flatMap { Right<String, Int>(it.length) }.right().get()shouldBe  6
        }

        "map" {
            left.left().map(Int::toString).left().get() shouldBe "5"
            right.right().map { it.length }.right().get() shouldBe 6
        }

        "filter" {
            left.left().filter { it == 5 }.get().left().get() shouldBe 5
            left.left().filter { it == 6 } shouldBe None
            right.right().filter { it.startsWith('k') }.get().right().get() shouldBe "kotlin"
            right.right().filter { it.startsWith('j') } shouldBe None
        }

        "toList" {
            left.left().toList() shouldBe listOf(5)
            left.right().toList() shouldBe listOf<Int>()
        }

        "toOption" {
            left.left().toOption().get() shouldBe 5
            left.right().toOption() shouldBe None
        }

        "fold" {
            left.fold(Int::toString) { it } shouldBe "5"
        }

        "swap" {
            left.swap().right().get() shouldBe 5
            right.swap().left().get() shouldBe "kotlin"
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

        "either" {
            val e: Either<Throwable, Nothing> = eitherTry {
                throw RuntimeException()
            }
            e.isLeft() shouldBe true
        }

        "sequential" {
            fun parseInts(ints: List<String>): Either<Throwable, List<Int>> {
                return ints.map { eitherTry { it.toInt() } }.eitherSequential()
            }

            parseInts(listOf("1", "2", "3")) shouldBe Right<Exception, List<Int>>(listOf(1, 2, 3))
            (parseInts(listOf("1", "foo", "3")) is Left) shouldBe true
        }

        val some: Option<String> = "kotlin".toOption()
        val none: Option<String> = null.toOption()

        "toRight" {
            (some.toEitherRight { 0 }.isRight()) shouldBe true
            (none.toEitherRight { 0 }.isRight()) shouldBe false
        }


        "toLeft" {
            some.toEitherLeft { 0 }.isLeft() shouldBe true
            none.toEitherLeft { 0 }.isLeft() shouldBe false
        }
        
    }
}
