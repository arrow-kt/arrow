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

package org.funktionale.validation

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import kategory.UnitSpec
import org.funktionale.either.Disjunction
import org.junit.runner.RunWith

data class ExampleForValidation(val number: Int, val text: String)

@RunWith(KTestJUnitRunner::class)
class UtilTest : UnitSpec() {

    init {

        "validationTest" {
            val d1 = Disjunction.right(1)
            val d2 = Disjunction.right(2)
            val d3 = Disjunction.right(3)

            val validation = Validation(d1, d2, d3)
            (validation.hasFailures) shouldBe false
            validation.failures shouldBe listOf<String>()
        }

        "validationTestWithError" {
            val d1 = Disjunction.right(1)
            val d2 = Disjunction.left("Not a number")
            val d3 = Disjunction.right(3)

            val validation = Validation(d1, d2, d3)
            (validation.hasFailures) shouldBe true
            validation.failures shouldBe listOf("Not a number")
        }

        "validate2Test" {
            val r1 = Disjunction.right(1)
            val r2 = Disjunction.right("blahblah")
            val l1 = Disjunction.left("fail1")
            val l2 = Disjunction.left("fail2")
            validate(r1, r2, ::ExampleForValidation) shouldBe Disjunction.right(ExampleForValidation(1, "blahblah"))
            validate(r1, l2, ::ExampleForValidation) shouldBe Disjunction.left(listOf("fail2"))
            validate(l1, l2, ::ExampleForValidation) shouldBe Disjunction.left(listOf("fail1", "fail2"))
        }
    }
}