/*
 * Copyright (C) 2017 The Katz Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package katz

/**
 * A List that can not be empty
 */
class NonEmptyList<out A> private constructor(
        val head: A,
        val tail: List<A>,
        val all: List<A>) {

    constructor(head: A, tail: List<A>) : this(head, tail, listOf(head) + tail)
    private constructor(list: List<A>) : this(list[0], list.drop(1), list)

    val size: Int = all.size

    fun contains(element: @UnsafeVariance A): Boolean {
        return (head == element).or(tail.contains(element))
    }

    fun containsAll(elements: Collection<@UnsafeVariance A>): Boolean =
            elements.all { contains(it) }

    fun isEmpty(): Boolean = false

    fun <B> map(f: (A) -> B): NonEmptyList<B> =
            NonEmptyList(f(head), tail.map(f))

    fun <B> flatMap(f: (A) -> NonEmptyList<B>): NonEmptyList<B> =
            f(head) + tail.flatMap { f(it).all }

    operator fun <A> NonEmptyList<A>.plus(l: NonEmptyList<A>): NonEmptyList<A> = NonEmptyList(all + l.all)

    operator fun <A> NonEmptyList<A>.plus(l: List<A>): NonEmptyList<A> = NonEmptyList(all + l)

    operator fun <A> NonEmptyList<A>.plus(a: A): NonEmptyList<A> = NonEmptyList(all + a)

    fun iterator(): Iterator<A> = all.iterator()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as NonEmptyList<*>

        if (all != other.all) return false

        return true
    }

    override fun hashCode(): Int {
        return all.hashCode()
    }

    override fun toString(): String {
        return "NonEmptyList(all=$all)"
    }

    companion object Factory {
        fun <A> of(head: A, vararg t: A): NonEmptyList<A> = NonEmptyList(head, t.asList())
    }
}
