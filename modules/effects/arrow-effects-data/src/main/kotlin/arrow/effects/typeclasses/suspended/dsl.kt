package arrow.effects.typeclasses.suspended

import arrow.Kind
import arrow.core.Either
import arrow.core.identity
import arrow.data.extensions.list.foldable.sequence_
import arrow.data.k
import arrow.effects.CancelToken
import arrow.effects.typeclasses.*
import arrow.typeclasses.suspended.MonadErrorSyntax
import arrow.typeclasses.suspended.MonadSyntax
import kotlin.coroutines.CoroutineContext

interface ListTraverseSyntax<F> : MonadSyntax<F> {
  suspend fun <A, B> List<Kind<F, A>>.traverse(f: (Kind<F, A>) -> Kind<F, B>) : List<B> =
    k().traverse(this@ListTraverseSyntax, f).bind()

  suspend fun <A> List<Kind<F, A>>.sequence() : List<A> =
    traverse(::identity)

  suspend fun <A> List<Kind<F, A>>.sequence_() : Unit =
    k().sequence_(this@ListTraverseSyntax).bind()
}

interface BracketSyntax<F, E> : MonadErrorSyntax<F, E>, Bracket<F, E>, ListTraverseSyntax<F> {

  private suspend fun <A> bracketing(fb: Bracket<F, E>.() -> Kind<F, A>): A =
    run<Bracket<F, E>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A, B> Kind<F, A>.bracketCase(
    unit: Unit = Unit,
    release: (A, ExitCase<E>) -> Kind<F, Unit>,
    use: (A) -> Kind<F, B>
  ): B =
    bracketing { bracketCase(release, use) }

  suspend fun <A, B> Kind<F, A>.bracket(
    unit: Unit = Unit,
    release: (A) -> Kind<F, Unit>,
    use: (A) -> Kind<F, B>
  ): B =
    bracketing { bracket(release, use) }

  suspend fun <A> Kind<F, A>.uncancelable(unit: Unit = Unit): A =
    bracketing { uncancelable() }

  suspend fun <A> Kind<F, A>.guarantee(
    unit: Unit = Unit,
    finalizer: Kind<F, Unit>
  ): A =
    bracketing { guarantee(finalizer) }

  suspend fun <A> Kind<F, A>.guaranteeCase(
    unit: Unit = Unit,
    finalizer: (ExitCase<E>) -> Kind<F, Unit>
  ): A =
    bracketing { guaranteeCase(finalizer) }

}

interface MonadDeferSyntax<F> : BracketSyntax<F, Throwable>, MonadDefer<F> {

  private suspend fun <A> deferring(fb: MonadDefer<F>.() -> Kind<F, A>): A =
    run<MonadDefer<F>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A> defer(unit: Unit = Unit, fb: () -> Kind<F, A>): A =
    deferring { defer(fb) }

  suspend fun <A> delay(unit: Unit = Unit, f: () -> A): A =
    deferring { delay(f) }

  suspend fun <A> delay(unit: Unit = Unit, fb: Kind<F, A>): A =
    deferring { delay(fb) }

  suspend fun lazy(unit: Unit = Unit): Unit =
    deferring { lazy() }

}

interface AsyncSyntax<F> : MonadDeferSyntax<F>, Async<F> {

  private suspend fun <A> asyncOp(fb: Async<F>.() -> Kind<F, A>): A =
    run<Async<F>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A> async(
    unit: Unit = Unit,
    fa: Proc<A>
  ): A =
    asyncOp { async(fa) }

  suspend fun <A> asyncF(
    unit: Unit = Unit,
    k: ProcF<F, A>
  ): A =
    asyncOp { asyncF(k) }

  suspend fun <A> delay(
    unit: Unit = Unit,
    ctx: CoroutineContext,
    f: () -> A
  ): A =
    asyncOp { delay(ctx, f) }

  suspend fun <A> defer(
    unit: Unit = Unit,
    ctx: CoroutineContext,
    f: () -> Kind<F, A>
  ): A =
    asyncOp { defer(ctx, f) }

  suspend fun CoroutineContext.shift(unit: Unit = Unit): Unit =
    asyncOp { shift() }

  suspend fun <A> never(unit: Unit = Unit): A =
    asyncOp { never<A>() }

  suspend fun <A> cancelable(
    unit: Unit = Unit,
    k: ((Either<Throwable, A>) -> Unit) -> CancelToken<F>
  ): A =
    asyncOp { cancelable(k) }

  suspend fun <A> cancelableF(
    unit: Unit = Unit,
    k: ((Either<Throwable, A>) -> Unit) -> Kind<F, CancelToken<F>>
  ): A =
    asyncOp { cancelableF(k) }
}

interface EffectSyntax<F> : Effect<F>, AsyncSyntax<F> {

  private suspend fun <A> effect(fb: Effect<F>.() -> Kind<F, A>): A =
    run<Effect<F>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A> Kind<F, A>.runAsync(
    unit: Unit = Unit,
    cb: (Either<Throwable, A>) -> Kind<F, Unit>
  ): Unit =
    effect { runAsync(cb) }

}

interface ConcurrentEffectSyntax<F> : ConcurrentEffect<F>, EffectSyntax<F> {

  private suspend fun <A> concurrentEffect(fb: ConcurrentEffect<F>.() -> Kind<F, A>): A =
    run<ConcurrentEffect<F>, Kind<F, A>> { fb(this) }.bind()

  suspend fun <A> Kind<F, A>.runAsyncCancellable(
    unit: Unit = Unit,
    cb: (Either<Throwable, A>) -> Kind<F, Unit>
  ): Disposable =
    concurrentEffect { runAsyncCancellable(cb) }

}

