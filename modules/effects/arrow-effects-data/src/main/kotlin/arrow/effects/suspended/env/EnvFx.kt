package arrow.effects.suspended.env

import arrow.core.*
import arrow.effects.KindConnection
import arrow.effects.internal.Platform
import arrow.effects.internal.UnsafePromise
import arrow.effects.internal.asyncContinuation
import arrow.effects.suspended.TrampolinePool
import arrow.effects.suspended.error.mapLeft
import arrow.effects.suspended.fx.*
import arrow.effects.typeclasses.*
import arrow.effects.typeclasses.suspended.concurrent.Fx
import arrow.extension
import arrow.typeclasses.*
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.*

class ForEnvFx private constructor() {
  companion object
}
typealias EnvFxOf<R, E, A> = arrow.Kind3<ForEnvFx, R, E, A>
typealias EnvFxPartialOf<R, E> = arrow.Kind2<ForEnvFx, R, E>
typealias EnvFxProcF<R, E, A> = ConnectedProcF<EnvFxPartialOf<R, E>, A>
typealias EnvFxConnectedProc<R, E, A> = (KindConnection<EnvFxPartialOf<R, E>>, ((Either<Throwable, Either<E, A>>) -> Unit)) -> Unit

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <R, E, A> EnvFxOf<R, E, A>.fix(): EnvFx<R, E, A> =
  this as EnvFx<R, E, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
fun <R, E, A> effect(f: suspend R.() -> Either<E, A>): EnvFx<R, E, A> =
  EnvFx(f)

fun <R, E, A> env(f: R.() -> EnvFx<R, E, A>): EnvFx<R, E, A> =
  EnvFx { f(this).fa(this) }

class EnvFx<R, out E, out A>(internal val fa: suspend R.() -> Either<E, A>) : EnvFxOf<R, E, A> {
  companion object {
    fun <R, E> unit(): EnvFx<R, E, Unit> =
      EnvFx { Unit.right() }

    fun <R, E, A> just(a: A): EnvFx<R, E, A> =
      EnvFx { a.right() }
  }
}

fun <R, E, A> EnvFxOf<R, E, A>.toFx(r: R): arrow.effects.suspended.fx.Fx<Either<E, A>> =
  arrow.effects.suspended.fx.Fx { fix().fa.invoke(r) }

suspend operator fun <R, E, A> EnvFxOf<R, E, A>.invoke(r: R): Either<E, A> =
  fix().fa.invoke(r)

fun <R, E, A> A.just(): EnvFx<R, E, A> =
  EnvFx { this@just.right() }

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
  { arrow.effects.suspended.fx.attempt(fa).mapLeft(onError)() }

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

fun <R, E> EnvFxConnection(r: R): KindConnection<EnvFxPartialOf<R, E>> =
  KindConnection(object : EnvFxMonadDefer<R, E> {}) { suspend { it.fix().fa(r) }.foldContinuation { e -> throw e } }

