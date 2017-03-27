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

interface Applicative<F> : Functor<F> {

    fun <A> pure(a: A): HK<F, A>

    fun <A, B> ap(fa: HK<F, A>, ff: HK<F, (A) -> B>): HK<F, B>

    fun <A, B> product(fa: HK<F, A>, fb: HK<F, B>): HK<F, Pair<A, B>> =
            ap(fb, map(fa) { a: A -> { b: B -> Pair(a, b) } })

    override fun <A, B> map(fa: HK<F, A>, f: (A) -> B): HK<F, B> = ap(fa, pure(f))

    fun <A, B, Z> map2(fa: HK<F, A>, fb: HK<F, B>, f: (Pair<A, B>) -> Z): HK<F, Z> =
            map(product(fa, fb), f)
}
