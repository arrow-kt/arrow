package arrow.aql.extensions

import arrow.aql.From
import arrow.aql.Select
import arrow.aql.Where
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.extensions.io.applicative.applicative
import arrow.effects.extensions.io.functor.functor
import arrow.effects.extensions.io.functorFilter.functorFilter
import arrow.extension
import arrow.mtl.typeclasses.FunctorFilter
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor

@extension
interface IOFrom : From<ForIO> {
  override fun applicative(): Applicative<ForIO> = IO.applicative()
}

@extension
interface IOSelect : Select<ForIO> {
  override fun functor(): Functor<ForIO> = IO.functor()
}

@extension
interface IOWhere : Where<ForIO> {
  override fun functorFilter(): FunctorFilter<ForIO> = IO.functorFilter()
}