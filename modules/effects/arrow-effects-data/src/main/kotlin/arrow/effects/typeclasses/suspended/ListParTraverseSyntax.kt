package arrow.effects.typeclasses.suspended

import arrow.effects.typeclasses.Fiber
import arrow.typeclasses.suspended.MonadSyntax
import kotlin.coroutines.CoroutineContext

interface ListParTraverseSyntax<F> : MonadSyntax<F> {

  suspend fun <A> CoroutineContext.startFiber(f: suspend () -> A): Fiber<F, A>

  suspend fun <A, B> List<suspend () -> A>.parTraverse(
    ctx: CoroutineContext,
    f: suspend (A) -> B
  ): List<B> =
    fold(emptyList<Fiber<F, B>>()) { acc, fa ->
      acc + ctx.startFiber { f(fa()) }
    }.map { it.join().bind() }

  suspend fun <A> List<suspend () -> A>.parSequence(ctx: CoroutineContext): List<A> =
    parTraverse(ctx, ::effectIdentity)

}