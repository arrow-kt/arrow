package arrow.fx.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.fx.IODeprecation

/**
 * ank_macro_hierarchy(arrow.fx.typeclasses.Effect)
 */
@Deprecated(IODeprecation)
interface Effect<F> : Async<F> {
  fun <A> Kind<F, A>.runAsync(cb: (Either<Throwable, A>) -> Kind<F, Unit>): Kind<F, Unit>
}
