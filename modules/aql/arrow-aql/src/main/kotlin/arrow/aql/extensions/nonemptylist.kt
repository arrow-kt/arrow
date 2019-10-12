package arrow.aql.extensions

import arrow.aql.*
import arrow.core.ForNonEmptyList
import arrow.core.NonEmptyList
import arrow.extension
import arrow.core.extensions.nonemptylist.applicative.applicative
import arrow.core.extensions.nonemptylist.foldable.foldable
import arrow.core.extensions.nonemptylist.functor.functor
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

@extension
interface NonEmptyListMax : Max<ForNonEmptyList> {
  override fun foldable(): Foldable<ForNonEmptyList> = NonEmptyList.foldable()
}

@extension
interface NonEmptyListMin : Min<ForNonEmptyList> {
  override fun foldable(): Foldable<ForNonEmptyList> = NonEmptyList.foldable()
}
