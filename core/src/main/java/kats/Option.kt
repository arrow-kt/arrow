/*
 * Copyright (C) 2017
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

package kats

import java.util.*

/**
 * Port of https://github.com/scala/scala/blob/v2.12.1/src/library/scala/Option.scala
 *
 * Represents optional values. Instances of `Option`
 * are either an instance of $some or the object $none.
 */
sealed class Option<A> {

    /**
     * Returns true if the option is $none, false otherwise.
     */
    abstract val isEmpty: Boolean

    /**
     * Returns true if the option is an instance of $some, false otherwise.
     */
    val isDefined: Boolean = !isEmpty

    /**
     * Returns the option's value.
     * @note The option must be nonempty.
     * @throws java.util.NoSuchElementException if the option is empty.
     */
    abstract fun get(): A

    /**
     * Returns the option's value if the option is nonempty, otherwise
     * return the result of evaluating `default`.
     *
     * @param default  the default expression.
     */
    inline fun getOrElse(default: () -> A): A = if (isEmpty) default() else get()

    /**
     * Returns a $some containing the result of applying $f to this $option's
     * value if this $option is nonempty. Otherwise return $none.
     *
     *  @note This is similar to `flatMap` except here,
     *  $f does not need to wrap its result in an $option.
     *
     *  @param  f   the function to apply
     *  @see flatMap
     *  @see foreach
     */
    inline fun <B> map(f: (A) -> B): Option<B> = if (isEmpty) None() else Some(f(get()))

    /**
     * Returns the result of applying $f to this $option's value if
     * this $option is nonempty.
     * Returns $none if this $option is empty.
     * Slightly different from `map` in that $f is expected to
     * return an $option (which could be $none).
     *
     * @param  f   the function to apply
     * @see map
     * @see foreach
     */
    inline fun <B> flatMap(f: (A) -> Option<B>): Option<B> = if (isEmpty) None() else f(get())

    /**
     * Returns the result of applying $f to this $option's
     * value if the $option is nonempty.  Otherwise, evaluates
     * expression `ifEmpty`.
     *
     * @note This is equivalent to `$option map f getOrElse ifEmpty`.
     *
     * @param  ifEmpty the expression to evaluate if empty.
     * @param  f       the function to apply if nonempty.
     */
    inline fun <B> fold(ifEmpty: () -> B, f: (A) -> B): B = if (isEmpty) ifEmpty() else f(get())

    /**
     * Returns this $option if it is nonempty '''and''' applying the predicate $p to
     * this $option's value returns true. Otherwise, return $none.
     *
     *  @param  p   the predicate used for testing.
     */
    inline fun filter(p: (A) -> Boolean): Option<A> = if (isEmpty || p(get())) this else None()

    /**
     * Returns this $option if it is nonempty '''and''' applying the predicate $p to
     * this $option's value returns false. Otherwise, return $none.
     *
     * @param  p   the predicate used for testing.
     */
    inline fun filterNot(p: (A) -> Boolean): Option<A> = if (isEmpty || !p(get())) this else None()

    /**
     * Returns false if the option is $none, true otherwise.
     * @note   Implemented here to avoid the implicit conversion to Iterable.
     */
    val nonEmpty = isDefined

    /**
     * Tests whether the option contains a given value as an element.
     *
     * @param elem the element to test.
     * @return `true` if the option has an element that is equal (as
     * determined by `==`) to `elem`, `false` otherwise.
     */
    fun contains(elem: A): Boolean = !isEmpty && get() == elem

    /**
     * Returns true if this option is nonempty '''and''' the predicate
     * $p returns true when applied to this $option's value.
     * Otherwise, returns false.
     *
     * @param  p   the predicate to test
     */
    inline fun exists(p: (A) -> Boolean): Boolean = !isEmpty && p(get())

    /**
     * Returns true if this option is empty '''or''' the predicate
     * $p returns true when applied to this $option's value.
     *
     * @param  p   the predicate to test
     */
    inline fun forall(p: (A) -> Boolean): Boolean = exists(p)

    /**
     * Apply the given procedure $f to the option's value,
     * if it is nonempty. Otherwise, do nothing.
     *
     * @param  f   the procedure to apply.
     * @see map
     * @see flatMap
     */
    inline fun <B> foreach(f: (A) -> B) { if (!isEmpty) f(get()) }

    class Some<A>(val value: A) : Option<A>() {
        override val isEmpty = false
        override fun get(): A = value
    }

    class None<A> : Option<A>() {
        override val isEmpty = true
        override fun get(): Nothing = throw NoSuchElementException("None.get")
    }
}