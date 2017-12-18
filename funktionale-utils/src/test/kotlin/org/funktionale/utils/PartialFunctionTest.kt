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

package org.funktionale.utils


import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import kategory.UnitSpec
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class PartialFunctionTest : UnitSpec() {

    private val definetAt: (Int) -> Boolean = { it.rem(2) == 0 }
    private val body: (Int) -> String = {
        "is even"
    }

    init {

        "partial" {
            val isEven = PartialFunction(definetAt, body)

            (isEven.isDefinedAt(2)) shouldBe true
            isEven(2) shouldBe "is even"
        }

        "toPartialFunction"{
            val isEven = body.toPartialFunction(definetAt)
            (isEven.isDefinedAt(2)) shouldBe true
            isEven(2) shouldBe "is even"
        }

        "orElse" {
            val isEven = body.toPartialFunction(definetAt)
            val isOdd = { _: Int -> "is odd" }.toPartialFunction { !definetAt(it) }
            listOf(1, 2, 3).map(isEven orElse isOdd) shouldBe listOf("is odd", "is even", "is odd")
        }

        "invokeOrElse" {
            val isEven = body.toPartialFunction(definetAt)
            listOf(1, 2, 3).map { isEven.invokeOrElse(it, "is odd") } shouldBe listOf("is odd", "is even", "is odd")
        }
    }
}