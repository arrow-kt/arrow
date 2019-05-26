package arrow.core.typeclasses.suspended.monad.commutative.unsafe

import arrow.core.typeclasses.suspended.monad.Fx as MonadFx
import arrow.core.typeclasses.suspended.monad.commutative.safe.Fx as SafeFx

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
interface Fx<F> : SafeFx<F>, MonadFx<F>
