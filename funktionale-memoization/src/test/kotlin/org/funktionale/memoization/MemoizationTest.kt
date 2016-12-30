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

package org.funktionale.memoization

import org.testng.Assert.assertEquals
import org.testng.annotations.Test

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 15/02/16
 * Time: 11:27 PM
 */
@Test
class MemoizationTest {


    @Test fun memoize() {
        var counterA = 0
        var counterB = 0

        val a = { i: Int -> counterA++ }
        val b = { i: Int -> counterB++ }.memoize()


        (1..5).forEach { a(1) }
        (1..5).forEach { b(1) }

        assertEquals(counterA, 5)
        assertEquals(counterB, 1) // calling several times a memoized function with the same parameter is computed just once

    }

    @Test fun memoizeEmpty() {
        var counterA = 0
        var counterB = 0

        val a = { counterA++ }
        val b = { counterB++ }.memoize()


        (1..5).forEach { a() }
        (1..5).forEach { b() }

        assertEquals(counterA, 5)
        assertEquals(counterB, 1) // calling several times a memoized function with the same parameter is computed just once

    }
}

