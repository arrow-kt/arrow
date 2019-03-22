package arrow.effects.extensions

import arrow.Kind
import arrow.core.*
import arrow.effects.IODispatchers
import arrow.effects.KindConnection
import arrow.effects.extensions.fx.dispatchers.dispatchers
import arrow.effects.internal.Platform
import arrow.effects.internal.UnsafePromise
import arrow.effects.internal.asyncContinuation
import arrow.effects.suspended.fx.*
import arrow.effects.typeclasses.*
import arrow.extension
import arrow.typeclasses.*
import arrow.unsafe
import java.util.concurrent.CancellationException
import java.util.concurrent.Executor
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.*

@extension
interface FxDispatchers : Dispatchers<ForFx> {
  override fun default(): CoroutineContext =
    IODispatchers.CommonPool

  override fun trampoline(): CoroutineContext =
    TrampolinePool(Executor { command -> command?.run() })
}

val NonBlocking: CoroutineContext = Fx.dispatchers().default()

@extension
interface FxUnsafeRun : UnsafeRun<ForFx> {

  override suspend fun <A> unsafe.runBlocking(fa: () -> FxOf<A>): A =
    fa().fix().fa.foldContinuation(arrow.effects.suspended.fx.Trampoline) { throw it }

  override suspend fun <A> unsafe.runNonBlocking(fa: () -> FxOf<A>, cb: (Either<Throwable, A>) -> Unit) =
    fa().fix().fa.startCoroutine(asyncContinuation(NonBlocking, cb))
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
    Fx { fix().fa.map(f)() }
}

@extension
interface FxApplicative : Applicative<ForFx>, FxFunctor {
  override fun <A> just(a: A): Fx<A> =
    Fx { a }

  override fun <A, B> FxOf<A>.ap(ff: FxOf<(A) -> B>): Fx<B> =
    Fx { fix().fa.ap(ff.fix().fa)() }

  override fun <A, B> FxOf<A>.map(f: (A) -> B): Fx<B> =
    Fx { fix().fa.map(f)() }
}

@extension
interface FxApplicativeError : ApplicativeError<ForFx, Throwable>, FxApplicative {
  override fun <A> raiseError(e: Throwable): Fx<A> =
    Fx { throw e }

  override fun <A> FxOf<A>.handleErrorWith(f: (Throwable) -> FxOf<A>): Fx<A> =
    Fx { fix().fa.handleErrorWith { f(it).fix().fa }() }
}

@extension
interface FxMonad : Monad<ForFx>, FxApplicative {

  override fun <A, B> FxOf<A>.flatMap(f: (A) -> FxOf<B>): Fx<B> =
    Fx { fix().fa.flatMap { f(it).fix().fa }() }

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForFx, Either<A, B>>): FxOf<B> =
    Fx { tailRecLoop(a, f.andThen { it.fix().fa })() }

  override fun <A, B> FxOf<A>.map(f: (A) -> B): Fx<B> =
    Fx { fix().fa.map(f)() }

  override fun <A, B> FxOf<A>.ap(ff: FxOf<(A) -> B>): Fx<B> =
    Fx { fix().fa.ap(ff.fix().fa)() }

}

@extension
interface FxMonadError : MonadError<ForFx, Throwable>, FxApplicativeError, FxMonad

@extension
interface FxMonadThrow : MonadThrow<ForFx>, FxMonadError

@extension
interface FxBracket : Bracket<ForFx, Throwable>, FxMonadThrow {
  override fun <A, B> FxOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> FxOf<Unit>, use: (A) -> FxOf<B>): Fx<B> =
    Fx {
      fix().fa.bracketCase(
        release.curry().andThen { a -> a.andThen { b -> b.fix().fa } }.uncurried(),
        use.andThen { a -> a.fix().fa }
      )()
    }
}

@extension
interface FxMonadDefer : MonadDefer<ForFx>, FxBracket {
  override fun <A> defer(fa: () -> FxOf<A>): Fx<A> =
    unit().flatMap { fa() }
}

@extension
interface FxAsync : Async<ForFx>, FxMonadDefer {

  override fun <A> async(fa: Proc<A>): Fx<A> =
    Fx { fromAsync(fa)() }

