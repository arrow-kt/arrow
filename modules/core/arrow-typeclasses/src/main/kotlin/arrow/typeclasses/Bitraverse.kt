package arrow.typeclasses

import arrow.Kind
import arrow.Kind2
import arrow.core.Id
import arrow.core.identity
import arrow.core.value
import arrow.typeclasses.internal.IdBimonad

/**
 * Bitraverse TODO Complete descrition
 */
interface Bitraverse<F> : Bifunctor<F>, Bifoldable<F> {

  /**
   * TODO FIX BITRAVERSE COMMENT
   * Given a function which returns a G effect, thread this effect through the running of this function on all the
   * values in F, returning an F<B> in a G context.
   */
  fun <G, A, B, C, D> Kind2<F, A, B>.bitraverse(AP: Applicative<G>, f: (A) -> Kind<G, C>, g: (B) -> Kind<G, D>):
    Kind<G, Kind2<F, C, D>>

  /**
   * TODO FIX THE COMMENT
   * Thread all the G effects through the F structure to invert the structure from F<G<A>> to G<F<A>>.
   */
  fun <G, A, B> Kind2<F, Kind<G, A>, Kind<G, B>>.bisequence(AP: Applicative<G>): Kind<G, Kind2<F, A, B>> = bitraverse(AP, ::identity, ::identity)

  override fun <A, B, C, D> Kind2<F, A, B>.bimap(f: (A) -> C, g: (B) -> D): Kind2<F, C, D> =
    bitraverse(IdBimonad, { Id(f(it)) }, { Id(g(it)) }).value()

}
