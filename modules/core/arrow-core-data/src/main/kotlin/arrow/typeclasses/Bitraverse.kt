package arrow.typeclasses

import arrow.Kind
import arrow.Kind2
import arrow.core.Id
import arrow.core.identity
import arrow.core.value
import arrow.documented
import arrow.typeclasses.internal.IdBimonad

/**
 * ank_macro_hierarchy(arrow.typeclasses.Bitraverse)
 *
 * The type class `Bitraverse` defines the behaviour of two separetes `Traverse` over a data type.
 *
 * Every instance of `Bitraverse<F>` must contains the next functions:
 *
 * ## Bitraverse
 *
 * `Bitraverse` perfoms a`Traverse` over both side of the Data type which is `Bifoldable`.
 *
 * ```kotlin:ank:playground
 * import arrow.core.*
 * import arrow.core.extensions.option.applicative.applicative
 * import arrow.core.extensions.*
 * import arrow.core.extensions.tuple2.bitraverse.bitraverse
 * fun main() {
 * //sampleStart
 *  val f: (Int) -> Option<Int> = { Some(it + 1) }
 *  val g: (Int) -> Option<Int> = { Some(it * 3) }
 *
 *  val tuple = Tuple2(1, 2)
 *  val bitraverseResult = tuple.bitraverse(Option.applicative(), f, g)
 *  //sampleEnd
 *  println(bitraverseResult)
 * }
 * ```
 *
 * ## Bisequence
 *
 * `Bisequence` invert the original structure `F<G<A,B>>` to `G<F<A>,F<B>>`
 * ```kotlin:ank:playground
 * import arrow.core.*
 * import arrow.core.extensions.*
 * import arrow.core.extensions.option.applicative.applicative
 * import arrow.core.extensions.tuple2.bitraverse.bisequence
 * fun main() {
 *  //sampleStart
 *  val tuple = Tuple2(Some(1), Some(2))
 *  val sequenceResult = tuple.bisequence(Option.applicative())
 * //sampleEnd
 *  println(sequenceResult)
 * }
 * ```
 */
@documented
interface Bitraverse<F> : Bifunctor<F>, Bifoldable<F> {

  fun <G, A, B, C, D> Kind2<F, A, B>.bitraverse(AP: Applicative<G>, f: (A) -> Kind<G, C>, g: (B) -> Kind<G, D>):
    Kind<G, Kind2<F, C, D>>

  fun <G, A, B> Kind2<F, Kind<G, A>, Kind<G, B>>.bisequence(AP: Applicative<G>): Kind<G, Kind2<F, A, B>> = bitraverse(AP, ::identity, ::identity)

  override fun <A, B, C, D> Kind2<F, A, B>.bimap(f: (A) -> C, g: (B) -> D): Kind2<F, C, D> =
    bitraverse(IdBimonad, { Id(f(it)) }, { Id(g(it)) }).value()
}
