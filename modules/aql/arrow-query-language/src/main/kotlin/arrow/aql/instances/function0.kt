package arrow.aql.instances

import arrow.aql.From
import arrow.aql.Select
import arrow.core.ForFunction0
import arrow.core.Function0
import arrow.extension
import arrow.instances.function0.applicative.applicative
import arrow.instances.function0.functor.functor
import arrow.typeclasses.Applicative
import arrow.typeclasses.Functor

@extension interface Function0Select : Select<ForFunction0> {
  override fun functor(): Functor<ForFunction0> = Function0.functor()
}

@extension
interface Function0From : From<ForFunction0> {
  override fun applicative(): Applicative<ForFunction0> = Function0.applicative()
}