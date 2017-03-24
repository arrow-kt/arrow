/*
 * Copyright (C) 2017 The Kats Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package katz

import io.kotlintest.KTestJUnitRunner
import katz.Either.Left
import katz.Either.Right
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class EitherTest : UnitSpec() {
    init {

        "map" should "modify value" {
            Right(12).map { "flower" } shouldBe Right("flower")
            Left(12).map { "flower" } shouldBe Left(12)
        }

        "flatMap" should "modify entity" {
            val left: Either<String, Int> = Left("Nope")

            Right(1).flatMap { left } shouldBe left
            Right(1).flatMap { Right("something") } shouldBe Right("something")
            left.flatMap { Right("something") } shouldBe left
        }

        "getOrElse" should "return value" {
            Right(12).getOrElse { 17 } shouldBe 12
            Left(12).getOrElse { 17 } shouldBe 17
        }

        "exits" should "evaluate value" {
            val left: Either<Int, Int> = Left(12)

            Right(12).exists { it > 10 } shouldBe true
            Right(7).exists { it > 10 } shouldBe false
            left.exists { it > 10 } shouldBe false
        }

        "filterOrElse" should "filters value" {
            val left: Either<Int, Int> = Left(12)

            Right(12).filterOrElse({ it > 10 }, { -1 }) shouldBe Right(12)
            Right(7).filterOrElse({ it > 10 }, { -1 }) shouldBe Left(-1)
            left.filterOrElse({ it > 10 }, { -1 }) shouldBe Left(12)
        }

        "swap" should "interchange values" {
            Left("left").swap() shouldBe Right("left")
            Right("right").swap() shouldBe Left("right")
        }

        "fold" should "call left function on Left" {
            val exception = Exception()
            val result: Either<Exception, String> = Left(exception)
            result.fold(
                    { it shouldBe exception },
                    { fail("Right should not be called") }
            )
        }

        "fold" should "call right function on Right" {
            val value = "Some value"
            val result: Either<Exception, String> = Right(value)
            result.fold(
                    { fail("Left should not be called") },
                    { it shouldBe value }
            )
        }

        "toOption" should "convert" {
            Right(12).toOption() shouldBe Option.Some(12)
            Left(12).toOption() shouldBe Option.None
        }

        "contains" should "check value" {
            Right("something").contains { "something" } shouldBe true
            Right("something").contains { "anything" } shouldBe false
            Left("something").contains { "something" } shouldBe false
        }
    }
}