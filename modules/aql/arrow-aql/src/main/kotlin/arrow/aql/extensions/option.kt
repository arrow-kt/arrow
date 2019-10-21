package arrow.aql.extensions

import arrow.aql.Select
import arrow.aql.Where
import arrow.aql.From
import arrow.aql.GroupBy
import arrow.aql.Count
import arrow.aql.Sum
import arrow.aql.OrderBy
import arrow.aql.Union
import arrow.aql.Max
import arrow.aql.Min
import arrow.core.ForOption
import arrow.core.Option
import arrow.extension
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.foldable.foldable
import arrow.core.extensions.option.functor.functor
import arrow.core.extensions.option.functorFilter.functorFilter
import arrow.typeclasses.FunctorFilter
import arrow.typeclasses.Applicative
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor

@extension
interface OptionFrom : From<ForOption> {
  override fun applicative(): Applicative<ForOption> = Option.applicative()
}

@extension
interface OptionSelect : Select<ForOption> {
  override fun functor(): Functor<ForOption> = Option.functor()
}

@extension
interface OptionWhere : Where<ForOption> {
  override fun functorFilter(): FunctorFilter<ForOption> = Option.functorFilter()
}

@extension
interface OptionGroupBy : GroupBy<ForOption> {
  override fun foldable(): Foldable<ForOption> = Option.foldable()
}

@extension
interface OptionCount : Count<ForOption> {
  override fun foldable(): Foldable<ForOption> = Option.foldable()
}

@extension
interface OptionSum : Sum<ForOption> {
  override fun foldable(): Foldable<ForOption> = Option.foldable()
}

@extension
interface OptionOrderBy : OrderBy<ForOption> {
  override fun foldable(): Foldable<ForOption> = Option.foldable()
}

@extension
interface OptionUnion : Union<ForOption> {
  override fun foldable(): Foldable<ForOption> = Option.foldable()
}

@extension
interface OptionMax : Max<ForOption> {
  override fun foldable(): Foldable<ForOption> = Option.foldable()
}

@extension
interface OptionMin : Min<ForOption> {
  override fun foldable(): Foldable<ForOption> = Option.foldable()
}
