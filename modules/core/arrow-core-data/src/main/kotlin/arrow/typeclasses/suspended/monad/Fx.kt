package arrow.typeclasses.suspended.monad

import arrow.Kind
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadSyntax

/**
 * Fx allows you to run pure sequential code as if it was imperative.
 *
 * You can use any of the methods defined in [arrow.typeclasses.suspended.BindSyntax] to execute a [Kind] for the current [F]
 *
 * @see [arrow.typeclasses.suspended.BindSyntax]
 * @see [arrow.typeclasses.suspended.monad.Fx]
 * @see [arrow.typeclasses.suspended.monaderror.Fx]
 * @see [arrow.fx.typeclasses.suspended.monaddefer.Fx]
 * @see [arrow.fx.typeclasses.suspended.concurrent.Fx]
 * @see [arrow.typeclasses.suspended.monad.commutative.safe.Fx]
 * @see [arrow.typeclasses.suspended.monad.commutative.unsafe.Fx]
 */
interface Fx<F> {
  fun monad(): Monad<F>
  fun <A> fx(f: suspend MonadSyntax<F>.() -> A): Kind<F, A> =
    monad().fx.monad(f)
}
