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

object OptionMonad : Monad<Option.F> {

    fun <A> HK<Option.F, A>.ev(): Option<A> = this as Option<A>

    override fun <A, B> map(fa: HK<Option.F, A>, f: (A) -> B): HK<Option.F, B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): HK<Option.F, A> = Option.Some(a)

    override fun <A, B> flatMap(fa: HK<Option.F, A>, f: (A) -> HK<Option.F, B>): HK<Option.F, B> =
            fa.ev().flatMap { f(it).ev() }

}