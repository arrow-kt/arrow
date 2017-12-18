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

import org.funktionale.either.Either.Left
import org.funktionale.either.Either.Right
import org.funktionale.option.Option
import org.funktionale.option.Option.None
import org.funktionale.option.Option.Some
import java.util.*

class LeftProjection<out L, out R>(val e: Either<L, R>) {

    fun get(): L = when (e) {
        is Left -> e.l
        is Right -> throw NoSuchElementException("Either.left.value on Right")
    }

    fun forEach(f: (L) -> Unit) {
        when (e) {
            is Left<L, R> -> f(e.l)
        }
    }

    fun exists(predicate: (L) -> Boolean): Boolean = when (e) {
        is Left -> predicate(e.l)
        is Right -> false
    }

    fun <X> map(f: (L) -> X): Either<X, R> = flatMap { Left<X, R>(f(it)) }

    fun filter(predicate: (L) -> Boolean): Option<Either<L, R>> = when (e) {
        is Left -> {
            if (predicate(e.l)) {
                Some(e)
            } else {
                None
            }
        }
        is Right -> None
    }

    fun toList(): List<L> = when (e) {
        is Left -> listOf(e.l)
        is Right -> listOf()
    }

    fun toOption(): Option<L> = when (e) {
        is Left -> Some(e.l)
        is Right -> None
    }

}

fun <L, R, X> LeftProjection<L, R>.flatMap(f: (L) -> Either<X, R>): Either<X, R> = when (e) {
    is Left -> f(e.l)
    is Right -> Right(e.r)
}

fun <L, R, X, Y> LeftProjection<L, R>.map(x: Either<X, R>, f: (L, X) -> Y): Either<Y, R> = flatMap { l -> x.left().map { xx -> f(l, xx) } }

fun <L, R> LeftProjection<L, R>.getOrElse(default: () -> L): L = when (e) {
    is Left -> e.l
    is Right -> default()
}
