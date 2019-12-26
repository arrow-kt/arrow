package arrow.fx.reactor.extensions

import arrow.Kind
import arrow.core.Either
import arrow.extension
import arrow.fx.Timer
import arrow.fx.reactor.ForMonoK
import arrow.fx.reactor.MonoK
import arrow.fx.reactor.MonoKOf
import arrow.fx.reactor.extensions.monok.async.async
import arrow.fx.reactor.fix
import arrow.fx.typeclasses.Async
import arrow.fx.typeclasses.AsyncSyntax
import arrow.fx.typeclasses.Bracket
import arrow.fx.typeclasses.ConcurrentEffect
import arrow.fx.typeclasses.Disposable
import arrow.fx.typeclasses.Duration
import arrow.fx.typeclasses.Effect
import arrow.fx.typeclasses.ExitCase
import arrow.fx.typeclasses.MonadDefer
import arrow.fx.typeclasses.Proc
import arrow.fx.typeclasses.ProcF
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadThrow
import reactor.core.publisher.Mono
import kotlin.coroutines.CoroutineContext
import arrow.fx.reactor.handleErrorWith as monoHandleErrorWith

@extension
interface MonoKFunctor : Functor<ForMonoK> {
  override fun <A, B> MonoKOf<A>.map(f: (A) -> B): MonoK<B> =
    fix().map(f)
}

@extension
interface MonoKApplicative : Applicative<ForMonoK>, MonoKFunctor {
  override fun <A, B> MonoKOf<A>.map(f: (A) -> B): MonoK<B> =
    fix().map(f)

  override fun <A, B> MonoKOf<A>.ap(ff: MonoKOf<(A) -> B>): MonoK<B> =
    fix().ap(ff)

  override fun <A> just(a: A): MonoK<A> =
    MonoK.just(a)

  override fun <A, B> Kind<ForMonoK, A>.lazyAp(ff: () -> Kind<ForMonoK, (A) -> B>): Kind<ForMonoK, B> =
    fix().flatMap { a -> ff().map { f -> f(a) } }
}

@extension
interface MonoKMonad : Monad<ForMonoK>, MonoKApplicative {
  override fun <A, B> MonoKOf<A>.map(f: (A) -> B): MonoK<B> =
    fix().map(f)

  override fun <A, B> MonoKOf<A>.ap(ff: MonoKOf<(A) -> B>): MonoK<B> =
    fix().ap(ff)

  override fun <A, B> MonoKOf<A>.flatMap(f: (A) -> MonoKOf<B>): MonoK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, MonoKOf<Either<A, B>>>): MonoK<B> =
    MonoK.tailRecM(a, f)

  override fun <A, B> Kind<ForMonoK, A>.lazyAp(ff: () -> Kind<ForMonoK, (A) -> B>): Kind<ForMonoK, B> =
    fix().flatMap { a -> ff().map { f -> f(a) } }
}

@extension
interface MonoKApplicativeError : ApplicativeError<ForMonoK, Throwable>, MonoKApplicative {
  override fun <A> raiseError(e: Throwable): MonoK<A> =
    MonoK.raiseError(e)

  override fun <A> MonoKOf<A>.handleErrorWith(f: (Throwable) -> MonoKOf<A>): MonoK<A> =
    fix().monoHandleErrorWith { f(it).fix() }
}

@extension
interface MonoKMonadError : MonadError<ForMonoK, Throwable>, MonoKMonad, MonoKApplicativeError {
  override fun <A, B> MonoKOf<A>.map(f: (A) -> B): MonoK<B> =
    fix().map(f)

  override fun <A> raiseError(e: Throwable): MonoK<A> =
    MonoK.raiseError(e)

  override fun <A> MonoKOf<A>.handleErrorWith(f: (Throwable) -> MonoKOf<A>): MonoK<A> =
    fix().monoHandleErrorWith { f(it).fix() }
}

@extension
interface MonoKMonadThrow : MonadThrow<ForMonoK>, MonoKMonadError

@extension
interface MonoKBracket : Bracket<ForMonoK, Throwable>, MonoKMonadThrow {
  override fun <A, B> MonoKOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> MonoKOf<Unit>, use: (A) -> MonoKOf<B>): MonoK<B> =
    fix().bracketCase({ use(it) }, { a, e -> release(a, e) })
}

@extension
interface MonoKMonadDefer : MonadDefer<ForMonoK>, MonoKBracket {
  override fun <A> defer(fa: () -> MonoKOf<A>): MonoK<A> =
    MonoK.defer(fa)
}

@extension
interface MonoKAsync : Async<ForMonoK>, MonoKMonadDefer {
  override fun <A> async(fa: Proc<A>): MonoK<A> =
    MonoK.async(fa)

  override fun <A> asyncF(k: ProcF<ForMonoK, A>): MonoK<A> =
    MonoK.asyncF(k)

  override fun <A> MonoKOf<A>.continueOn(ctx: CoroutineContext): MonoK<A> =
    fix().continueOn(ctx)
}

@extension
interface MonoKEffect : Effect<ForMonoK>, MonoKAsync {
  override fun <A> MonoKOf<A>.runAsync(cb: (Either<Throwable, A>) -> MonoKOf<Unit>): MonoK<Unit> =
    fix().runAsync(cb)
}

@extension
interface MonoKConcurrentEffect : ConcurrentEffect<ForMonoK>, MonoKEffect {
  override fun <A> MonoKOf<A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> MonoKOf<Unit>): MonoK<Disposable> =
    fix().runAsyncCancellable(cb)
}

@extension
interface MonoKTimer : Timer<ForMonoK> {
  override fun sleep(duration: Duration): MonoK<Unit> =
    MonoK(Mono.delay(java.time.Duration.ofNanos(duration.nanoseconds))
      .map { Unit })
}

// TODO FluxK does not yet have a Concurrent instance
fun <A> MonoK.Companion.fx(c: suspend AsyncSyntax<ForMonoK>.() -> A): MonoK<A> =
  defer { MonoK.async().fx.async(c).fix() }
