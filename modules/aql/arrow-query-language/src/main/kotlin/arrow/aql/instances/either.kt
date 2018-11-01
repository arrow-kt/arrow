package arrow.aql.instances

import arrow.aql.From
import arrow.aql.Select
import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.extension
import arrow.instances.either.applicative.applicative
import arrow.instances.either.functor.functor
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor

@extension interface EitherSelect<L> : Select<EitherPartialOf<L>> {
  override fun functor(): Functor<EitherPartialOf<L>> = Either.functor()
}

@extension
interface EitherFrom<L> : From<EitherPartialOf<L>> {
  override fun applicative(): Applicative<EitherPartialOf<L>> = Either.applicative()
}