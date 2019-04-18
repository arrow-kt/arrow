package arrow.effects.extensions

import arrow.Kind
import arrow.core.*
import arrow.effects.IODispatchers
import arrow.effects.extensions.fx.dispatchers.dispatchers
import arrow.effects.internal.Platform
import arrow.effects.internal.UnsafePromise
import arrow.effects.internal.asyncContinuation
import arrow.effects.suspended.fx.*
import arrow.effects.typeclasses.*
import arrow.effects.typeclasses.suspended.FxSyntax
import arrow.extension
import arrow.typeclasses.*
import arrow.unsafe
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.*
import  arrow.effects.suspended.fx.guaranteeCase as guaranteeC
import  arrow.effects.suspended.fx.bracketCase as bracketC

@extension
interface FxDispatchers : Dispatchers<ForFx> {
  override fun default(): CoroutineContext =
    IODispatchers.CommonPool
}

val NonBlocking: CoroutineContext = Fx.dispatchers().default()

@extension
interface FxUnsafeRun : UnsafeRun<ForFx> {

  override suspend fun <A> unsafe.runBlocking(fa: () -> FxOf<A>): A =
    Fx.unsafeRunBlocking(fa())

  override suspend fun <A> unsafe.runNonBlocking(fa: () -> FxOf<A>, cb: (Either<Throwable, A>) -> Unit) =
    Fx.unsafeRunNonBlocking(fa(), cb)
}

@extension
interface FxEnvironment : Environment<ForFx> {
  override fun dispatchers(): Dispatchers<ForFx> =
    Fx.dispatchers()

  override fun handleAsyncError(e: Throwable): Fx<Unit> =
    Fx { println("Found uncaught async exception!"); e.printStackTrace() }
}


@extension
interface FxFunctor : Functor<ForFx> {
  override fun <A, B> FxOf<A>.map(f: (A) -> B): Fx<B> =
    fix().map(f)
}

@extension
interface FxApplicative : Applicative<ForFx>, FxFunctor {
  override fun <A> just(a: A): Fx<A> =
    Fx.just(a)

  override fun unit(): Fx<Unit> = Fx.unit

  override fun <A, B> FxOf<A>.ap(ff: FxOf<(A) -> B>): Fx<B> =
    fix().ap(ff)

  override fun <A, B> FxOf<A>.map(f: (A) -> B): Fx<B> =
    fix().map(f)
}

@extension
interface FxApplicativeError : ApplicativeError<ForFx, Throwable>, FxApplicative {
  override fun <A> raiseError(e: Throwable): Fx<A> =
    Fx.raiseError(e)

  override fun <A> FxOf<A>.handleErrorWith(f: (Throwable) -> FxOf<A>): Fx<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface FxMonad : Monad<ForFx>, FxApplicative {

  override fun <A, B> FxOf<A>.flatMap(f: (A) -> FxOf<B>): Fx<B> =
    fix().flatMap { f(it) }

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForFx, Either<A, B>>): FxOf<B> =
    Fx.tailRecM(a, f)

  override fun <A, B> FxOf<A>.map(f: (A) -> B): Fx<B> =
    fix().map(f)

  override fun <A, B> FxOf<A>.ap(ff: FxOf<(A) -> B>): Fx<B> =
    fix().ap(ff)

  override fun <A, B> FxOf<A>.followedBy(fb: FxOf<B>): Fx<B> =
    fix().followedBy(fb)
}

@extension
interface FxMonadError : MonadError<ForFx, Throwable>, FxApplicativeError, FxMonad

@extension
interface FxMonadThrow : MonadThrow<ForFx>, FxMonadError

@extension
interface FxBracket : Bracket<ForFx, Throwable>, FxMonadThrow {
  override fun <A, B> FxOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> FxOf<Unit>, use: (A) -> FxOf<B>): Fx<B> =
    bracketC(release, use)

  override fun <A> FxOf<A>.guaranteeCase(finalizer: (ExitCase<Throwable>) -> FxOf<Unit>): Fx<A> =
    guaranteeC(finalizer)
}

@extension
interface FxMonadDefer : MonadDefer<ForFx>, FxBracket {
  override fun <A> defer(fa: () -> FxOf<A>): Fx<A> =
    Fx.unit.flatMap { fa() }
}

@extension
interface FxAsync : Async<ForFx>, FxMonadDefer {

  override fun <A> async(fa: Proc<A>): Fx<A> =
    Fx.async { _, cb -> fa(cb) }

  override fun <A> asyncF(k: ProcF<ForFx, A>): Fx<A> =
    Fx.asyncF { _, cb -> k(cb) }

  override fun <A> FxOf<A>.continueOn(ctx: CoroutineContext): Fx<A> =
    fix().continueOn(ctx)

}

@extension
interface FxConcurrent : Concurrent<ForFx>, FxAsync {

  override fun dispatchers(): Dispatchers<ForFx> =
    Fx.dispatchers()

  override fun <A> async(fa: FxProc<A>): Fx<A> =
    Fx.async(fa)

  override fun <A> asyncF(fa: FxProcF<A>): Fx<A> =
    Fx.asyncF(fa = fa)

  override fun <A> CoroutineContext.fork(fa: FxOf<A>): Fx<Fiber<ForFx, A>> =
    fa.fix().fork(this)

  override fun <A, B> CoroutineContext.racePair(fa: FxOf<A>, fb: FxOf<B>): Fx<RacePair<ForFx, A, B>> =
    Fx.racePair(this@racePair, fa, fb)

  override fun <A, B, C> CoroutineContext.raceTriple(fa: FxOf<A>, fb: FxOf<B>, fc: FxOf<C>): Fx<RaceTriple<ForFx, A, B, C>> =
    Fx.raceTriple(this@raceTriple, fa, fb, fc)

  override fun <A> asyncF(k: ProcF<ForFx, A>): Fx<A> =
    Fx.asyncF { _, cb -> k(cb) }

  override fun <A> async(fa: Proc<A>): Fx<A> =
    Fx.async { _, cb -> fa(cb) }
}

@extension
interface FxFx : arrow.effects.typeclasses.suspended.concurrent.Fx<ForFx> {
  override fun concurrent(): Concurrent<ForFx> = object : FxConcurrent {}
}
