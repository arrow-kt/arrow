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
import arrow.core.ForSequenceK
import arrow.core.SequenceK
import arrow.extension
import arrow.core.extensions.sequencek.applicative.applicative
import arrow.core.extensions.sequencek.foldable.foldable
import arrow.core.extensions.sequencek.functor.functor
import arrow.core.extensions.sequencek.functorFilter.functorFilter
import arrow.typeclasses.FunctorFilter
import arrow.typeclasses.Applicative
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor

@extension
interface SequenceFrom : From<ForSequenceK> {
  override fun applicative(): Applicative<ForSequenceK> = SequenceK.applicative()
}

@extension
interface SequenceSelect : Select<ForSequenceK> {
  override fun functor(): Functor<ForSequenceK> = SequenceK.functor()
}

@extension
interface SequenceWhere : Where<ForSequenceK> {
  override fun functorFilter(): FunctorFilter<ForSequenceK> = SequenceK.functorFilter()
}

@extension
interface SequenceGroupBy : GroupBy<ForSequenceK> {
  override fun foldable(): Foldable<ForSequenceK> = SequenceK.foldable()
}

@extension
interface SequenceCount : Count<ForSequenceK> {
  override fun foldable(): Foldable<ForSequenceK> = SequenceK.foldable()
}

@extension
interface SequenceSum : Sum<ForSequenceK> {
  override fun foldable(): Foldable<ForSequenceK> = SequenceK.foldable()
}

@extension
interface SequenceOrderBy : OrderBy<ForSequenceK> {
  override fun foldable(): Foldable<ForSequenceK> = SequenceK.foldable()
}

@extension
interface SequenceUnion : Union<ForSequenceK> {
  override fun foldable(): Foldable<ForSequenceK> = SequenceK.foldable()
}

@extension
interface SequenceMax : Max<ForSequenceK> {
  override fun foldable(): Foldable<ForSequenceK> = SequenceK.foldable()
}

@extension
interface SequenceMin : Min<ForSequenceK> {
  override fun foldable(): Foldable<ForSequenceK> = SequenceK.foldable()
}
