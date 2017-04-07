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

/**
 * Data structures that can be folded to a summary value.
 *
 * In the case of a collection (such as `List` or `Set`), these methods will fold together (combine) the values
 * contained in the collection to produce a single result. Most collection types have `foldLeft` methods, which will
 * usually be used by the associated `Foldable[_]` instance.
 *
 * Foldable[F] is implemented in terms of two basic methods:
 *
 *  - `foldLeft(fa, b)(f)` eagerly folds `fa` from left-to-right.
 *  - `foldRight(fa, b)(f)` lazily folds `fa` from right-to-left.
 *
 * Beyond these it provides many other useful methods related to folding over F[A] values.
 */
interface Foldable<F> {

    fun <A, B> foldL(fa: HK<F, A>, b: B): (B, A) -> B

    fun <A, B> foldR(fa: HK<F, A>, lb: Eval<B>): (A, Eval<B>) -> Eval<B>

    /**
     * The size of this Foldable.
     *
     * This is overriden in structures that have more efficient size implementations
     * (e.g. Vector, Set, Map).
     *
     * Note: will not terminate for infinite-sized collections.
     */
    fun <A> size(fa: HK<F,A>): Long = foldMap(fa)(_ => 1)

    /**
     * Fold implemented using the given Monoid[A] instance.
     */
    fun <A> fold(fa: HK<F,A>)(implicit A: Monoid<A>): A =
    foldL(fa, A.empty) { (acc, a) =>
        A.combine(acc, a)
    }
}
