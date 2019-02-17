package arrow.effects.typeclasses.suspended

import arrow.Kind
import arrow.core.*
import arrow.effects.KindConnection
import arrow.effects.internal.Platform
import arrow.effects.internal.UnsafePromise
import arrow.effects.internal.asyncContinuation
import arrow.effects.typeclasses.*
import arrow.effects.typeclasses.suspended.fx.concurrent.startFiber
import arrow.effects.typeclasses.suspended.fx.dispatchers.dispatchers
import arrow.extension
import arrow.typeclasses.*
import arrow.typeclasses.Continuation
import arrow.unsafe
import java.util.concurrent.CancellationException
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock
import kotlin.coroutines.*

class ForFx private constructor() {
  companion object
}
typealias FxOf<A> = Kind<ForFx, A>
typealias FxProc<A> = ConnectedProc<ForFx, A>
typealias FxProcF<A> = ConnectedProcF<ForFx, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A> FxOf<A>.fix(): Fx<A> =
  this as Fx<A>

suspend operator fun <A> FxOf<A>.invoke(): A = fix().fa.invoke()

inline class Fx<A>(internal val fa: suspend () -> A) : FxOf<A> {
  companion object
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

  override fun resumeWith(result: Result<T>) {
    pool.execute { cont.resumeWith(result) }
  }
}

val NonBlocking: CoroutineContext = EmptyCoroutineContext + Pool(ForkJoinPool())

@extension
interface FxDispatchers : Dispatchers<ForFx> {
  override fun default(): CoroutineContext =
    NonBlocking
}

@extension
interface FxConcurrent : Concurrent<ForFx>, FxAsync {
  override fun dispatchers(): Dispatchers<ForFx> = Fx.dispatchers()

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

private class BlockingCoroutine<T>(override val context: CoroutineContext) : kotlin.coroutines.Continuation<T> {
  private val lock = ReentrantLock()
  private val done = lock.newCondition()
  private var result: Result<T>? = null

  private inline fun <T> locked(block: () -> T): T {
    lock.lock()
    return try {
      block()
    } finally {
      lock.unlock()
    }
  }

  private inline fun loop(block: () -> Unit): Nothing {
    while (true) {
      block()
    }
  }

  override fun resumeWith(result: Result<T>) = locked {
    this.result = result
    done.signal()
  }

  fun getValue(): T = locked<T> {
    loop {
      val result = this.result
      if (result == null) {
        done.awaitUninterruptibly()
      } else {
        return@locked result.getOrThrow()
      }
    }
  }
}

@extension
interface FxUnsafeRun : UnsafeRun<ForFx> {
  override suspend fun <A> unsafe.runBlocking(fa: () -> FxOf<A>): A =
    BlockingCoroutine<A>(EmptyCoroutineContext).also { fa().fix().fa.startCoroutine(it) }.getValue()

