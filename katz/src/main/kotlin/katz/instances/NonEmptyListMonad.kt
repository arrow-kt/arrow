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

    @Suppress("UNCHECKED_CAST")
    private tailrec fun <A, B> go(buf: ArrayList<B>, f: (A) -> HK<NonEmptyList.F, Either<A, B>>, v: NonEmptyList<Either<A, B>>) =
            when (v.head) {
                is Either.Right<*> -> {
                    buf += v.head.b as B
                    val x = NonEmptyList.fromList(v.tail)
                    when (x) {
                        is Option.Some<NonEmptyList<Either<A, B>>> -> go(buf, f, x.value)
                        is Option.None -> Unit
                    }
                }
                is Either.Left<*> -> go(buf, f, NonEmptyList.fromListUnsafe(f(v.head.a as A).ev().all + v.tail))
            }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<NonEmptyList.F, Either<A, B>>): NonEmptyList<B> {
        val buf = ArrayList<B>()
        go(buf, f, f(a).ev())
        return NonEmptyList.fromListUnsafe(buf)
    }
}

fun <A> NonEmptyListKind<A>.ev(): NonEmptyList<A> = this as NonEmptyList<A>
