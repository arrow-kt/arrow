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


data class OptionT<F, A>(val value: HK<F, Option<A>>) : HK2<OptionT.F, F, A> {

    class F private constructor()

    inline fun <B> flatMap(F: Monad<F>, crossinline f: (A) -> OptionT<F, B>): OptionT<F, B> = flatMapF(F, { it -> f(it).value })

    inline fun <B> flatMapF(F: Monad<F>, crossinline f: (A) -> HK<F, Option<B>>): OptionT<F, B> =
            OptionT(F.flatMap(value, { option -> option.fold({ F.pure(Option.None) }, { f(it) }) }))

    inline fun <B> map(F: Functor<F>, crossinline f: (Option<A>) -> Option<B>): OptionT<F, B> =
            OptionT(F.map(value, { f(it) }))

    fun getOrElse(F: Functor<F>, default: () -> A): HK<F, A> = F.map(value, { it.getOrElse(default) })

    inline fun filter(F: Functor<F>, crossinline p: (A) -> Boolean): OptionT<F, A> = OptionT(F.map(value, { it.filter(p) }))

    inline fun forall(F: Functor<F>, crossinline p: (A) -> Boolean): HK<F, Boolean> = F.map(value, { it.forall(p) })

    fun isDefined(F: Functor<F>): HK<F, Boolean> = F.map(value, { it.isDefined })

    fun isEmpty(F: Functor<F>): HK<F, Boolean> = F.map(value, { it.isEmpty })

    fun orElse(F: Monad<F>, default: () -> OptionT<F, A>): OptionT<F, A> =
            orElseF(F, { default().value })

    fun orElseF(F: Monad<F>, default: () -> HK<F, Option<A>>): OptionT<F, A> =
            OptionT(F.flatMap(value) {
                when (it) {
                    is Option.Some<A> -> F.pure(it)
                    is Option.None -> default()
                }
            })
}