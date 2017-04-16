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

sealed class Free<S, A> : FreeKind<S, A> {

    class F private constructor()

    companion object {
        fun <S, A> pure(a : A): Free<S, A> = Pure(a)
        fun <S, A> liftF(fa: HK<S, A>): Free<S, A> = Suspend(fa)
    }

    private data class Pure<S, A>(val a: A) : Free<S, A>()
    private data class Suspend<S, A>(val a: HK<S, A>) : Free<S, A>()
    private data class FlatMapped<S, B, C>(val c: Free<S, C>, val f: (C) -> Free<S, B>) : Free<S, B>()

    fun <B> map(f: (A) -> B): Free<S, B> =
            flatMap { Pure<S, B>(f(it)) }

    fun <B> flatMap(f: (A) -> Free<S, B>): Free<S, B> =
            FlatMapped(this, f)

    /**
     * Mutable nonsense ahead
     * Can't get Kotlin to find tailrec position in recursive version
     */
    @Suppress("UNCHECKED_CAST")
    fun step(): Free<S, A> {
        var self = this
        while (true) {
            if (self is Pure<*,*> || self is Suspend<*, *>) {
                break
            } else if (self is FlatMapped<*, *, *>) {
                val xf = self.f as (A) -> Free<S, A>
                if (self.c is Pure) {
                    val xc = self.c as Pure<S, A>
                    self = xf(xc.a)
                    continue
                }
                if (self.c is FlatMapped<*, *, *>) {
                    val xc = self.c as FlatMapped<S, A, A>
                    val xc2 = xc.c
                    self = xc2.flatMap { cc -> xc.f(cc).flatMap(xc.f)}
                    continue
                } else {
                    break
                }
            }
        }
        return self
    }


//    @Suppress("UNCHECKED_CAST")
//    tailrec fun step(): Free<S, A> {
//        return when (this) {
//            is Free.Pure -> this
//            is Free.Suspend -> this
//            is Free.FlatMapped<S, *, *> -> {
//                val xf = this.f as (A) -> Free<S, A>
//                val xc = this.c as Free<S, A>
//                return when (this) {
//                    is Free.Pure -> xf(this.a)
//                    is Free.Suspend -> this
//                    is Free.FlatMapped<S, *, *> -> xc.flatMap { xf(it).flatMap(xf) }.step()
//                }
//            }
//        }
//    }

    @Suppress("UNCHECKED_CAST")
    fun resume(SF: Functor<S>): Either<HK<S, Free<S, A>>, A> = when (this) {
        is Pure -> Either.Right(this.a)
        is Suspend -> Either.Left(SF.map(this.a, { Pure<S, A>(it) }))
        is FlatMapped<S, *, *> -> {
            val xf = (this.f as (A) -> Free<S, A>)
            val xc = this.c as Free<S, A>
            when (xc) {
                is FlatMapped<S, *, *> -> {
                    val xc2 = xc.c as Free<S, A>
                    xc2.flatMap { xf(it).flatMap(xf) }.resume(SF)
                }
                is Pure<S, A> -> xf(xc.a).resume(SF)
                is Suspend<S, A> -> Either.Left(SF.map(xc.a, xf))

            }
        }
    }

    fun <B> fold(SF: Functor<S>, r: (A) -> B, s: (HK<S, Free<S, A>>) -> B): B =
            resume(SF).fold(s, r)

    private fun loop(SF: Functor<S>, t: Free<S, A>, f: (HK<S, Free<S, A>>) -> Free<S, A>): A =
            t.resume(SF).fold({ l -> loop(SF, f(l), f) }, { it })

    fun go(SF: Functor<S>, f: (HK<S, Free<S, A>>) -> Free<S, A>): A = loop(SF, this, f)

    @Suppress("UNCHECKED_CAST")
    fun <M> foldMap(MM: Monad<M>, f: FunctionK<S, M>): HK<M, A> =
            MM.tailRecM(step()) {
                when (it) {
                    is Pure<S, A> -> MM.pure(Either.Right(it.a))
                    is Suspend<S, A> -> MM.map(f(it.a), { a -> Either.Right(a) })
                    is FlatMapped<S, A, *> -> {
                        val xf = (it.f as (A) -> Free<S, A>)
                        val xc = it.c as Free<S, A>
                        MM.map(xc.foldMap(MM, f), { a -> Either.Left(xf(a)) })
                    }
                }
            }

}
