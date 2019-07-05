package arrow.aql.extensions

import arrow.aql.Count
import arrow.aql.From
import arrow.aql.GroupBy
import arrow.aql.OrderBy
import arrow.aql.Select
import arrow.aql.Sum
import arrow.aql.Union
import arrow.aql.Where
import arrow.core.ForListK
import arrow.core.ListK
import arrow.extension
import arrow.core.extensions.listk.applicative.applicative
import arrow.core.extensions.listk.foldable.foldable
import arrow.core.extensions.listk.functor.functor
import arrow.core.extensions.listk.functorFilter.functorFilter
import arrow.typeclasses.FunctorFilter
import arrow.typeclasses.Applicative
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor

@extension
interface ListFrom : From<ForListK> {
  override fun applicative(): Applicative<ForListK> = ListK.applicative()
}

@extension
interface ListSelect : Select<ForListK> {
  override fun functor(): Functor<ForListK> = ListK.functor()
}

@extension
interface ListWhere : Where<ForListK> {
  override fun functorFilter(): FunctorFilter<ForListK> = ListK.functorFilter()
}

@extension
interface ListGroupBy : GroupBy<ForListK> {
  override fun foldable(): Foldable<ForListK> = ListK.foldable()
}

@extension
interface ListCount : Count<ForListK> {
  override fun foldable(): Foldable<ForListK> = ListK.foldable()
}

@extension
interface ListSum : Sum<ForListK> {
  override fun foldable(): Foldable<ForListK> = ListK.foldable()
}

@extension
interface ListOrderBy : OrderBy<ForListK> {
  override fun foldable(): Foldable<ForListK> = ListK.foldable()
}

@extension
interface ListUnion : Union<ForListK> {
  override fun foldable(): Foldable<ForListK> = ListK.foldable()
}
