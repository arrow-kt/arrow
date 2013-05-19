/*
 * Copyright 2013 Mario Arias
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

import org.testng.annotations.Test
import org.testng.Assert.*


public class CompositionTest {


    private val add5 = {(i: Int)-> i + 5 }
    private val multiplyBy2 = {(i: Int)-> i * 2 }

    [Test] fun testAndThen() {
        val add5andMultiplyBy2 = add5 andThen multiplyBy2
        assertEquals(add5andMultiplyBy2(2), 14)
    }

    [Test] fun testForwardCompose() {
        val add5andMultiplyBy2 = add5 forwardCompose multiplyBy2
        assertEquals(add5andMultiplyBy2(2), 14)
    }

    [Test] fun testCompose() {
        val multiplyBy2andAdd5 = add5 compose multiplyBy2
        assertEquals(multiplyBy2andAdd5(2), 9)
    }
}