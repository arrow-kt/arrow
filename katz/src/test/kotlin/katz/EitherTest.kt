/*
 * Copyright (C) 2017 The Katz Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package katz

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

import katz.Either.*

@RunWith(KTestJUnitRunner::class)
class EitherTest : UnitSpec() {
    init {
        "map should modify value" {
            forAll { a: Int, b: String ->
                Right(a).map { b } == Right(b)
                        && Left(a).map { b } == Left(a)
            }
        }

        "flatMap should modify entity" {
            forAll { a: Int, b: String ->
                {
                    val left: Either<Int, Int> = Left(a)

                    Right(a).flatMap { left } == left
                            && Right(a).flatMap { Right(b) } == Right(b)
                            && left.flatMap { Right(b) } == left
                }()
            }
        }

        "getOrElse should return value" {
            forAll { a: Int, b: Int ->
                Right(a).getOrElse { b } == a
                        && Left(a).getOrElse { b } == b
            }

        }

        "exits should evaluate value" {
            forAll { a: Int ->
                {
                    val left: Either<Int, Int> = Left(a)

                    Right(a).exists { it > a - 1 } == true
                            && Right(a).exists { it > a + 1 } == false
                            && left.exists { it > a - 1 } == false
                }()
            }
        }

        "filterOrElse should filters value" {
            forAll { a: Int, b: Int ->
                {
                    val left: Either<Int, Int> = Left(a)

                    Right(a).filterOrElse({ it > a - 1 }, { b }) == Right(a)
                            && Right(a).filterOrElse({ it > a + 1 }, { b }) == Left(b)
                            && left.filterOrElse({ it > a - 1 }, { b }) == Left(a)
                            && left.filterOrElse({ it > a + 1 }, { b }) == Left(a)
                }()
            }
        }

        "swap should interchange values" {
            forAll { a: Int ->
                Left(a).swap() == Right(a)
                        && Right(a).swap() == Left(a)
            }
        }

        "fold should call left function on Left" {
            forAll { a: Int, b: Int ->
                Left(a).fold({ b }, { a }) == b
            }
        }

        "fold should call right function on Right" {
            forAll { a: Int, b: Int ->
                Right(a).fold({ b }, { a }) == a
            }
        }

        "toOption should convert" {
            forAll { a: Int ->
                Right(a).toOption() == Option.Some(a)
                        && Left(a).toOption() == Option.None
            }
        }

        "contains should check value" {
            forAll { a: Int, b: Int ->
                Right(a).contains(a)
                        && !Right(a).contains(b)
                        && !Left(a).contains(a)
            }
        }
    }
}
