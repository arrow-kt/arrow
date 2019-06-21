package arrow.typeclasses

import arrow.Kind
import arrow.core.Id
import arrow.core.identity
import arrow.core.value
import arrow.typeclasses.internal.IdBimonad

/**
 * ank_macro_hierarchy(arrow.typeclasses.Traverse)
 *
 * Traverse, also known as Traversable. Traversal over a structure with an effect.
 */
interface Traverse<F> : Functor<F>, Foldable<F> {

  /**
   * Given a function which returns a G effect, thread this effect through the running of this function on all the
   * values in F, returning an F<B> in a G context.
   */
  fun <G, A, B> Kind<F, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<F, B>>

  /**
   * Thread all the G effects through the F structure to invert the structure from F<G<A>> to G<F<A>>.
   */
  fun <G, A> Kind<F, Kind<G, A>>.sequence(AG: Applicative<G>): Kind<G, Kind<F, A>> = traverse(AG, ::identity)

  override fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B> =
    traverse(IdBimonad) { Id(f(it)) }.value()

  fun <G, A, B> Kind<F, A>.flatTraverse(MF: Monad<F>, AG: Applicative<G>, f: (A) -> Kind<G, Kind<F, B>>): Kind<G, Kind<F, B>> =
    AG.run { traverse(this, f).map { MF.run { it.flatten() } } }
}
