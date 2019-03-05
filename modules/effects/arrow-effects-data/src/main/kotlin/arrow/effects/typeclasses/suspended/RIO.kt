package arrow.effects.typeclasses.suspended

import arrow.core.*
import arrow.effects.KindConnection
import arrow.effects.internal.Platform
import arrow.effects.internal.UnsafePromise
import arrow.effects.internal.asyncContinuation
import arrow.effects.typeclasses.*
import arrow.effects.typeclasses.suspended.concurrent.Fx
import arrow.extension
import arrow.typeclasses.*
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.*

class ForRIO private constructor() {
  companion object
}
typealias RIOOf<R, E, A> = arrow.Kind3<ForRIO, R, E, A>
typealias RIOPartialOf<R, E> = arrow.Kind2<ForRIO, R, E>
typealias RIOProcF<R, E, A> = ConnectedProcF<RIOPartialOf<R, E>, A>
typealias RIOConnectedProc<R, E, A> = (KindConnection<RIOPartialOf<R, E>>, ((Either<Throwable, Either<E, A>>) -> Unit)) -> Unit

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <R, E, A> RIOOf<R, E, A>.fix(): RIO<R, E, A> =
  this as RIO<R, E, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
fun <R, E, A> effect(f: suspend (R) -> Either<E, A>): RIO<R, E, A> =
  RIO(f)

class RIO<R, out E, out A>(internal val fa: suspend R.() -> Either<E, A>) : RIOOf<R, E, A> {
  companion object {
    fun <R, E> unit(): RIO<R, E, Unit> =
      RIO { Unit.right() }

    fun <R, E, A> just(a: A): RIO<R, E, A> =
      RIO { a.right() }
  }
}

fun <R, E, A> RIOOf<R, E, A>.toFx(r: R): arrow.effects.typeclasses.suspended.Fx<Either<E, A>> =
  Fx { fix().fa.invoke(r) }

suspend operator fun <R, E, A> RIOOf<R, E, A>.invoke(r: R): Either<E, A> =
  fix().fa.invoke(r)

fun <A> A.just(unit: Unit = Unit): RIO<Nothing, Nothing, A> =
  RIO { right() }

suspend fun <R, E, A, B> (suspend R.() -> Either<E, A>).map(f: (A) -> B): suspend R.() -> Either<E, B> =
  { this@map(this).map(f) }

suspend fun <R, E, A, B> (suspend R.() -> Either<E, A>).mapLeft(f: (E) -> B): suspend R.() -> Either<B, A> =
  { mapLeft(f)(this) }

suspend fun <R, E, A> (suspend R.() -> Either<E, A>).attempt(): suspend R.() -> Either<E, A> = {
  try {
    this@attempt(this)
  } catch (e: Throwable) {
    throw RaisedError(e.nonFatalOrThrow())
  }
}

@Suppress("UNCHECKED_CAST")
suspend fun <R, E, A, B> (suspend R.() -> Either<E, A>).flatMap(f: (A) -> suspend R.() -> Either<E, B>): suspend R.() -> Either<E, B> = {
  when (val x = this@flatMap.attempt()(this)) {
    is Either.Left -> x.a.left()
    is Either.Right -> f(x.b).attempt()(this)
  }
}

suspend fun <R, E, A, B> (suspend R.() -> Either<E, A>).ap(ff: suspend () -> (A) -> B): suspend R.() -> Either<E, B> =
  map(ff())

suspend fun <R, E, A> attempt(
  fa: suspend () -> A,
  onError: (Throwable) -> E,
  unit: Unit = Unit
): suspend R.() -> Either<E, A> =
  { attempt(fa).mapLeft(onError)() }

fun <R, E> E.raiseError(unit: Unit = Unit): suspend R.() -> Either<E, Nothing> =
  { this@raiseError.left() }

suspend fun <R, E, A> (suspend R.() -> Either<E, A>).handleErrorWith(f: (E) -> suspend R.() -> Either<E, A>): suspend R.() -> Either<E, A> = {
  when (val result = this@handleErrorWith.attempt()(this)) {
    is Either.Left -> f(result.a).attempt()(this)
    is Either.Right -> this@handleErrorWith.attempt()(this)
  }
}

suspend fun <R, E, A> (suspend R.() -> Either<E, A>).handleErrorWith(unit: Unit = Unit, f: (Throwable) -> suspend R.() -> Either<E, A>): suspend R.() -> Either<E, A> = {
  try {
    this@handleErrorWith(this)
  } catch (t: Throwable) {
    f(t.nonFatalOrThrow())(this)
  }
}

