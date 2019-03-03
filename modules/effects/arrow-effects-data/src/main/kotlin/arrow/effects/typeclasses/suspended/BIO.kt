package arrow.effects.typeclasses.suspended

import arrow.Kind
import arrow.core.*
import arrow.effects.KindConnection
import arrow.effects.internal.Platform
import arrow.effects.internal.UnsafePromise
import arrow.effects.internal.asyncContinuation
import arrow.effects.typeclasses.*
import arrow.effects.typeclasses.suspended.bio.applicativeError.raiseError
import arrow.effects.typeclasses.suspended.bio.monad.flatMap
import arrow.extension
import arrow.typeclasses.*
import java.util.concurrent.CancellationException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

class ForBIO private constructor() {
  companion object
}
typealias BIOOf<E, A> = arrow.Kind2<ForBIO, E, A>
typealias BIOPartialOf<E> = arrow.Kind<ForBIO, E>
typealias BIOProc<E, A> = ConnectedProc<BIOPartialOf<E>, A>
typealias BIOProcF<E, A> = ConnectedProcF<BIOPartialOf<E>, A>

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

fun <A> A.just(): BIO<Nothing, A> =
  BIO { right() }

suspend fun <E, A, B> (suspend () -> Either<E, A>).map(f: (A) -> B): suspend () -> Either<E, B> =
  { !map(f) }

suspend fun <E, A, B> (suspend () -> Either<E, A>).mapLeft(f: (E) -> B): suspend () -> Either<B, A> =
  { !mapLeft(f) }

@Suppress("UNCHECKED_CAST")
suspend fun <E, A, B> (suspend () -> Either<E, A>).flatMap(f: (A) -> suspend () -> Either<E, B>): suspend () -> Either<E, B> = {
  when (val x = !this) {
    is Either.Left -> x.a.left()
    is Either.Right -> !f(x.b)
  }
}

suspend fun <E, A, B> (suspend () -> Either<E, A>).ap(ff: suspend () -> (A) -> B): suspend () -> Either<E, B> =
  map(ff())

suspend fun <E, A> attempt(
  fa: suspend () -> A,
  onError: (Throwable) -> E
): suspend () -> Either<E, A> =
  { !attempt(fa).mapLeft(onError) }

fun <E> E.raiseError(): suspend () -> Either<E, Nothing> =
  { left() }

suspend fun <E, A> (suspend () -> Either<E, A>).handleErrorWith(f: (E) -> suspend () -> Either<E, A>): suspend () -> Either<E, A> = {
  when (val result = !this) {
    is Either.Left -> !f(result.a)
    is Either.Right -> !this@handleErrorWith
  }
}

suspend fun <E, A> (suspend () -> Either<E, A>).handleError(f: (E) -> A): suspend () -> Either<E, A> = {
  when (val result = !this) {
    is Either.Left -> {
      f(result.a).right()
    }
    is Either.Right -> !this@handleError
  }
}

suspend fun <E, A> (suspend () -> Either<E, A>).ensure(
  error: () -> E,
  predicate: (A) -> Boolean
): suspend () -> Either<E, A> = {
  when (val result = !this) {
    is Either.Left -> !this@ensure
    is Either.Right -> if (predicate(result.b)) !this@ensure
    else {
      error().left()
    }
  }
}

suspend fun <E, A, B> (suspend () -> Either<E, A>).bracketCase(
  release: (A, ExitCase<Throwable>) -> suspend () -> Either<E, Unit>,
  use: (A) -> suspend () -> Either<E, B>
): suspend () -> Either<E, B> = {
  when (val result = !this) {
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

suspend fun <E, A> fromAsync(fa: BIOProc<E, A>): suspend () -> Either<E, A> =
  suspendCoroutine { continuation ->
    val conn = BIOConnection<E>()
    //Is CancellationException from kotlin in kotlinx package???
    conn.push(BIO { continuation.resumeWith(kotlin.Result.failure(CancellationException())).right() })
    fa(conn) { either ->
      continuation.resumeWith(
        either.fold(
          { kotlin.Result.failure<suspend () -> Either<E, A>>(it) },
          { kotlin.Result.success(suspend { it.right() }) }
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
    TODO()

  override fun <A, B> CoroutineContext.racePair(fa: BIOOf<E, A>, fb: BIOOf<E, B>): BIO<E, RacePair<BIOPartialOf<E>, A, B>> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun <A, B, C> CoroutineContext.raceTriple(fa: BIOOf<E, A>, fb: BIOOf<E, B>, fc: BIOOf<E, C>): BIO<E, RaceTriple<BIOPartialOf<E>, A, B, C>> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}

internal fun <E, A> BIOFiber(
  promise: UnsafePromise<Either<E, A>>,
  conn: KindConnection<BIOPartialOf<E>>
): Fiber<BIOPartialOf<E>, A> {
  val join: BIO<E, A> = BIO {
    fromAsync<E, Either<E, A>> { conn2: KindConnection<BIOPartialOf<E>>, cb ->
      conn2.push(BIO { promise.remove(cb).right() })
      conn.push(conn2.cancel())
      promise.get { a ->
        cb(a)
        conn2.pop()
        conn.pop()
      }
    }()
  }.flatMap {
    when (it) {
      is Either.Left -> it.a.raiseError<E, A>()
      is Either.Right -> it.b.just()
    }
  }
  return Fiber(join, conn.cancel())
}