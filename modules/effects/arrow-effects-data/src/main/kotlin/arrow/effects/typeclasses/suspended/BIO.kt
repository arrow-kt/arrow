package arrow.effects.typeclasses.suspended

import arrow.core.*
import arrow.effects.KindConnection
import arrow.effects.internal.Platform
import arrow.effects.internal.UnsafePromise
import arrow.effects.internal.asyncContinuation
import arrow.effects.typeclasses.*
import arrow.effects.typeclasses.suspended.bio.monad.flatMap
import arrow.effects.typeclasses.suspended.concurrent.Fx
import arrow.extension
import arrow.typeclasses.*
import arrow.unsafe
import java.util.concurrent.CancellationException
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.*

class ForBIO private constructor() {
  companion object
}
typealias BIOOf<E, A> = arrow.Kind2<ForBIO, E, A>
typealias BIOPartialOf<E> = arrow.Kind<ForBIO, E>
typealias BIOProc<E, A> = ConnectedProc<BIOPartialOf<E>, A>
typealias BIOProcF<E, A> = ConnectedProcF<BIOPartialOf<E>, A>
typealias BIOConnectedProc<E, A> = (KindConnection<BIOPartialOf<E>>, ((Either<Throwable, Either<E, A>>) -> Unit)) -> Unit

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <E, A> BIOOf<E, A>.fix(): BIO<E, A> =
  this as BIO<E, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <E, A> (suspend () -> Either<E, A>).k(): BIO<E, A> =
  BIO(this)

class BIO<out E, out A>(internal val fa: suspend () -> Either<E, A>) : BIOOf<E, A> {
  companion object {
    val unit: BIO<Nothing, Unit> = BIO { Unit.right() }

    fun <A> just(a: A): BIO<Nothing, A> =
      BIO { a.right() }
  }
}

suspend operator fun <E, A> BIOOf<Nothing, A>.invoke(): Either<E, A> =
  fix().fa.invoke()

fun <A> A.just(): BIO<Nothing, A> =
  BIO { right() }

suspend fun <E, A, B> (suspend () -> Either<E, A>).map(f: (A) -> B): suspend () -> Either<E, B> =
  { this().map(f) }

suspend fun <E, A, B> (suspend () -> Either<E, A>).mapLeft(f: (E) -> B): suspend () -> Either<B, A> =
  { mapLeft(f)() }

@Suppress("UNCHECKED_CAST")
suspend fun <E, A, B> (suspend () -> Either<E, A>).flatMap(f: (A) -> suspend () -> Either<E, B>): suspend () -> Either<E, B> = {
  when (val x = this()) {
    is Either.Left -> x.a.left()
    is Either.Right -> f(x.b)()
  }
}

suspend fun <E, A, B> (suspend () -> Either<E, A>).ap(ff: suspend () -> (A) -> B): suspend () -> Either<E, B> =
  map(ff())

suspend fun <E, A> attempt(
  fa: suspend () -> A,
  onError: (Throwable) -> E
): suspend () -> Either<E, A> =
  { attempt(fa).mapLeft(onError)() }

fun <E> E.raiseError(): suspend () -> Either<E, Nothing> =
  { left() }

suspend fun <E, A> (suspend () -> Either<E, A>).handleErrorWith(f: (E) -> suspend () -> Either<E, A>): suspend () -> Either<E, A> = {
  when (val result = this()) {
    is Either.Left -> f(result.a)()
    is Either.Right -> this@handleErrorWith()
  }
}

suspend fun <E, A> (suspend () -> Either<E, A>).handleError(f: (E) -> A): suspend () -> Either<E, A> = {
  when (val result = this()) {
    is Either.Left -> {
      f(result.a).right()
    }
    is Either.Right -> this@handleError()
  }
}

suspend fun <E, A> (suspend () -> Either<E, A>).ensure(
  error: () -> E,
  predicate: (A) -> Boolean
): suspend () -> Either<E, A> = {
  when (val result = this()) {
    is Either.Left -> this@ensure()
    is Either.Right -> if (predicate(result.b)) this@ensure()
    else {
      error().left()
    }
  }
}

suspend fun <E, A, B> (suspend () -> Either<E, A>).bracketCase(
  release: (A, ExitCase<Throwable>) -> suspend () -> Either<E, Unit>,
  use: (A) -> suspend () -> Either<E, B>
): suspend () -> Either<E, B> = {
  when (val result = this()) {
    is Either.Left -> result.a.left()
    is Either.Right -> {
      val a = result.b
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
      release(a, ExitCase.Completed)()
      b
    }
  }

}