@Suppress("UNCHECKED_CAST")
suspend fun <R, E, A> (suspend R.() -> Either<E, A>).handleError(f: (E) -> A): suspend R.() -> Either<E, A> = {
  when (val result = this@handleError.attempt()(this)) {
    is Either.Left -> {
      f(result.a).right()
    }
    is Either.Right -> this@handleError(this)
  }
}

suspend fun <R, E, A> (suspend R.() -> Either<E, A>).ensure(
  error: () -> E,
  predicate: (A) -> Boolean
): suspend R.() -> Either<E, A> = {
  when (val result = this@ensure.attempt()(this)) {
    is Either.Left -> this@ensure(this)
    is Either.Right -> if (predicate(result.b)) this@ensure(this)
    else {
      error().left()
    }
  }
}

suspend fun <R, E, A, B> (suspend R.() -> Either<E, A>).bracketCase(
  release: (A, ExitCase<Throwable>) -> suspend R.() -> Either<E, Unit>,
  use: (A) -> suspend R.() -> Either<E, B>
): suspend R.() -> Either<E, B> = {
  when (val result = this@bracketCase(this)) {
    is Either.Left -> result.a.left()
    is Either.Right -> {
      val a = result.b
      val fxB = try {
        use(a)
      } catch (e: Throwable) {
        suspend { release(a, ExitCase.Error(e))(this) }.foldContinuation { e2 ->
          throw Platform.composeErrors(e, e2)
        }
        throw e
      }

      val b = suspend { fxB(this) }.foldContinuation { e ->
        when (e) {
          is CancellationException -> suspend { release(a, ExitCase.Canceled)(this) }.foldContinuation { e2 ->
            throw Platform.composeErrors(e, e2)
          }
          else -> suspend { release(a, ExitCase.Error(e))(this) }.foldContinuation { e2 ->
            throw Platform.composeErrors(e, e2)
          }
        }
        throw e
      }
      release(a, ExitCase.Completed)(this)
      b
    }
  }

}

fun <R, E> RIOConnection(r: R): KindConnection<RIOPartialOf<R, E>> =
  KindConnection(object : RIOMonadDefer<R, E> {}) { suspend { it.fix().fa(r) }.foldContinuation { e -> throw e } }

suspend fun <R, E, A> fromAsync(fa: RIOConnectedProc<R, E, A>): suspend R.() -> Either<E, A> = {
  suspendCoroutine { continuation ->
    val conn = RIOConnection<R, E>(this)
    //Is CancellationException from kotlin in kotlinx package???
    conn.push(RIO { continuation.resumeWith(Result.failure(CancellationException())).right() })
    fa(conn) { either: Either<Throwable, Either<E, A>> ->
      continuation.resumeWith(
        either.fold(
          { ex ->
            Result.failure<Either<E, A>>(ex)
          },
          { e -> Result.success<Either<E, A>>(e) }
        )
      )
    }
  }
}

/** Hide member because it's discouraged to use uncancelable builder for cancelable concrete type **/
internal fun <R, E, A> fromAsyncF(fa: ProcF<RIOPartialOf<R, E>, A>): suspend R.() -> A = {
  suspendCoroutine { continuation ->
    suspend {
      fa { either ->
        continuation.resumeWith(either.fold({ kotlin.Result.failure<A>(it) }, { kotlin.Result.success(it) }))
      }.fix().fa(this)
    }.foldContinuation(EmptyCoroutineContext, mapUnit)
  }
}

fun <R, E, A> fromAsyncF(fa: RIOProcF<R, E, A>): suspend R.() -> A = {
  suspendCoroutine { continuation ->
    val conn = RIOConnection<R, E>(this)
    //Is CancellationException from kotlin in kotlinx package???
    conn.push(RIO { continuation.resumeWith(kotlin.Result.failure(CancellationException())).right() })
    suspend {
      fa(conn) { either ->
        continuation.resumeWith(either.fold({ kotlin.Result.failure<A>(it) }, { kotlin.Result.success(it) }))
      }.fix().fa(this)
    }.foldContinuation(EmptyCoroutineContext, mapUnit)
  }
}

@extension
interface RIOFunctor<R, E> : Functor<RIOPartialOf<R, E>> {

  override fun <A, B> RIOOf<R, E, A>.map(f: (A) -> B): RIO<R, E, B> =
    RIO { fix().fa.map(f)(this) }

}

@extension
interface RIOApplicative<R, E> : Applicative<RIOPartialOf<R, E>>, RIOFunctor<R, E> {
  override fun <A> just(a: A): RIO<R, E, A> =
    RIO.just(a)

