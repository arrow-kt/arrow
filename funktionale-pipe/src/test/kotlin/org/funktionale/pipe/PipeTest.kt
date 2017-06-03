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

package org.funktionale.pipe

import org.funktionale.utils.identity
import org.testng.Assert.assertEquals
import org.testng.annotations.Test


class PipeTest {


    private val values = listOf(1, "String", 10.2)
    private val intFunctions = listOf({ x: Int -> x }, { x: Int -> x * x })



    @Test fun testPipe() {

        values.forEach {
            assertEquals(it pipe identity() , it)
        }

        intFunctions.forEach {
            assertEquals(it(2), 2 pipe it)
        }
    }
}