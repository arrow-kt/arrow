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
import io.kotlintest.matchers.shouldBe
import io.kotlintest.properties.forAll
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class OptionTTest : UnitSpec() {
    init {
        "map should modify value" {
            forAll { a: String ->
                val ot = OptionT(Id(Option.Some(a)))
                val mapped = ot.map(IdMonad, { "$it power" })
                val expected = OptionT(Id(Option.Some("$a power")))

                mapped == expected
            }
        }

        "flatMap should modify entity" {
            forAll { a: String ->
                val ot = OptionT(NonEmptyList.of(Option.Some(a)))
                val mapped = ot.flatMap(NonEmptyListMonad) { OptionT(NonEmptyList.of(Option.Some(3))) }
                val expected = OptionT.pure(NonEmptyListMonad, 3)

                mapped == expected
            }

            forAll { ignored: String ->
                val ot = OptionT(NonEmptyList.of(Option.Some(ignored)))
                val mapped = ot.flatMap(NonEmptyListMonad, { OptionT(NonEmptyList.of(Option.None)) })
                val expected = OptionT.none(NonEmptyListMonad)

                mapped == expected
            }

            OptionT.none(NonEmptyListMonad)
                    .flatMap(NonEmptyListMonad, { OptionT(NonEmptyList.of(Option.Some(2))) }) shouldBe OptionT(NonEmptyList.of(Option.None))
        }

        "from option should build a correct OptionT" {
            forAll { a: String ->
                OptionT.fromOption(NonEmptyListMonad, Option.Some(a)) == OptionT.pure(NonEmptyListMonad, a)
            }
        }

        "OptionTMonad.flatMap should be consistent with OptionT#flatMap" {
            forAll { a: Int ->
                val x = { b: Int -> OptionT.pure(IdMonad, b * a) }
                val option = OptionT.pure(IdMonad, a)
                option.flatMap(IdMonad, x) == OptionTMonad(IdMonad).flatMap(option, x)
            }
        }

        "OptionTMonad.binding should for comprehend over option" {
            val result = OptionTMonad(NonEmptyListMonad).binding {
                val x = !OptionT.pure(NonEmptyListMonad, 1)
                val y = OptionT.pure(NonEmptyListMonad, 1).bind()
                val z = bind { OptionT.pure(NonEmptyListMonad, 1) }
                yields(x + y + z)
            }
            result shouldBe OptionT.pure(NonEmptyListMonad, 3)
        }

        "Cartesian builder should build products over option" {
            OptionTMonad(IdMonad).map(OptionT.pure(IdMonad, 1), OptionT.pure(IdMonad, "a"), OptionT.pure(IdMonad, true), { (a, b, c) ->
                "$a $b $c"
            }) shouldBe OptionT.pure(IdMonad, "1 a true")
        }

        "Cartesian builder works inside for comprehensions" {
            val result = OptionTMonad(NonEmptyListMonad).binding {
                val (x, y, z) = !OptionTMonad(NonEmptyListMonad).tupled(OptionT.pure(NonEmptyListMonad, 1), OptionT.pure(NonEmptyListMonad, 1), OptionT.pure(NonEmptyListMonad, 1))
                val a = OptionT.pure(NonEmptyListMonad, 1).bind()
                val b = bind { OptionT.pure(NonEmptyListMonad, 1) }
                yields(x + y + z + a + b)
            }
            result shouldBe OptionT.pure(NonEmptyListMonad, 5)
        }
    }
}
