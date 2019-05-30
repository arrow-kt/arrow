package arrow.effects.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.IOOf
import arrow.effects.RacePair
import arrow.effects.RaceTriple
import arrow.effects.fix
import arrow.effects.racePair
import arrow.effects.raceTriple
import arrow.effects.toIOProc
import arrow.effects.toIOProcF
import arrow.effects.typeclasses.Async
import arrow.effects.typeclasses.Bracket
import arrow.effects.typeclasses.Concurrent
import arrow.effects.typeclasses.ConcurrentEffect
import arrow.effects.typeclasses.ConnectedProc
import arrow.effects.typeclasses.ConnectedProcF
import arrow.effects.typeclasses.Dispatchers
import arrow.effects.typeclasses.Disposable
import arrow.effects.typeclasses.Effect
import arrow.effects.typeclasses.ExitCase
import arrow.effects.typeclasses.Fiber
import arrow.effects.typeclasses.MonadDefer
import arrow.effects.typeclasses.Proc
import arrow.effects.typeclasses.ProcF
import arrow.effects.Timer
import arrow.effects.typeclasses.AsyncSyntax
import arrow.effects.typeclasses.AsyncContinuation
import arrow.effects.typeclasses.AsyncFx
import arrow.effects.typeclasses.ConcurrentSyntax
import arrow.effects.typeclasses.ConcurrentContinuation
import arrow.effects.typeclasses.ConcurrentFx
import arrow.effects.typeclasses.UnsafeRun
import arrow.extension
import arrow.typeclasses.Applicative
import arrow.typeclasses.Apply
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.BindingStrategy
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadSyntax
import arrow.typeclasses.MonadContinuation
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadFx
import arrow.typeclasses.MonadThrow
import arrow.typeclasses.MonadThrowSyntax
import arrow.typeclasses.MonadThrowContinuation
import arrow.typeclasses.MonadThrowFx
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.unsafe
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import arrow.effects.handleErrorWith as ioHandleErrorWith
import arrow.effects.handleError as ioHandleError

@extension
interface IOFunctor : Functor<ForIO> {
  override fun <A, B> IOOf<A>.map(f: (A) -> B): IO<B> =
    fix().map(f)
}

@extension
interface IOApply : Apply<ForIO> {
  override fun <A, B> IOOf<A>.map(f: (A) -> B): IO<B> =
    fix().map(f)

  override fun <A, B> IOOf<A>.ap(ff: IOOf<(A) -> B>): IO<B> =
    fix().ap(ff)
}

@extension
interface IOApplicative : Applicative<ForIO> {
  override fun <A, B> IOOf<A>.map(f: (A) -> B): IO<B> =
    fix().map(f)

  override fun <A> just(a: A): IO<A> =
    IO.just(a)

  override fun <A, B> IOOf<A>.ap(ff: IOOf<(A) -> B>): IO<B> =
    fix().ap(ff)
}

@extension
interface IOMonad : Monad<ForIO> {
  override fun <A, B> IOOf<A>.flatMap(f: (A) -> IOOf<B>): IO<B> =
    fix().flatMap(f)

  override fun <A, B> IOOf<A>.map(f: (A) -> B): IO<B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> IOOf<Either<A, B>>): IO<B> =
    IO.tailRecM(a, f)

  override fun <A> just(a: A): IO<A> =
    IO.just(a)

  override fun <A> MonadContinuation<ForIO, *>.bindStrategy(fa: Kind<ForIO, A>): BindingStrategy<ForIO, A> = BindingStrategy.Suspend {
    fa.fix().suspended()
  }

  override val fx: MonadFx<ForIO>
    get() = object : MonadFx<ForIO> {
      override val M: Monad<ForIO> = this@IOMonad
      override fun <A> monad(c: suspend MonadSyntax<ForIO>.() -> A): IO<A> = IO.async { _, cb ->
        val continuation = MonadContinuation<ForIO, A>(M)
        suspend { c(continuation) }.startCoroutine(Continuation(EmptyCoroutineContext) { r ->
          r.fold({ cb(Right(it)) }, { cb(Left(it)) })
        })
      }
    }
}

@extension
interface IOApplicativeError : ApplicativeError<ForIO, Throwable>, IOApplicative {
  override fun <A> IOOf<A>.attempt(): IO<Either<Throwable, A>> =
    fix().attempt()

