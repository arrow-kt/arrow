package arrow.effects.typeclasses

import kotlin.coroutines.CoroutineContext

interface Dispatchers<F> {
  fun default(): CoroutineContext
}
