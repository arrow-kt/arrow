package arrow.typeclasses.suspended.monaderror

import arrow.Kind
import arrow.typeclasses.MonadErrorContinuation
import arrow.typeclasses.MonadThrow

interface Fx<F> {
  fun monadError(): MonadThrow<F>
  fun <A> fx(f: suspend MonadErrorContinuation<F, *>.() -> A): Kind<F, A> =
    monadError().bindingCatch(f)
}

