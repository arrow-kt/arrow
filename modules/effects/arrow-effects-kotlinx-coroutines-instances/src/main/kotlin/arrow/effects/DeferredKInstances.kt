@file:Suppress("UnusedImports")
package arrow.effects

import arrow.Kind
import arrow.core.*
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.effects.deferredk.applicative.applicative
import arrow.effects.typeclasses.*
import arrow.extension
import arrow.typeclasses.*
import kotlin.coroutines.CoroutineContext
import arrow.effects.handleErrorWith as deferredHandleErrorWith
import arrow.effects.runAsync as deferredRunAsync
import arrow.effects.startF as deferredStartF

@extension
interface DeferredKFunctorInstance : Functor<ForDeferredK> {
  override fun <A, B> DeferredKOf<A>.map(f: (A) -> B): DeferredK<B> =
    fix().map(f)
}

@extension
interface DeferredKApplicativeInstance : Applicative<ForDeferredK> {
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
interface DeferredKMonadInstance : Monad<ForDeferredK> {
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
interface DeferredKApplicativeErrorInstance : ApplicativeError<ForDeferredK, Throwable>, DeferredKApplicativeInstance {
  override fun <A> raiseError(e: Throwable): DeferredK<A> =
    DeferredK.raiseError(e)

  override fun <A> DeferredKOf<A>.handleErrorWith(f: (Throwable) -> DeferredKOf<A>): DeferredK<A> =
    deferredHandleErrorWith { f(it).fix() }
}

@extension
interface DeferredKMonadErrorInstance : MonadError<ForDeferredK, Throwable>, DeferredKMonadInstance {
  override fun <A> raiseError(e: Throwable): DeferredK<A> =
    DeferredK.raiseError(e)

  override fun <A> DeferredKOf<A>.handleErrorWith(f: (Throwable) -> DeferredKOf<A>): DeferredK<A> =
    deferredHandleErrorWith { f(it).fix() }
}

@extension
interface DeferredKBracketInstance : Bracket<ForDeferredK, Throwable>, DeferredKMonadErrorInstance {
  override fun <A, B> DeferredKOf<A>.bracketCase(
    release: (A, ExitCase<Throwable>) -> Kind<ForDeferredK, Unit>,
    use: (A) -> Kind<ForDeferredK, B>
  ): DeferredK<B> =
    fix().bracketCase({ a -> use(a).fix() }, { a, e -> release(a, e).fix() })
}

@extension
interface DeferredKMonadDeferInstance : MonadDefer<ForDeferredK>, DeferredKBracketInstance {
  override fun <A> defer(fa: () -> DeferredKOf<A>): DeferredK<A> =
    DeferredK.defer(fa = fa)
}

@extension
interface DeferredKAsyncInstance : Async<ForDeferredK>, DeferredKMonadDeferInstance {
  override fun <A> async(fa: Proc<A>): DeferredK<A> =
    DeferredK.async(fa = { _, cb -> fa(cb) })

  override fun <A> asyncF(k: ProcF<ForDeferredK, A>): DeferredK<A> =
    DeferredK.asyncF(fa = { _, cb -> k(cb) })

  override fun <A> DeferredKOf<A>.continueOn(ctx: CoroutineContext): DeferredK<A> =
    fix().continueOn(ctx = ctx)

  override fun <A> invoke(ctx: CoroutineContext, f: () -> A): DeferredK<A> =
    DeferredK.invoke(ctx = ctx, f = { f() })
}

interface DeferredKConcurrentInstance : Concurrent<ForDeferredK>, DeferredKAsyncInstance {

  override fun <A> Kind<ForDeferredK, A>.startF(ctx: CoroutineContext): DeferredK<Fiber<ForDeferredK, A>> =
    deferredStartF(ctx)

  override fun <A> asyncF(k: ConnectedProcF<ForDeferredK, A>): DeferredK<A> =
    DeferredK.asyncF(fa = k)

  override fun <A> async(fa: ConnectedProc<ForDeferredK, A>): DeferredK<A> =
    DeferredK.async(fa = fa)

  override fun <A> asyncF(k: ProcF<ForDeferredK, A>): DeferredK<A> =
    DeferredK.asyncF(fa = { _, cb -> k(cb) })

  override fun <A> async(fa: Proc<A>): DeferredK<A> =
    DeferredK.async(fa = { _, cb -> fa(cb) })

  override fun <A, B> racePair(ctx: CoroutineContext,
                               lh: Kind<ForDeferredK, A>,
                               rh: Kind<ForDeferredK, B>): Kind<ForDeferredK, Either<Tuple2<A, Fiber<ForDeferredK, B>>, Tuple2<Fiber<ForDeferredK, A>, B>>> =
    DeferredK.racePair(ctx, lh, rh)

}

@extension
interface DeferredKEffectInstance : Effect<ForDeferredK>, DeferredKAsyncInstance {
  override fun <A> DeferredKOf<A>.runAsync(cb: (Either<Throwable, A>) -> DeferredKOf<Unit>): DeferredK<Unit> =
    fix().deferredRunAsync(cb = cb)
}

@extension
interface DeferredKConcurrentEffectInstance : ConcurrentEffect<ForDeferredK>, DeferredKEffectInstance, DeferredKConcurrentInstance {
  override fun <A> DeferredKOf<A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> Kind<ForDeferredK, Unit>): DeferredK<Disposable> =
    fix().runAsyncCancellable(onCancel = OnCancel.ThrowCancellationException, cb = cb)
}

object DeferredKContext : DeferredKConcurrentEffectInstance

@Deprecated(ExtensionsDSLDeprecated)
infix fun <A> ForDeferredK.Companion.extensions(f: DeferredKContext.() -> A): A =
  f(DeferredKContext)
