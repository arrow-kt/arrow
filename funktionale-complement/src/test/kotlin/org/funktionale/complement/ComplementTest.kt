/*
Copyright 2016 Joel Whittaker-Smith

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.funktionale.complement

import org.testng.Assert.assertEquals
import org.testng.annotations.Test

class ComplementTest {

    @Test fun testComplement() {
        val isEven = { x: Int -> x % 2 == 0 }
        assertEquals(isEven(2), true)

        val notEven = isEven.complement()
        assertEquals(notEven(2), false)
    }
}
