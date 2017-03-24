/*
 * Copyright (C) 2017 The Kats Authors
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

/**
 * Port of https://github.com/scala/scala/blob/v2.12.1/src/library/scala/Option.scala
 *
 * Represents optional values. Instances of `Option`
 * are either an instance of $some or the object $none.
 */
sealed class Option<out A> {

    companion object {

        inline operator fun <A> invoke(f: () -> A): Option<A> = f().let { if (it == null) None else Some(it) }
    }

    /**
     * Returns true if the option is $none, false otherwise.
     */
    abstract val isEmpty: Boolean

    /**
     * Returns true if the option is an instance of $some, false otherwise.
     */
    val isDefined: Boolean = !isEmpty

    /**
     * Returns a $some containing the result of applying $f to this $option's
     * value if this $option is nonempty. Otherwise return $none.
     *
     *  @note This is similar to `flatMap` except here,
     *  $f does not need to wrap its result in an $option.
     *
     *  @param  f   the function to apply
     *  @see flatMap
     */
    inline fun <B> map(f: (A) -> B): Option<B> = fold({ None }, { a -> Some(f(a)) })

    /**
     * Returns the result of applying $f to this $option's value if
     * this $option is nonempty.
     * Returns $none if this $option is empty.
     * Slightly different from `map` in that $f is expected to
     * return an $option (which could be $none).
     *
     * @param  f   the function to apply
     * @see map
     */
    inline fun <B> flatMap(f: (A) -> Option<B>): Option<B> = fold({ None }, { a -> f(a) })

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
    inline fun <B> fold(ifEmpty: () -> B, f: (A) -> B): B = when(this) {
        is None -> ifEmpty()
        is Some -> f(value)
    }

    /**
     * Returns this $option if it is nonempty '''and''' applying the predicate $p to
     * this $option's value returns true. Otherwise, return $none.
     *
     *  @param  p   the predicate used for testing.
     */
    inline fun filter(p: (A) -> Boolean): Option<A> = fold({ None }, { a -> if (p(a)) Some(a) else None })

    /**
     * Returns this $option if it is nonempty '''and''' applying the predicate $p to
     * this $option's value returns false. Otherwise, return $none.
     *
     * @param  p   the predicate used for testing.
     */
    inline fun filterNot(p: (A) -> Boolean): Option<A> = fold({ None }, { a -> if (!p(a)) Some(a) else None })

    /**
     * Returns false if the option is $none, true otherwise.
     * @note   Implemented here to avoid the implicit conversion to Iterable.
     */
    val nonEmpty = isDefined

    /**
     * Returns true if this option is nonempty '''and''' the predicate
     * $p returns true when applied to this $option's value.
     * Otherwise, returns false.
     *
     * @param  p   the predicate to test
     */
    inline fun exists(p: (A) -> Boolean): Boolean = fold({ false }, { a -> p(a) })

    /**
     * Returns true if this option is empty '''or''' the predicate
     * $p returns true when applied to this $option's value.
     *
     * @param  p   the predicate to test
     */
    inline fun forall(p: (A) -> Boolean): Boolean = exists(p)

    class Some<A>(val value: A) : Option<A>() {
        override val isEmpty = false
    }

    object None : Option<Nothing>() {
        override val isEmpty = true
    }
}

/**
 * Returns the option's value if the option is nonempty, otherwise
 * return the result of evaluating `default`.
 *
 * @param default  the default expression.
 */
fun <B> Option<B>.getOrElse(default: () -> B): B = fold({ default() }, { b -> b })