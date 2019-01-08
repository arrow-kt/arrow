@file:Suppress("UnusedImports")

package arrow.effects.coroutines.extensions

import arrow.Kind
import arrow.core.Either
import arrow.effects.OnCancel
import arrow.effects.coroutines.*
import arrow.effects.coroutines.extensions.deferredk.applicative.applicative
import arrow.effects.typeclasses.*
import arrow.extension
import arrow.typeclasses.*
import kotlin.coroutines.CoroutineContext
import arrow.effects.coroutines.handleErrorWith as deferredHandleErrorWith
import arrow.effects.coroutines.runAsync as deferredRunAsync

@extension
interface DeferredKFunctor : Functor<ForDeferredK> {
  override fun <A, B> DeferredKOf<A>.map(f: (A) -> B): DeferredK<B> =
    fix().map(f)
}

@extension
interface DeferredKApplicative : Applicative<ForDeferredK> {
  override fun <A, B> DeferredKOf<A>.map(f: (A) -> B): DeferredK<B> =
    fix().map(f)

  override fun <A> just(a: A): DeferredK<A> =
    DeferredK.just(a)

  override fun <A, B> DeferredKOf<A>.ap(ff: DeferredKOf<(A) -> B>): DeferredK<B> =
    fix().ap(ff)
}

suspend fun <F, A> Kind<F, DeferredKOf<A>>.awaitAll(T: Traverse<F>): Kind<F, A> = T.run {
  this@awaitAll.sequence(DeferredK.applicative()).await()
}

@extension
interface DeferredKMonad : Monad<ForDeferredK> {
  override fun <A, B> DeferredKOf<A>.flatMap(f: (A) -> Kind<ForDeferredK, B>): DeferredK<B> =
    fix().flatMap(f = f)

  override fun <A, B> DeferredKOf<A>.map(f: (A) -> B): DeferredK<B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> DeferredKOf<Either<A, B>>): DeferredK<B> =
    DeferredK.tailRecM(a, f)

  override fun <A, B> DeferredKOf<A>.ap(ff: DeferredKOf<(A) -> B>): DeferredK<B> =
    fix().ap(ff)

  override fun <A> just(a: A): DeferredK<A> =
    DeferredK.just(a)
}

@extension
interface DeferredKApplicativeError : ApplicativeError<ForDeferredK, Throwable>, DeferredKApplicative {
  override fun <A> raiseError(e: Throwable): DeferredK<A> =
    DeferredK.raiseError(e)

  override fun <A> DeferredKOf<A>.handleErrorWith(f: (Throwable) -> DeferredKOf<A>): DeferredK<A> =
    deferredHandleErrorWith { f(it).fix() }
}

@extension
interface DeferredKMonadError : MonadError<ForDeferredK, Throwable>, DeferredKMonad {
  override fun <A> raiseError(e: Throwable): DeferredK<A> =
    DeferredK.raiseError(e)

  override fun <A> DeferredKOf<A>.handleErrorWith(f: (Throwable) -> DeferredKOf<A>): DeferredK<A> =
    deferredHandleErrorWith { f(it).fix() }
}

@extension
interface DeferredKMonadThrow : MonadThrow<ForDeferredK>, DeferredKMonadError

@extension
interface DeferredKBracket : Bracket<ForDeferredK, Throwable>, DeferredKMonadThrow {
  override fun <A, B> DeferredKOf<A>.bracketCase(
    release: (A, ExitCase<Throwable>) -> DeferredKOf<Unit>,
    use: (A) -> DeferredKOf<B>
  ): DeferredK<B> =
    fix().bracketCase(use = use, release = release)
}

@extension
interface DeferredKMonadDefer : MonadDefer<ForDeferredK>, DeferredKBracket {
  override fun <A> defer(fa: () -> DeferredKOf<A>): DeferredK<A> =
    DeferredK.defer(fa = fa)
}

@extension
interface DeferredKAsync : Async<ForDeferredK>, DeferredKMonadDefer {
  override fun <A> async(fa: Proc<A>): DeferredK<A> =
    DeferredK.async(fa = { _, cb -> fa(cb) })

  override fun <A> asyncF(k: ProcF<ForDeferredK, A>): DeferredK<A> =
    DeferredK.asyncF(fa = { _, cb -> k(cb) })

  override fun <A> DeferredKOf<A>.continueOn(ctx: CoroutineContext): DeferredK<A> =
    fix().continueOn(ctx = ctx)

  override fun <A> invoke(ctx: CoroutineContext, f: () -> A): DeferredK<A> =
    DeferredK.invoke(ctx = ctx, f = { f() })
}

@extension
interface DeferredKEffect : Effect<ForDeferredK>, DeferredKAsync {
  override fun <A> DeferredKOf<A>.runAsync(cb: (Either<Throwable, A>) -> DeferredKOf<Unit>): DeferredK<Unit> =
    deferredRunAsync(cb = cb)
}

@extension
interface DeferredKConcurrentEffect : ConcurrentEffect<ForDeferredK>, DeferredKEffect {
  override fun <A> DeferredKOf<A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> Kind<ForDeferredK, Unit>): DeferredK<Disposable> =
    fix().runAsyncCancellable(onCancel = OnCancel.ThrowCancellationException, cb = cb)
}
