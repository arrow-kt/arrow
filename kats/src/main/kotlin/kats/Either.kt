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
 * Port of https://github.com/scala/scala/blob/v2.12.1/src/library/scala/Either.scala
 *
 * Represents a value of one of two possible types (a disjoint union.)
 * An instance of Either is either an instance of [Left] or [Right].
 */
sealed class Either<out A, out B> {

    inline fun <C> fold(fl: (A) -> C, fr: (B) -> C): C = when (this) {
        is Right -> fr(b)
        is Left -> fl(a)
    }

    fun swap(): Either<B, A> =
            fold({ Right<B, A>(it) }, { Left<B, A>(it) })

    inline fun <C> map(f: (B) -> C): Either<A, C> =
            fold({ it as Either<A, C> }, { Right(f(it)) })

    fun <BB, B> contains(elem: BB): Boolean where B : BB =
            fold({ false }, { it == elem })

    inline fun exists(predicate: (B) -> Boolean): Boolean =
            fold({ false }, { predicate(it) })

    inline fun <AA, A> filterOrElse(predicate: (B) -> Boolean, default: AA): Either<AA, B> where A : AA =
            fold({ this as Either<AA, B> }, { if (predicate(it)) this as Either<AA, B> else Left(default) })



    class Left<out A, out B>(val a: A) : Either<A, B>()
    class Right<out A, out B>(val b: B) : Either<A, B>()
}

inline fun <AA, A, B, C> Either<A, B>.flatMap(f: (B) -> Either<AA, C>): Either<AA, C> where A : AA =
        fold({ this as Either<AA, C> }, { f(it) })

inline fun <A, B, BB> Either<A, B>.getOrElse(default: () -> BB): BB where B : BB = fold({ default() }, { b -> b })