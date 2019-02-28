package arrow.effects.typeclasses.suspended.monaddefer

import arrow.Kind
import arrow.effects.typeclasses.MonadDefer
import arrow.effects.typeclasses.MonadDeferCancellableContinuation

interface Fx<F> {
  fun monadDefer(): MonadDefer<F>
  fun <A> fx(f: suspend MonadDeferCancellableContinuation<F, *>.() -> A): Kind<F, A> =
    monadDefer().bindingCancellable(f).a
}