  override fun <A, B> RIOOf<R, E, A>.map(f: (A) -> B): RIO<R, E, B> =
    RIO { fix().fa.map(f)(this) }

  @Suppress("UNCHECKED_CAST")
  override fun <A, B> RIOOf<R, E, A>.ap(ff: RIOOf<R, E, (A) -> B>): RIO<R, E, B> =
    RIO {
      val result: Either<E, (A) -> B> = ff.fix().fa(this)
      when (result) {
        is Either.Left -> this@ap.fix().fa(this) as Either<E, B>
        is Either.Right -> fix().fa.ap { result.b }(this)
      }
    }
}

@extension
interface RIOApplicativeError<R, E> : ApplicativeError<RIOPartialOf<R, E>, E>, RIOApplicative<R, E> {
  override fun <A> raiseError(e: E): RIO<R, E, A> =
    RIO { e.left() }

  override fun <A> RIOOf<R, E, A>.handleErrorWith(f: (E) -> RIOOf<R, E, A>): RIO<R, E, A> =
    RIO { fix().fa.handleErrorWith { e: E -> f(e).fix().fa }(this) }

}

@extension
interface RIOMonad<R, E> : Monad<RIOPartialOf<R, E>>, RIOApplicative<R, E> {
  override fun <A, B> RIOOf<R, E, A>.flatMap(f: (A) -> RIOOf<R, E, B>): RIO<R, E, B> =
    RIO { fix().fa.flatMap { a: A -> f(a).fix().fa }(this) }

  override fun <A, B> tailRecM(a: A, f: (A) -> RIOOf<R, E, Either<A, B>>): RIO<R, E, B> =
    f(a).flatMap {
      it.fold(
        { x -> tailRecM(x, f) },
        { y -> y.just() }
      )
    }

  @Suppress("UNCHECKED_CAST")
  override fun <A, B> RIOOf<R, E, A>.ap(ff: RIOOf<R, E, (A) -> B>): RIO<R, E, B> =
    RIO {
      val result: Either<E, (A) -> B> = ff.fix().fa(this)
      when (result) {
        is Either.Left -> this@ap.fix().fa(this) as Either<E, B>
        is Either.Right -> fix().fa.ap { result.b }(this)
      }
    }

  override fun <A, B> RIOOf<R, E, A>.map(f: (A) -> B): RIO<R, E, B> =
    RIO { fix().fa.map(f)(this) }

}

@extension
interface RIOMonadError<R, E> : MonadError<RIOPartialOf<R, E>, E>, RIOApplicativeError<R, E>, RIOMonad<R, E>

@extension
interface RIOMonadThrow<R, E> : MonadThrow<RIOPartialOf<R, E>>, RIOMonad<R, E> {
  override fun <A> raiseError(e: Throwable): RIO<R, E, A> =
    RIO { throw RaisedError(e) }

  override fun <A> RIOOf<R, E, A>.handleErrorWith(f: (Throwable) -> RIOOf<R, E, A>): RIO<R, E, A> =
    RIO { fix().fa.handleErrorWith { t: Throwable -> f(t).fix().fa }(this) }
}

@extension
interface RIOBracket<R, E> : Bracket<RIOPartialOf<R, E>, Throwable>, RIOMonadThrow<R, E> {
  override fun <A, B> RIOOf<R, E, A>.bracketCase(release: (A, ExitCase<Throwable>) -> RIOOf<R, E, Unit>, use: (A) -> RIOOf<R, E, B>): RIO<R, E, B> =
    RIO {
      fix().fa.bracketCase(
        release.curry().andThen { a -> a.andThen { b -> b.fix().fa } }.uncurried(),
        use.andThen { a -> a.fix().fa }
      )(this)
    }
}

@extension
interface RIOMonadDefer<R, E> : MonadDefer<RIOPartialOf<R, E>>, RIOBracket<R, E> {
  override fun <A> defer(fa: () -> RIOOf<R, E, A>): RIO<R, E, A> =
    unit().flatMap { fa() }
}

@extension
interface RIOAsync<R, E> : Async<RIOPartialOf<R, E>>, RIOMonadDefer<R, E> {

  override fun <A> async(fa: Proc<A>): RIO<R, E, A> =
    RIO { fromAsync(fa)().right() }

  override fun <A> asyncF(k: ProcF<RIOPartialOf<R, E>, A>): RIO<R, E, A> =
    RIO { fromAsyncF(k)(this).right() }

