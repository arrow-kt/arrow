package arrow.aql.extensions

import arrow.aql.Select
import arrow.aql.From
import arrow.aql.GroupBy
import arrow.aql.Count
import arrow.aql.Sum
import arrow.aql.OrderBy
import arrow.aql.Union
import arrow.aql.Max
import arrow.aql.Min
import arrow.core.ForTry
import arrow.core.Try
import arrow.core.extensions.`try`.applicative.applicative
import arrow.core.extensions.`try`.foldable.foldable
import arrow.core.extensions.`try`.functor.functor
import arrow.extension
import arrow.typeclasses.Applicative
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
  ReplaceWith("EitherFrom<L>")
)
@extension
interface TryFrom : From<ForTry> {
  override fun applicative(): Applicative<ForTry> = Try.applicative()
}

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
  ReplaceWith("EitherSelect<L>")
)
@extension
interface TrySelect : Select<ForTry> {
  override fun functor(): Functor<ForTry> = Try.functor()
}

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
  ReplaceWith("EitherGroupBy<L>")
)
@extension
interface TryGroupBy : GroupBy<ForTry> {
  override fun foldable(): Foldable<ForTry> = Try.foldable()
}

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
  ReplaceWith("EitherCount<L>")
)
@extension
interface TryCount : Count<ForTry> {
  override fun foldable(): Foldable<ForTry> = Try.foldable()
}

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
  ReplaceWith("EitherSum<L>")
)
@extension
interface TrySum : Sum<ForTry> {
  override fun foldable(): Foldable<ForTry> = Try.foldable()
}

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
  ReplaceWith("EitherOrderBy<L>")
)
@extension
interface TryOrderBy : OrderBy<ForTry> {
  override fun foldable(): Foldable<ForTry> = Try.foldable()
}

@Deprecated(
  "Try will be deleted soon as it promotes eager execution of effects, so it’s better if you work with Either’s suspend constructors or a an effect handler like IO",
  ReplaceWith("EitherUnion<L>")
)
@extension
interface TryUnion : Union<ForTry> {
  override fun foldable(): Foldable<ForTry> = Try.foldable()
}

@extension
interface TryMax : Max<ForTry> {
  override fun foldable(): Foldable<ForTry> = Try.foldable()
}

@extension
interface TryMin : Min<ForTry> {
  override fun foldable(): Foldable<ForTry> = Try.foldable()
}
