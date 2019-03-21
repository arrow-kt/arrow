package arrow.effects.extensions

import arrow.core.*
import arrow.effects.IODispatchers
import arrow.effects.KindConnection
import arrow.effects.extensions.catchfx.monad.flatMap
import arrow.effects.internal.Platform
import arrow.effects.internal.UnsafePromise
import arrow.effects.internal.asyncContinuation
import arrow.effects.suspended.error.*
import arrow.effects.suspended.fx.RaisedError
import arrow.effects.suspended.fx.foldContinuation
import arrow.effects.typeclasses.*
import arrow.extension
import arrow.typeclasses.*
import java.util.concurrent.CancellationException
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.*

@extension
interface CatchFxFunctor<E> : Functor<CatchFxPartialOf<E>> {
  override fun <A, B> CatchFxOf<E, A>.map(f: (A) -> B): CatchFx<E, B> =
    CatchFx { fix().fa.map(f)() }
}

@extension
interface CatchFxApplicative<E> : Applicative<CatchFxPartialOf<E>>, CatchFxFunctor<E> {
  override fun <A> just(a: A): CatchFx<E, A> =
    CatchFx.just(a)

  @Suppress("UNCHECKED_CAST")
  override fun <A, B> CatchFxOf<E, A>.ap(ff: CatchFxOf<E, (A) -> B>): CatchFx<E, B> =
    CatchFx {
      val result = ff.fix().fa()
      when (result) {
        is Either.Left -> this@ap.fix().fa as (suspend () -> Either<E, B>)
        is Either.Right -> fix().fa.ap<E, A, B> { result.b }
      }()
    }

  override fun <A, B> CatchFxOf<E, A>.map(f: (A) -> B): CatchFx<E, B> =
    CatchFx { fix().fa.map(f)() }
}

@extension
interface CatchFxApplicativeError<E> : ApplicativeError<CatchFxPartialOf<E>, E>, CatchFxApplicative<E> {
  override fun <A> raiseError(e: E): CatchFx<E, A> =
    CatchFx { e.left() }

  override fun <A> CatchFxOf<E, A>.handleErrorWith(f: (E) -> CatchFxOf<E, A>): CatchFx<E, A> =
    CatchFx { fix().fa.handleErrorWith { e: E -> f(e).fix().fa }() }

}

@extension
interface CatchFxMonad<E> : Monad<CatchFxPartialOf<E>>, CatchFxApplicative<E> {
  override fun <A, B> CatchFxOf<E, A>.flatMap(f: (A) -> CatchFxOf<E, B>): CatchFx<E, B> =
    CatchFx { fix().fa.flatMap { a: A -> f(a).fix().fa }() }

  override fun <A, B> tailRecM(a: A, f: (A) -> CatchFxOf<E, Either<A, B>>): CatchFx<E, B> =
    f(a).flatMap {
      it.fold(
        { x -> tailRecM(x, f) },
        { y -> y.just() }
      )
    }

  @Suppress("UNCHECKED_CAST")
  override fun <A, B> CatchFxOf<E, A>.ap(ff: CatchFxOf<E, (A) -> B>): CatchFx<E, B> =
    CatchFx {
      val result = ff.fix().fa()
      when (result) {
        is Either.Left -> this@ap.fix().fa as (suspend () -> Either<E, B>)
        is Either.Right -> fix().fa.ap<E, A, B> { result.b }
      }()
    }

  override fun <A, B> CatchFxOf<E, A>.map(f: (A) -> B): CatchFx<E, B> =
    CatchFx { fix().fa.map(f)() }

}

@extension
interface CatchFxMonadError<E> : MonadError<CatchFxPartialOf<E>, E>, CatchFxApplicativeError<E>, CatchFxMonad<E>

@extension
interface CatchFxMonadThrow<E> : MonadThrow<CatchFxPartialOf<E>>, CatchFxMonad<E> {
  override fun <A> raiseError(e: Throwable): CatchFx<E, A> =
    CatchFx { throw RaisedError(e) }

  override fun <A> CatchFxOf<E, A>.handleErrorWith(f: (Throwable) -> CatchFxOf<E, A>): CatchFx<E, A> =
    CatchFx { fix().fa.handleErrorWith { t: Throwable -> f(t).fix().fa }() }
}

@extension
interface CatchFxBracket<E> : Bracket<CatchFxPartialOf<E>, Throwable>, CatchFxMonadThrow<E> {
  override fun <A, B> CatchFxOf<E, A>.bracketCase(release: (A, ExitCase<Throwable>) -> CatchFxOf<E, Unit>, use: (A) -> CatchFxOf<E, B>): CatchFx<E, B> =
    CatchFx {
      fix().fa.bracketCase(
        release.curry().andThen { a -> a.andThen { b -> b.fix().fa } }.uncurried(),
        use.andThen { a -> a.fix().fa }
      )()
    }
}

