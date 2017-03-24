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
 * Port of https://github.com/scala/scala/blob/v2.12.1/src/library/scala/util/Either.scala
 *
 * Represents a value of one of two possible types (a disjoint union.)
 * An instance of Either is either an instance of [Left] or [Right].
 */
sealed class Either<out A, out B> {

    inline fun <C> fold(fa: (A) -> C, fb: (B) -> C): C = when (this) {
        is Right -> fb(b)
        is Left -> fa(a)
    }

    fun swap(): Either<B, A> =
            fold({ Right(it) }, { Left(it) })

    inline fun <C> map(f: (B) -> C): Either<A, C> =
            fold({ Left(it) }, { Right(f(it)) })

    fun <BB, B> contains(elem: () -> BB): Boolean where B : BB =
            fold({ false }, { it == elem() })

    inline fun exists(predicate: (B) -> Boolean): Boolean =
            fold({ false }, { predicate(it) })

    inline fun filterOrElse(predicate: (B) -> Boolean, default: () -> A): Either<A, B> =
            fold({ Left(it) }, { if (predicate(it)) else Left(default()) })

    inline fun <C>flatMap(f: (B) -> Either<out A, C>): Either<A, C> =
            fold({ Either.Left(it) }, { f(it) })

    fun toOption(): Option<B> =
            fold({ Option.None }, { Option.Some(it) })


    data class Left<out A>(val a: A) : Either<A, Nothing>()
    data class Right<out B>(val b: B) : Either<Nothing, B>()
}


inline fun <A, B> Either<A, B>.getOrElse(default: () -> B): B =
        fold({ default() }, { b -> b })