  override fun <A> asyncF(k: ProcF<ForFx, A>): Fx<A> =
    Fx { fromAsyncF(k)() }

  override fun <A> FxOf<A>.continueOn(ctx: CoroutineContext): Fx<A> =
    Fx { ctx.continueOn(fix().fa)() }
}

private class Pool(val pool: ForkJoinPool) : AbstractCoroutineContextElement(ContinuationInterceptor), ContinuationInterceptor {
  override fun <T> interceptContinuation(continuation: kotlin.coroutines.Continuation<T>): kotlin.coroutines.Continuation<T> =
    PoolContinuation(pool, continuation.context.fold(continuation) { cont, element ->
      if (element != this@Pool && element is ContinuationInterceptor)
        element.interceptContinuation(cont) else cont
    })
}

private class PoolContinuation<T>(
  val pool: ForkJoinPool,
  val cont: kotlin.coroutines.Continuation<T>
) : kotlin.coroutines.Continuation<T> {
  override val context: CoroutineContext = cont.context

  override fun resumeWith(result: kotlin.Result<T>) {
    pool.execute { cont.resumeWith(result) }
  }
}

@extension
interface FxConcurrent : Concurrent<ForFx>, FxAsync {

  override fun dispatchers(): Dispatchers<ForFx> =
    Fx.dispatchers()

  override fun <A> async(fa: FxProc<A>): Fx<A> =
    Fx { fromAsync(fa)() }

  override fun <A> asyncF(fa: FxProcF<A>): Fx<A> =
    Fx { fromAsyncF(fa)() }

  override fun <A> CoroutineContext.startFiber(fa: FxOf<A>): Fx<Fiber<ForFx, A>> {
    val promise = UnsafePromise<A>()
    val conn = FxConnection()
    fa.fix().fa.startCoroutine(asyncContinuation(this) { either ->
      either.fold(
        { promise.complete(it.left()) },
        { promise.complete(it.right()) }
      )
    })
    return Fx {
      FxFiber(promise, conn)
    }
  }

  override fun <A, B> CoroutineContext.racePair(fa: FxOf<A>, fb: FxOf<B>): Fx<RacePair<ForFx, A, B>> =
    Fx { racePair(fa.fix().fa, fb.fix().fa)() }

  override fun <A, B, C> CoroutineContext.raceTriple(fa: FxOf<A>, fb: FxOf<B>, fc: FxOf<C>): Fx<RaceTriple<ForFx, A, B, C>> =
    Fx { raceTriple(fa.fix().fa, fb.fix().fa, fc.fix().fa)() }

  override fun <A> asyncF(k: ProcF<ForFx, A>): Fx<A> =
    Fx { fromAsyncF(k)() }

  override fun <A> async(fa: Proc<A>): Fx<A> =
    Fx { fromAsync(fa)() }
}

class BlockingCoroutine<T>(override val context: CoroutineContext) : kotlin.coroutines.Continuation<T> {

  private val retVal: AtomicReference<T> = AtomicReference()

  override fun resumeWith(result: Result<T>) {
    retVal.set(result.getOrThrow())
  }

  val value: T
    get() = retVal.get()

}


@extension
interface FxFx : arrow.effects.typeclasses.suspended.concurrent.Fx<ForFx> {
  override fun concurrent(): Concurrent<ForFx> =
    object : FxConcurrent {}
}


fun FxConnection(): KindConnection<ForFx> =
  KindConnection(object : FxMonadDefer {}) { it.fix().fa.foldContinuation { e -> throw e } }

internal fun <A> FxFiber(promise: UnsafePromise<A>, conn: KindConnection<ForFx>): Fiber<ForFx, A> {
  val join: Fx<A> = Fx {
    fromAsync<A> { conn2, cb ->
      conn2.push(Fx { promise.remove(cb) })
      conn.push(conn2.cancel())
      promise.get { a ->
        cb(a)
        conn2.pop()
        conn.pop()
      }
    }()
  }
  return Fiber(join, conn.cancel())
}