@extension
interface CatchFxMonadDefer<E> : MonadDefer<CatchFxPartialOf<E>>, CatchFxBracket<E> {
  override fun <A> defer(fa: () -> CatchFxOf<E, A>): CatchFx<E, A> =
    unit().flatMap { fa() }
}

@extension
interface CatchFxAsync<E> : Async<CatchFxPartialOf<E>>, CatchFxMonadDefer<E> {

  override fun <A> async(fa: Proc<A>): CatchFx<E, A> =
    CatchFx { (fromAsync(fa))().right() }

  override fun <A> asyncF(k: ProcF<CatchFxPartialOf<E>, A>): CatchFx<E, A> =
    CatchFx { fromAsyncF(k)().right() }

  override fun <A> CatchFxOf<E, A>.continueOn(ctx: CoroutineContext): CatchFx<E, A> =
    CatchFx { ctx.continueOn(fix().fa)() }

}

@extension
interface CatchFxConcurrent<E> : Concurrent<CatchFxPartialOf<E>>, CatchFxAsync<E> {

  override fun dispatchers(): Dispatchers<CatchFxPartialOf<E>> =
    object : CatchFxDispatchers<E> {}

  override fun <A> async(fa: Proc<A>): CatchFx<E, A> =
    CatchFx { (fromAsync(fa))().right() }

  override fun <A> asyncF(k: ProcF<CatchFxPartialOf<E>, A>): CatchFx<E, A> =
    CatchFx { fromAsyncF(k)().right() }

  override fun <A> CatchFxOf<E, A>.continueOn(ctx: CoroutineContext): CatchFx<E, A> =
    CatchFx { ctx.continueOn(fix().fa)() }

  override fun <A> CoroutineContext.startFiber(kind: CatchFxOf<E, A>): CatchFx<E, Fiber<CatchFxPartialOf<E>, A>> {
    val promise = UnsafePromise<Either<E, A>>()
    val conn = CatchFxConnection<E>()
    kind.fix().fa.startCoroutine(asyncContinuation(this) { either ->
      either.fold(
        { promise.complete(it.left()) },
        {
          promise.complete(it.right())
        }
      )
    })
    return CatchFx { CatchFxFiber(promise, conn).right() }
  }

  override fun <A> asyncF(fa: ConnectedProcF<CatchFxPartialOf<E>, A>): CatchFx<E, A> =
    CatchFx { fromAsyncF(fa)().right() }

  override fun <A, B> CoroutineContext.racePair(fa: CatchFxOf<E, A>, fb: CatchFxOf<E, B>): CatchFx<E, RacePair<CatchFxPartialOf<E>, A, B>> =
    CatchFx { racePair(fa.fix().fa, fb.fix().fa)() }

  override fun <A, B, C> CoroutineContext.raceTriple(fa: CatchFxOf<E, A>, fb: CatchFxOf<E, B>, fc: CatchFxOf<E, C>): CatchFx<E, RaceTriple<CatchFxPartialOf<E>, A, B, C>> =
    CatchFx { raceTriple(fa.fix().fa, fb.fix().fa, fc.fix().fa)() }

}

@extension
interface CatchFxDispatchers<E> : Dispatchers<CatchFxPartialOf<E>> {
  override fun default(): CoroutineContext =
    IODispatchers.CommonPool

  override fun trampoline(): CoroutineContext =
    IODispatchers.TrampolinePool(Executor { command -> command?.run() })
}

@extension
interface CatchFxFx<E> : arrow.effects.typeclasses.suspended.concurrent.Fx<CatchFxPartialOf<E>> {
  override fun concurrent(): Concurrent<CatchFxPartialOf<E>> =
    object : CatchFxConcurrent<E> {}
}


fun <E> CatchFxConnection(): KindConnection<CatchFxPartialOf<E>> =
  KindConnection(object : CatchFxMonadDefer<E> {}) { it.fix().fa.foldContinuation { e -> throw e } }

suspend fun <E, A> fromAsync(fa: CatchFxConnectedProc<E, A>): suspend () -> Either<E, A> =
  suspendCoroutine { continuation ->
    val conn = CatchFxConnection<E>()
    //Is CancellationException from kotlin in kotlinx package???
    conn.push(CatchFx { continuation.resumeWith(Result.failure(CancellationException())).right() })
    fa(conn) { either ->
      continuation.resumeWith(
        either.fold(
          {
            Result.failure<suspend () -> Either<E, A>>(it)
          },
          { Result.success(suspend { it }) }
        )
      )
    }
  }

