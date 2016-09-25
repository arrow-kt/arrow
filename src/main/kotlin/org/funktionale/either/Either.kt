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

import org.funktionale.collections.prependTo
import org.funktionale.either.Either.Left
import org.funktionale.either.Either.Right

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 17/05/13
 * Time: 19:01
 */
sealed class Either<out L : Any, out R : Any> : EitherLike {

    fun left(): LeftProjection<L, R> = LeftProjection(this)
    fun right(): RightProjection<L, R> = RightProjection(this)

    operator abstract fun component1(): L?
    operator abstract fun component2(): R?

    fun toDisjunction(): Disjunction<L, R> = when (this) {
        is Right -> Disjunction.Right(r)
        is Left -> Disjunction.Left(l)
    }

    fun <X : Any?> fold(fl: (L) -> X, fr: (R) -> X): X = when (this) {
        is Left -> fl(l)
        is Right -> fr(r)
    }

    fun swap(): Either<R, L> = when (this) {
        is Left -> Right(this.l)
        is Right -> Left(this.r)
    }

    class Left<out L : Any, out R : Any>(val l: L) : Either<L, R>(), LeftLike {
        override fun component1(): L = l
        override fun component2(): R? = null

        override fun equals(other: Any?): Boolean = when (other) {
            is Left<*, *> -> l.equals(other.l)
            else -> false

        }

        override fun hashCode(): Int = 43 * l.hashCode()

        override fun toString(): String = "Either.Left($l)"
    }

    class Right<out L : Any, out R : Any>(val r: R) : Either<L, R>(), RightLike {
        override fun component1(): L? = null
        override fun component2(): R = r

        override fun equals(other: Any?): Boolean = when (other) {
            is Right<*, *> -> r.equals(other.r)
            else -> false
        }

        override fun hashCode(): Int = 43 * r.hashCode()

        override fun toString(): String = "Either.Right($r)"
    }
}

fun <T : Any> Either<T, T>.merge(): T = when (this) {
    is Left -> this.l
    is Right -> this.r
}

fun <L : Any, R : Any> Pair<L, R>.toLeft(): Left<L, R> = Left(this.component1())

fun <L : Any, R : Any> Pair<L, R>.toRight(): Right<L, R> = Right(this.component2())

@Deprecated("Use eitherTry", ReplaceWith("eitherTry(body)"))
fun <T : Any> either(body: () -> T): Either<Exception, T> = eitherTry(body)

fun <T : Any> eitherTry(body: () -> T): Either<Exception, T> = try {
    Right(body())
} catch(e: Exception) {
    Left(e)
}

@Deprecated("Use eitherTraverse", ReplaceWith("eitherTraverse(f)"))
fun <T : Any?, L : Any, R : Any> List<T>.traverse(f: (T) -> Either<L, R>) = eitherTraverse(f)

fun <T : Any?, L : Any, R : Any> List<T>.eitherTraverse(f: (T) -> Either<L, R>): Either<L, List<R>> = foldRight(Right(emptyList())) { i: T, accumulator: Either<L, List<R>> ->
    val either = f(i)
    when (either) {
        is Right -> either.right().map(accumulator) { head: R, tail: List<R> ->
            head prependTo tail
        }
        is Left -> Left(either.l)
    }
}

@Deprecated("Use eitherSequential", ReplaceWith("eitherSequential()"))
fun <L : Any, R : Any> List<Either<L, R>>.sequential() = eitherSequential()

fun <L : Any, R : Any> List<Either<L, R>>.eitherSequential(): Either<L, List<R>> = eitherTraverse { it }
