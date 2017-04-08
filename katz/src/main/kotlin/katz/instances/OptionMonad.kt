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

interface OptionMonad :
        Monad<Option.F> {

    override fun <A, B> map(fa: OptionKind<A>, f: (A) -> B): Option<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): Option<A> = Option.Some(a)

    override fun <A, B> flatMap(fa: OptionKind<A>, f: (A) -> OptionKind<B>): Option<B> =
            fa.ev().flatMap { f(it).ev() }

    tailrec override fun <A, B> tailRecM(a: A, f: (A) -> HK<Option.F, Either<A, B>>): Option<B> {
        val option = f(a).ev()
        return when(option) {
            is Option.Some -> {
                when (option.value) {
                    is Either.Left -> tailRecM(option.value.a, f)
                    is Either.Right -> Option.Some(option.value.b)
                }
            }
            is Option.None -> Option.None
        }
    }
}

fun <A> OptionKind<A>.ev(): Option<A> = this as Option<A>
