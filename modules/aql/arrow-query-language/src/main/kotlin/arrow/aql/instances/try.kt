package arrow.aql.instances

import arrow.aql.From
import arrow.aql.GroupBy
import arrow.aql.Select
import arrow.core.ForTry
import arrow.core.Try
import arrow.extension
import arrow.instances.`try`.applicative.applicative
import arrow.instances.`try`.foldable.foldable
import arrow.instances.`try`.functor.functor
import arrow.typeclasses.Applicative
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor

@extension interface TrySelect : Select<ForTry> {
  override fun functor(): Functor<ForTry> = Try.functor()
}

@extension
interface TryFrom : From<ForTry> {
  override fun applicative(): Applicative<ForTry> = Try.applicative()
}

@extension
interface TryGroupBy : GroupBy<ForTry> {
  override fun foldable(): Foldable<ForTry> = Try.foldable()
}