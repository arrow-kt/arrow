package arrow.typeclasses.suspended.monad.commutative.unsafe

import arrow.typeclasses.suspended.monad.Fx as MonadFx
import arrow.typeclasses.suspended.monad.commutative.safe.Fx as SafeFx

interface Fx<F> : SafeFx<F>, MonadFx<F>
