package arrow.aql.extensions

import arrow.aql.From
import arrow.aql.Select
import arrow.core.ForId
import arrow.core.Id
import arrow.extension
import arrow.core.extensions.id.applicative.applicative
import arrow.core.extensions.id.functor.functor
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor

@extension interface IdSelect : Select<ForId> {
  override fun functor(): Functor<ForId> = Id.functor()
}

@extension
interface IdFrom : From<ForId> {
  override fun applicative(): Applicative<ForId> = Id.applicative()
}