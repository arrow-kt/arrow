/*
 * Copyright 2013 - 2017 Mario Arias
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

package org.funktionale.composition

import org.testng.Assert.assertEquals
import org.testng.annotations.Test
import java.util.*

class ComposeTest {

    @Test
    fun `it should compose function correctly (andThen)`() {
        val potato = "potato"
        val ninja = "ninja"
        val get = { potato }
        val map = { word: String -> ninja + word }
        assertEquals(ninja + potato, (get andThen map)())
    }

    @Test
    fun `it should compose function correctly (forwardCompose)`() {
        val randomDigit = Random().nextInt()
        val get = { randomDigit }
        val pow = { i: Int -> i * i }
        assertEquals(randomDigit * randomDigit, (get forwardCompose pow)())
    }

}