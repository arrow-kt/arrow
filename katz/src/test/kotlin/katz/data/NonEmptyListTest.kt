/*
 * Copyright (C) 2017 The Katz Authors
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
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class NonEmptyListTest : UnitSpec() {
    init {
        "map should modify values" {
            NonEmptyList.of(14).map { it * 3 } shouldBe NonEmptyList.of(42)
        }

        "flatMap should modify entity" {
            Option.Some(1).flatMap { Option.None } shouldBe Option.None
            Option.Some(1).flatMap { Option.Some("something") } shouldBe Option.Some("something")
            Option.None.flatMap { Option.Some("something") } shouldBe Option.None
        }

        "flatMap should work" {
            val nel = NonEmptyList.of(1, 2)
            val nel2 = nel.flatMap { it -> NonEmptyList.of(it, it) }
            nel2 shouldBe NonEmptyList.of(1, 1, 2, 2)
        }

        "NonEmptyListMonad.flatMap should be consistent with NonEmptyList#flatMap" {
            val nel = NonEmptyList.of(1, 2)
            val nel2 = NonEmptyList.of(1, 2)
            nel.flatMap { nel2 } shouldBe NonEmptyListMonad.flatMap(nel) { nel2 }
        }

        "NonEmptyListMonad.binding should for comprehend over NonEmptyList" {
            val result = NonEmptyListMonad.binding {
                val x = !NonEmptyList.of(1)
                val y = NonEmptyList.of(2).bind()
                val z = bind { NonEmptyList.of(3) }
                yields(x + y + z)
            }
            result shouldBe NonEmptyList.of(6)
        }

        "NonEmptyListMonad.binding should for comprehend over complex NonEmptyList" {
            val result = NonEmptyListMonad.binding {
                val x = !NonEmptyList.of(1, 2)
                val y = NonEmptyList.of(3).bind()
                val z = bind { NonEmptyList.of(4) }
                yields(x + y + z)
            }
            result shouldBe NonEmptyList.of(8, 9)
        }

        "NonEmptyListMonad.binding should for comprehend over all values of multiple NonEmptyList" {
            forAll { a: Int, b: List<Int> ->
                val nel: NonEmptyList<Int> = NonEmptyList(a, b)
                val nel2 = NonEmptyList.of(1, 2)
                val nel3 = NonEmptyList.of(3, 4, 5)
                val result: HK<NonEmptyList.F, Int> = NonEmptyListMonad.binding {
                    val x = !nel
                    val y = nel2.bind()
                    val z = bind { nel3 }
                    yields(x + y + z)
                }
                result.ev().size == nel.size * nel2.size * nel3.size
            }
        }
    }
}
