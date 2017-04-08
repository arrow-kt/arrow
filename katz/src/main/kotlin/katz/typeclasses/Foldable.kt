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

    /**
     * Left associative fold on 'F' using the function 'f'.
     */
    fun <A, B> foldL(fa: HK<F, A>, b: B): ((B, A) -> B) -> B

    /**
     * Right associative lazy fold on `F` using the folding function 'f'.
     *
     * This method evaluates `lb` lazily (in some cases it will not be needed), and returns a lazy value. We are using
     * `(A, Eval[B]) => Eval[B]` to support laziness in a stack-safe way. Chained computation should be performed via
     * .map and .flatMap.
     *
     * For more detailed information about how this method works see the documentation for `Eval[_]`.
     */
    fun <A, B> foldR(fa: HK<F, A>, lb: Eval<B>): ((A, Eval<B>) -> Eval<B>) -> Eval<B>

    /**
     * The size of this Foldable.
     *
     * This is overriden in structures that have more efficient size implementations (e.g. Vector, Set, Map).
     *
     * Note: will not terminate for infinite-sized collections.
     */
    fun <A> size(ml: Monoid<Long>, fa: HK<F, A>): Long {
        val foldM = foldMap(ml, fa)
        return foldM { _ -> 1L }
    }

    /**
     * Fold implemented using the given Monoid[A] instance.
     */
    fun <A> fold(ma: Monoid<A>, fa: HK<F, A>): A {
        val foldL = foldL(fa, ma.empty())
        return foldL { acc, a -> ma.combine(acc, a) }
    }

    /**
     * Alias for [[fold]].
     */
    fun <A : Monoid<*>> combineAll(m: Monoid<A>, fa: HK<F, A>): A = fold(m, fa)

    /**
     * Fold implemented by mapping `A` values into `B` and then
     * combining them using the given `Monoid[B]` instance.
     */
    fun <A, B> foldMap(mb: Monoid<B>, fa: HK<F, A>): (f: (A) -> B) -> B {
        val foldL = foldL(fa, mb.empty())
        return { f: (A) -> B -> foldL { b, a -> mb.combine(b, f(a)) } }
    }

    /**
     * Left associative monadic folding on `F`.
     *
     * The default implementation of this is based on `foldLeft`, and thus will
     * always fold across the entire structure. Certain structures are able to
     * implement this in such a way that folds can be short-circuited (not
     * traverse the entirety of the structure), depending on the `G` result
     * produced at a given step.
     */
    fun <G, A, B> foldM(g: Monad<G>, fa: HK<F, A>, z: B): ((B, A) -> HK<G, B>) -> HK<G, B> {
        val foldL = foldL(fa, g.pure(z))
        return { f: (B, A) -> HK<G, B> ->
            foldL { gb, a -> g.flatMap(gb) { f(it, a) } }
        }
    }

    /**
     * Monadic folding on `F` by mapping `A` values to `G[B]`, combining the `B`
     * values using the given `Monoid[B]` instance.
     *
     * Similar to [[foldM]], but using a `Monoid[B]`.
     *
     * {{{
     * scala> import cats.Foldable
     * scala> import cats.implicits._
     * scala> val evenNumbers = List(2,4,6,8,10)
     * scala> val evenOpt: Int => Option[Int] =
     *      |   i => if (i % 2 == 0) Some(i) else None
     * scala> Foldable[List].foldMapM(evenNumbers)(evenOpt)
     * res0: Option[Int] = Some(30)
     * scala> Foldable[List].foldMapM(evenNumbers :+ 11)(evenOpt)
     * res1: Option[Int] = None
     * }}}
     */
    fun <G, A, B> foldMapM(g: Monad<G>, bb: Monoid<B>, fa: HK<F, A>): ((A) -> HK<G, B>) -> HK<G, B> {
        val foldM = foldM(g, fa, bb.empty())
        return { f: (A) -> HK<G, B> ->
            foldM { b, a -> g.map(f(a)) { bb.combine(b, it) } }
        }
    }

    /**
     * Traverse `F[A]` using `Applicative[G]`.
     *
     * `A` values will be mapped into `G[B]` and combined using
     * `Applicative#map2`.
     *
     * For example:
     *
     * {{{
     * scala> import cats.implicits._
     * scala> def parseInt(s: String): Option[Int] = Either.catchOnly[NumberFormatException](s.toInt).toOption
     * scala> val F = Foldable[List]
     * scala> F.traverse_(List("333", "444"))(parseInt)
     * res0: Option[Unit] = Some(())
     * scala> F.traverse_(List("333", "zzz"))(parseInt)
     * res1: Option[Unit] = None
     * }}}
     *
     * This method is primarily useful when `G[_]` represents an action
     * or effect, and the specific `A` aspect of `G[A]` is not otherwise
     * needed.
     */
    fun <G, A, B> traverse_(ag: Applicative<G>, fa: HK<F, A>): (f: (A) -> HK<G, B>) -> HK<G, Unit> {

        /*foldRight(fa, Always(G.pure(()))) { (a, acc) =>
            G.map2Eval(f(a), acc) { (_, _) => () }
        }.value*/
    }
}
