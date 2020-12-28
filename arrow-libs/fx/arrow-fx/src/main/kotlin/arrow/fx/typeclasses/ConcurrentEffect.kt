package arrow.fx.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.fx.IODeprecation

/**
 * ank_macro_hierarchy(arrow.fx.typeclasses.ConcurrentEffect)
 */
@Deprecated(IODeprecation)
interface ConcurrentEffect<F> : Effect<F> {
  fun <A> Kind<F, A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> Kind<F, Unit>): Kind<F, Disposable>
}
