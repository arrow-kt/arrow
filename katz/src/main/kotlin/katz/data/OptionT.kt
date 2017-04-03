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

/**
 * [OptionT]`<F, A>` is a light wrapper on an `F<`[Option]`<A>>` with some
 * convenient methods for working with this nested structure.
 *
 * It may also be said that [OptionT] is a monad transformer for [Option].
 */
data class OptionT<F, A>(val value: HK<F, Option<A>>) : HK2<OptionT.F, F, A> {

    class F private constructor()

    companion object {
        fun <M, A> pure(F: Monad<M>, a: A): OptionT<M, A> = OptionT(F.pure(Option.Some(a)))

        fun <M> none(F: Monad<M>): OptionT<M, Nothing> = OptionT(F.pure(Option.None))

        fun <M, A> fromOption(F: Monad<M>, value: Option<A>): OptionT<M, A> = OptionT(F.pure(value))
    }

    inline fun <B> fold(F: Functor<F>, crossinline default: () -> B, crossinline f: (A) -> B): HK<F, B> =
            F.map(value, { option -> option.fold({ default() }, { f(it) }) })

    inline fun <B> cata(F: Functor<F>, crossinline default: () -> B, crossinline f: (A) -> B): HK<F, B> =
            fold(F, { default() }, { f(it) })

    inline fun <B> flatMap(F: Monad<F>, crossinline f: (A) -> OptionT<F, B>): OptionT<F, B> = flatMapF(F, { it -> f(it).value })

    inline fun <B> flatMapF(F: Monad<F>, crossinline f: (A) -> HK<F, Option<B>>): OptionT<F, B> =
            OptionT(F.flatMap(value, { option -> option.fold({ F.pure(Option.None) }, { f(it) }) }))

    fun <B> liftF(F: Functor<F>, fa: HK<F, B>): OptionT<F, B> = OptionT(F.map(fa, { Option.Some(it) }))

    inline fun <B> semiflatMap(F: Monad<F>, crossinline f: (A) -> HK<F, B>): OptionT<F, B> =
            flatMap(F, { option -> liftF(F, f(option)) })

    inline fun <B> map(F: Functor<F>, crossinline f: (A) -> B): OptionT<F, B> =
            OptionT(F.map(value, { it.map(f) }))

    fun getOrElse(F: Functor<F>, default: () -> A): HK<F, A> = F.map(value, { it.getOrElse(default) })

    inline fun getOrElseF(F: Monad<F>, crossinline default: () -> HK<F, A>): HK<F, A> = F.flatMap(value, { it.fold(default, { F.pure(it) }) })

    inline fun filter(F: Functor<F>, crossinline p: (A) -> Boolean): OptionT<F, A> = OptionT(F.map(value, { it.filter(p) }))

    inline fun forall(F: Functor<F>, crossinline p: (A) -> Boolean): HK<F, Boolean> = F.map(value, { it.forall(p) })

    fun isDefined(F: Functor<F>): HK<F, Boolean> = F.map(value, { it.isDefined })

    fun isEmpty(F: Functor<F>): HK<F, Boolean> = F.map(value, { it.isEmpty })

    inline fun orElse(F: Monad<F>, crossinline default: () -> OptionT<F, A>): OptionT<F, A> =
            orElseF(F, { default().value })

    inline fun orElseF(F: Monad<F>, crossinline default: () -> HK<F, Option<A>>): OptionT<F, A> =
            OptionT(F.flatMap(value) {
                when (it) {
                    is Option.Some<A> -> F.pure(it)
                    is Option.None -> default()
                }
            })

    inline fun <B> transform(F: Monad<F>, crossinline f: (Option<A>) -> Option<B>): OptionT<F, B> =
            OptionT(F.map(value, { f(it) }))

    inline fun <B> subflatMap(F: Monad<F>, crossinline f: (A) -> Option<B>): OptionT<F, B> =
            transform(F, { it.flatMap(f) })

    //TODO: add toRight() and toLeft() once EitherT it's available
}