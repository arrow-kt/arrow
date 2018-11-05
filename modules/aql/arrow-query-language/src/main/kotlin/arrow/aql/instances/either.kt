package arrow.aql.instances

import arrow.aql.*
import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.extension
import arrow.instances.either.applicative.applicative
import arrow.instances.either.foldable.foldable
import arrow.instances.either.functor.functor
import arrow.instances.listk.foldable.foldable
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