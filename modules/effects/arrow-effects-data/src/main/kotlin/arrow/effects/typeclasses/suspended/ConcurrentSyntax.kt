package arrow.effects.typeclasses.suspended

import arrow.Kind
import arrow.core.identity
import arrow.data.extensions.list.traverse.traverse
import arrow.data.fix
import arrow.effects.typeclasses.Concurrent
import arrow.effects.typeclasses.Fiber
import kotlin.coroutines.CoroutineContext

interface ConcurrentSyntax<F> : AsyncSyntax<F>, Concurrent<F> {

  val NonBlocking: CoroutineContext
    get() = dispatchers().default()

  fun <A, B> CoroutineContext.parTraverse(
    effects: Iterable<Kind<F, A>>,
    f: (A) -> B
  ): Kind<F, List<B>> =
    effects.fold(emptyList<Kind<F, Fiber<F, B>>>()) { acc, fa ->
      acc + startFiber(fa.map(f))
    }.traverse(this@ConcurrentSyntax) { kind ->
      kind.flatMap { it.join() }
    }.map { it.fix() }

  fun <A> CoroutineContext.parSequence(effects: Iterable<Kind<F, A>>): Kind<F, List<A>> =
    parTraverse(effects, ::identity)

}