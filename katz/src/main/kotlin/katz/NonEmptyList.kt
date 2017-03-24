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
        override val head: A,
        override val tail: List<A>,
        val all: List<A>) : NonEmptyCollection<A> {

    constructor(head: A, tail: List<A>) : this(head, tail, listOf(head) + tail)
    private constructor(list: List<A>) : this(list[0], list.drop(1), list)

    override val size: Int = all.size

    inline fun <reified B> map(f: (A) -> B): NonEmptyList<B> =
            NonEmptyList(f(head), tail.map(f))

    inline fun <reified B> flatMap(f: (A) -> List<B>): NonEmptyList<B> =
            unsafeFromList(all.flatMap(f))

    override fun iterator(): Iterator<A> = all.iterator()

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
        inline fun <reified A> of(head: A, vararg t: A): NonEmptyList<A> = NonEmptyList(head, t.asList())
        fun <A> unsafeFromList(l: List<A>): NonEmptyList<A> = NonEmptyList(l)
    }
}

interface NonEmptyCollection<out A> : Collection<A> {
    val head: A
    val tail: Collection<A>

    override fun contains(element: @UnsafeVariance A): Boolean {
        return (head == element).or(tail.contains(element))
    }

    override fun containsAll(elements: Collection<@UnsafeVariance A>): Boolean =
            elements.all { contains(it) }

    override fun isEmpty(): Boolean = false
}