fun <E> BIOConnection(): KindConnection<BIOPartialOf<E>> =
  KindConnection(object : BIOMonadDefer<E> {}) { it.fix().fa.foldContinuation { e -> throw e } }

suspend fun <E, A> fromAsync(fa: BIOConnectedProc<E, A>): suspend () -> Either<E, A> =
  suspendCoroutine { continuation ->
    val conn = BIOConnection<E>()
    //Is CancellationException from kotlin in kotlinx package???
    conn.push(BIO { continuation.resumeWith(kotlin.Result.failure(CancellationException())).right() })
    fa(conn) { either ->
      continuation.resumeWith(
        either.fold(
          {
            kotlin.Result.failure<suspend () -> Either<E, A>>(it)
          },
          { kotlin.Result.success(suspend { it }) }
        )
      )
    }
  }

/** Hide member because it's discouraged to use uncancelable builder for cancelable concrete type **/
internal fun <E, A> fromAsyncF(fa: ProcF<BIOPartialOf<E>, A>): suspend () -> A = {
  suspendCoroutine { continuation ->
    fa { either ->
      continuation.resumeWith(either.fold({ kotlin.Result.failure<A>(it) }, { kotlin.Result.success(it) }))
    }.fix().fa.foldContinuation(EmptyCoroutineContext, mapUnit)
  }
}

@extension
interface BIOFunctor<E> : Functor<BIOPartialOf<E>> {
  override fun <A, B> BIOOf<E, A>.map(f: (A) -> B): BIO<E, B> =
    BIO { !fix().fa.map(f) }
}

@extension
interface BIOApplicative<E> : Applicative<BIOPartialOf<E>>, BIOFunctor<E> {
  override fun <A> just(a: A): BIO<E, A> =
    BIO.just(a)

  @Suppress("UNCHECKED_CAST")
  override fun <A, B> BIOOf<E, A>.ap(ff: BIOOf<E, (A) -> B>): BIO<E, B> =
    BIO {
      val result = !ff.fix().fa
      !when (result) {
        is Either.Left -> this@ap.fix().fa as (suspend () -> Either<E, B>)
        is Either.Right -> fix().fa.ap<E, A, B> { result.b }
      }
    }

  override fun <A, B> BIOOf<E, A>.map(f: (A) -> B): BIO<E, B> =
    BIO { !fix().fa.map(f) }
}

@extension
interface BIOApplicativeError<E> : ApplicativeError<BIOPartialOf<E>, E>, BIOApplicative<E> {
  override fun <A> raiseError(e: E): BIO<E, A> =
    BIO { e.left() }

  override fun <A> BIOOf<E, A>.handleErrorWith(f: (E) -> BIOOf<E, A>): BIO<E, A> =
    BIO { !fix().fa.handleErrorWith { e: E -> f(e).fix().fa } }

}

@extension
interface BIOMonad<E> : Monad<BIOPartialOf<E>>, BIOApplicative<E> {
  override fun <A, B> BIOOf<E, A>.flatMap(f: (A) -> BIOOf<E, B>): BIO<E, B> =
    BIO { !fix().fa.flatMap { a: A -> f(a).fix().fa } }

  override fun <A, B> tailRecM(a: A, f: (A) -> BIOOf<E, Either<A, B>>): BIO<E, B> =
    f(a).flatMap {
      it.fold(
        { x -> tailRecM(x, f) },
        { y -> y.just() }
      )
    }

  @Suppress("UNCHECKED_CAST")
  override fun <A, B> BIOOf<E, A>.ap(ff: BIOOf<E, (A) -> B>): BIO<E, B> =
    BIO {
      val result = !ff.fix().fa
      !when (result) {
        is Either.Left -> this@ap.fix().fa as (suspend () -> Either<E, B>)
        is Either.Right -> fix().fa.ap<E, A, B> { result.b }
      }
    }

  override fun <A, B> BIOOf<E, A>.map(f: (A) -> B): BIO<E, B> =
    BIO { !fix().fa.map(f) }

}

@extension
interface BIOMonadError<E> : MonadError<BIOPartialOf<E>, E>, BIOApplicativeError<E>, BIOMonad<E>

