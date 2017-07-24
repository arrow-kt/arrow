/*
 * Copyright (C) 2017 The Kategory Authors
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

package kategory

import io.kotlintest.KTestJUnitRunner
import io.kotlintest.matchers.shouldBe
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class PartialFunctionTests : UnitSpec() {

    init {

        "case syntax can be used to create partial functions" {
            val predicate = {n : Int -> n > 0 }
            val fa = case({n : Int -> n > 0 } then { it * 2 })
            val fb = object: PartialFunction<Int, Int>() {
                override fun isDefinedAt(a: Int): Boolean = predicate(a)
                override fun invoke(p1: Int): Int = p1 * 2
            }
            fa(1) shouldBe fb(1)
            fa.isDefinedAt(-1) shouldBe false
        }

    }
}
