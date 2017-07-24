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
class IterableTests : UnitSpec() {

    init {

        "Iterable.collect can filter and transform" {
            listOf(1, 2, 3, 4, 5).collect(
                    case({ n: Int -> n > 0 } then { (it * 2).toString() })
            ) shouldBe listOf("2", "4", "6", "8", "10")
        }

        "Iterable.collect can match and coalesce on multiple cases" {
            val l: List<Int> = listOf("1", Option.Some(1), 1).collect(
                    case(typeOf<String>() then { (it).toInt() }),
                    case(typeOf<Option.Some<Int>>() then { it.value }),
                    case(typeOf<Int>() then { it })
            )
            l shouldBe listOf(1, 1, 1)
        }

        "Iterable.collect returns emptyList when no matches are found" {
            listOf(1).collect(
                    case(typeOf<String>() then { (it).toInt() })
            ) shouldBe emptyList<Int>()
        }

        "Iterable.collect picks a matching partial function from the ones provided" {
            listOf(1).collect(
                    case(typeOf<String>() then { (it).toInt() }),
                    case(typeOf<Int>() then { it })
            ) shouldBe listOf(1)
        }

    }
}
