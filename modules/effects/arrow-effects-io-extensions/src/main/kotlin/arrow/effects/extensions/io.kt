package arrow.effects.extensions

import arrow.core.Either
import arrow.effects.*
import arrow.effects.typeclasses.*
import arrow.extension
import kotlin.coroutines.CoroutineContext
import arrow.effects.IODispatchers as IOD
import arrow.effects.ap as ioAp
import arrow.effects.handleErrorWith as ioHandleErrorWith
import arrow.effects.startF as ioStart

@extension
interface IODispatchers : Dispatchers<ForIO> {
  override fun default(): CoroutineContext =
    IOD.CommonPool
}

@extension
interface IOEnvironment : Environment<ForIO> {
  override fun dispatchers(): Dispatchers<ForIO> =
    IO.dispatchers()

  override fun handleAsyncError(e: Throwable): IO<Unit> =
    IO { println(e.message) }
}

@extension
interface IOConcurrent : Concurrent<ForIO>, IOAsync {

  override fun environment(): Environment<ForIO> =
    IO.environment()

  override fun <A> IOOf<A>.startF(ctx: CoroutineContext): IO<Fiber<ForIO, A>> =
    ioStart(ctx)

  override fun <A> asyncF(fa: ConnectedProcF<ForIO, A>): IO<A> =
    IO.asyncF(fa)

  override fun <A> async(fa: ConnectedProc<ForIO, A>): IO<A> =
    IO.async(fa)

  override fun <A> asyncF(k: ProcF<ForIO, A>): IO<A> =
    IO.asyncF { _, cb -> k(cb) }

  override fun <A> async(fa: Proc<A>): IO<A> =
    IO.async { _, cb -> fa(cb) }

  override fun <A, B> racePair(ctx: CoroutineContext, fa: IOOf<A>, fb: IOOf<B>): IO<RacePair<ForIO, A, B>> =
    IO.racePair(ctx, fa, fb)

  override fun <A, B, C> raceTriple(ctx: CoroutineContext, fa: IOOf<A>, fb: IOOf<B>, fc: IOOf<C>): IO<RaceTriple<ForIO, A, B, C>> =
    IO.raceTriple(ctx, fa, fb, fc)

}

@extension
interface IOConcurrentEffect : ConcurrentEffect<ForIO>, IOEffect, IOConcurrent {

  override fun environment(): Environment<ForIO> =
    IO.environment()

  override fun <A> IOOf<A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> IOOf<Unit>): IO<Disposable> =
    fix().runAsyncCancellable(OnCancel.ThrowCancellationException, cb)
}
