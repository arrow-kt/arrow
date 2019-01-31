package arrow.effects.typeclasses.suspended

import arrow.effects.typeclasses.Fiber
import arrow.typeclasses.suspended.MonadSyntax
import kotlin.coroutines.CoroutineContext

interface IterableParTraverseSyntax<F> : MonadSyntax<F> {

  suspend fun <A> CoroutineContext.startFiber(f: suspend () -> A): Fiber<F, A>

  suspend fun <A, B> Iterable<suspend () -> A>.parTraverse(
    ctx: CoroutineContext,
    f: suspend (A) -> B
  ): List<B> =
    fold(emptyList<Fiber<F, B>>()) { acc, fa ->
      acc + ctx.startFiber { f(fa()) }
    }.map { it.join().bind() }

  suspend fun <A> Iterable<suspend () -> A>.parSequence(ctx: CoroutineContext): List<A> =
    parTraverse(ctx, ::effectIdentity)

}