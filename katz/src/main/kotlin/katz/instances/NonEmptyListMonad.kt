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

interface NonEmptyListMonad : Monad<NonEmptyList.F> {

    override fun <A, B> map(fa: NonEmptyListKind<A>, f: (A) -> B): NonEmptyList<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): NonEmptyList<A> = NonEmptyList.of(a)

    override fun <A, B> flatMap(fa: NonEmptyListKind<A>, f: (A) -> NonEmptyListKind<B>): NonEmptyList<B> =
            fa.ev().flatMap { f(it).ev() }

}

fun <A> NonEmptyListKind<A>.ev(): NonEmptyList<A> = this as NonEmptyList<A>
