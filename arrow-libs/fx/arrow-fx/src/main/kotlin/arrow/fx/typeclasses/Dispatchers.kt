package arrow.fx.typeclasses

import arrow.fx.IODeprecation
import kotlin.coroutines.CoroutineContext

@Deprecated(IODeprecation)
interface Dispatchers<F> {
  fun default(): CoroutineContext
  fun io(): CoroutineContext
}
