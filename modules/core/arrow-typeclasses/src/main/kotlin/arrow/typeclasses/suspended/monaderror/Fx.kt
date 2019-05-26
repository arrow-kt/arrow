package arrow.core.typeclasses.suspended.monaderror

import arrow.Kind
import arrow.core.typeclasses.MonadErrorContinuation
import arrow.core.typeclasses.MonadThrow

/**
 * Fx allows you to run pure sequential code as if it was imperative.
 *
 * @see [arrow.core.typeclasses.suspended.BindSyntax]
 * @see [arrow.core.typeclasses.suspended.monad.Fx]
 * @see [arrow.core.typeclasses.suspended.monaderror.Fx]
 * @see [arrow.effects.typeclasses.suspended.monaddefer.Fx]
 * @see [arrow.effects.typeclasses.suspended.concurrent.Fx]
 * @see [arrow.core.typeclasses.suspended.monad.commutative.safe.Fx]
 * @see [arrow.core.typeclasses.suspended.monad.commutative.unsafe.Fx]
 */
interface Fx<F> {
  fun monadError(): MonadThrow<F>
  fun <A> fx(f: suspend MonadErrorContinuation<F, *>.() -> A): Kind<F, A> =
    monadError().bindingCatch(f)
}
