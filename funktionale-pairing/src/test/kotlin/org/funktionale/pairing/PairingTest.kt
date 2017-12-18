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

package org.funktionale.pairing

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import kategory.UnitSpec
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class PairingTest : UnitSpec() {

    init {
        "testPaired" {
            val sum2ints = { x: Int, y: Int -> x + y }

            val paired = sum2ints.paired()
            val unpaired = paired.unpaired()

            sum2ints(5, 9) shouldBe paired(5 to 9)
            paired(5 to 9) shouldBe unpaired(5, 9)
        }

        "testTripled" {
            val sum3ints = { x: Int, y: Int, z: Int -> x + y + z }

            val tripled = sum3ints.tripled()
            val untripled = tripled.untripled()

            sum3ints(1, 2, 3) shouldBe tripled(Triple(1, 2, 3))
            tripled(Triple(9, 8, 7)) shouldBe untripled(9, 8, 7)
        }
    }
}