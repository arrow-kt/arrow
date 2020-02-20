package arrow.typeclasses

import arrow.Kind
import arrow.core.ForId
import arrow.core.Id
import arrow.core.IdOf
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.fix
import arrow.core.value

/**
 * ank_macro_hierarchy(arrow.typeclasses.TraverseFilter)
 */
interface TraverseFilter<F> : Traverse<F>, FunctorFilter<F> {

  private object IdApplicative : Applicative<ForId> {
    override fun <A, B> IdOf<A>.ap(ff: IdOf<(A) -> B>): Id<B> =
      fix().ap(ff)

    override fun <A, B> IdOf<A>.map(f: (A) -> B): Id<B> =
      fix().map(f)

    override fun <A> just(a: A): Id<A> =
      Id.just(a)
  }

  fun <G, A, B> Kind<F, A>.traverseFilter(AP: Applicative<G>, f: (A) -> Kind<G, Option<B>>): Kind<G, Kind<F, B>>

  override fun <A, B> Kind<F, A>.filterMap(f: (A) -> Option<B>): Kind<F, B> =
    traverseFilter(IdApplicative) { Id(f(it)) }.value()

  fun <G, A> Kind<F, A>.filterA(f: (A) -> Kind<G, Boolean>, GA: Applicative<G>): Kind<G, Kind<F, A>> = GA.run {
    traverseFilter(this) { a -> f(a).map { b -> if (b) Some(a) else None } }
  }

  override fun <A> Kind<F, A>.filter(f: (A) -> Boolean): Kind<F, A> =
    filterA({ Id(f(it)) }, IdApplicative).value()
}