  override fun <A> RIOOf<R, E, A>.continueOn(ctx: CoroutineContext): RIO<R, E, A> =
    RIO { ctx.continueOn(suspend { fix().fa(this) })() }

}

@extension
interface RIODispatchers<R, E> : Dispatchers<RIOPartialOf<R, E>> {
  override fun default(): CoroutineContext =
    NonBlocking
}

@extension
interface RIOConcurrent<R, E> : Concurrent<RIOPartialOf<R, E>>, RIOAsync<R, E> {
  override fun dispatchers(): Dispatchers<RIOPartialOf<R, E>> =
    object : RIODispatchers<R, E> {}

  override fun <A> async(fa: Proc<A>): RIO<R, E, A> =
    RIO { fromAsync(fa)().right() }

  override fun <A> asyncF(k: ProcF<RIOPartialOf<R, E>, A>): RIO<R, E, A> =
    RIO { fromAsyncF(k)(this).right() }

  override fun <A> RIOOf<R, E, A>.continueOn(ctx: CoroutineContext): RIO<R, E, A> =
    RIO { ctx.continueOn(suspend { fix().fa(this) })() }

  override fun <A> CoroutineContext.startFiber(kind: RIOOf<R, E, A>): RIO<R, E, Fiber<RIOPartialOf<R, E>, A>> =
    RIO {
      val promise = UnsafePromise<Either<E, A>>()
      val conn = RIOConnection<R, E>(this)
      suspend { kind.fix().fa(this) }.startCoroutine(asyncContinuation(this@startFiber) { either ->
        either.fold(
          { promise.complete(it.left()) },
          {
            promise.complete(it.right())
          }
        )
      })
      RIOFiber(promise, conn).right()
    }

  override fun <A> asyncF(fa: ConnectedProcF<RIOPartialOf<R, E>, A>): RIO<R, E, A> =
    RIO { fromAsyncF(fa)(this).right() }

  override fun <A, B> CoroutineContext.racePair(fa: RIOOf<R, E, A>, fb: RIOOf<R, E, B>): RIO<R, E, RacePair<RIOPartialOf<R, E>, A, B>> =
    RIO { racePair(fa.fix().fa, fb.fix().fa)(this) }

  override fun <A, B, C> CoroutineContext.raceTriple(fa: RIOOf<R, E, A>, fb: RIOOf<R, E, B>, fc: RIOOf<R, E, C>): RIO<R, E, RaceTriple<RIOPartialOf<R, E>, A, B, C>> =
    RIO { raceTriple(fa.fix().fa, fb.fix().fa, fc.fix().fa)(this) }

}

@extension
interface RIOFx<R, E> : Fx<RIOPartialOf<R, E>> {
  override fun concurrent(): Concurrent<RIOPartialOf<R, E>> =
    object : RIOConcurrent<R, E> {}
}

internal fun <R, E, A> RIOFiber(
  promise: UnsafePromise<Either<E, A>>,
  conn: KindConnection<RIOPartialOf<R, E>>
): Fiber<RIOPartialOf<R, E>, A> {
  val join: RIO<R, E, A> = RIO {
    fromAsync<R, E, A> { conn2: KindConnection<RIOPartialOf<R, E>>, cb ->
      conn2.push(RIO { promise.remove(cb).right() })
      conn.push(conn2.cancel())
      promise.get { a ->
        cb(a)
        conn2.pop()
        conn.pop()
      }
    }(this)
  }
  return Fiber(join, conn.cancel())
}