/** Hide member because it's discouraged to use uncancelable builder for cancelable concrete type **/
fun <E, A> fromAsyncF(fa: ProcF<CatchFxPartialOf<E>, A>): suspend () -> A = {
  suspendCoroutine { continuation ->
    fa { either ->
      continuation.resumeWith(either.fold({ Result.failure<A>(it) }, { Result.success(it) }))
    }.fix().fa.foldContinuation(EmptyCoroutineContext, mapUnit)
  }
}

fun <E, A> CatchFxFiber(
  promise: UnsafePromise<Either<E, A>>,
  conn: KindConnection<CatchFxPartialOf<E>>
): Fiber<CatchFxPartialOf<E>, A> {
  val join: CatchFx<E, A> = CatchFx {
    fromAsync<E, A> { conn2: KindConnection<CatchFxPartialOf<E>>, cb ->
      conn2.push(CatchFx { promise.remove(cb).right() })
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

fun <E, A> fromAsyncF(fa: CatchFxProcF<E, A>): suspend () -> A = {
  suspendCoroutine { continuation ->
    val conn = CatchFxConnection<E>()
    //Is CancellationException from kotlin in kotlinx package???
    conn.push(CatchFx { continuation.resumeWith(Result.failure(CancellationException())).right() })
    fa(conn) { either ->
      continuation.resumeWith(either.fold({ Result.failure<A>(it) }, { Result.success(it) }))
    }.fix().fa.foldContinuation(EmptyCoroutineContext, mapUnit)
  }
}

suspend fun <E, A, B> CoroutineContext.racePair(
  fa: suspend () -> Either<E, A>,
  fb: suspend () -> Either<E, B>
): suspend () ->
Either<E, RacePair<CatchFxPartialOf<E>, A, B>> = {
  fromAsync<E, RacePair<CatchFxPartialOf<E>, A, B>> { conn: KindConnection<CatchFxPartialOf<E>>, cb ->
    val active: AtomicBoolean = AtomicBoolean(true)
    val upstreamCancelToken: CatchFx<E, Unit> = CatchFx.unit.flatMap { if (conn.isCanceled()) CatchFx.unit else conn.cancel() }

    val connA: KindConnection<CatchFxPartialOf<E>> = CatchFxConnection()
    connA.push(upstreamCancelToken)
    val promiseA: UnsafePromise<Either<E, A>> = UnsafePromise()

    val connB: KindConnection<CatchFxPartialOf<E>> = CatchFxConnection()
    connB.push(upstreamCancelToken)
    val promiseB = UnsafePromise<Either<E, B>>()

    conn.pushPair(connA, connB)

    fa.startCoroutine(asyncContinuation(this) { either ->
      either.fold({ error: Throwable ->
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
          val fiber = CatchFxFiber(promiseB, connB)
          val tuple = a.map { Tuple2(it, fiber).left() }
          cb(Right(tuple))
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
          val fiber = CatchFxFiber(promiseA, connA)
          val tuple = b.map { Tuple2(fiber, it).right() }
          cb(Right(tuple))
        } else {
          promiseB.complete(Right(b))
        }
      })
    })
  }()
}

suspend fun <E, A, B, C> CoroutineContext.raceTriple(
  fa: suspend () -> Either<E, A>,
  fb: suspend () -> Either<E, B>,
  fc: suspend () -> Either<E, C>
): suspend () -> Either<E, RaceTriple<CatchFxPartialOf<E>, A, B, C>> =
  fromAsync { conn: KindConnection<CatchFxPartialOf<E>>,
              cb: (Either<Throwable, Either<E, RaceTriple<CatchFxPartialOf<E>, A, B, C>>>) -> Unit ->

    val active = AtomicBoolean(true)

    val upstreamCancelToken = CatchFx.unit.flatMap { if (conn.isCanceled()) CatchFx.unit else conn.cancel() }

    val connA = CatchFxConnection<E>()
    connA.push(upstreamCancelToken)
    val promiseA = UnsafePromise<Either<E, A>>()

    val connB = CatchFxConnection<E>()
    connB.push(upstreamCancelToken)
    val promiseB = UnsafePromise<Either<E, B>>()

    val connC = CatchFxConnection<E>()
    connC.push(upstreamCancelToken)
    val promiseC = UnsafePromise<Either<E, C>>()

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
      }, { a: Either<E, A> ->
        if (active.getAndSet(false)) {
          conn.pop()
          val tuple = a.map { Tuple3(it, CatchFxFiber(promiseB, connB), CatchFxFiber(promiseC, connC)).left() }
          cb(tuple.right())
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
          cb(b.map { Right(Left(Tuple3(CatchFxFiber(promiseA, connA), it, CatchFxFiber(promiseC, connC)))) }.right())
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
          cb(c.map { Tuple3(CatchFxFiber(promiseA, connA), CatchFxFiber(promiseB, connB), it).right().right() }.right())
        } else {
          promiseC.complete(Right(c))
        }
      })
    })

  }