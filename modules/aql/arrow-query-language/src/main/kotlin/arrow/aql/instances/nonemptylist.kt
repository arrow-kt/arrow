package arrow.aql.instances

import arrow.aql.From
import arrow.aql.Select
import arrow.aql.Where
import arrow.data.ForNonEmptyList
import arrow.data.NonEmptyList
import arrow.extension
import arrow.instances.nonemptylist.applicative.applicative
import arrow.instances.nonemptylist.functor.functor
import arrow.mtl.instances.nonemptylist.functorFilter.functorFilter
import arrow.mtl.typeclasses.FunctorFilter
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor

@extension interface NonEmptyListSelect : Select<ForNonEmptyList> {
  override fun functor(): Functor<ForNonEmptyList> = NonEmptyList.functor()
}

@extension
interface NonEmptyListFrom : From<ForNonEmptyList> {
  override fun applicative(): Applicative<ForNonEmptyList> = NonEmptyList.applicative()
}

@extension
interface NonEmptyListWhere : Where<ForNonEmptyList> {
  override fun functorFilter(): FunctorFilter<ForNonEmptyList> = NonEmptyList.functorFilter()
}

