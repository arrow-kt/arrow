/*
 * Copyright (C) 2017 The Katz Authors
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

package katz

typealias FreeKind<S, A> = HK2<Free.F, S, A>
typealias FreeF<S> = HK<Free.F, S>

fun <S, A> FreeKind<S, A>.ev(): Free<S, A> = this as Free<S, A>

sealed class Free<out S, out A> : FreeKind<S, A> {

    class F private constructor()

    companion object {
        fun <S, A> pure(a: A): Free<S, A> = Pure(a)
        fun <S, A> liftF(fa: HK<S, A>): Free<S, A> = Suspend(fa)
    }

    data class Pure<out S, out A>(val a: A) : Free<S, A>()
    data class Suspend<out S, out A>(val a: HK<S, A>) : Free<S, A>()
    data class FlatMapped<out S, out B, C>(val c: Free<S, C>, val f: (C) -> Free<S, B>) : Free<S, B>()

    override fun toString(): String = "Free(...) : toString is not stack-safe"
}

fun <S, A, B> Free<S, A>.map(f: (A) -> B): Free<S, B> =
        flatMap { Free.Pure<S, B>(f(it)) }

fun <S, A, B> Free<S, A>.flatMap(f: (A) -> Free<S, B>): Free<S, B> =
        Free.FlatMapped(this, f)

@Suppress("UNCHECKED_CAST")
tailrec fun <S, A> Free<S, A>.step(): Free<S, A> =
    if (this is Free.FlatMapped<S, A, *> && this.c is Free.FlatMapped<S, *, *>) {
        val g = this.f as (A) -> Free<S, A>
        val c = this.c.c as Free<S, A>
        val f = this.c.f as (A) -> Free<S, A>
        c.flatMap { cc -> f(cc).flatMap(g) }.step()
    } else if (this is Free.FlatMapped<S, A, *> && this.c is Free.Pure<S, *>) {
        val a = this.c.a as A
        val f = this.f as (A) -> Free<S, A>
        f(a).step()
    } else {
        this
    }

@Suppress("UNCHECKED_CAST")
fun <M, S, A> Free<S, A>.foldMap(MM: Monad<M>, f: FunctionK<S, M>): HK<M, A> =
        MM.tailRecM(this) {
            val x = it.step()
            when (x) {
                is Free.Pure<S, A> -> MM.pure(Either.Right(x.a))
                is Free.Suspend<S, A> -> MM.map(f(x.a), { Either.Right(it) })
                is Free.FlatMapped<S, A, *> -> {
                    val g = (x.f as (A) -> Free<S, A>)
                    val c = x.c as Free<S, A>
                    MM.map(c.foldMap(MM, f), { cc -> Either.Left(g(cc)) })
                }
            }
        }
