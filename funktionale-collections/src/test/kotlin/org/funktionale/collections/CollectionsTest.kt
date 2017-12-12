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

package org.funktionale.collections

import org.testng.Assert.assertEquals
import org.testng.annotations.Test


class CollectionsTest {
    @Test fun tail() {
        assertEquals(listOf(1, 2, 3).tail(), listOf(2, 3))
    }

    @Test fun prependTo() {
        assertEquals(1 prependTo listOf(2, 3), listOf(1, 2, 3))
    }


    @Test fun destructured() {
        val (head, tail) = listOf(1, 2, 3).destructured()
        assertEquals(head, 1)
        assertEquals(tail, listOf(2, 3))
    }
}