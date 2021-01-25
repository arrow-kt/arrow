package arrow.typeclasses

import arrow.Kind
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.typeclasses.internal.Id
import arrow.typeclasses.internal.fix
import arrow.typeclasses.internal.idApplicative

interface TraverseFilter<F> : Traverse<F>, FunctorFilter<F> {

  /**
   * Returns [F]<[B]> in [G] context by applying [AP] on a selector function [f], which returns [Option] of [B]
   * in [G] context.
   */
  fun <G, A, B> Kind<F, A>.traverseFilter(AP: Applicative<G>, f: (A) -> Kind<G, Option<B>>): Kind<G, Kind<F, B>>

  override fun <A, B> Kind<F, A>.filterMap(f: (A) -> Option<B>): Kind<F, B> =
    traverseFilter(idApplicative) { Id(f(it)) }.fix().value

  /**
   * Returns [F]<[A]> in [G] context by applying [GA] on a selector function [f] in [G] context.
   */
  fun <G, A> Kind<F, A>.filterA(f: (A) -> Kind<G, Boolean>, GA: Applicative<G>): Kind<G, Kind<F, A>> = GA.run {
    traverseFilter(this) { a -> f(a).map { b -> if (b) Some(a) else None } }
  }

  override fun <A> Kind<F, A>.filter(f: (A) -> Boolean): Kind<F, A> =
    filterA({ Id(f(it)) }, idApplicative).fix().value

  /**
   * Filter out instances of [B] type and traverse the [G] context.
   */
  fun <G, A, B> Kind<F, A>.traverseFilterIsInstance(AP: Applicative<G>, klass: Class<B>): Kind<G, Kind<F, B>> = AP.run {
    filterA({ a -> just(klass.isInstance(a)) }, AP)
      .map { fa -> fa.map { a -> klass.cast(a) } }
  }
}
