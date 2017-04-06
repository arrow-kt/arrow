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

object IntMonoid : Monoid<Int>, GlobalInstance<Monoid<Int>>() {
    override fun empty(): Int = 0

    override fun combine(a: Int, b: Int): Int = a + b
}

inline fun <reified A> ListMonoid(): Monoid<List<A>> = object : Monoid<List<A>>, GlobalInstance<Monoid<List<A>>>() {
    override fun empty(): List<A> = emptyList()

    override fun combine(a: List<A>, b: List<A>): List<A> = a + b
}