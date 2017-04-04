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
import katz.Validated.*
import org.junit.runner.RunWith

@RunWith(KTestJUnitRunner::class)
class ValidatedTest : UnitSpec() {

    init {

        "map should modify value" {
            Valid(10).map { "David Tennant" } shouldBe Valid("David Tennant")
            Invalid(13).map { "Comming soon!" } shouldBe Invalid(13)
        }

        "leftMap should modify error" {
            Valid(10).leftMap { "David Tennant" } shouldBe Valid(10)
            Invalid(13).map { "Coming soon!" } shouldBe Invalid("Coming soon!")
        }

        "bimap should modify the value if this is Valid or the error in otherwise" {
            Valid(10).bimap({ "Coming soon!" }, { "David Tennant" }) shouldBe Valid("David Tennant")
            Invalid(13).bimap({ "Coming soon!" }, { "David Tennant" }) shouldBe Invalid("Coming soon!")
        }
    }
}
