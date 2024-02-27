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

package arrow.core

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

class CollectionsSyntaxTests {
    @Test fun tail() = runTest {
      listOf(1, 2, 3).tail() shouldBe listOf(2, 3)
    }

    @Test fun prependTo() = runTest {
      1 prependTo listOf(2, 3) shouldBe listOf(1, 2, 3)
    }

    @Test fun destructured() = runTest {
      val (head, tail) = listOf(1, 2, 3).let { it.first() to it.tail() }
      head shouldBe 1
      tail shouldBe listOf(2, 3)
    }
}