  override fun <A> IOOf<A>.handleErrorWith(f: (Throwable) -> IOOf<A>): IO<A> =
    ioHandleErrorWith(f)

  override fun <A> IOOf<A>.handleError(f: (Throwable) -> A): IO<A> =
    ioHandleError(f)

  override fun <A, B> IOOf<A>.redeem(fe: (Throwable) -> B, fb: (A) -> B): IO<B> =
    fix().redeem(fe, fb)

  override fun <A> raiseError(e: Throwable): IO<A> =
    IO.raiseError(e)
}

@extension
interface IOMonadError : MonadError<ForIO, Throwable>, IOApplicativeError, IOMonad {

  override fun <A> just(a: A): IO<A> = IO.just(a)

  override fun <A, B> IOOf<A>.ap(ff: IOOf<(A) -> B>): IO<B> =
    fix().ap(ff)

  override fun <A, B> IOOf<A>.map(f: (A) -> B): IO<B> =
    fix().map(f)

  override fun <A> IOOf<A>.attempt(): IO<Either<Throwable, A>> =
    fix().attempt()

  override fun <A> IOOf<A>.handleErrorWith(f: (Throwable) -> IOOf<A>): IO<A> =
    ioHandleErrorWith(f)

  override fun <A, B> IOOf<A>.redeemWith(fe: (Throwable) -> IOOf<B>, fb: (A) -> IOOf<B>): IO<B> =
    fix().redeemWith(fe, fb)

  override fun <A> raiseError(e: Throwable): IO<A> =
    IO.raiseError(e)
}

@extension
interface IOMonadThrow : MonadThrow<ForIO>, IOMonadError {
  override val fx: MonadThrowFx<ForIO>
    get() = object : MonadThrowFx<ForIO> {
      override val ME: MonadThrow<ForIO> = this@IOMonadThrow
      override fun <A> monadThrow(c: suspend MonadThrowSyntax<ForIO>.() -> A): IO<A> = IO.async { _, cb ->
        val continuation = MonadThrowContinuation<ForIO, A>(ME)
        suspend { c(continuation) }.startCoroutine(Continuation(EmptyCoroutineContext) { r ->
          r.fold({ cb(Right(it)) }, { cb(Left(it)) })
        })
      }
    }
}

@extension
interface IOBracket : Bracket<ForIO, Throwable>, IOMonadThrow {
  override fun <A, B> IOOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> IOOf<Unit>, use: (A) -> IOOf<B>): IO<B> =
    fix().bracketCase(release, use)

  override fun <A, B> IOOf<A>.bracket(release: (A) -> IOOf<Unit>, use: (A) -> IOOf<B>): IO<B> =
    fix().bracket(release, use)

  override fun <A> IOOf<A>.guarantee(finalizer: IOOf<Unit>): IO<A> =
    fix().guarantee(finalizer)

  override fun <A> IOOf<A>.guaranteeCase(finalizer: (ExitCase<Throwable>) -> IOOf<Unit>): IO<A> =
    fix().guaranteeCase(finalizer)
}

@extension
interface IOMonadDefer : MonadDefer<ForIO>, IOBracket {
  override fun <A> defer(fa: () -> IOOf<A>): IO<A> =
    IO.defer(fa)

  override fun lazy(): IO<Unit> = IO.lazy
}

@extension
interface IOAsync : Async<ForIO>, IOMonadDefer {
  override fun <A> async(fa: Proc<A>): IO<A> =
    IO.async(fa.toIOProc())

  override fun <A> asyncF(k: ProcF<ForIO, A>): IO<A> =
    IO.asyncF(k.toIOProcF())

  override fun <A> IOOf<A>.continueOn(ctx: CoroutineContext): IO<A> =
    fix().continueOn(ctx)

  override fun <A> effect(ctx: CoroutineContext, f: suspend () -> A): IO<A> =
    IO.effect(ctx, f)

  override fun <A> effect(f: suspend () -> A): IO<A> =
    IO.effect(f)

  override val fx: AsyncFx<ForIO>
    get() = object : AsyncFx<ForIO> {
      override val async: Async<ForIO> = this@IOAsync
      override fun <A> async(c: suspend AsyncSyntax<ForIO>.() -> A): IO<A> = IO.async { _, cb ->
        val continuation = AsyncContinuation<ForIO, A>(async)
        suspend { c(continuation) }.startCoroutine(Continuation(EmptyCoroutineContext) { r ->
          r.fold({ cb(Right(it)) }, { cb(Left(it)) })
        })
      }
    }
}

