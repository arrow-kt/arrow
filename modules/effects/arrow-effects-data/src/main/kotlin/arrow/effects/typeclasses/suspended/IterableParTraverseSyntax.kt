package arrow.effects.typeclasses.suspended

import arrow.Kind
import arrow.data.extensions.list.traverse.traverse
import arrow.data.fix
import arrow.effects.typeclasses.Fiber
import arrow.typeclasses.suspended.MonadSyntax
import kotlin.coroutines.CoroutineContext

interface IterableParTraverseSyntax<F> : MonadSyntax<F> {

  fun <A> CoroutineContext.startFiber(f: suspend () -> A): Kind<F, Fiber<F, A>>

  fun <A, B> Iterable<Kind<F, A>>.parTraverse(
    ctx: CoroutineContext,
    f: suspend (A) -> B
  ): Kind<F, List<B>> =
    fold(emptyList<Kind<F, Fiber<F, B>>>()) { acc, fa ->
      acc + ctx.startFiber { f(!fa) }
    }.traverse(this@IterableParTraverseSyntax) { kind ->
      kind.flatMap { it.join() }
    }.map { it.fix() }

  fun <A> Iterable<Kind<F, A>>.parSequence(ctx: CoroutineContext): Kind<F, List<A>> =
    parTraverse(ctx, ::effectIdentity)

}