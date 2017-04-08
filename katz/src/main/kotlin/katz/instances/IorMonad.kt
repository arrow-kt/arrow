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

class IorMonad<L>(val SL: Semigroup<L>) : Monad<HK<Ior.F, L>> {
    override fun <A, B> flatMap(fa: IorKind<L, A>, f: (A) -> IorKind<L, B>): Ior<L, B> =
            fa.ev().flatMap(SL, { f(it).ev() })

    override fun <A> pure(a: A): Ior<L, A> = Ior.Right(a)

    private tailrec fun <A, B> loop(v: Ior<L, Either<A, B>>, f: (A) -> IorKind<L, Either<A, B>>) : Ior<L, B> {
        return when(v) {
            is Ior.Left -> Ior.Left(v.value)
            is Ior.Right -> when (v.value) {
                is Either.Right -> Ior.Right(v.value.b)
                is Either.Left -> loop(f(v.value.a).ev().ev(), f)
            }
            is Ior.Both -> when (v.rightValue) {
                is Either.Right -> Ior.Both(v.leftValue, v.rightValue.b)
                is Either.Left -> {
                    val fnb = f(v.rightValue.a).ev()
                    when (fnb) {
                        is Ior.Left -> Ior.Left(SL.combine(v.leftValue, fnb.value))
                        is Ior.Right -> loop(Ior.Both(v.leftValue, fnb.value), f)
                        is Ior.Both -> loop(Ior.Both(SL.combine(v.leftValue, fnb.leftValue), fnb.rightValue), f)
                    }
                }
            }
        }
    }

    override fun <A, B> tailRecM(a: A, f: (A) -> IorKind<L, Either<A, B>>): Ior<L, B> {
        return loop(f(a).ev(), f)
    }
}

fun <A, B> IorKind<A, B>.ev(): Ior<A, B> = this as Ior<A, B>
