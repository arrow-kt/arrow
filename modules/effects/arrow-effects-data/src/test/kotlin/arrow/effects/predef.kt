package arrow.effects

import arrow.Kind
import arrow.core.extensions.either.eq.eq
import arrow.core.extensions.option.eq.eq
import arrow.effects.typeclasses.Duration
import arrow.effects.typeclasses.seconds
import arrow.typeclasses.Eq

fun <A> EQ(EQA: Eq<A> = Eq.any(), timeout: Duration = 60.seconds): Eq<Kind<ForIO, A>> = Eq { a, b ->
  arrow.core.Option.eq(arrow.core.Either.eq(Eq.any(), EQA)).run {
    a.fix().attempt().unsafeRunTimed(timeout).eqv(b.fix().attempt().unsafeRunTimed(timeout))
  }
}
