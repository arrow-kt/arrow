package arrow.effects.typeclasses

import arrow.Kind
import arrow.core.Either

interface ConcurrentEffect<F> : Effect<F>, Concurrent<F> {
  fun <A> Kind<F, A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> Kind<F, Unit>): Kind<F, Disposable>
}
