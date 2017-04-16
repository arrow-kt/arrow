/*
 * Copyright (C) 2017 The Katz Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package katz

interface TryMonadError : MonadError<Try.F, Throwable> {

    override fun <A, B> map(fa: TryKind<A>, f: (A) -> B): Try<B> = fa.ev().map(f)

    override fun <A> pure(a: A): Try<A> = Try.Success(a)

    override fun <A, B> flatMap(fa: TryKind<A>, f: (A) -> TryKind<B>): Try<B> = fa.ev().flatMap { f(it).ev() }

    override fun <A> raiseError(e: Throwable): Try<A> = Try.Failure(e)

    override fun <A> handleErrorWith(fa: TryKind<A>, f: (Throwable) -> TryKind<A>): Try<A> =
            fa.ev().recoverWith { f(it).ev() }

    @Suppress("UNCHECKED_CAST")
    override fun <A, B> tailRecM(a: A, f: (A) -> TryKind<Either<A, B>>): Try<B> {
        val x = f(a).ev()
        return if (x is Try.Success && x.value is Either.Left<A>) tailRecM(x.value.a, f)
        else if (x is Try.Success && x.value is Either.Right<B>) Try.Success(x.value.b)
        else x as Try.Failure<B>
    }
}

fun <A> TryKind<A>.ev(): Try<A> = this as Try<A>