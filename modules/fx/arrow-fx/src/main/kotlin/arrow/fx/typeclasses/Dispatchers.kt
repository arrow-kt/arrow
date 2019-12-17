package arrow.fx.typeclasses

import kotlin.coroutines.CoroutineContext

interface Dispatchers<F> {
  fun default(): CoroutineContext
  fun io(): CoroutineContext
}