suspend fun <R, E, A, B> CoroutineContext.racePair(
  fa: suspend R.() -> Either<E, A>,
  fb: suspend R.() -> Either<E, B>
): suspend R.() ->
Either<E, RacePair<RIOPartialOf<R, E>, A, B>> = {
  fromAsync<R, E, RacePair<RIOPartialOf<R, E>, A, B>> { conn: KindConnection<RIOPartialOf<R, E>>, cb ->
    val active: AtomicBoolean = AtomicBoolean(true)
    val upstreamCancelToken: RIO<R, E, Unit> = if (conn.isCanceled()) RIO.unit() else conn.cancel().fix()

    val connA: KindConnection<RIOPartialOf<R, E>> = RIOConnection(this)
    connA.push(upstreamCancelToken)
    val promiseA: UnsafePromise<Either<E, A>> = UnsafePromise()

    val connB: KindConnection<RIOPartialOf<R, E>> = RIOConnection(this)
    connB.push(upstreamCancelToken)
    val promiseB = UnsafePromise<Either<E, B>>()

    conn.pushPair(connA, connB)

    suspend { fa(this) }.startCoroutine(asyncContinuation(this@racePair) { either ->
      either.fold({ error: Throwable ->
        if (active.getAndSet(false)) { //if an error finishes first, stop the race.
          suspend { connB.cancel().fix().fa(this) }.startCoroutine(Continuation(EmptyCoroutineContext) { result ->
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
          val fiber = RIOFiber(promiseB, connB)
          val tuple = a.map { Tuple2(it, fiber).left() }
          cb(Right(tuple))
        } else {
          promiseA.complete(Right(a))
        }
      })
    })

    suspend { fb(this) }.startCoroutine(asyncContinuation(this@racePair) { either ->
      either.fold({ error ->
        if (active.getAndSet(false)) { //if an error finishes first, stop the race.
          suspend { connA.cancel().fix().fa(this) }.startCoroutine(Continuation(EmptyCoroutineContext) { result ->
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
          val fiber = RIOFiber(promiseA, connA)
          val tuple = b.map { Tuple2(fiber, it).right() }
          cb(Right(tuple))
        } else {
          promiseB.complete(Right(b))
        }
      })
    })
  }(this)
}

suspend fun <R, E, A, B, C> CoroutineContext.raceTriple(
  fa: suspend R.() -> Either<E, A>,
  fb: suspend R.() -> Either<E, B>,
  fc: suspend R.() -> Either<E, C>
): suspend R.() -> Either<E, RaceTriple<RIOPartialOf<R, E>, A, B, C>> = {
  fromAsync { conn: KindConnection<RIOPartialOf<R, E>>,
              cb: (Either<Throwable, Either<E, RaceTriple<RIOPartialOf<R, E>, A, B, C>>>) -> Unit ->

    val active = AtomicBoolean(true)

    val upstreamCancelToken: RIO<R, E, Unit> = if (conn.isCanceled()) RIO.unit() else conn.cancel().fix()

    val connA = RIOConnection<R, E>(this)
    connA.push(upstreamCancelToken)
    val promiseA = UnsafePromise<Either<E, A>>()

    val connB = RIOConnection<R, E>(this)
    connB.push(upstreamCancelToken)
    val promiseB = UnsafePromise<Either<E, B>>()

    val connC = RIOConnection<R, E>(this)
    connC.push(upstreamCancelToken)
    val promiseC = UnsafePromise<Either<E, C>>()

    conn.push(connA.cancel(), connB.cancel(), connC.cancel())

    suspend { fa(this) }.startCoroutine(asyncContinuation(this@raceTriple) { either ->
      either.fold({ error ->
        if (active.getAndSet(false)) { //if an error finishes first, stop the race.
          suspend { connB.cancel().fix().fa(this) }.startCoroutine(Continuation(EmptyCoroutineContext) { r2 ->
            suspend { connC.cancel().fix().fa(this) }.startCoroutine(Continuation(EmptyCoroutineContext) { r3 ->
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
          val tuple = a.map { Tuple3(it, RIOFiber(promiseB, connB), RIOFiber(promiseC, connC)).left() }
          cb(tuple.right())
        } else {
          promiseA.complete(Right(a))
        }
      })
    })

    suspend { fb(this) }.startCoroutine(asyncContinuation(this@raceTriple) { either ->
      either.fold({ error ->
        if (active.getAndSet(false)) { //if an error finishes first, stop the race.
          suspend { connA.cancel().fix().fa(this) }.startCoroutine(Continuation(EmptyCoroutineContext) { r2 ->
            suspend { connC.cancel().fix().fa(this) }.startCoroutine(Continuation(EmptyCoroutineContext) { r3 ->
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
          cb(b.map { Right(Left(Tuple3(RIOFiber(promiseA, connA), it, RIOFiber(promiseC, connC)))) }.right())
        } else {
          promiseB.complete(Right(b))
        }
      })
    })

    suspend { fc(this) }.startCoroutine(asyncContinuation(this@raceTriple) { either ->
      either.fold({ error ->
        if (active.getAndSet(false)) { //if an error finishes first, stop the race.
          suspend { connA.cancel().fix().fa(this) }.startCoroutine(Continuation(EmptyCoroutineContext) { r2 ->
            suspend { connB.cancel().fix().fa(this) }.startCoroutine(Continuation(EmptyCoroutineContext) { r3 ->
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
          cb(c.map { Tuple3(RIOFiber(promiseA, connA), RIOFiber(promiseB, connB), it).right().right() }.right())
        } else {
          promiseC.complete(Right(c))
        }
      })
    })

  }(this)
}