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
public class LeftProjection<out L, out R>(val e: Either<L, R>) {

    public fun get(): L {
        return when (e) {
            is Left<L, R> -> e.l
            else -> throw NoSuchElementException("Either.left.value on Right")
        }
    }

    public fun forEach(f: (L) -> Unit) {
        return when (e) {
            is Left<L, R> -> f(e.l)
            else -> {
            }
        }
    }


    public fun exists(predicate: (L) -> Boolean): Boolean {
        return when (e) {
            is Left<L, R> -> predicate(e.l)
            else -> false
        }
    }


    public fun<X> map(f: (L) -> X): Either<X, R> {
        return flatMap { Left(f(it)) }
    }

    public fun filter(predicate: (L) -> Boolean): Option<Either<L, R>> {
        return when (e) {
            is Left<L, R> -> {
                if (predicate(e.l)) {
                    Some(e)
                } else {
                    None
                }
            }
            else -> None
        }
    }

    public fun toList(): List<L> {
        return when (e) {
            is Left<L, R> -> listOf(e.l)
            else -> listOf()
        }
    }

    public fun toOption(): Option<L> {
        return when (e) {
            is Left<L, R> -> Some(e.l)
            else -> None
        }
    }

}

public fun<L, R, X> LeftProjection<L, R>.flatMap(f: (L) -> Either<X, R>): Either<X, R> {
    return when (e) {
        is Left<L, R> -> f(e.l)
        is Right<L, R> -> Right(e.r)
    }
}

public fun<L, R, X, Y> LeftProjection<L, R>.map(x: Either<X, R>, f: (L, X) -> Y): Either<Y, R> {
    return flatMap { l -> x.left().map { xx -> f(l,xx) } }
}

public fun<R, L> LeftProjection<L, R>.getOrElse(default: () -> L): L {
    return when (e) {
        is Left<L, R> -> e.l
        else -> default()
    }
}