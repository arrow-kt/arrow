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
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class GlobalInstancesTest : UnitSpec() {

    inline fun <reified F> testTypeclassHierarchyInference(a : Any) {
        functor<F>() shouldBe a
        applicative<F>() shouldBe a
        monad<F>() shouldBe a
    }

    init {

        "Id inference" {
            testTypeclassHierarchyInference<Id.F>(Id)
        }

        "NonEmptyList monad inference" {
            testTypeclassHierarchyInference<NonEmptyList.F>(NonEmptyList)
        }

        "Option monad inference" {
            testTypeclassHierarchyInference<Option.F>(Option)
        }
    }
}
