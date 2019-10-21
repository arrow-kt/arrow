package arrow.typeclasses.suspended.monad.commutative.unsafe

import arrow.typeclasses.suspended.monad.Fx as MonadFx
import arrow.typeclasses.suspended.monad.commutative.safe.Fx as SafeFx

/**
 * Fx allows you to run pure sequential code as if it was imperative.
 *
 * @see [arrow.typeclasses.suspended.BindSyntax]
 * @see [arrow.typeclasses.suspended.monad.Fx]
 * @see [arrow.typeclasses.suspended.monaderror.Fx]
 * @see [arrow.fx.typeclasses.suspended.monaddefer.Fx]
 * @see [arrow.fx.typeclasses.suspended.concurrent.Fx]
 * @see [arrow.typeclasses.suspended.monad.commutative.safe.Fx]
 * @see [arrow.typeclasses.suspended.monad.commutative.unsafe.Fx]
 */
interface Fx<F> : SafeFx<F>, MonadFx<F>
