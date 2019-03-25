package arrow.effects.extensions.fx2

import arrow.Kind
import arrow.core.*
import arrow.effects.IODispatchers
import arrow.effects.KindConnection
import arrow.effects.extensions.*
import arrow.effects.internal.Platform
import arrow.effects.internal.UnsafePromise
import arrow.effects.internal.asyncContinuation
import arrow.effects.suspended.fx.*
import arrow.effects.typeclasses.*
import arrow.effects.suspended.fx2.fix
import arrow.effects.suspended.fx2.foldContinuation
import arrow.extension
import arrow.typeclasses.*
import arrow.typeclasses.Continuation
import arrow.unsafe
import java.util.concurrent.CancellationException
import java.util.concurrent.Executor
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.*
import arrow.effects.extensions.fx2.fx.dispatchers.dispatchers
import arrow.effects.extensions.fx2.fx.monadDefer.monadDefer
import arrow.effects.suspended.fx2.ForFx


@extension
interface Fx2Dispatchers : Dispatchers<arrow.effects.suspended.fx2.ForFx> {
  override fun default(): CoroutineContext =
    IODispatchers.CommonPool

  override fun trampoline(): CoroutineContext =
    TrampolinePool(Executor { command -> command?.run() })
}

val NonBlocking2: CoroutineContext = arrow.effects.suspended.fx2.Fx.dispatchers().default()

@extension
interface Fx2UnsafeRun : UnsafeRun<arrow.effects.suspended.fx2.ForFx> {

  override suspend fun <A> unsafe.runBlocking(fa: () -> arrow.effects.suspended.fx2.FxOf<A>): A =
    fa().fix().fa.foldContinuation(arrow.effects.suspended.fx.Trampoline) { throw it }

  override suspend fun <A> unsafe.runNonBlocking(fa: () -> arrow.effects.suspended.fx2.FxOf<A>, cb: (Either<Throwable, A>) -> Unit) =
    fa().fix().fa.startCoroutine(asyncContinuation(NonBlocking, cb))
}

@extension
interface Fx2Environment : Environment<arrow.effects.suspended.fx2.ForFx> {
  override fun dispatchers(): Dispatchers<arrow.effects.suspended.fx2.ForFx> =
    arrow.effects.suspended.fx2.Fx.dispatchers()

  override fun handleAsyncError(e: Throwable): arrow.effects.suspended.fx2.Fx<Unit> =
    arrow.effects.suspended.fx2.Fx { println("Found uncaught async exception!"); e.printStackTrace() }
}


@extension
interface Fx2Functor : Functor<arrow.effects.suspended.fx2.ForFx> {
  override fun <A, B> arrow.effects.suspended.fx2.FxOf<A>.map(f: (A) -> B): arrow.effects.suspended.fx2.Fx<B> =
    arrow.effects.suspended.fx2.Fx { fix().fa.map(f)() }
}

@extension
interface Fx2Applicative : Applicative<arrow.effects.suspended.fx2.ForFx>, Fx2Functor {
  override fun <A> just(a: A): arrow.effects.suspended.fx2.Fx<A> =
    arrow.effects.suspended.fx2.Fx { a }

  override fun <A, B> arrow.effects.suspended.fx2.FxOf<A>.ap(ff: arrow.effects.suspended.fx2.FxOf<(A) -> B>): arrow.effects.suspended.fx2.Fx<B> =
    arrow.effects.suspended.fx2.Fx { fix().fa.ap(ff.fix().fa)() }

  override fun <A, B> arrow.effects.suspended.fx2.FxOf<A>.map(f: (A) -> B): arrow.effects.suspended.fx2.Fx<B> =
    arrow.effects.suspended.fx2.Fx { fix().fa.map(f)() }
}

@extension
interface Fx2ApplicativeError : ApplicativeError<arrow.effects.suspended.fx2.ForFx, Throwable>, Fx2Applicative {
  override fun <A> raiseError(e: Throwable): arrow.effects.suspended.fx2.Fx<A> =
    arrow.effects.suspended.fx2.Fx { throw e }

  override fun <A> arrow.effects.suspended.fx2.FxOf<A>.handleErrorWith(f: (Throwable) -> arrow.effects.suspended.fx2.FxOf<A>): arrow.effects.suspended.fx2.Fx<A> =
    arrow.effects.suspended.fx2.Fx { fix().fa.handleErrorWith { f(it).fix().fa }() }
}

