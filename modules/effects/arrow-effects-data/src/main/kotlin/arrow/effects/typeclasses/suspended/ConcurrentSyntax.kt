package arrow.effects.typeclasses.suspended

import arrow.Kind
import arrow.effects.typeclasses.Concurrent
import arrow.effects.typeclasses.Fiber
import kotlin.coroutines.CoroutineContext

interface ConcurrentSyntax<F> : AsyncSyntax<F>, Concurrent<F>, IterableParTraverseSyntax<F> {

  private fun <A> concurrently(fb: Concurrent<F>.() -> Kind<F, A>): Kind<F, A> =
    run<Concurrent<F>, Kind<F, A>> { fb(this) }

  override fun <A> CoroutineContext.startFiber(f: suspend () -> A): Kind<F, Fiber<F, A>> =
    concurrently { f.effect().startF(this@startFiber) }

}