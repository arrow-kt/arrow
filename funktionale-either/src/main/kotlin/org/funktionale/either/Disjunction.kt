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

package org.funktionale.either

import arrow.Option
import arrow.hashCodeForNullable
import arrow.syntax.collections.prependTo

sealed class Disjunction<out L, out R> : EitherLike {

    companion object {
        fun <L> left(left: L): Left<L, Nothing> = Left(left)
        fun <R> right(right: R): Right<Nothing, R> = Right(right)
    }

    operator abstract fun component1(): L?
    operator abstract fun component2(): R?

    fun swap(): Disjunction<R, L> = when (this) {
        is Right -> Left(value)
        is Left -> Right(value)
    }

    fun <X> fold(fl: (L) -> X, fr: (R) -> X): X = when (this) {
        is Right -> fr(value)
        is Left -> fl(value)
    }

    fun get(): R = when (this) {
        is Right -> value
        is Left -> throw NoSuchElementException("Disjunction.Left")
    }

    fun forEach(f: (R) -> Unit) {
        when (this) {
            is Right -> f(value)
        }
    }

    fun exists(predicate: (R) -> Boolean): Boolean = when (this) {
        is Right -> predicate(value)
        is Left -> false
    }

    fun <X> map(f: (R) -> X): Disjunction<L, X> = when (this) {
        is Right -> Right(f(value))
        is Left -> Left(value)
    }

    fun filter(predicate: (R) -> Boolean): Option<Disjunction<L, R>> = when (this) {
        is Right -> if (predicate(value)) {
            Option.Some(this)
        } else {
            Option.None
        }
        is Left -> Option.None
    }

    fun toList(): List<R> = when (this) {
        is Right -> listOf(value)
        is Left -> listOf()
    }

    fun toOption(): Option<R> = when (this) {
        is Right -> Option.Some(value)
        is Left -> Option.None
    }

    fun toEither(): Either<L, R> = when (this) {
        is Right -> Either.Right(value)
        is Left -> Either.Left(value)
    }

    class Left<out L, out R>(val value: L) : Disjunction<L, R>(), LeftLike {
        override fun component1(): L = value
        override fun component2(): R? = null
        override fun equals(other: Any?): Boolean = when (other) {
            is Left<*, *> -> value == other.value
            else -> false
        }

        override fun hashCode(): Int = value.hashCodeForNullable(43) { a, b -> a * b }

        override fun toString(): String = "Disjunction.Left($value)"

    }

    class Right<out L, out R>(val value: R) : Disjunction<L, R>(), RightLike {
        override fun component1(): L? = null
        override fun component2(): R = value

        override fun equals(other: Any?): Boolean = when (other) {
            is Right<*, *> -> value == other.value
            else -> false
        }

        override fun hashCode(): Int = value.hashCodeForNullable(43) { a, b -> a * b }

        override fun toString(): String = "Disjunction.Right($value)"

    }
}

inline fun <T> disjunctionTry(body: () -> T): Disjunction<Throwable, T> = try {
    Disjunction.Right(body())
} catch (t: Throwable) {
    Disjunction.Left(t)
}

fun <T> Disjunction<T, T>.merge(): T = when (this) {
    is Disjunction.Right -> value
    is Disjunction.Left -> value
}

fun <L, R> Disjunction<L, R>.getOrElse(default: () -> R): R = when (this) {
    is Disjunction.Right -> value
    is Disjunction.Left -> default()
}

fun <X, L, R> Disjunction<L, R>.flatMap(f: (R) -> Disjunction<L, X>): Disjunction<L, X> = when (this) {
    is Disjunction.Right -> f(value)
    is Disjunction.Left -> Disjunction.Left(value)
}

fun <L, R, X, Y> Disjunction<L, R>.map(x: Disjunction<L, X>, f: (R, X) -> Y): Disjunction<L, Y> = flatMap { r -> x.map { xx -> f(r, xx) } }

fun <T, L, R> List<T>.disjuntionTraverse(f: (T) -> Disjunction<L, R>): Disjunction<L, List<R>> = foldRight(Disjunction.Right(emptyList())) { i: T, accumulator: Disjunction<L, List<R>> ->
    val disjunction = f(i)
    when (disjunction) {
        is Disjunction.Right -> disjunction.map(accumulator) { head: R, tail: List<R> ->
            head prependTo tail
        }
        is Disjunction.Left -> Disjunction.Left(disjunction.value)
    }
}

fun <L, R> List<Disjunction<L, R>>.disjunctionSequential(): Disjunction<L, List<R>> = disjuntionTraverse { it }

inline fun <X, T> Option<T>.toDisjunctionRight(left: () -> X): Disjunction<X, T> = toEitherRight(left).toDisjunction()

inline fun <X, T> Option<T>.toDisjunctionLeft(right: () -> X): Disjunction<T, X> = toEitherLeft(right).toDisjunction()