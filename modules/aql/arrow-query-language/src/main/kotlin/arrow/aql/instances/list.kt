package arrow.aql.instances

import arrow.aql.*
import arrow.data.ForListK
import arrow.data.ListK
import arrow.extension
import arrow.instances.listk.applicative.applicative
import arrow.instances.listk.foldable.foldable
import arrow.instances.listk.functor.functor
import arrow.mtl.instances.listk.functorFilter.functorFilter
import arrow.mtl.typeclasses.FunctorFilter
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