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


data class OptionT<F, out A>(val value: HK<F, Option<A>>) : HK2<OptionT.F, F, A> {

    class F private constructor()

    inline fun <B> flatMap(F: Monad<F>, crossinline f: (A) -> OptionT<F, B>): OptionT<F, B> =
            OptionT(F.flatMap(value, { option: Option<A> -> option.fold({ F.pure(Option.None) }, { it: A -> f(it).value }) }))

    inline fun <B> map(F: Functor<F>, crossinline f: (Option<A>) -> Option<B>): OptionT<F, B> =
            OptionT(F.map(value, { f(it) }))

}