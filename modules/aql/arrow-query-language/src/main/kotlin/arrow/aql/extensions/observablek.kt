package arrow.aql.extensions

import arrow.aql.*
import arrow.effects.rx2.ForObservableK
import arrow.effects.rx2.ObservableK
import arrow.effects.rx2.extensions.observablek.applicative.applicative
import arrow.effects.rx2.extensions.observablek.foldable.foldable
import arrow.effects.rx2.extensions.observablek.functor.functor
import arrow.effects.rx2.extensions.observablek.functorFilter.functorFilter
import arrow.extension
import arrow.mtl.typeclasses.FunctorFilter
import arrow.typeclasses.Applicative
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor

@extension
interface ObservableKFrom : From<ForObservableK> {
  override fun applicative(): Applicative<ForObservableK> = ObservableK.applicative()
}

@extension
interface ObservableKSelect : Select<ForObservableK> {
  override fun functor(): Functor<ForObservableK> = ObservableK.functor()
}

@extension
interface ObservableKWhere : Where<ForObservableK> {
  override fun functorFilter(): FunctorFilter<ForObservableK> = ObservableK.functorFilter()
}

@extension
interface ObservableKGroupBy : GroupBy<ForObservableK> {
  override fun foldable(): Foldable<ForObservableK> = ObservableK.foldable()
}

@extension
interface ObservableKCount : Count<ForObservableK> {
  override fun foldable(): Foldable<ForObservableK> = ObservableK.foldable()
}

@extension
interface ObservableKSum : Sum<ForObservableK> {
  override fun foldable(): Foldable<ForObservableK> = ObservableK.foldable()
}

@extension
interface ObservableKOrderBy : OrderBy<ForObservableK> {
  override fun foldable(): Foldable<ForObservableK> = ObservableK.foldable()
}

@extension
interface ObservableKUnion : Union<ForObservableK> {
  override fun foldable(): Foldable<ForObservableK> = ObservableK.foldable()
}