@extension
interface BIOMonadThrow<E> : MonadThrow<BIOPartialOf<E>>, BIOMonad<E> {
  override fun <A> raiseError(e: Throwable): BIO<E, A> =
    BIO { throw RaisedError(e) }

  override fun <A> BIOOf<E, A>.handleErrorWith(f: (Throwable) -> BIOOf<E, A>): BIO<E, A> =
    BIO { fix().fa.handleErrorWith { t: Throwable -> f(t).fix().fa }() }
}

@extension
interface BIOBracket<E> : Bracket<BIOPartialOf<E>, Throwable>, BIOMonadThrow<E> {
  override fun <A, B> BIOOf<E, A>.bracketCase(release: (A, ExitCase<Throwable>) -> BIOOf<E, Unit>, use: (A) -> BIOOf<E, B>): BIO<E, B> =
    BIO {
      fix().fa.bracketCase(
        release.curry().andThen { a -> a.andThen { b -> b.fix().fa } }.uncurried(),
        use.andThen { a -> a.fix().fa }
      )()
    }
}

@extension
interface BIOMonadDefer<E> : MonadDefer<BIOPartialOf<E>>, BIOBracket<E> {
  override fun <A> defer(fa: () -> BIOOf<E, A>): BIO<E, A> =
    unit().flatMap { fa() }
}

@extension
interface BIOAsync<E> : Async<BIOPartialOf<E>>, BIOMonadDefer<E> {

  override fun <A> async(fa: Proc<A>): BIO<E, A> =
    BIO { fromAsync(fa)().right() }

  override fun <A> asyncF(k: ProcF<BIOPartialOf<E>, A>): BIO<E, A> =
    BIO { fromAsyncF(k)().right() }

  override fun <A> BIOOf<E, A>.continueOn(ctx: CoroutineContext): BIO<E, A> =
    BIO { ctx.continueOn(fix().fa)() }

}

@extension
interface BIODispatchers<E> : Dispatchers<BIOPartialOf<E>> {
  override fun default(): CoroutineContext =
    NonBlocking
}

@extension
interface BIOConcurrent<E> : Concurrent<BIOPartialOf<E>>, BIOAsync<E> {
  override fun dispatchers(): Dispatchers<BIOPartialOf<E>> =
    object : BIODispatchers<E> {}

  override fun <A> async(fa: Proc<A>): BIO<E, A> =
    BIO { fromAsync(fa)().right() }

  override fun <A> asyncF(k: ProcF<BIOPartialOf<E>, A>): BIO<E, A> =
    BIO { fromAsyncF(k)().right() }

  override fun <A> BIOOf<E, A>.continueOn(ctx: CoroutineContext): BIO<E, A> =
    BIO { ctx.continueOn(fix().fa)() }

  override fun <A> CoroutineContext.startFiber(kind: BIOOf<E, A>): BIO<E, Fiber<BIOPartialOf<E>, A>> {
    val promise = UnsafePromise<Either<E, A>>()
    val conn = BIOConnection<E>()
    kind.fix().fa.startCoroutine(asyncContinuation(this) { either ->
      either.fold(
        { promise.complete(it.left()) },
        {
          promise.complete(it.right())
        }
      )
    })
    return BIO { BIOFiber(promise, conn).right() }
  }

  override fun <A> asyncF(fa: ConnectedProcF<BIOPartialOf<E>, A>): BIO<E, A> =
    BIO { fromAsyncF(fa)().right() }

  override fun <A, B> CoroutineContext.racePair(fa: BIOOf<E, A>, fb: BIOOf<E, B>): BIO<E, RacePair<BIOPartialOf<E>, A, B>> =
    BIO { !racePair(fa.fix().fa, fb.fix().fa) }

  override fun <A, B, C> CoroutineContext.raceTriple(fa: BIOOf<E, A>, fb: BIOOf<E, B>, fc: BIOOf<E, C>): BIO<E, RaceTriple<BIOPartialOf<E>, A, B, C>> =
    BIO { !raceTriple(fa.fix().fa, fb.fix().fa, fc.fix().fa) }

}

@extension
interface BIOFx<E> : Fx<BIOPartialOf<E>> {
  override fun concurrent(): Concurrent<BIOPartialOf<E>> =
    object : BIOConcurrent<E> {}
}

