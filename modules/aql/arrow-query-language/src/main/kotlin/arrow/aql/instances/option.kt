package arrow.aql.instances

import arrow.aql.From
import arrow.aql.Select
import arrow.aql.Where
import arrow.core.ForOption
import arrow.core.Option
import arrow.extension
import arrow.instances.option.applicative.applicative
import arrow.instances.option.functor.functor
import arrow.mtl.instances.option.functorFilter.functorFilter
import arrow.mtl.typeclasses.FunctorFilter
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor

@extension interface OptionSelect : Select<ForOption> {
  override fun functor(): Functor<ForOption> = Option.functor()
}

@extension
interface OptionFrom : From<ForOption> {
  override fun applicative(): Applicative<ForOption> = Option.applicative()
}

@extension
interface OptionWhere : Where<ForOption> {
  override fun functorFilter(): FunctorFilter<ForOption> = Option.functorFilter()
}