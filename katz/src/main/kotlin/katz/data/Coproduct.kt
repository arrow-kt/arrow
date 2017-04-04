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

typealias CoproductF<F> = HK<Coproduct.F, F>
typealias CoproductFG<F, G> = HK<CoproductF<F>, G>
typealias CoproductKind<F, G, A> = HK<CoproductFG<F, G>, A>

fun <F, G, A> CoproductKind<F, G, A>.ev(): Coproduct<F, G, A> = this as Coproduct<F, G, A>

data class Coproduct<F, G, A>(val run: Either<HK<F, A>, HK<G, A>>) : CoproductKind<F, G, A> {

    class F private constructor()

    companion object {

        fun <F, G, A> leftc(x: HK<F, A>): Coproduct<F, G, A> =
                Coproduct(Either.Left(x))

        fun <F, G, A> rightc(x: HK<G, A>): Coproduct<F, G, A> =
                Coproduct(Either.Right(x))

    }

    fun <B> map(f: (A) -> B): Coproduct<F, G, B> {
        return Coproduct(run.bimap(instance<Functor<F>>().lift(f), instance<Functor<G>>().lift(f)))
    }

    fun <B> coflatMap(f: (Coproduct<F, G, A>) -> B): Coproduct<F, G, B> =
            Coproduct(run.bimap(
                    { instance<Comonad<F>>().coflatMap(it, { f(Coproduct.leftc(it)) }) },
                    { instance<Comonad<G>>().coflatMap(it, { f(Coproduct.rightc(it)) }) }
            ))

    fun extract(): A =
            run.fold({ instance<Comonad<F>>().extract(it) }, { instance<Comonad<G>>().extract(it) })

    fun <H> fold(f: FunctionK<F, H>, g: FunctionK<G, H>): HK<H, A> =
            run.fold({ f(it) }, { g(it) })

}
