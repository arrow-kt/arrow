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


class OptionTMonad<F>(val M: Monad<F>) : Monad<HK<OptionT.F, F>> {
    override fun <A> pure(a: A): OptionT<F, A> = OptionT.pure(M, a)

    override fun <A, B> flatMap(fa: HK<HK<OptionT.F, F>, A>, f: (A) -> HK<HK<OptionT.F, F>, B>): OptionT<F, B> =
            fa.ev().flatMap(M, { f(it).ev() })

    override fun <A, B> map(fa: HK<HK<OptionT.F, F>, A>, f: (A) -> B): OptionT<F, B> =
            fa.ev().map(M, f)
}

fun <F, A> HK2<OptionT.F, F, A>.ev(): OptionT<F, A> = this as OptionT<F, A>
