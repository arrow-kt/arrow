package arrow.aql.extensions

import arrow.aql.From
import arrow.aql.Select
import arrow.aql.Where
import arrow.effects.reactor.ForMonoK
import arrow.effects.reactor.MonoK
import arrow.effects.reactor.extensions.monok.applicative.applicative
import arrow.effects.reactor.extensions.monok.functor.functor
import arrow.effects.reactor.extensions.monok.functorFilter.functorFilter
import arrow.extension
import arrow.mtl.typeclasses.FunctorFilter
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor

@extension
interface MonoKFrom : From<ForMonoK> {
  override fun applicative(): Applicative<ForMonoK> = MonoK.applicative()
}

@extension
interface MonoKSelect : Select<ForMonoK> {
  override fun functor(): Functor<ForMonoK> = MonoK.functor()
}

@extension
interface MonoKWhere : Where<ForMonoK> {
  override fun functorFilter(): FunctorFilter<ForMonoK> = MonoK.functorFilter()
}