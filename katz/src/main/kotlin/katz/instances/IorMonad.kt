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

class IorMonad<A>(val semigroup: Semigroup<A>) : Monad<HK<Ior.F, A>> {
    override fun <B> pure(b: B): HK2<Ior.F, A, B> =
            Ior.Right(b)

    override fun <B, C> flatMap(fa: HK<HK<Ior.F, A>, B>, f: (B) -> HK<HK<Ior.F, A>, C>): HK<HK<Ior.F, A>, C> =
            fa.ev().flatMap(semigroup) { f(it).ev() }
}

fun <A, B> HK2<Ior.F, A, B>.ev(): Ior<A, B> = this as Ior<A, B>
