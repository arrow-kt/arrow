package arrow.fx.typeclasses

import arrow.Kind
import arrow.core.Either

/**
 * ank_macro_hierarchy(arrow.fx.typeclasses.Effect)
 */
interface Effect<F> : Async<F> {
  fun <A> Kind<F, A>.runAsync(cb: (Either<Throwable, A>) -> Kind<F, Unit>): Kind<F, Unit>
}