suspend fun <R, E, A> fromAsync(fa: EnvFxConnectedProc<R, E, A>): suspend R.() -> Either<E, A> = {
  suspendCoroutine { continuation ->
    val conn = EnvFxConnection<R, E>(this)
    //Is CancellationException from kotlin in kotlinx package???
    conn.push(EnvFx { continuation.resumeWith(Result.failure(CancellationException())).right() })
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
internal fun <R, E, A> fromAsyncF(fa: ProcF<EnvFxPartialOf<R, E>, A>): suspend R.() -> A = {
  suspendCoroutine { continuation ->
    suspend {
      fa { either ->
        continuation.resumeWith(either.fold({ kotlin.Result.failure<A>(it) }, { kotlin.Result.success(it) }))
      }.fix().fa(this)
    }.foldContinuation(EmptyCoroutineContext, mapUnit)
  }
}

fun <R, E, A> fromAsyncF(fa: EnvFxProcF<R, E, A>): suspend R.() -> A = {
  suspendCoroutine { continuation ->
    val conn = EnvFxConnection<R, E>(this)
    //Is CancellationException from kotlin in kotlinx package???
    conn.push(EnvFx { continuation.resumeWith(Result.failure(CancellationException())).right() })
    suspend {
      fa(conn) { either ->
        continuation.resumeWith(either.fold({ kotlin.Result.failure<A>(it) }, { kotlin.Result.success(it) }))
      }.fix().fa(this)
    }.foldContinuation(EmptyCoroutineContext, mapUnit)
  }
}

@extension
interface EnvFxFunctor<R, E> : Functor<EnvFxPartialOf<R, E>> {

  override fun <A, B> EnvFxOf<R, E, A>.map(f: (A) -> B): EnvFx<R, E, B> =
    EnvFx { fix().fa.map(f)(this) }

}

@extension
interface EnvFxApplicative<R, E> : Applicative<EnvFxPartialOf<R, E>>, EnvFxFunctor<R, E> {
  override fun <A> just(a: A): EnvFx<R, E, A> =
    EnvFx.just(a)

  override fun <A, B> EnvFxOf<R, E, A>.map(f: (A) -> B): EnvFx<R, E, B> =
    EnvFx { fix().fa.map(f)(this) }

  @Suppress("UNCHECKED_CAST")
  override fun <A, B> EnvFxOf<R, E, A>.ap(ff: EnvFxOf<R, E, (A) -> B>): EnvFx<R, E, B> =
    EnvFx {
      val result: Either<E, (A) -> B> = ff.fix().fa(this)
      when (result) {
        is Either.Left -> this@ap.fix().fa(this) as Either<E, B>
        is Either.Right -> fix().fa.ap { result.b }(this)
      }
    }
}

@extension
interface EnvFxApplicativeError<R, E> : ApplicativeError<EnvFxPartialOf<R, E>, E>, EnvFxApplicative<R, E> {
  override fun <A> raiseError(e: E): EnvFx<R, E, A> =
    EnvFx { e.left() }

  override fun <A> EnvFxOf<R, E, A>.handleErrorWith(f: (E) -> EnvFxOf<R, E, A>): EnvFx<R, E, A> =
    EnvFx { fix().fa.handleErrorWith { e: E -> f(e).fix().fa }(this) }

}

@extension
interface EnvFxMonad<R, E> : Monad<EnvFxPartialOf<R, E>>, EnvFxApplicative<R, E> {
  override fun <A, B> EnvFxOf<R, E, A>.flatMap(f: (A) -> EnvFxOf<R, E, B>): EnvFx<R, E, B> =
    EnvFx { fix().fa.flatMap { a: A -> f(a).fix().fa }(this) }

  override fun <A, B> tailRecM(a: A, f: (A) -> EnvFxOf<R, E, Either<A, B>>): EnvFx<R, E, B> =
    f(a).flatMap {
      it.fold(
        { x -> tailRecM(x, f) },
        { y -> y.just() }
      )
    }

  @Suppress("UNCHECKED_CAST")
  override fun <A, B> EnvFxOf<R, E, A>.ap(ff: EnvFxOf<R, E, (A) -> B>): EnvFx<R, E, B> =
    EnvFx {
      val result: Either<E, (A) -> B> = ff.fix().fa(this)
      when (result) {
        is Either.Left -> this@ap.fix().fa(this) as Either<E, B>
        is Either.Right -> fix().fa.ap { result.b }(this)
      }
    }

  override fun <A, B> EnvFxOf<R, E, A>.map(f: (A) -> B): EnvFx<R, E, B> =
    EnvFx { fix().fa.map(f)(this) }

}

@extension
interface EnvFxMonadError<R, E> : MonadError<EnvFxPartialOf<R, E>, E>, EnvFxApplicativeError<R, E>, EnvFxMonad<R, E>

@extension
interface EnvFxMonadThrow<R, E> : MonadThrow<EnvFxPartialOf<R, E>>, EnvFxMonad<R, E> {
  override fun <A> raiseError(e: Throwable): EnvFx<R, E, A> =
    EnvFx { throw RaisedError(e) }

  override fun <A> EnvFxOf<R, E, A>.handleErrorWith(f: (Throwable) -> EnvFxOf<R, E, A>): EnvFx<R, E, A> =
    EnvFx { fix().fa.handleErrorWith { t: Throwable -> f(t).fix().fa }(this) }
}

@extension
interface EnvFxBracket<R, E> : Bracket<EnvFxPartialOf<R, E>, Throwable>, EnvFxMonadThrow<R, E> {
  override fun <A, B> EnvFxOf<R, E, A>.bracketCase(release: (A, ExitCase<Throwable>) -> EnvFxOf<R, E, Unit>, use: (A) -> EnvFxOf<R, E, B>): EnvFx<R, E, B> =
    EnvFx {
      fix().fa.bracketCase(
        release.curry().andThen { a -> a.andThen { b -> b.fix().fa } }.uncurried(),
        use.andThen { a -> a.fix().fa }
      )(this)
    }
}

@extension
interface EnvFxMonadDefer<R, E> : MonadDefer<EnvFxPartialOf<R, E>>, EnvFxBracket<R, E> {
  override fun <A> defer(fa: () -> EnvFxOf<R, E, A>): EnvFx<R, E, A> =
    unit().flatMap { fa() }
}

@extension
interface EnvFxAsync<R, E> : Async<EnvFxPartialOf<R, E>>, EnvFxMonadDefer<R, E> {

  override fun <A> async(fa: Proc<A>): EnvFx<R, E, A> =
    EnvFx { (arrow.effects.suspended.fx.fromAsync(fa))().right() }

  override fun <A> asyncF(k: ProcF<EnvFxPartialOf<R, E>, A>): EnvFx<R, E, A> =
    EnvFx { fromAsyncF(k)(this).right() }

  override fun <A> EnvFxOf<R, E, A>.continueOn(ctx: CoroutineContext): EnvFx<R, E, A> =
    EnvFx { ctx.continueOn(suspend { fix().fa(this) })() }

}

@extension
interface EnvFxDispatchers<R, E> : Dispatchers<EnvFxPartialOf<R, E>> {
  override fun default(): CoroutineContext =
    NonBlocking

  override fun trampoline(): CoroutineContext =
    TrampolinePool
}

@extension
interface EnvFxConcurrent<R, E> : Concurrent<EnvFxPartialOf<R, E>>, EnvFxAsync<R, E> {
  override fun dispatchers(): Dispatchers<EnvFxPartialOf<R, E>> =
    object : EnvFxDispatchers<R, E> {}

  override fun <A> async(fa: Proc<A>): EnvFx<R, E, A> =
    EnvFx { (arrow.effects.suspended.fx.fromAsync(fa))().right() }

  override fun <A> asyncF(k: ProcF<EnvFxPartialOf<R, E>, A>): EnvFx<R, E, A> =
    EnvFx { fromAsyncF(k)(this).right() }

  override fun <A> EnvFxOf<R, E, A>.continueOn(ctx: CoroutineContext): EnvFx<R, E, A> =
    EnvFx { ctx.continueOn(suspend { fix().fa(this) })() }

  override fun <A> CoroutineContext.startFiber(kind: EnvFxOf<R, E, A>): EnvFx<R, E, Fiber<EnvFxPartialOf<R, E>, A>> =
    EnvFx {
      val promise = UnsafePromise<Either<E, A>>()
      val conn = EnvFxConnection<R, E>(this)
      suspend { kind.fix().fa(this) }.startCoroutine(asyncContinuation(this@startFiber) { either ->
        either.fold(
          { promise.complete(it.left()) },
          {
            promise.complete(it.right())
          }
        )
      })
      EnvFxFiber(promise, conn).right()
    }

  override fun <A> asyncF(fa: ConnectedProcF<EnvFxPartialOf<R, E>, A>): EnvFx<R, E, A> =
    EnvFx { fromAsyncF(fa)(this).right() }

  override fun <A, B> CoroutineContext.racePair(fa: EnvFxOf<R, E, A>, fb: EnvFxOf<R, E, B>): EnvFx<R, E, RacePair<EnvFxPartialOf<R, E>, A, B>> =
    EnvFx { racePair(fa.fix().fa, fb.fix().fa)(this) }

  override fun <A, B, C> CoroutineContext.raceTriple(fa: EnvFxOf<R, E, A>, fb: EnvFxOf<R, E, B>, fc: EnvFxOf<R, E, C>): EnvFx<R, E, RaceTriple<EnvFxPartialOf<R, E>, A, B, C>> =
    EnvFx { raceTriple(fa.fix().fa, fb.fix().fa, fc.fix().fa)(this) }

}

@extension
interface EnvFxFx<R, E> : Fx<EnvFxPartialOf<R, E>> {
  override fun concurrent(): Concurrent<EnvFxPartialOf<R, E>> =
    object : EnvFxConcurrent<R, E> {}
}

internal fun <R, E, A> EnvFxFiber(
  promise: UnsafePromise<Either<E, A>>,
  conn: KindConnection<EnvFxPartialOf<R, E>>
): Fiber<EnvFxPartialOf<R, E>, A> {
  val join: EnvFx<R, E, A> = EnvFx {
    fromAsync<R, E, A> { conn2: KindConnection<EnvFxPartialOf<R, E>>, cb ->
      conn2.push(EnvFx { promise.remove(cb).right() })
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
Either<E, RacePair<EnvFxPartialOf<R, E>, A, B>> = {
  fromAsync<R, E, RacePair<EnvFxPartialOf<R, E>, A, B>> { conn: KindConnection<EnvFxPartialOf<R, E>>, cb ->
    val active: AtomicBoolean = AtomicBoolean(true)
    val upstreamCancelToken: EnvFx<R, E, Unit> = if (conn.isCanceled()) EnvFx.unit() else conn.cancel().fix()

    val connA: KindConnection<EnvFxPartialOf<R, E>> = EnvFxConnection(this)
    connA.push(upstreamCancelToken)
    val promiseA: UnsafePromise<Either<E, A>> = UnsafePromise()

    val connB: KindConnection<EnvFxPartialOf<R, E>> = EnvFxConnection(this)
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
          val fiber = EnvFxFiber(promiseB, connB)
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
          val fiber = EnvFxFiber(promiseA, connA)
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
): suspend R.() -> Either<E, RaceTriple<EnvFxPartialOf<R, E>, A, B, C>> = {
  fromAsync { conn: KindConnection<EnvFxPartialOf<R, E>>,
                                           cb: (Either<Throwable, Either<E, RaceTriple<EnvFxPartialOf<R, E>, A, B, C>>>) -> Unit ->

    val active = AtomicBoolean(true)

    val upstreamCancelToken: EnvFx<R, E, Unit> = if (conn.isCanceled()) EnvFx.unit() else conn.cancel().fix()

    val connA = EnvFxConnection<R, E>(this)
    connA.push(upstreamCancelToken)
    val promiseA = UnsafePromise<Either<E, A>>()

    val connB = EnvFxConnection<R, E>(this)
    connB.push(upstreamCancelToken)
    val promiseB = UnsafePromise<Either<E, B>>()

    val connC = EnvFxConnection<R, E>(this)
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
          val tuple = a.map { Tuple3(it, EnvFxFiber(promiseB, connB), EnvFxFiber(promiseC, connC)).left() }
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
          cb(b.map { Right(Left(Tuple3(EnvFxFiber(promiseA, connA), it, EnvFxFiber(promiseC, connC)))) }.right())
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
          cb(c.map { Tuple3(EnvFxFiber(promiseA, connA), EnvFxFiber(promiseB, connB), it).right().right() }.right())
        } else {
          promiseC.complete(Right(c))
        }
      })
    })

  }(this)
}
