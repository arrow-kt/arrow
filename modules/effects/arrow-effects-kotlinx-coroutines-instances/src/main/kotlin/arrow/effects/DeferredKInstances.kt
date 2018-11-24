package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.effects.deferredk.applicative.applicative
import arrow.effects.typeclasses.*
import arrow.extension
import arrow.typeclasses.*
import kotlin.coroutines.CoroutineContext
import arrow.effects.handleErrorWith as deferredHandleErrorWith
import arrow.effects.runAsync as deferredRunAsync

@extension
interface DeferredKFunctorInstance : Functor<ForDeferredK> {
  override fun <A, B> Kind<ForDeferredK, A>.map(f: (A) -> B): DeferredK<B> =
    fix().map(f)
}

@extension
interface DeferredKApplicativeInstance : Applicative<ForDeferredK> {
  override fun <A, B> Kind<ForDeferredK, A>.map(f: (A) -> B): DeferredK<B> =
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
  override fun <A, B> Kind<ForDeferredK, A>.flatMap(f: (A) -> Kind<ForDeferredK, B>): DeferredK<B> =
    fix().flatMap(f = f)

  override fun <A, B> Kind<ForDeferredK, A>.map(f: (A) -> B): DeferredK<B> =
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
  override fun <A, B> Kind<ForDeferredK, A>.bracketCase(
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
    DeferredK.async(fa = fa)

  override fun <A> DeferredKOf<A>.continueOn(ctx: CoroutineContext): DeferredK<A> =
    fix().continueOn(ctx = ctx)

  override fun <A> invoke(ctx: CoroutineContext, f: () -> A): Kind<ForDeferredK, A> =
    DeferredK.invoke(ctx = ctx, f = f)
}

@extension
interface DeferredKEffectInstance : Effect<ForDeferredK>, DeferredKAsyncInstance {
  override fun <A> Kind<ForDeferredK, A>.runAsync(cb: (Either<Throwable, A>) -> DeferredKOf<Unit>): DeferredK<Unit> =
    fix().deferredRunAsync(cb = cb)
}

@extension
interface DeferredKConcurrentEffectInstance : ConcurrentEffect<ForDeferredK>, DeferredKEffectInstance {
  override fun <A> Kind<ForDeferredK, A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> Kind<ForDeferredK, Unit>): Kind<ForDeferredK, Disposable> =
    fix().runAsyncCancellable(onCancel = OnCancel.ThrowCancellationException, cb = cb)
}

object DeferredKContext : DeferredKConcurrentEffectInstance

@Deprecated(ExtensionsDSLDeprecated)
infix fun <A> ForDeferredK.Companion.extensions(f: DeferredKContext.() -> A): A =
  f(DeferredKContext)