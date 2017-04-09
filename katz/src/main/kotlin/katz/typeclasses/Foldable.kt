/*
Copyright (C) 2017 The Katz Authors

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

------------------------------------------------------------------------

Code in Katz is derived in part from typelevel/Cats and Scalaz.

Cats license follows:

------------------------------------------------------------------------

Cats Copyright (c) 2015 Erik Osheim.

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

Scalaz license follows:

------------------------------------------------------------------------

Copyright (c) 2009-2014 Tony Morris, Runar Bjarnason, Tom Adams,
Kristian Domagala, Brad Clow, Ricky Clarkson, Paul Chiusano, Trygve
Laugstøl, Nick Partridge, Jason Zaugg. All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:
1. Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
3. The name of the author may not be used to endorse or promote products
   derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

------------------------------------------------------------------------
 */

package katz

import katz.Eval.EvalFactory.always

/**
 * Data structures that can be folded to a summary value.
 *
 * Foldable<F> is implemented in terms of two basic methods:
 *
 *  - `foldLeft(fa, b)(f)` eagerly folds `fa` from left-to-right.
 *  - `foldRight(fa, b)(f)` lazily folds `fa` from right-to-left.
 *
 * Beyond these it provides many other useful methods related to folding over F<A> values.
 */
interface Foldable<F> {

    /**
     * Left associative fold on F using the provided function.
     */
    fun <A, B> foldL(fa: HK<F, A>, b: B): ((B, A) -> B) -> B

    /**
     * Right associative lazy fold on F using the provided function.
     *
     * This method evaluates lb lazily (in some cases it will not be needed), and returns a lazy value. We are using
     * (A, Eval<B>) => Eval<B> to support laziness in a stack-safe way. Chained computation should be performed via
     * .map and .flatMap.
     *
     * For more detailed information about how this method works see the documentation for Eval<A>.
     */
    fun <A, B> foldR(fa: HK<F, A>, lb: Eval<B>): ((A, Eval<B>) -> Eval<B>) -> Eval<B>

    /**
     * The size of this Foldable.
     *
     * This can be overriden in structures that have more efficient size implementations.
     *
     * Note: will not terminate for infinite-sized collections.
     */
    fun <A> size(ml: Monoid<Long>, fa: HK<F, A>): Long = foldMap(ml, fa)({ _ -> 1L })

    /**
     * Fold implemented using the given Monoid<A> instance.
     */
    fun <A> fold(ma: Monoid<A>, fa: HK<F, A>): A =
            foldL(fa, ma.empty())({ acc, a -> ma.combine(acc, a) })

    /**
     * Alias for fold.
     */
    fun <A : Monoid<*>> combineAll(m: Monoid<A>, fa: HK<F, A>): A = fold(m, fa)

    /**
     * Fold implemented by mapping A values into B and then combining them using the given Monoid<B> instance.
     */
    fun <A, B> foldMap(mb: Monoid<B>, fa: HK<F, A>): (f: (A) -> B) -> B =
            { f: (A) -> B -> foldL(fa, mb.empty())({ b, a -> mb.combine(b, f(a)) }) }

    /**
     * Left associative monadic folding on F.
     *
     * The default implementation of this is based on foldL, and thus will always fold across the entire structure.
     * Certain structures are able to implement this in such a way that folds can be short-circuited (not traverse the
     * entirety of the structure), depending on the G result produced at a given step.
     */
    fun <G, A, B> foldM(g: Monad<G>, fa: HK<F, A>, z: B): ((B, A) -> HK<G, B>) -> HK<G, B> {
        val foldL = foldL(fa, g.pure(z))
        return { f: (B, A) -> HK<G, B> ->
            foldL { gb, a -> g.flatMap(gb) { f(it, a) } }
        }
    }

    /**
     * Monadic folding on F by mapping A values to G<B>, combining the B values using the given Monoid<B> instance.
     *
     * Similar to foldM, but using a Monoid<B>.
     */
    fun <G, A, B> foldMapM(g: Monad<G>, bb: Monoid<B>, fa: HK<F, A>): ((A) -> HK<G, B>) -> HK<G, B> {
        val foldM = foldM(g, fa, bb.empty())
        return { f: (A) -> HK<G, B> ->
            foldM { b, a -> g.map(f(a)) { bb.combine(b, it) } }
        }
    }

    /**
     * Traverse F<A> using Applicative<G>.
     *
     * A typed values will be mapped into G<B> by function f and combined using Applicative#map2.
     *
     * This method is primarily useful when G<_> represents an action or effect, and the specific A aspect of G<A> is
     * not otherwise needed.
     */
    fun <G, A, B> traverse_(ag: Applicative<G>, fa: HK<F, A>): (f: (A) -> HK<G, B>) -> HK<G, Unit> = {
        f: (A) -> HK<G, B> -> foldR(fa, always { ag.pure(Unit) })({ a, acc -> ag.map2Eval(f(a), acc) { Unit } }).value()
    }

    /**
     * Sequence F<G<A>> using Applicative<G>.
     *
     * Similar to traverse except it operates on F<G<A>> values, so no additional functions are needed.
     */
    fun <G, A> sequence_(ag: Applicative<G>, fga: HK<F, HK<G, A>>): HK<G, Unit> =
            traverse_<G, HK<G, A>, A>(ag, fga)({ it })

    /**
     * Check whether at least one element satisfies the predicate.
     *
     * If there are no elements, the result is false.
     */
    fun <A> exists(fa: HK<F, A>): (p: (A) -> Boolean) -> Boolean =
            { p: (A) -> Boolean -> foldR(fa, Eval.False)({ a, lb -> if (p(a)) Eval.True else lb }).value() }

    /**
     * Check whether all elements satisfy the predicate.
     *
     * If there are no elements, the result is true.
     */
    fun <A> forall(fa: HK<F, A>): (p: (A) -> Boolean) -> Boolean =
            { p: (A) -> Boolean -> foldR(fa, Eval.True)({ a, lb -> if (p(a)) lb else Eval.False }).value() }

    /**
     * Returns true if there are no elements. Otherwise false.
     */
    fun <A> isEmpty(fa: HK<F, A>): Boolean = foldR(fa, Eval.True)({ _, _ -> Eval.False }).value()

    fun <A> nonEmpty(fa: HK<F, A>): Boolean = !isEmpty(fa)

    companion object {
        fun <A, B> iterateRight(it: Iterator<A>, lb: Eval<B>): (f: (A, Eval<B>) -> Eval<B>) -> Eval<B> = {
            f: (A, Eval<B>) -> Eval<B> ->
            fun loop(): Eval<B> =
                    Eval.defer { if (it.hasNext()) f(it.next(), loop()) else lb }
            loop()
        }
    }
}
