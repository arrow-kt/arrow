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

package org.funktionale.currying

import org.testng.Assert.assertEquals
import org.testng.Assert.assertTrue
import org.testng.annotations.Test

class CurryingTest {
    @Test fun testCurrying() {
        val sum2ints = { x: Int, y: Int -> x + y }
        val curried = sum2ints.curried()
        assertEquals(curried(2)(4), 6)
        val add5 = curried(5)
        assertEquals(add5(7), 12)
    }

    @Test fun testUncurrying() {
        val sum2ints: (Int, Int) -> Int = { x, y -> x + y }
        val curried: (Int) -> (Int) -> Int = sum2ints.curried()
        assertEquals(curried(2)(4), 6)
        //same type as sum2ints,
        assertEquals(curried.uncurried()(2, 4), 6)
        assertEquals(sum2ints(2, 4), 6)

        val sum3ints: (Int, Int, Int) -> Int = { x, y, _ -> x + y }
        val f: (Int) -> (Int) -> (Int) -> Int = sum3ints.curried()
        assertTrue(f.uncurried() is (Int, Int, Int) -> Int)
    }
}