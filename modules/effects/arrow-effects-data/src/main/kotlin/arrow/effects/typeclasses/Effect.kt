package arrow.effects.typeclasses

import arrow.Kind
import arrow.core.Either

/**
 * ank_macro_hierarchy(arrow.effects.typeclasses.Effect)
 */
interface Effect<F> : Async<F> {
  fun <A> Kind<F, A>.runAsync(cb: (Either<Throwable, A>) -> Kind<F, Unit>): Kind<F, Unit>
}
