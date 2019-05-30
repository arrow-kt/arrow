package arrow.typeclasses.suspended.monaderror

import arrow.Kind
import arrow.typeclasses.MonadThrowContinuation
import arrow.typeclasses.MonadThrow
import arrow.typeclasses.MonadThrowContext

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
  fun monadError(): MonadThrow<F>
  fun <A> fx(f: suspend MonadThrowContext<F>.() -> A): Kind<F, A> =
    monadError().bindingCatch(f)
}
