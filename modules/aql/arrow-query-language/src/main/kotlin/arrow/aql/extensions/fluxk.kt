package arrow.aql.extensions

import arrow.aql.*
import arrow.effects.reactor.FluxK
import arrow.effects.reactor.ForFluxK
import arrow.effects.reactor.extensions.fluxk.applicative.applicative
import arrow.effects.reactor.extensions.fluxk.foldable.foldable
import arrow.effects.reactor.extensions.fluxk.functor.functor
import arrow.effects.reactor.extensions.fluxk.functorFilter.functorFilter
import arrow.extension
import arrow.mtl.typeclasses.FunctorFilter
import arrow.typeclasses.Applicative
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor

@extension
interface FluxKFrom : From<ForFluxK> {
  override fun applicative(): Applicative<ForFluxK> = FluxK.applicative()
}

@extension
interface FluxKSelect : Select<ForFluxK> {
  override fun functor(): Functor<ForFluxK> = FluxK.functor()
}

@extension
interface FluxKWhere : Where<ForFluxK> {
  override fun functorFilter(): FunctorFilter<ForFluxK> = FluxK.functorFilter()
}

@extension
interface FluxKGroupBy : GroupBy<ForFluxK> {
  override fun foldable(): Foldable<ForFluxK> = FluxK.foldable()
}

@extension
interface FluxKCount : Count<ForFluxK> {
  override fun foldable(): Foldable<ForFluxK> = FluxK.foldable()
}

@extension
interface FluxKSum : Sum<ForFluxK> {
  override fun foldable(): Foldable<ForFluxK> = FluxK.foldable()
}

@extension
interface FluxKOrderBy : OrderBy<ForFluxK> {
  override fun foldable(): Foldable<ForFluxK> = FluxK.foldable()
}

@extension
interface FluxKUnion : Union<ForFluxK> {
  override fun foldable(): Foldable<ForFluxK> = FluxK.foldable()
}