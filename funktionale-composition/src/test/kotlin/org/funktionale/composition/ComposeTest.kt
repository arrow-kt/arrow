/*
 * Copyright 2013 - 2017 Mario Arias
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

package org.funktionale.composition

import java.util.*

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import kategory.UnitSpec
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ComposeTest : UnitSpec() {

    private val add5 = { i: Int -> i + 5 }
    private val multiplyBy2 = { i: Int -> i * 2 }

    init {

        "it should compose function correctly (andThen)" {
            val potato = "potato"
            val ninja = "ninja"
            val get = { potato }
            val map = { word: String -> ninja + word }
            ninja + potato shouldBe (get andThen map)()
        }

        "it should compose function correctly (forwardCompose)" {
            val randomDigit = Random().nextInt()
            val get = { randomDigit }
            val pow = { i: Int -> i * i }
            randomDigit * randomDigit shouldBe (get forwardCompose pow)()
        }



        "testAndThen" {
            val add5andMultiplyBy2 = add5 andThen multiplyBy2
            add5andMultiplyBy2(2) shouldBe 14
        }

        "testForwardCompose" {
            val add5andMultiplyBy2 = add5 forwardCompose multiplyBy2
            add5andMultiplyBy2(2) shouldBe 14
        }

        "testCompose" {
            val multiplyBy2andAdd5 = add5 compose multiplyBy2
            multiplyBy2andAdd5(2) shouldBe 9
        }

    }
}