suspend fun <A, B> CoroutineContext.racePair(
  fa: suspend () -> A,
  fb: suspend () -> B
): suspend () -> Either<Tuple2<A, Fiber<ForFx, B>>, Tuple2<Fiber<ForFx, A>, B>> = {
  fromAsync<Either<Tuple2<A, Fiber<ForFx, B>>, Tuple2<Fiber<ForFx, A>, B>>> { conn, cb ->
    val active = AtomicBoolean(true)
    val upstreamCancelToken: Fx<Unit> = Fx { if (conn.isCanceled()) Fx { Unit }() else conn.cancel()() }

    val connA = FxConnection()
    connA.push(upstreamCancelToken)
    val promiseA = UnsafePromise<A>()

    val connB = FxConnection()
    connB.push(upstreamCancelToken)
    val promiseB = UnsafePromise<B>()

    conn.pushPair(connA, connB)

    fa.startCoroutine(asyncContinuation(this) { either ->
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

    fb.startCoroutine(asyncContinuation(this) { either ->
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
  }()
}

suspend fun <A, B, C> CoroutineContext.raceTriple(fa: suspend () -> A, fb: suspend () -> B, fc: suspend () -> C): suspend () -> RaceTriple<ForFx, A, B, C> =
  fromAsync<RaceTriple<ForFx, A, B, C>> { conn, cb ->
    val active = AtomicBoolean(true)

    val upstreamCancelToken = Fx { if (conn.isCanceled()) Fx { Unit }() else conn.cancel()() }

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

    fa.startCoroutine(asyncContinuation(this) { either ->
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

    fb.startCoroutine(asyncContinuation(this) { either ->
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

    fc.startCoroutine(asyncContinuation(this) { either ->
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

suspend fun <A> CoroutineContext.continueOn(fa: suspend () -> A): suspend () -> A =
  { !startFiber(fa) }

tailrec suspend fun <A, B> tailRecLoop(a: A, f: (A) -> suspend () -> Either<A, B>): suspend () -> B =
  when (val result = f(a)()) {
    is Either.Left -> tailRecLoop(result.a, f)
    is Either.Right -> result.b.just
  }

suspend fun <A> CoroutineContext.startFiber(fa: suspend () -> A): suspend () -> A = {
  val promise = UnsafePromise<A>()
  fa.startCoroutine(asyncContinuation(this) { either ->
    either.fold(
      { promise.complete(it.left()) },
      { promise.complete(it.right()) }
    )
  })
  FxFiber(promise, FxConnection()).join().fix().fa()
}

/** Hide member because it's discouraged to use uncancelable builder for cancelable concrete type **/
fun <A> fromAsync(fa: Proc<A>): suspend () -> A = {
  suspendCoroutine { continuation ->
    fa { either ->
      continuation.resumeWith(either.fold({ Result.failure<A>(it) }, { Result.success(it) }))
    }
  }
}

suspend fun <A> fromAsync(fa: FxProc<A>): suspend () -> A = {
  suspendCoroutine { continuation ->
    val conn = FxConnection()
    //Is CancellationException from kotlin in kotlinx package???
    conn.push(Fx { continuation.resumeWith(Result.failure(CancellationException())) })
    fa(conn) { either ->
      continuation.resumeWith(either.fold({ Result.failure<A>(it) }, { Result.success(it) }))
    }
  }
}

/** Hide member because it's discouraged to use uncancelable builder for cancelable concrete type **/
internal fun <A> fromAsyncF(fa: ProcF<ForFx, A>): suspend () -> A = {
  suspendCoroutine { continuation ->
    fa { either ->
      continuation.resumeWith(either.fold({ Result.failure<A>(it) }, { Result.success(it) }))
    }.fix().fa.foldContinuation(EmptyCoroutineContext, mapUnit)
  }
}

fun <A> fromAsyncF(fa: FxProcF<A>): suspend () -> A = {
  suspendCoroutine { continuation ->
    val conn = FxConnection()
    //Is CancellationException from kotlin in kotlinx package???
    conn.push(Fx { continuation.resumeWith(Result.failure(CancellationException())) })
    fa(conn) { either ->
      continuation.resumeWith(either.fold({ Result.failure<A>(it) }, { Result.success(it) }))
    }.fix().fa.foldContinuation(EmptyCoroutineContext, mapUnit)
  }
}

fun <P1, P2, R> ((P1) -> (P2) -> R).uncurried(): (P1, P2) -> R = { p1: P1, p2: P2 -> this(p1)(p2) }
