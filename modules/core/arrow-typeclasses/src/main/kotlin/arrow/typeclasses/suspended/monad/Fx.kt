package arrow.typeclasses.suspended.monad

import arrow.Kind
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadContinuation

interface Fx<F> {
  fun monad(): Monad<F>
  fun <A> fx(f: suspend MonadContinuation<F, *>.() -> A): Kind<F, A> =
    monad().fx(f)
}