  override suspend fun <A> unsafe.runNonBlocking(fa: () -> FxOf<A>, cb: (Either<Throwable, A>) -> Unit) =
    fa().fix().fa.startCoroutine(asyncContinuation(NonBlocking, cb))
}

private fun <A> (suspend () -> A).foldContinuation(
  context: CoroutineContext = EmptyCoroutineContext,
  onError: (Throwable) -> A
): A {
  val result: AtomicReference<A> = AtomicReference()
  startCoroutine(object : Continuation<A> {
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

fun FxConnection(): KindConnection<ForFx> =
  KindConnection(object : FxMonadDefer {}) { it.fix().fa.foldContinuation { e -> throw e } }

internal fun <A> FxFiber(promise: UnsafePromise<A>, conn: KindConnection<ForFx>): Fiber<ForFx, A> {
  val join: Fx<A> = Fx { fromAsync<A> { conn2, cb ->
    conn2.push(Fx { promise.remove(cb) })
    conn.push(conn2.cancel())
    promise.get { a ->
      cb(a)
      conn2.pop()
      conn.pop()
    }
  }()}
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
  fromAsync { conn, cb ->
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

fun <A> fx(f: suspend () -> A): suspend () -> A =
  f

suspend inline operator fun <A> (suspend () -> A).not(): A =
  this()

suspend inline fun <A> (suspend () -> A).bind(): A =
  this()

suspend inline operator fun <A> (suspend () -> A).component1(): A =
  this()

/**
 * Avoid recalculating stack traces on rethrows
 */
class RaisedError(val exception: Throwable) : Throwable() {
  override fun fillInStackTrace(): Throwable =
    this

  override val cause: Throwable?
    get() = exception
}

inline fun <A> Throwable.raiseError(): suspend () -> A =
  { throw RaisedError(this) }

suspend fun <A, B> (suspend () -> A).map(f: (A) -> B): suspend () -> B =
  { f(this()) }

fun <A> just(a: A): suspend () -> A =
  { a }

fun <A> A.just(unit: Unit = Unit): suspend () -> A =
  { this }

suspend fun <A, B> (suspend () -> A).ap(ff: suspend () -> (A) -> B): suspend () -> B =
  map(ff())

suspend fun <A, B> (suspend () -> A).flatMap(f: (A) -> suspend () -> B): suspend () -> B =
  {
    try {
      !f(this())
    } catch (e: Throwable) {
      !raiseError<B>(e)
    }
  }

suspend inline fun <A> (suspend () -> A).attempt(unit: Unit = Unit): suspend () -> Either<Throwable, A> =
  attempt(this)

suspend inline fun <A> attempt(crossinline f: suspend () -> A): suspend () -> Either<Throwable, A> =
  {
    try {
      f().right()
    } catch (e: Throwable) {
      e.left()
    }
  }

val unit: suspend () -> Unit = { Unit }

fun <A> raiseError(e: Throwable, unit: Unit = Unit): suspend () -> A =
  { throw RaisedError(e) }

inline fun <A> (suspend () -> A).handleErrorWith(crossinline f: (Throwable) -> suspend () -> A): suspend () -> A =
  {
    try {
      this()
    } catch (r: RaisedError) {
      f(r.exception)()
    } catch (e: Throwable) {
      f(e)()
    }
  }

inline fun <A> (suspend () -> A).handleError(crossinline f: (Throwable) -> A): suspend () -> A =
  {
    try {
      this()
    } catch (r: RaisedError) {
      f(r.exception)
    } catch (e: Throwable) {
      f(e)
    }
  }

suspend inline fun <A> defer(noinline fa: suspend () -> A): suspend () -> A =
  unit.flatMap { fa }

inline fun <A> (suspend () -> A).ensure(
  crossinline error: () -> Throwable,
  crossinline predicate: (A) -> Boolean
): suspend () -> A =
  {
    val result = this()
    if (!predicate(result)) throw error()
    else result
  }

suspend fun <A, B> (suspend () -> A).bracketCase(
  release: (A, ExitCase<Throwable>) -> suspend () -> Unit,
  use: (A) -> suspend () -> B
): suspend () -> B = {
  val a = invoke()

  val fxB = try {
    use(a)
  } catch (e: Throwable) {
    release(a, ExitCase.Error(e)).foldContinuation { e2 ->
      throw Platform.composeErrors(e, e2)
    }
    throw e
  }

  val b = fxB.foldContinuation { e ->
    when (e) {
      is CancellationException -> release(a, ExitCase.Canceled).foldContinuation { e2 ->
        throw Platform.composeErrors(e, e2)
      }
      else -> release(a, ExitCase.Error(e)).foldContinuation { e2 ->
        throw Platform.composeErrors(e, e2)
      }
    }
    throw e
  }
  release(a, ExitCase.Completed).invoke()
  b
}

suspend fun <A> CoroutineContext.continueOn(fa: suspend () -> A): suspend () -> A =
  { !startFiber(fa) }

tailrec suspend fun <A, B> tailRecLoop(a: A, f: (A) -> suspend () -> Either<A, B>): suspend () -> B =
  when (val result = f(a)()) {
    is Either.Left -> tailRecLoop(result.a, f)
    is Either.Right -> result.b.just()
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
internal fun <A> fromAsync(fa: Proc<A>): suspend () -> A = {
  suspendCoroutine { continuation ->
    fa { either ->
      continuation.resumeWith(either.fold(Result.Companion::failure, Result.Companion::success))
    }
  }
}

suspend fun <A> fromAsync(fa: FxProc<A>): suspend () -> A = {
  suspendCoroutine { continuation ->
    val conn = FxConnection()
    //Is CancellationException from kotlin in kotlinx package???
    conn.push(Fx { continuation.resumeWith(Result.failure(CancellationException())) })
    fa(conn) { either ->
      continuation.resumeWith(either.fold(Result.Companion::failure, Result.Companion::success))
    }
  }
}

/** Hide member because it's discouraged to use uncancelable builder for cancelable concrete type **/
internal fun <A> fromAsyncF(fa: ProcF<ForFx, A>): suspend () -> A = {
  suspendCoroutine { continuation ->
    fa { either ->
      continuation.resumeWith(either.fold(Result.Companion::failure, Result.Companion::success))
    }.fix().fa.foldContinuation(EmptyCoroutineContext, mapUnit)
  }
}

fun <A> fromAsyncF(fa: FxProcF<A>): suspend () -> A = {
  suspendCoroutine { continuation ->
    val conn = FxConnection()
    //Is CancellationException from kotlin in kotlinx package???
    conn.push(Fx { continuation.resumeWith(Result.failure(CancellationException())) })
    fa(conn) { either ->
      continuation.resumeWith(either.fold(Result.Companion::failure, Result.Companion::success))
    }.fix().fa.foldContinuation(EmptyCoroutineContext, mapUnit)
  }
}

private fun <P1, P2, R> ((P1) -> (P2) -> R).uncurried(): (P1, P2) -> R = { p1: P1, p2: P2 -> this(p1)(p2) }