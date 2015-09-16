/*
 * Copyright 2013 Mario Arias
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
import org.funktionale.option.Option.*
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 17/05/13
 * Time: 20:20
 */
public class RightProjection<out L, out R>(val e: Either<L, R>) {

    public fun get(): R {
        return when (e) {
            is Right<L, R> -> e.r
            else -> throw NoSuchElementException("Either.right.value on Left")
        }
    }

    public fun forEach(f: (R) -> Unit) {
        return when (e) {
            is Right<L, R> -> f(e.r)
            else -> {
            }
        }
    }


    public fun exists(predicate: (R) -> Boolean): Boolean {
        return when (e) {
            is Right<L, R> -> predicate(e.r)
            else -> false
        }
    }

    public fun<X> map(f: (R) -> X): Either<L, X> {
        return flatMap { Right(f(it)) }
    }

    public fun filter(predicate: (R) -> Boolean): Option<Either<L, R>> {
        return when (e) {
            is Right<L, R> -> {
                if (predicate(e.r)) {
                    Some(e)
                } else {
                    None
                }
            }
            else -> None
        }
    }

    public fun toList(): List<R> {
        return when (e) {
            is Right<L, R> -> listOf(e.r)
            else -> listOf()
        }
    }

    public fun toOption(): Option<R> {
        return when (e) {
            is Right<L, R> -> Some(e.r)
            else -> None
        }
    }

}

public fun<L, R> RightProjection<L, R>.getOrElse(default: () -> R): R {
    return when (e) {
        is Right<L, R> -> e.r
        else -> default()
    }
}

public fun<X, L, R> RightProjection<L, R>.flatMap(f: (R) -> Either<L, X>): Either<L, X> {
    return when (e) {
        is Left<L, R> -> Left(e.l)
        is Right<L, R> -> f(e.r)
    }
}



public fun<L, R, X, Y> RightProjection<L, R>.map(x: Either<L, X>, f: (R, X) -> Y): Either<L, Y> {
    return flatMap { r -> x.right().map { xx -> f(r,xx) } }
}