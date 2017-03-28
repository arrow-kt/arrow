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

typealias FOption<F, A> = HK<F, Option<A>>

data class OptionT<F, A>(val value: FOption<F, A>) {

    fun <B> fold(F: Functor<F>, default: () -> B, f: (A) -> B): HK<F, B> =
            F.map(value, { it.fold(default, f) })

    fun <B> flatMapF(F: Monad<F>, f: (A) -> FOption<F, B>): OptionT<F, B> =
            OptionT(F.flatMap(value, { it.fold({ F.pure<Option<B>>(Option.None) }, { f(it) }) }))

    fun <B> flatMap(F: Monad<F>, f: (A) -> OptionT<F, B>): OptionT<F, B> =
            flatMapF(F, { f(it).value })

    inline fun <B> map(F: Functor<F>, crossinline f: (A) -> B): OptionT<F, B> =
            OptionT(F.map(value, { it.map(f) }))

    fun getOrElse(F: Functor<F>, default: () -> A): HK<F, A> =
            F.map(value, { it.getOrElse(default) })

    fun filter(F: Functor<F>, p: (A) -> Boolean): OptionT<F, A> =
            OptionT(F.map(value, { it.filter(p) }))

    fun forall(F: Functor<F>, p: (A) -> Boolean): HK<F, Boolean> =
            F.map(value, { it.forall(p) })

    fun isDefined(F: Functor<F>): HK<F, Boolean> =
            F.map(value, { it.isDefined })

    fun isEmpty(F: Functor<F>): HK<F, Boolean> =
            F.map(value, { it.isEmpty })

    fun orElse(F: Monad<F>, default: () -> OptionT<F, A>): OptionT<F, A> =
            orElseF(F, { default().value })

    fun orElseF(F: Monad<F>, default: () -> FOption<F, A>): OptionT<F, A> =
            OptionT(F.flatMap(value) {
                when (it) {
                    is Option.Some<A> -> F.pure(it)
                    is Option.None -> default()
                }
            })
}