@extension
interface Fx2Monad : Monad<arrow.effects.suspended.fx2.ForFx>, Fx2Applicative {

  override fun <A, B> arrow.effects.suspended.fx2.FxOf<A>.flatMap(f: (A) -> arrow.effects.suspended.fx2.FxOf<B>): arrow.effects.suspended.fx2.Fx<B> =
    arrow.effects.suspended.fx2.Fx { fix().fa.flatMap { f(it).fix().fa }() }

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<arrow.effects.suspended.fx2.ForFx, Either<A, B>>): arrow.effects.suspended.fx2.FxOf<B> =
    arrow.effects.suspended.fx2.Fx { tailRecLoop(a, f.andThen { it.fix().fa })() }

  override fun <A, B> arrow.effects.suspended.fx2.FxOf<A>.map(f: (A) -> B): arrow.effects.suspended.fx2.Fx<B> =
    arrow.effects.suspended.fx2.Fx { fix().fa.map(f)() }

  override fun <A, B> arrow.effects.suspended.fx2.FxOf<A>.ap(ff: arrow.effects.suspended.fx2.FxOf<(A) -> B>): arrow.effects.suspended.fx2.Fx<B> =
    arrow.effects.suspended.fx2.Fx { fix().fa.ap(ff.fix().fa)() }

}

@extension
interface Fx2MonadError : MonadError<arrow.effects.suspended.fx2.ForFx, Throwable>, Fx2ApplicativeError, Fx2Monad

@extension
interface Fx2MonadThrow : MonadThrow<arrow.effects.suspended.fx2.ForFx>, Fx2MonadError

@extension
interface Fx2Bracket : Bracket<arrow.effects.suspended.fx2.ForFx, Throwable>, Fx2MonadThrow {
  override fun <A, B> arrow.effects.suspended.fx2.FxOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> arrow.effects.suspended.fx2.FxOf<Unit>, use: (A) -> arrow.effects.suspended.fx2.FxOf<B>): arrow.effects.suspended.fx2.Fx<B> =
    arrow.effects.suspended.fx2.Fx {
      fix().fa.bracketCase(
        release.curry().andThen { a -> a.andThen { b -> b.fix().fa } }.uncurried(),
        use.andThen { a -> a.fix().fa }
      )()
    }
}

@extension
interface Fx2MonadDefer : MonadDefer<arrow.effects.suspended.fx2.ForFx>, Fx2Bracket {
  override fun <A> defer(fa: () -> arrow.effects.suspended.fx2.FxOf<A>): arrow.effects.suspended.fx2.Fx<A> =
    unit().flatMap { fa() }
}

@extension
interface Fx2Async : Async<arrow.effects.suspended.fx2.ForFx>, Fx2MonadDefer {

  override fun <A> async(fa: Proc<A>): arrow.effects.suspended.fx2.Fx<A> =
    arrow.effects.suspended.fx2.Fx { fromAsync(fa)() }

  override fun <A> asyncF(k: ProcF<arrow.effects.suspended.fx2.ForFx, A>): arrow.effects.suspended.fx2.Fx<A> =
    arrow.effects.suspended.fx2.Fx { arrow.effects.suspended.fx2.Fx.asyncF(k)() }

  override fun <A> arrow.effects.suspended.fx2.FxOf<A>.continueOn(ctx: CoroutineContext): arrow.effects.suspended.fx2.Fx<A> =
    arrow.effects.suspended.fx2.Fx { ctx.continueOn(fix().fa)() }
}

private class Pool2(val pool: ForkJoinPool) : AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {
  override fun <T> interceptContinuation(continuation: kotlin.coroutines.Continuation<T>): kotlin.coroutines.Continuation<T> =
    Pool2Continuation(pool, continuation.context.fold(continuation) { cont, element ->
      if (element != this@Pool2 && element is ContinuationInterceptor)
        element.interceptContinuation(cont) else cont
    })
}

private class Pool2Continuation<T>(
  val pool: ForkJoinPool,
  val cont: kotlin.coroutines.Continuation<T>
) : kotlin.coroutines.Continuation<T> {
  override val context: CoroutineContext = cont.context

  override fun resumeWith(result: kotlin.Result<T>) {
    pool.execute { cont.resumeWith(result) }
  }
}

