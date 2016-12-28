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

class RightProjection<out L, out R>(val e: Either<L, R>) {

    fun get(): R = when (e) {
        is Right -> e.r
        is Left -> throw NoSuchElementException("Either.right.value on Left")
    }

    fun forEach(f: (R) -> Unit) {
        when (e) {
            is Right -> f(e.r)
        }
    }


    fun exists(predicate: (R) -> Boolean): Boolean = when (e) {
        is Right -> predicate(e.r)
        is Left -> false
    }

    fun <X> map(f: (R) -> X): Either<L, X> = flatMap { Right<L, X>(f(it)) }

    fun filter(predicate: (R) -> Boolean): Option<Either<L, R>> = when (e) {
        is Right -> {
            if (predicate(e.r)) {
                Some(e)
            } else {
                None
            }
        }
        is Left -> None
    }

    fun toList(): List<R> = when (e) {
        is Right -> listOf(e.r)
        is Left -> listOf()
    }

    fun toOption(): Option<R> = when (e) {
        is Right -> Some(e.r)
        is Left -> None
    }

}

fun <L, R> RightProjection<L, R>.getOrElse(default: () -> R): R = when (e) {
    is Right -> e.r
    is Left -> default()
}

fun <X, L, R> RightProjection<L, R>.flatMap(f: (R) -> Either<L, X>): Either<L, X> = when (e) {
    is Left -> Left(e.l)
    is Right -> f(e.r)
}


fun <L, R, X, Y> RightProjection<L, R>.map(x: Either<L, X>, f: (R, X) -> Y): Either<L, Y> = flatMap { r -> x.right().map { xx -> f(r, xx) } }
