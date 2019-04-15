package arrow.effects.typeclasses.suspended.monaddefer

import arrow.Kind
import arrow.effects.typeclasses.MonadDefer
import arrow.effects.typeclasses.MonadDeferCancellableContinuation

/**
 * Fx allows you to run pure sequential code as if it was imperative.
 *
 * @see [arrow.typeclasses.suspended.BindSyntax]
 * @see [arrow.typeclasses.suspended.monad.Fx]
 * @see [arrow.typeclasses.suspended.monaderror.Fx]
 * @see [arrow.effects.typeclasses.suspended.monaddefer.Fx]
 * @see [arrow.effects.typeclasses.suspended.concurrent.Fx]
 * @see [arrow.typeclasses.suspended.monad.commutative.safe.Fx]
 * @see [arrow.typeclasses.suspended.monad.commutative.unsafe.Fx]
 */
interface Fx<F> {
  fun monadDefer(): MonadDefer<F>
  fun <A> fx(f: suspend MonadDeferCancellableContinuation<F, *>.() -> A): Kind<F, A> =
    monadDefer().bindingCancellable(f).a
}