@extension
interface Fx2Concurrent : Concurrent<arrow.effects.suspended.fx2.ForFx>, Fx2Async {

  override fun dispatchers(): Dispatchers<arrow.effects.suspended.fx2.ForFx> =
    arrow.effects.suspended.fx2.Fx.dispatchers()

  override fun <A> async(fa: arrow.effects.suspended.fx2.FxProc<A>): arrow.effects.suspended.fx2.Fx<A> =
    arrow.effects.suspended.fx2.Fx { arrow.effects.suspended.fx2.Fx.async(fa)() }

  override fun <A> asyncF(fa: arrow.effects.suspended.fx2.FxProcF<A>): arrow.effects.suspended.fx2.Fx<A> =
    arrow.effects.suspended.fx2.Fx { arrow.effects.suspended.fx2.Fx.asyncF(fa)() }

  override fun <A> CoroutineContext.startFiber(fa: arrow.effects.suspended.fx2.FxOf<A>): arrow.effects.suspended.fx2.Fx<Fiber<arrow.effects.suspended.fx2.ForFx, A>> {
    val promise = UnsafePromise<A>()
    val conn = Fx2Connection()
    fa.fix().fa.startCoroutine(asyncContinuation(this) { either ->
      either.fold(
        { promise.complete(it.left()) },
        { promise.complete(it.right()) }
      )
    })
    return arrow.effects.suspended.fx2.Fx {
      Fx2Fiber(promise, conn)
    }
  }

  override fun <A, B> CoroutineContext.racePair(fa: arrow.effects.suspended.fx2.FxOf<A>, fb: arrow.effects.suspended.fx2.FxOf<B>): arrow.effects.suspended.fx2.Fx<RacePair<arrow.effects.suspended.fx2.ForFx, A, B>> =
    arrow.effects.suspended.fx2.Fx { arrow.effects.suspended.fx2.Fx.racePair(this@racePair, fa, fb)() }

  override fun <A, B, C> CoroutineContext.raceTriple(fa: arrow.effects.suspended.fx2.FxOf<A>, fb: arrow.effects.suspended.fx2.FxOf<B>, fc: arrow.effects.suspended.fx2.FxOf<C>): arrow.effects.suspended.fx2.Fx<RaceTriple<arrow.effects.suspended.fx2.ForFx, A, B, C>> =
    arrow.effects.suspended.fx2.Fx { arrow.effects.suspended.fx2.Fx.raceTriple(this@raceTriple, fa, fb, fc)() }

  override fun <A> asyncF(k: ProcF<arrow.effects.suspended.fx2.ForFx, A>): arrow.effects.suspended.fx2.Fx<A> =
    arrow.effects.suspended.fx2.Fx { arrow.effects.suspended.fx2.Fx.asyncF(k)() }

  override fun <A> async(fa: Proc<A>): arrow.effects.suspended.fx2.Fx<A> =
    arrow.effects.suspended.fx2.Fx { fromAsync(fa)() }
}

class BlockingCoroutine2<T>(override val context: CoroutineContext) : kotlin.coroutines.Continuation<T> {

  private val retVal: AtomicReference<T> = AtomicReference()

  override fun resumeWith(result: Result<T>) {
    retVal.set(result.getOrThrow())
  }

  val value: T
    get() = retVal.get()

}

fun <A> arrow.effects.suspended.fx2.Fx.Companion.async(fa: arrow.effects.suspended.fx2.FxProc<A>): arrow.effects.suspended.fx2.Fx<A> = arrow.effects.suspended.fx2.Fx<A> {
  suspendCoroutine { continuation ->
    val conn = Fx2Connection()
    //Is CancellationException from kotlin in kotlinx package???
    conn.push(arrow.effects.suspended.fx2.Fx { continuation.resumeWith(Result.failure(CancellationException())) })
    fa(conn) { either ->
      continuation.resumeWith(either.fold(Result.Companion::failure, Result.Companion::success))
    }
  }
}

