package arrow.fx.typeclasses

import arrow.Kind
import arrow.fx.IODeprecation

@Deprecated(IODeprecation)
interface Environment<F> {
  fun dispatchers(): Dispatchers<F>

  fun handleAsyncError(e: Throwable): Kind<F, Unit>
}
