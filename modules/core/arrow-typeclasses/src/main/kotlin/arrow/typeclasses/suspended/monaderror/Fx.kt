package arrow.typeclasses.suspended.monaderror

import arrow.Kind
import arrow.typeclasses.MonadContinuation
import arrow.typeclasses.MonadThrow

interface Fx<F> {
  fun monadError(): MonadThrow<F>
  fun <A> fx(f: suspend MonadContinuation<F, *>.() -> A): Kind<F, A> =
    monadError().bindingCatch(f)
}

