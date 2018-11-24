package arrow.aql.instances

import arrow.aql.*
import arrow.data.ForNonEmptyList
import arrow.data.NonEmptyList
import arrow.extension
import arrow.instances.nonemptylist.applicative.applicative
import arrow.instances.nonemptylist.foldable.foldable
import arrow.instances.nonemptylist.functor.functor
import arrow.mtl.instances.nonemptylist.functorFilter.functorFilter
import arrow.mtl.typeclasses.FunctorFilter
import arrow.typeclasses.Applicative
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor

@extension
interface NonEmptyListFrom : From<ForNonEmptyList> {
  override fun applicative(): Applicative<ForNonEmptyList> = NonEmptyList.applicative()
}

@extension
interface NonEmptyListSelect : Select<ForNonEmptyList> {
  override fun functor(): Functor<ForNonEmptyList> = NonEmptyList.functor()
}

@extension
interface NonEmptyListWhere : Where<ForNonEmptyList> {
  override fun functorFilter(): FunctorFilter<ForNonEmptyList> = NonEmptyList.functorFilter()
}

@extension
interface NonEmptyListGroupBy : GroupBy<ForNonEmptyList> {
  override fun foldable(): Foldable<ForNonEmptyList> = NonEmptyList.foldable()
}

@extension
interface NonEmptyListCount : Count<ForNonEmptyList> {
  override fun foldable(): Foldable<ForNonEmptyList> = NonEmptyList.foldable()
}

@extension
interface NonEmptyListSum : Sum<ForNonEmptyList> {
  override fun foldable(): Foldable<ForNonEmptyList> = NonEmptyList.foldable()
}

@extension
interface NonEmptyListOrderBy : OrderBy<ForNonEmptyList> {
  override fun foldable(): Foldable<ForNonEmptyList> = NonEmptyList.foldable()
}

@extension
interface NonEmptyListUnion : Union<ForNonEmptyList> {
  override fun foldable(): Foldable<ForNonEmptyList> = NonEmptyList.foldable()
}
