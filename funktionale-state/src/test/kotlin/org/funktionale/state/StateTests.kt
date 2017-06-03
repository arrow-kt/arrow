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

package org.funktionale.state

import org.testng.Assert.assertEquals
import org.testng.annotations.Test

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 29/05/17
 * Time: 1:14 AM
 */
class StateTests {

	val add1 = State { n: Int -> n + 1 to n }

	@Test fun basic() {
		assertEquals(add1.run(1), 2 to 1)
	}

	@Test fun traverse() {
		val ns = (0..10).toList()
		val x = ns.stateTraverse { add1 }
		assertEquals(x.run(0).first, 11)
	}

	@Test fun pure() {
		val s1 = State.pure<String, Int>(1)
		assertEquals(s1.run("foo"), "foo" to 1)
	}

	@Test fun get() {
		val s1 = State.get<String>()
		assertEquals(s1.run("foo"), "foo" to "foo")
	}

	@Test fun modify() {
		val s1 = State.modify<String> { "bar" }
		val s2 = State.set("bar")
		assertEquals(s1.run("foo"), s2.run("foo"))
	}
}