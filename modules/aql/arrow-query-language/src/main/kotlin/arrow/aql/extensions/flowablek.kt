package arrow.aql.extensions

import arrow.aql.*
import arrow.effects.rx2.ForFlowableK
import arrow.effects.rx2.FlowableK
import arrow.effects.rx2.extensions.flowablek.applicative.applicative
import arrow.effects.rx2.extensions.flowablek.foldable.foldable
import arrow.effects.rx2.extensions.flowablek.functor.functor
import arrow.effects.rx2.extensions.flowablek.functorFilter.functorFilter
import arrow.extension
import arrow.mtl.typeclasses.FunctorFilter
import arrow.typeclasses.Applicative
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor

@extension
interface FlowableKFrom : From<ForFlowableK> {
  override fun applicative(): Applicative<ForFlowableK> = FlowableK.applicative()
}

@extension
interface FlowableKSelect : Select<ForFlowableK> {
  override fun functor(): Functor<ForFlowableK> = FlowableK.functor()
}

@extension
interface FlowableKWhere : Where<ForFlowableK> {
  override fun functorFilter(): FunctorFilter<ForFlowableK> = FlowableK.functorFilter()
}

@extension
interface FlowableKGroupBy : GroupBy<ForFlowableK> {
  override fun foldable(): Foldable<ForFlowableK> = FlowableK.foldable()
}

@extension
interface FlowableKCount : Count<ForFlowableK> {
  override fun foldable(): Foldable<ForFlowableK> = FlowableK.foldable()
}

@extension
interface FlowableKSum : Sum<ForFlowableK> {
  override fun foldable(): Foldable<ForFlowableK> = FlowableK.foldable()
}

@extension
interface FlowableKOrderBy : OrderBy<ForFlowableK> {
  override fun foldable(): Foldable<ForFlowableK> = FlowableK.foldable()
}

@extension
interface FlowableKUnion : Union<ForFlowableK> {
  override fun foldable(): Foldable<ForFlowableK> = FlowableK.foldable()
}