// FIXME default @extension are temporarily declared in arrow-effects-io-extensions due to multiplatform needs
interface IOConcurrent : Concurrent<ForIO>, IOAsync {

  override fun <A> CoroutineContext.startFiber(kind: IOOf<A>): IO<Fiber<ForIO, A>> =
    kind.fix().startFiber(this)

  override fun <A> asyncF(fa: ConnectedProcF<ForIO, A>): IO<A> =
    IO.asyncF(fa)

  override fun <A> async(fa: ConnectedProc<ForIO, A>): IO<A> =
    IO.async(fa)

  override fun <A> asyncF(k: ProcF<ForIO, A>): IO<A> =
    IO.asyncF { _, cb -> k(cb) }

  override fun <A> async(fa: Proc<A>): IO<A> =
    IO.async { _, cb -> fa(cb) }

  override fun <A, B> CoroutineContext.racePair(fa: Kind<ForIO, A>, fb: Kind<ForIO, B>): IO<RacePair<ForIO, A, B>> =
    IO.racePair(this, fa, fb)

  override fun <A, B, C> CoroutineContext.raceTriple(fa: Kind<ForIO, A>, fb: Kind<ForIO, B>, fc: Kind<ForIO, C>): IO<RaceTriple<ForIO, A, B, C>> =
    IO.raceTriple(this, fa, fb, fc)

  override val fx: ConcurrentFx<ForIO>
    get() = object : ConcurrentFx<ForIO> {
      override val concurrent: Concurrent<ForIO> = this@IOConcurrent
      override fun <A> concurrent(c: suspend ConcurrentSyntax<ForIO>.() -> A): IO<A> = IO.async { _, cb ->
        val continuation = ConcurrentContinuation<ForIO, A>(concurrent)
        suspend { c(continuation) }.startCoroutine(Continuation(EmptyCoroutineContext) { r ->
          r.fold({ cb(Right(it)) }, { cb(Left(it)) })
        })
      }
    }
}

fun IO.Companion.concurrent(dispatchers: Dispatchers<ForIO>): Concurrent<ForIO> = object : IOConcurrent {
  override fun dispatchers(): Dispatchers<ForIO> = dispatchers
}

fun IO.Companion.timer(CF: Concurrent<ForIO>): Timer<ForIO> =
  Timer(CF)

@extension
interface IOEffect : Effect<ForIO>, IOAsync {
  override fun <A> IOOf<A>.runAsync(cb: (Either<Throwable, A>) -> IOOf<Unit>): IO<Unit> =
    fix().runAsync(cb)
}

// FIXME default @extension are temporarily declared in arrow-effects-io-extensions due to multiplatform needs
interface IOConcurrentEffect : ConcurrentEffect<ForIO>, IOEffect, IOConcurrent {

  override fun <A> IOOf<A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> IOOf<Unit>): IO<Disposable> =
    fix().runAsyncCancellable(OnCancel.ThrowCancellationException, cb)
}

fun IO.Companion.concurrentEffect(dispatchers: Dispatchers<ForIO>): ConcurrentEffect<ForIO> = object : IOConcurrentEffect {
  override fun dispatchers(): Dispatchers<ForIO> = dispatchers
}

@extension
interface IOSemigroup<A> : Semigroup<IO<A>> {

  fun SG(): Semigroup<A>

  override fun IO<A>.combine(b: IO<A>): IO<A> =
    flatMap { a1: A -> b.map { a2: A -> SG().run { a1.combine(a2) } } }
}

@extension
interface IOMonoid<A> : Monoid<IO<A>>, IOSemigroup<A> {

  override fun SG(): Semigroup<A> = SM()

  fun SM(): Monoid<A>

  override fun empty(): IO<A> = IO.just(SM().empty())
}

@extension
interface IOUnsafeRun : UnsafeRun<ForIO> {

  override suspend fun <A> unsafe.runBlocking(fa: () -> Kind<ForIO, A>): A = fa().fix().unsafeRunSync()

  override suspend fun <A> unsafe.runNonBlocking(fa: () -> Kind<ForIO, A>, cb: (Either<Throwable, A>) -> Unit) =
    fa().fix().unsafeRunAsync(cb)
}
