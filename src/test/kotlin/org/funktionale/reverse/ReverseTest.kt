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

package org.funktionale.reverse

import org.testng.annotations.Test
import org.testng.Assert.*
import org.funktionale.partials.*

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 15/05/13
 * Time: 21:22
 */
public class ReverseTest {
    [Test] fun testReverse() {
        val f = { prefix: String, numericPostfix: Int, values: List<String> ->
            values.map { "$prefix$it$numericPostfix" }
        }

        val j: (String, List<String>) -> List<String> = f(p2 = 1)

        assertEquals(j("x", listOf("a", "b", "c")), j.reverse()(listOf("a", "b", "c"), "x"))
    }
}