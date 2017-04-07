/*
 * Copyright (C) 2017 The Kats Authors
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
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ReaderTest : UnitSpec() {
    init {
        "map should return mapped value" {
            Reader<Int, Int> { it -> it * 2 }.map { it -> it * 3 }.runId(2) shouldBe 12
        }

        "flatMap should map over the inner value" {
            Reader<Int, Int> ({ it -> it * 2 }).flatMap { a -> Reader.pure<Int, Int>(a * 3) }
                    .run(2).value() shouldBe 12
        }

        "zip should return a new Reader zipping two given ones" {
            val r1 = Reader<Int, Int> ({ it -> it * 2 })
            val r2 = Reader<Int, Int> ({ it -> it * 3 })
            r1.zip(r2).run(2).value() shouldBe Pair(4, 6)
        }

        "local should switch context to be able to combine Readers with different contexts" {
            val r = Reader<Int, Int> ({ it -> it * 2 })
            r.local<Boolean> { it -> if (it) 1 else 3 }.runId(false) shouldBe 6
            r.local<Boolean> { it -> if (it) 1 else 3 }.runId(true) shouldBe 2
        }

        "reader should lift a reader from any (A) -> B function" {
            val r = { x: Int -> Id(x * 2) }.reader()
            r::class.java shouldBe Kleisli::class.java
            r.runId(2).value() shouldBe 4
        }

    }
}
