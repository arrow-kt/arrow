package arrow.aql.instances

import arrow.aql.*
import arrow.data.ForSequenceK
import arrow.data.SequenceK
import arrow.extension
import arrow.instances.sequencek.applicative.applicative
import arrow.instances.sequencek.foldable.foldable
import arrow.instances.sequencek.functor.functor
import arrow.mtl.instances.sequencek.functorFilter.functorFilter
import arrow.mtl.typeclasses.FunctorFilter
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