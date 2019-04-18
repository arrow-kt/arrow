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
import arrow.extension
import arrow.typeclasses.*
import arrow.unsafe
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.*
import  arrow.effects.suspended.fx.guaranteeCase as guaranteeC

@extension
interface Fx2Dispatchers : Dispatchers<ForFx> {
  override fun default(): CoroutineContext =
    IODispatchers.CommonPool
}

val NonBlocking: CoroutineContext = Fx.dispatchers().default()

@extension
interface Fx2UnsafeRun : UnsafeRun<ForFx> {

  override suspend fun <A> unsafe.runBlocking(fa: () -> FxOf<A>): A =
    Fx.unsafeRunBlocking(fa())

  override suspend fun <A> unsafe.runNonBlocking(fa: () -> FxOf<A>, cb: (Either<Throwable, A>) -> Unit) =
    Fx.unsafeRunNonBlocking(fa(), cb)
}

@extension
interface Fx2Environment : Environment<ForFx> {
  override fun dispatchers(): Dispatchers<ForFx> =
    Fx.dispatchers()

  override fun handleAsyncError(e: Throwable): Fx<Unit> =
    Fx { println("Found uncaught async exception!"); e.printStackTrace() }
}


@extension
interface Fx2Functor : Functor<ForFx> {
  override fun <A, B> FxOf<A>.map(f: (A) -> B): Fx<B> =
    fix().map(f)
}

@extension
interface Fx2Applicative : Applicative<ForFx>, Fx2Functor {
  override fun <A> just(a: A): Fx<A> =
    Fx.just(a)

  override fun unit(): Fx<Unit> = Fx.unit

  override fun <A, B> FxOf<A>.ap(ff: FxOf<(A) -> B>): Fx<B> =
    fix().ap(ff)

  override fun <A, B> FxOf<A>.map(f: (A) -> B): Fx<B> =
    fix().map(f)
}

@extension
interface Fx2ApplicativeError : ApplicativeError<ForFx, Throwable>, Fx2Applicative {
  override fun <A> raiseError(e: Throwable): Fx<A> =
    Fx.raiseError(e)

  override fun <A> FxOf<A>.handleErrorWith(f: (Throwable) -> FxOf<A>): Fx<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface Fx2Monad : Monad<ForFx>, Fx2Applicative {

  override fun <A, B> FxOf<A>.flatMap(f: (A) -> FxOf<B>): Fx<B> =
    fix().flatMap { f(it) }

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForFx, Either<A, B>>): FxOf<B> =
    Fx.tailRecM(a, f)

  override fun <A, B> FxOf<A>.map(f: (A) -> B): Fx<B> =
    fix().map(f)

  override fun <A, B> FxOf<A>.ap(ff: FxOf<(A) -> B>): Fx<B> =
    fix().ap(ff)

}

@extension
interface Fx2MonadError : MonadError<ForFx, Throwable>, Fx2ApplicativeError, Fx2Monad

@extension
interface Fx2MonadThrow : MonadThrow<ForFx>, Fx2MonadError

@extension
interface Fx2Bracket : Bracket<ForFx, Throwable>, Fx2MonadThrow {
  override fun <A, B> FxOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> FxOf<Unit>, use: (A) -> FxOf<B>): Fx<B> =
    fix().bracketCase(release, use)

  override fun <A> FxOf<A>.guaranteeCase(finalizer: (ExitCase<Throwable>) -> FxOf<Unit>): Fx<A> =
    guaranteeC(finalizer)
}

@extension
interface Fx2MonadDefer : MonadDefer<ForFx>, Fx2Bracket {
  override fun <A> defer(fa: () -> FxOf<A>): Fx<A> =
    Fx.unit.flatMap { fa() }
}

@extension
interface Fx2Async : Async<ForFx>, Fx2MonadDefer {

  override fun <A> async(fa: Proc<A>): Fx<A> =
    Fx.async { _, cb -> fa(cb) }

