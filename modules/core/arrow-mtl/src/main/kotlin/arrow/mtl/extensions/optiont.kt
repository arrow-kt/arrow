package arrow.mtl.extensions

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.extensions.option.applicative.applicative
import arrow.core.fix
import arrow.data.OptionT
import arrow.data.OptionTPartialOf
import arrow.data.extensions.OptionTFunctor
import arrow.data.extensions.OptionTTraverse
import arrow.data.fix
import arrow.data.mapFilter
import arrow.extension
import arrow.mtl.extensions.option.traverseFilter.traverseFilter
import arrow.mtl.typeclasses.FunctorFilter
import arrow.mtl.typeclasses.TraverseFilter
import arrow.typeclasses.*

@extension
interface OptionTFunctorFilter<F> : FunctorFilter<OptionTPartialOf<F>>, OptionTFunctor<F> {

  override fun FF(): Functor<F>

  override fun <A, B> Kind<OptionTPartialOf<F>, A>.mapFilter(f: (A) -> Option<B>): OptionT<F, B> =
    fix().mapFilter(FF(), f)
}

@extension
interface OptionTTraverseFilter<F> :
  TraverseFilter<OptionTPartialOf<F>>,
  OptionTTraverse<F> {

  override fun FFT(): Traverse<F> = FFF()

  override fun FFF(): TraverseFilter<F>

  override fun <G, A, B> Kind<OptionTPartialOf<F>, A>.traverseFilter(AP: Applicative<G>, f: (A) -> Kind<G, Option<B>>): Kind<G, OptionT<F, B>> =
    fix().traverseFilter(f, AP, FFF())
}

fun <F, G, A, B> OptionT<F, A>.traverseFilter(f: (A) -> Kind<G, Option<B>>, GA: Applicative<G>, FF: Traverse<F>): Kind<G, OptionT<F, B>> {
  val fa = ComposedTraverseFilter(FF, Option.traverseFilter(), Option.applicative()).traverseFilterC(value(), f, GA)
  val mapper: (Kind<Nested<F, ForOption>, B>) -> OptionT<F, B> = { nested -> OptionT(FF.run { nested.unnest().map { it.fix() } }) }
  return GA.run { fa.map(mapper) }
}
