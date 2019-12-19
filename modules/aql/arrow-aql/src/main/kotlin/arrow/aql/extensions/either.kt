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
import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.core.extensions.either.applicative.applicative
import arrow.core.extensions.either.foldable.foldable
import arrow.core.extensions.either.functor.functor
import arrow.extension
import arrow.typeclasses.Applicative
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor

@extension interface EitherSelect<L> : Select<EitherPartialOf<L>> {
  override fun functor(): Functor<EitherPartialOf<L>> = Either.functor()
}

@extension
interface EitherFrom<L> : From<EitherPartialOf<L>> {
  override fun applicative(): Applicative<EitherPartialOf<L>> = Either.applicative()
}

@extension
interface EitherGroupBy<L> : GroupBy<EitherPartialOf<L>> {
  override fun foldable(): Foldable<EitherPartialOf<L>> = Either.foldable()
}

@extension
interface EitherCount<L> : Count<EitherPartialOf<L>> {
  override fun foldable(): Foldable<EitherPartialOf<L>> = Either.foldable()
}

@extension
interface EitherSum<L> : Sum<EitherPartialOf<L>> {
  override fun foldable(): Foldable<EitherPartialOf<L>> = Either.foldable()
}

@extension
interface EitherOrderBy<L> : OrderBy<EitherPartialOf<L>> {
  override fun foldable(): Foldable<EitherPartialOf<L>> = Either.foldable()
}

@extension
interface EitherUnion<L> : Union<EitherPartialOf<L>> {
  override fun foldable(): Foldable<EitherPartialOf<L>> = Either.foldable()
}

@extension
interface EitherMax<L> : Max<EitherPartialOf<L>> {
  override fun foldable(): Foldable<EitherPartialOf<L>> = Either.foldable()
}

@extension
interface EitherMin<L> : Min<EitherPartialOf<L>> {
  override fun foldable(): Foldable<EitherPartialOf<L>> = Either.foldable()
}
