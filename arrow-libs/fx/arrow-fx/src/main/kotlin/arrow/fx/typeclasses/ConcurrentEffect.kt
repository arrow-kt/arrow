package arrow.fx.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.fx.IODeprecation

@Deprecated(IODeprecation)
interface ConcurrentEffect<F> : Effect<F> {
  fun <A> Kind<F, A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> Kind<F, Unit>): Kind<F, Disposable>
}
