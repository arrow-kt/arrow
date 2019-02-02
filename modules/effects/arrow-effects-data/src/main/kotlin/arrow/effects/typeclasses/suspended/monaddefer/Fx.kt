package arrow.effects.typeclasses.suspended.monaddefer

import arrow.Kind
import arrow.core.Tuple2
import arrow.effects.typeclasses.Disposable
import arrow.effects.typeclasses.MonadDefer
import arrow.effects.typeclasses.MonadDeferCancellableContinuation

interface Fx<F> {
  fun monadDefer(): MonadDefer<F>
  fun <A> fx(f: suspend MonadDeferCancellableContinuation<F, *>.() -> A): Kind<F, A> =
    monadDefer().bindingCancellable(f).a
  fun <A> fxCancellable(f: suspend MonadDeferCancellableContinuation<F, *>.() -> A): Tuple2<Kind<F, A>, Disposable> =
    monadDefer().bindingCancellable(f)
}

