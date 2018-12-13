package arrow.mtl.typeclasses

import arrow.Kind
import arrow.core.*
import arrow.instances.id.applicative.applicative
import arrow.typeclasses.Applicative
import arrow.typeclasses.Traverse

/**
 * ank_macro_hierarchy(arrow.mtl.typeclasses.TraverseFilter)
 */
interface TraverseFilter<F> : Traverse<F>, FunctorFilter<F> {

  fun <G, A, B> Kind<F, A>.traverseFilter(AP: Applicative<G>, f: (A) -> Kind<G, Option<B>>): Kind<G, Kind<F, B>>

  override fun <A, B> Kind<F, A>.mapFilter(f: (A) -> Option<B>): Kind<F, B> =
    traverseFilter(Id.applicative()) { Id(f(it)) }.value()

  fun <G, A> Kind<F, A>.filterA(f: (A) -> Kind<G, Boolean>, GA: Applicative<G>): Kind<G, Kind<F, A>> = GA.run {
    traverseFilter(this) { a -> f(a).map { b -> if (b) Some(a) else None } }
  }

  override fun <A> Kind<F, A>.filter(f: (A) -> Boolean): Kind<F, A> =
    filterA({ Id(f(it)) }, Id.applicative()).value()
}
