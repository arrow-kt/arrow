package arrow.aql.extensions

import arrow.aql.*
import arrow.data.ForNonEmptyList
import arrow.data.NonEmptyList
import arrow.extension
import arrow.data.extensions.nonemptylist.applicative.applicative
import arrow.data.extensions.nonemptylist.foldable.foldable
import arrow.data.extensions.nonemptylist.functor.functor
import arrow.mtl.extensions.nonemptylist.functorFilter.functorFilter
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
