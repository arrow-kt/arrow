package arrow.aql.instances

import arrow.aql.From
import arrow.aql.Select
import arrow.aql.Where
import arrow.data.ForSequenceK
import arrow.data.SequenceK
import arrow.extension
import arrow.instances.sequencek.applicative.applicative
import arrow.instances.sequencek.functor.functor
import arrow.mtl.instances.sequencek.functorFilter.functorFilter
import arrow.mtl.typeclasses.FunctorFilter
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor

@extension
interface SequenceKFrom : From<ForSequenceK> {
  override fun applicative(): Applicative<ForSequenceK> = SequenceK.applicative()
}

@extension interface SequenceKSelect : Select<ForSequenceK> {
  override fun functor(): Functor<ForSequenceK> = SequenceK.functor()
}

@extension
interface SequenceKWhere : Where<ForSequenceK> {
  override fun functorFilter(): FunctorFilter<ForSequenceK> = SequenceK.functorFilter()
}