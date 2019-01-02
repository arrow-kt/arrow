package arrow.aql.instances

import arrow.aql.*
import arrow.core.ForTry
import arrow.core.Try
import arrow.extension
import arrow.instances.`try`.applicative.applicative
import arrow.instances.`try`.foldable.foldable
import arrow.instances.`try`.functor.functor
import arrow.mtl.instances.`try`.functorFilter.functorFilter
import arrow.mtl.typeclasses.FunctorFilter
import arrow.typeclasses.Applicative
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor

@extension
interface TryFrom : From<ForTry> {
  override fun applicative(): Applicative<ForTry> = Try.applicative()
}

@extension
interface TrySelect : Select<ForTry> {
  override fun functor(): Functor<ForTry> = Try.functor()
}

@extension
interface TryWhere : Where<ForTry> {
  override fun functorFilter(): FunctorFilter<ForTry> = Try.functorFilter()
}

@extension
interface TryGroupBy : GroupBy<ForTry> {
  override fun foldable(): Foldable<ForTry> = Try.foldable()
}

@extension
interface TryCount : Count<ForTry> {
  override fun foldable(): Foldable<ForTry> = Try.foldable()
}

@extension
interface TrySum : Sum<ForTry> {
  override fun foldable(): Foldable<ForTry> = Try.foldable()
}

@extension
interface TryOrderBy : OrderBy<ForTry> {
  override fun foldable(): Foldable<ForTry> = Try.foldable()
}

@extension
interface TryUnion : Union<ForTry> {
  override fun foldable(): Foldable<ForTry> = Try.foldable()
}