/** Hide member because it's discouraged to use uncancelable builder for cancelable concrete type **/
internal fun <A> arrow.effects.suspended.fx2.Fx.Companion.asyncF(fa: ProcF<arrow.effects.suspended.fx2.ForFx, A>): arrow.effects.suspended.fx2.Fx<A> = arrow.effects.suspended.fx2.Fx<A> {
  suspendCoroutine { continuation ->
    fa { either ->
      continuation.resumeWith(either.fold(Result.Companion::failure, Result.Companion::success))
    }.fix().foldContinuation(EmptyCoroutineContext, mapUnit)
  }
}

fun <A> arrow.effects.suspended.fx2.Fx.Companion.asyncF(fa: arrow.effects.suspended.fx2.FxProcF<A>): arrow.effects.suspended.fx2.Fx<A> = arrow.effects.suspended.fx2.Fx<A> {
  suspendCoroutine { continuation ->
    val conn = Fx2Connection()
    //Is CancellationException from kotlin in kotlinx package???
    conn.push(arrow.effects.suspended.fx2.Fx { continuation.resumeWith(Result.failure(CancellationException())) })
    fa(conn) { either ->
      continuation.resumeWith(either.fold(Result.Companion::failure, Result.Companion::success))
    }.fix().foldContinuation(EmptyCoroutineContext, mapUnit)
  }
}


@extension
interface Fx2Fx : arrow.effects.typeclasses.suspended.concurrent.Fx<arrow.effects.suspended.fx2.ForFx> {
  override fun concurrent(): Concurrent<arrow.effects.suspended.fx2.ForFx> =
    object : Fx2Concurrent {}
}

fun <A> arrow.effects.suspended.fx2.FxOf<A>.startOn(ctx: CoroutineContext): arrow.effects.suspended.fx2.Fx<Fiber<arrow.effects.suspended.fx2.ForFx, A>> =
  arrow.effects.suspended.fx2.Fx {
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

fun <A> arrow.effects.suspended.fx2.Fx<A>.foldContinuation(
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

fun Fx2Connection(): KindConnection<arrow.effects.suspended.fx2.ForFx> =
  KindConnection(arrow.effects.suspended.fx2.Fx.monadDefer()) { it.fix().foldContinuation { e -> throw e } }

internal fun <A> Fx2Fiber(promise: UnsafePromise<A>, conn: KindConnection<arrow.effects.suspended.fx2.ForFx>): Fiber<arrow.effects.suspended.fx2.ForFx, A> {
  val join: arrow.effects.suspended.fx2.Fx<A> = arrow.effects.suspended.fx2.Fx.async { conn2, cb ->
    conn2.push(arrow.effects.suspended.fx2.Fx { promise.remove(cb) })
    conn.push(conn2.cancel())
    promise.get { a ->
      cb(a)
      conn2.pop()
      conn.pop()
    }
  }
  return Fiber(join, conn.cancel())
}

fun <A, B> arrow.effects.suspended.fx2.Fx.Companion.racePair(ctx: CoroutineContext, fa: arrow.effects.suspended.fx2.FxOf<A>, fb: arrow.effects.suspended.fx2.FxOf<B>): arrow.effects.suspended.fx2.Fx<Either<Tuple2<A, Fiber<arrow.effects.suspended.fx2.ForFx, B>>, Tuple2<Fiber<arrow.effects.suspended.fx2.ForFx, A>, B>>> =
  arrow.effects.suspended.fx2.Fx.async { conn, cb ->
    val active = AtomicBoolean(true)
    val upstreamCancelToken = arrow.effects.suspended.fx2.Fx.defer { if (conn.isCanceled()) arrow.effects.suspended.fx2.Fx { Unit } else conn.cancel() }

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

fun <A, B, C> arrow.effects.suspended.fx2.Fx.Companion.raceTriple(ctx: CoroutineContext, fa: arrow.effects.suspended.fx2.FxOf<A>, fb: arrow.effects.suspended.fx2.FxOf<B>, fc: arrow.effects.suspended.fx2.FxOf<C>): arrow.effects.suspended.fx2.Fx<RaceTriple<arrow.effects.suspended.fx2.ForFx, A, B, C>> =
  arrow.effects.suspended.fx2.Fx.async { conn, cb ->
    val active = AtomicBoolean(true)

    val upstreamCancelToken = arrow.effects.suspended.fx2.Fx.defer { if (conn.isCanceled()) arrow.effects.suspended.fx2.Fx { Unit } else conn.cancel() }

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
