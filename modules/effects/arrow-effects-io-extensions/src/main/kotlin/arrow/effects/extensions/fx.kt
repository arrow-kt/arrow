package arrow.effects.extensions

import arrow.Kind
import arrow.core.*
import arrow.effects.IODispatchers
import arrow.effects.KindConnection
import arrow.effects.extensions.fx.dispatchers.dispatchers
import arrow.effects.extensions.fx.monadDefer.monadDefer
import arrow.effects.internal.Platform
import arrow.effects.internal.UnsafePromise
import arrow.effects.internal.asyncContinuation
import arrow.effects.suspended.fx.*
import arrow.effects.typeclasses.*
import arrow.extension
import arrow.typeclasses.*
import arrow.typeclasses.Continuation
import arrow.unsafe
import java.lang.RuntimeException
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.*


@extension
interface Fx2Dispatchers : Dispatchers<ForFx> {
  override fun default(): CoroutineContext =
    IODispatchers.CommonPool
}

val NonBlocking: CoroutineContext = Fx.dispatchers().default()

fun <A> FxOf<A>.runNonBlockingCancellable(cb: (Either<Throwable, A>) -> Unit): Disposable {
  val token = CancelToken()
  FxRunLoop(fix()).startCoroutine(asyncContinuation(NonBlocking + token, cb))
  return { token.connection.cancel().fix().fa.startCoroutine(asyncContinuation(NonBlocking, mapUnit)) }
}

@extension
interface Fx2UnsafeRun : UnsafeRun<ForFx> {

  override suspend fun <A> unsafe.runBlocking(fa: () -> FxOf<A>): A =
    Fx.unsafeRunBlocking(fa().fix())

  override suspend fun <A> unsafe.runNonBlocking(fa: () -> FxOf<A>, cb: (Either<Throwable, A>) -> Unit) =
    FxRunLoop(fa().fix()).startCoroutine(asyncContinuation(NonBlocking, cb))
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

  override fun <A, B> FxOf<A>.ap(ff: FxOf<(A) -> B>): Fx<B> =
    fix().ap(ff.fix())

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
    fix().flatMap { f(it).fix() }

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForFx, Either<A, B>>): FxOf<B> =
    Fx.tailRecM(a, f)

  override fun <A, B> FxOf<A>.map(f: (A) -> B): Fx<B> =
    fix().map(f)

  override fun <A, B> FxOf<A>.ap(ff: FxOf<(A) -> B>): Fx<B> =
    fix().ap(ff.fix())

}

@extension
interface Fx2MonadError : MonadError<ForFx, Throwable>, Fx2ApplicativeError, Fx2Monad

@extension
interface Fx2MonadThrow : MonadThrow<ForFx>, Fx2MonadError

@extension
interface Fx2Bracket : Bracket<ForFx, Throwable>, Fx2MonadThrow {
  override fun <A, B> FxOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> FxOf<Unit>, use: (A) -> FxOf<B>): Fx<B> =
    fix().bracketCase(
      release.curry().andThen { a -> a.andThen { b -> b.fix() } }.uncurried(),
      use.andThen { a -> a.fix() }
    )
}

@extension
interface Fx2MonadDefer : MonadDefer<ForFx>, Fx2Bracket {
  override fun <A> defer(fa: () -> FxOf<A>): Fx<A> =
    unit().flatMap { fa() }
}

@extension
interface Fx2Async : Async<ForFx>, Fx2MonadDefer {

  override fun <A> async(fa: Proc<A>): Fx<A> =
    Fx.async(fa)

  override fun <A> asyncF(k: ProcF<ForFx, A>): Fx<A> =
    Fx.asyncF(k)

  override fun <A> FxOf<A>.continueOn(ctx: CoroutineContext): Fx<A> =
    ctx.shift().followedBy(fix()).fix()
}

@extension
interface Fx2Concurrent : Concurrent<ForFx>, Fx2Async {

  override fun dispatchers(): Dispatchers<ForFx> =
    Fx.dispatchers()

  override fun <A> async(fa: FxProc<A>): Fx<A> =
    Fx.async(fa)

  override fun <A> asyncF(fa: FxProcF<A>): Fx<A> =
    Fx.asyncF(fa)

  override fun <A> CoroutineContext.fork(fa: FxOf<A>): Fx<Fiber<ForFx, A>> {
    val promise = UnsafePromise<A>()
    val conn = Fx2Connection()
    fa.fix().fa.startCoroutine(asyncContinuation(this) { either ->
      either.fold(
        { promise.complete(it.left()) },
        { promise.complete(it.right()) }
      )
    })
    return Fx {
      Fx2Fiber(promise, conn)
    }
  }

