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
class StateTests : UnitSpec() {

    private val addOne = State<Int, Int>({ n -> Tuple2(n * 2, n) })

    init {
        "addOne.run(1) should return Pair(2, 1)" {
            addOne.run(1).ev().value shouldBe Tuple2(2, 1)
        }

        "addOne.map(n -> n).run(1) should return same Pair(2, 1)" {
            addOne.map { n -> n }.run(1).ev().value shouldBe Tuple2(2, 1)
        }

        "addOne.map(n -> n.toString).run(1) should return same Pair(2, \"1\")" {
            addOne.map(Int::toString).run(1).ev().value shouldBe Tuple2(2, "1")
        }

        "addOne.runS(1) should return 2" {
            addOne.runS(1).ev().value shouldBe 2
        }

        "addOne.runA(1) should return 1" {
            addOne.runA(1).ev().value shouldBe 1
        }
    }
}