@extension
interface BIOUnsafeRun<E> : UnsafeRun<BIOPartialOf<E>> {
  override suspend fun <A> unsafe.runBlocking(fa: () -> BIOOf<E, A>): A =
    BlockingCoroutine<Either<E, A>>(EmptyCoroutineContext).also { fa().fix().fa.startCoroutine(it) }.getValue().getOrHandle { throw IllegalStateException("Unhandled case: $it") }

  override suspend fun <A> unsafe.runNonBlocking(fa: () -> BIOOf<E, A>, cb: (Either<Throwable, A>) -> Unit) {
    fa().fix()
      .fa
      .startCoroutine(asyncContinuation(NonBlocking){ acb: Either<Throwable, Either<E, A>> ->
        when (acb) {
          is Either.Left -> cb(Left(acb.a))
          is Either.Right -> when (val result = acb.b) {
            is Either.Left -> cb(Left(IllegalStateException("BIO Unhandled case: ${result.a}")))
            is Either.Right -> cb(Right(result.b))
          }
        }
      })
  }

}

internal fun <E, A> BIOFiber(
  promise: UnsafePromise<Either<E, A>>,
  conn: KindConnection<BIOPartialOf<E>>
): Fiber<BIOPartialOf<E>, A> {
  val join: BIO<E, A> = BIO {
    fromAsync<E, A> { conn2: KindConnection<BIOPartialOf<E>>, cb ->
      conn2.push(BIO { promise.remove(cb).right() })
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

fun <E, A> fromAsyncF(fa: BIOProcF<E, A>): suspend () -> A = {
  suspendCoroutine { continuation ->
    val conn = BIOConnection<E>()
    //Is CancellationException from kotlin in kotlinx package???
    conn.push(BIO { continuation.resumeWith(kotlin.Result.failure(CancellationException())).right() })
    fa(conn) { either ->
      continuation.resumeWith(either.fold({ kotlin.Result.failure<A>(it) }, { kotlin.Result.success(it) }))
    }.fix().fa.foldContinuation(EmptyCoroutineContext, mapUnit)
  }
}

suspend fun <E, A, B> CoroutineContext.racePair(
  fa: suspend () -> Either<E, A>,
  fb: suspend () -> Either<E, B>
): suspend () ->
Either<E, RacePair<BIOPartialOf<E>, A, B>> = {
  fromAsync<E, RacePair<BIOPartialOf<E>, A, B>> { conn: KindConnection<BIOPartialOf<E>>, cb ->
    val active: AtomicBoolean = AtomicBoolean(true)
    val upstreamCancelToken: BIO<E, Unit> = BIO.unit.flatMap { if (conn.isCanceled()) BIO.unit else conn.cancel() }

    val connA: KindConnection<BIOPartialOf<E>> = BIOConnection()
    connA.push(upstreamCancelToken)
    val promiseA: UnsafePromise<Either<E, A>> = UnsafePromise()

    val connB: KindConnection<BIOPartialOf<E>> = BIOConnection()
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
          val fiber = BIOFiber(promiseB, connB)
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
          val fiber = BIOFiber(promiseA, connA)
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
): suspend () -> Either<E, RaceTriple<BIOPartialOf<E>, A, B, C>> =
  fromAsync { conn: KindConnection<BIOPartialOf<E>>,
              cb: (Either<Throwable, Either<E, RaceTriple<BIOPartialOf<E>, A, B, C>>>) -> Unit ->

    val active = AtomicBoolean(true)

    val upstreamCancelToken = BIO.unit.flatMap { if (conn.isCanceled()) BIO.unit else conn.cancel() }

    val connA = BIOConnection<E>()
    connA.push(upstreamCancelToken)
    val promiseA = UnsafePromise<Either<E, A>>()

    val connB = BIOConnection<E>()
    connB.push(upstreamCancelToken)
    val promiseB = UnsafePromise<Either<E, B>>()

    val connC = BIOConnection<E>()
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
          val tuple = a.map { Tuple3(it, BIOFiber(promiseB, connB), BIOFiber(promiseC, connC)).left() }
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
          cb(b.map { Right(Left(Tuple3(BIOFiber(promiseA, connA), it, BIOFiber(promiseC, connC)))) }.right())
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
          cb(c.map { Tuple3(BIOFiber(promiseA, connA), BIOFiber(promiseB, connB), it).right().right() }.right())
        } else {
          promiseC.complete(Right(c))
        }
      })
    })

  }