  override fun <A, B> CoroutineContext.racePair(fa: FxOf<A>, fb: FxOf<B>): Fx<RacePair<ForFx, A, B>> =
    Fx.racePair(this@racePair, fa, fb)

  override fun <A, B, C> CoroutineContext.raceTriple(fa: FxOf<A>, fb: FxOf<B>, fc: FxOf<C>): Fx<RaceTriple<ForFx, A, B, C>> =
    Fx.raceTriple(this@raceTriple, fa, fb, fc)

  override fun <A> asyncF(k: ProcF<ForFx, A>): Fx<A> =
    Fx.asyncF(k)

  override fun <A> async(fa: Proc<A>): Fx<A> =
    Fx.async(fa)
}

@extension
interface Fx2Fx : arrow.effects.typeclasses.suspended.concurrent.Fx<ForFx> {
  override fun concurrent(): Concurrent<ForFx> =
    object : Fx2Concurrent {}
}

fun <A> FxOf<A>.startOn(ctx: CoroutineContext): Fx<Fiber<ForFx, A>> =
  Fx {
    val promise = UnsafePromise<A>()
    val conn = Fx2Connection()
    fix().fa.startCoroutine(asyncContinuation(ctx) { either ->
      either.fold(
        { promise.complete(it.left()) },
        { promise.complete(it.right()) }
      )
    })

    Fx2Fiber(promise, conn)
  }

fun <A> Fx<A>.foldContinuation(
  context: CoroutineContext = EmptyCoroutineContext,
  onError: (Throwable) -> A
): A {
  val result: AtomicReference<A> = AtomicReference()
  fa.startCoroutine(object : Continuation<A> {
    override fun resume(value: A) {
      result.set(value)
    }

    override fun resumeWithException(exception: Throwable) {
      result.set(onError(exception))
    }

    override val context: CoroutineContext
      get() = context
  })
  return result.get()
}

fun Fx2Connection(): KindConnection<ForFx> =
  KindConnection(Fx.monadDefer()) { it.fix().foldContinuation { e -> throw e } }

internal fun <A> Fx2Fiber(promise: UnsafePromise<A>, conn: KindConnection<ForFx>): Fiber<ForFx, A> {
  val join: Fx<A> = Fx.async { conn2, cb ->
    conn2.push(Fx { promise.remove(cb) })
    conn.push(conn2.cancel())
    promise.get { a ->
      cb(a)
      conn2.pop()
      conn.pop()
    }
  }
  return Fiber(join, conn.cancel())
}

fun <A, B> Fx.Companion.racePair(ctx: CoroutineContext, fa: FxOf<A>, fb: FxOf<B>): Fx<Either<Tuple2<A, Fiber<ForFx, B>>, Tuple2<Fiber<ForFx, A>, B>>> =
  Fx.async { conn, cb ->
    val active = AtomicBoolean(true)
    val upstreamCancelToken = Fx.defer { if (conn.isCanceled()) Fx { Unit } else conn.cancel() }

    val connA = Fx2Connection()
    connA.push(upstreamCancelToken)
    val promiseA = UnsafePromise<A>()

    val connB = Fx2Connection()
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
          cb(Right(Left(Tuple2(a, Fx2Fiber(promiseB, connB)))))
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
          cb(Right(Right(Tuple2(Fx2Fiber(promiseA, connA), b))))
        } else {
          promiseB.complete(Right(b))
        }
      })
    })
  }

fun <A, B, C> Fx.Companion.raceTriple(ctx: CoroutineContext, fa: FxOf<A>, fb: FxOf<B>, fc: FxOf<C>): Fx<RaceTriple<ForFx, A, B, C>> =
  Fx.async { conn, cb ->
    val active = AtomicBoolean(true)

    val upstreamCancelToken = Fx.defer { if (conn.isCanceled()) Fx { Unit } else conn.cancel() }

    val connA = Fx2Connection()
    connA.push(upstreamCancelToken)
    val promiseA = UnsafePromise<A>()

    val connB = Fx2Connection()
    connB.push(upstreamCancelToken)
    val promiseB = UnsafePromise<B>()

    val connC = Fx2Connection()
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
          cb(Right(Left(Tuple3(a, Fx2Fiber(promiseB, connB), Fx2Fiber(promiseC, connC)))))
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
          cb(Right(Right(Left(Tuple3(Fx2Fiber(promiseA, connA), b, Fx2Fiber(promiseC, connC))))))
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
          cb(Right(Right(Right(Tuple3(Fx2Fiber(promiseA, connA), Fx2Fiber(promiseB, connB), c)))))
        } else {
          promiseC.complete(Right(c))
        }
      })
    })

  }



private fun <P1, P2, R> ((P1) -> (P2) -> R).uncurried(): (P1, P2) -> R = { p1: P1, p2: P2 -> this(p1)(p2) }