  override fun <A> asyncF(k: ProcF<ForFx, A>): Fx<A> =
    Fx.asyncF { _, cb -> k(cb) }

  override fun <A> FxOf<A>.continueOn(ctx: CoroutineContext): Fx<A> =
    fix().continueOn(ctx)

}

@extension
interface Fx2Concurrent : Concurrent<ForFx>, Fx2Async {

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
interface Fx2Fx : arrow.effects.typeclasses.suspended.concurrent.Fx<ForFx> {
  override fun concurrent(): Concurrent<ForFx> =
    object : Fx2Concurrent {}
}

@Suppress("FunctionName")
internal fun <A> FxFiber(promise: UnsafePromise<A>, conn: FxConnection): Fiber<ForFx, A> {
  val join: Fx<A> = Fx.async { conn2, cb ->
    val cb2: (Either<Throwable, A>) -> Unit = {
      cb(it)
      conn2.pop()
      conn.pop()
    }

    conn2.push(Fx { promise.remove(cb2) })
    conn.push(conn2.cancel())
    promise.get(cb2)
  }
  return Fiber(join, conn.cancel())
}

fun <A, B> Fx.Companion.racePair(ctx: CoroutineContext, fa: FxOf<A>, fb: FxOf<B>): Fx<Either<Tuple2<A, Fiber<ForFx, B>>, Tuple2<Fiber<ForFx, A>, B>>> =
  Fx.async { conn, cb ->
    val active = AtomicBoolean(true)
    val upstreamCancelToken = Fx.defer { if (conn.isCanceled()) Fx(suspendMapUnit) else conn.cancel() }

    val connA = FxConnection()
    connA.push(upstreamCancelToken)
    val promiseA = UnsafePromise<A>()

    val connB = FxConnection()
    connB.push(upstreamCancelToken)
    val promiseB = UnsafePromise<B>()

    conn.pushPair(connA, connB)

    fa.fix().fa.startCoroutine(asyncContinuation(ctx) { either ->
      either.fold({ error ->
        if (active.getAndSet(false)) { //if an error finishes first, stop the race.
          connB.cancel().fix().fa.startCoroutine(Continuation(EmptyCoroutineContext) { result ->
            conn.pop()
            result.fold(
              onSuccess = { cb(Left(error)) },
              onFailure = { cb(Left(Platform.composeErrors(error, it))) }
            )
          })
        } else {
          promiseA.complete(Left(error))
        }
      }, { a ->
        if (active.getAndSet(false)) {
          conn.pop()
          cb(Right(Left(Tuple2(a, FxFiber(promiseB, connB)))))
        } else {
          promiseA.complete(Right(a))
        }
      })
    })

    fb.fix().fa.startCoroutine(asyncContinuation(ctx) { either ->
      either.fold({ error ->
        if (active.getAndSet(false)) { //if an error finishes first, stop the race.
          connA.cancel().fix().fa.startCoroutine(Continuation(EmptyCoroutineContext) { result ->
            conn.pop()
            result.fold(
              onSuccess = { cb(Left(error)) },
              onFailure = { cb(Left(Platform.composeErrors(error, it))) }
            )
          })
        } else {
          promiseB.complete(Left(error))
        }
      }, { b ->
        if (active.getAndSet(false)) {
          conn.pop()
          cb(Right(Right(Tuple2(FxFiber(promiseA, connA), b))))
        } else {
          promiseB.complete(Right(b))
        }
      })
    })
  }

fun <A, B, C> Fx.Companion.raceTriple(ctx: CoroutineContext, fa: FxOf<A>, fb: FxOf<B>, fc: FxOf<C>): Fx<RaceTriple<ForFx, A, B, C>> =
  Fx.async { conn, cb ->
    val active = AtomicBoolean(true)

    val upstreamCancelToken = Fx.defer { if (conn.isCanceled()) Fx(suspendMapUnit) else conn.cancel() }

    val connA = FxConnection()
    connA.push(upstreamCancelToken)
    val promiseA = UnsafePromise<A>()

    val connB = FxConnection()
    connB.push(upstreamCancelToken)
    val promiseB = UnsafePromise<B>()

    val connC = FxConnection()
    connC.push(upstreamCancelToken)
    val promiseC = UnsafePromise<C>()

    conn.push(connA.cancel(), connB.cancel(), connC.cancel())

    fa.fix().fa.startCoroutine(asyncContinuation(ctx) { either ->
      either.fold({ error ->
        if (active.getAndSet(false)) { //if an error finishes first, stop the race.
          connB.cancel().fix().fa.startCoroutine(Continuation(EmptyCoroutineContext) { r2 ->
            connC.cancel().fix().fa.startCoroutine(Continuation(EmptyCoroutineContext) { r3 ->
              conn.pop()
              val errorResult = r2.fold(onFailure = { e2 ->
                r3.fold(onFailure = { e3 -> Platform.composeErrors(error, e2, e3) }, onSuccess = { Platform.composeErrors(error, e2) })
              }, onSuccess = {
                r3.fold(onFailure = { e3 -> Platform.composeErrors(error, e3) }, onSuccess = { error })
              })
              cb(Left(errorResult))
            })
          })
        } else {
          promiseA.complete(Left(error))
        }
      }, { a ->
        if (active.getAndSet(false)) {
          conn.pop()
          cb(Right(Left(Tuple3(a, FxFiber(promiseB, connB), FxFiber(promiseC, connC)))))
        } else {
          promiseA.complete(Right(a))
        }
      })
    })

    fb.fix().fa.startCoroutine(asyncContinuation(ctx) { either ->
      either.fold({ error ->
        if (active.getAndSet(false)) { //if an error finishes first, stop the race.
          connA.cancel().fix().fa.startCoroutine(Continuation(EmptyCoroutineContext) { r2 ->
            connC.cancel().fix().fa.startCoroutine(Continuation(EmptyCoroutineContext) { r3 ->
              conn.pop()
              val errorResult = r2.fold(onFailure = { e2 ->
                r3.fold(onFailure = { e3 -> Platform.composeErrors(error, e2, e3) }, onSuccess = { Platform.composeErrors(error, e2) })
              }, onSuccess = {
                r3.fold(onFailure = { e3 -> Platform.composeErrors(error, e3) }, onSuccess = { error })
              })
              cb(Left(errorResult))
            })
          })
        } else {
          promiseB.complete(Left(error))
        }
      }, { b ->
        if (active.getAndSet(false)) {
          conn.pop()
          cb(Right(Right(Left(Tuple3(FxFiber(promiseA, connA), b, FxFiber(promiseC, connC))))))
        } else {
          promiseB.complete(Right(b))
        }
      })
    })

    fc.fix().fa.startCoroutine(asyncContinuation(ctx) { either ->
      either.fold({ error ->
        if (active.getAndSet(false)) { //if an error finishes first, stop the race.
          connA.cancel().fix().fa.startCoroutine(Continuation(EmptyCoroutineContext) { r2 ->
            connB.cancel().fix().fa.startCoroutine(Continuation(EmptyCoroutineContext) { r3 ->
              conn.pop()
              val errorResult = r2.fold(onFailure = { e2 ->
                r3.fold(onFailure = { e3 -> Platform.composeErrors(error, e2, e3) }, onSuccess = { Platform.composeErrors(error, e2) })
              }, onSuccess = {
                r3.fold(onFailure = { e3 -> Platform.composeErrors(error, e3) }, onSuccess = { error })
              })
              cb(Left(errorResult))
            })
          })
        } else {
          promiseC.complete(Left(error))
        }
      }, { c ->
        if (active.getAndSet(false)) {
          conn.pop()
          cb(Right(Right(Right(Tuple3(FxFiber(promiseA, connA), FxFiber(promiseB, connB), c)))))
        } else {
          promiseC.complete(Right(c))
        }
      })
    })

  }
