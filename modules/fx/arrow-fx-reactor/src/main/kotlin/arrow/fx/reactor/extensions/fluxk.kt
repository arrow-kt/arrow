package arrow.fx.reactor.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Option
import arrow.core.Tuple2
import arrow.fx.Timer
import arrow.fx.reactor.FluxK
import arrow.fx.reactor.FluxKOf
import arrow.fx.reactor.ForFluxK
import arrow.fx.reactor.extensions.fluxk.async.async
import arrow.fx.reactor.extensions.fluxk.monad.monad
import arrow.fx.reactor.extensions.fluxk.monadError.monadError
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
import arrow.extension
import arrow.fx.reactor.k
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.FunctorFilter
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadFilter
import arrow.typeclasses.MonadThrow
import arrow.typeclasses.Traverse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.publisher.toFlux
import kotlin.coroutines.CoroutineContext
import arrow.fx.reactor.handleErrorWith as fluxHandleErrorWith

@extension
interface FluxKFunctor : Functor<ForFluxK> {
  override fun <A, B> FluxKOf<A>.map(f: (A) -> B): FluxK<B> =
    fix().map(f)
}

@extension
interface FluxKApplicative : Applicative<ForFluxK> {
  override fun <A> just(a: A): FluxK<A> =
    FluxK.just(a)

  override fun <A, B> FluxKOf<A>.ap(ff: FluxKOf<(A) -> B>): FluxK<B> =
    fix().ap(ff)

  override fun <A, B> FluxKOf<A>.map(f: (A) -> B): FluxK<B> =
    fix().map(f)
}

@extension
interface FluxKMonad : Monad<ForFluxK> {
  override fun <A, B> FluxKOf<A>.ap(ff: FluxKOf<(A) -> B>): FluxK<B> =
    fix().ap(ff)

  override fun <A, B> FluxKOf<A>.flatMap(f: (A) -> FluxKOf<B>): FluxK<B> =
    fix().flatMap(f)

  override fun <A, B> FluxKOf<A>.map(f: (A) -> B): FluxK<B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, FluxKOf<arrow.core.Either<A, B>>>): FluxK<B> =
    FluxK.tailRecM(a, f)

  override fun <A> just(a: A): FluxK<A> =
    FluxK.just(a)
}

@extension
interface FluxKFoldable : Foldable<ForFluxK> {
  override fun <A, B> FluxKOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> FluxKOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): arrow.core.Eval<B> =
    fix().foldRight(lb, f)
}

@extension
interface FluxKTraverse : Traverse<ForFluxK> {
  override fun <A, B> FluxKOf<A>.map(f: (A) -> B): FluxK<B> =
    fix().map(f)

  override fun <G, A, B> FluxKOf<A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, FluxK<B>> =
    fix().traverse(AP, f)

  override fun <A, B> FluxKOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> FluxKOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): arrow.core.Eval<B> =
    fix().foldRight(lb, f)
}

@extension
interface FluxKApplicativeError :
  ApplicativeError<ForFluxK, Throwable>,
  FluxKApplicative {
  override fun <A> raiseError(e: Throwable): FluxK<A> =
    FluxK.raiseError(e)

  override fun <A> FluxKOf<A>.handleErrorWith(f: (Throwable) -> FluxKOf<A>): FluxK<A> =
    fix().fluxHandleErrorWith { f(it).fix() }
}

@extension
interface FluxKMonadError :
  MonadError<ForFluxK, Throwable>,
  FluxKMonad {
  override fun <A> raiseError(e: Throwable): FluxK<A> =
    FluxK.raiseError(e)

  override fun <A> FluxKOf<A>.handleErrorWith(f: (Throwable) -> FluxKOf<A>): FluxK<A> =
    fix().fluxHandleErrorWith { f(it).fix() }
}

@extension
interface FluxKMonadThrow : MonadThrow<ForFluxK>, FluxKMonadError

@extension
interface FluxKBracket : Bracket<ForFluxK, Throwable>, FluxKMonadThrow {
  override fun <A, B> FluxKOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> Kind<ForFluxK, Unit>, use: (A) -> FluxKOf<B>): FluxK<B> =
    fix().bracketCase({ use(it) }, { a, e -> release(a, e) })
}

@extension
interface FluxKMonadDefer :
  MonadDefer<ForFluxK>,
  FluxKBracket {
  override fun <A> defer(fa: () -> FluxKOf<A>): FluxK<A> =
    FluxK.defer(fa)
}

@extension
interface FluxKAsync :
  Async<ForFluxK>,
  FluxKMonadDefer {
  override fun <A> async(fa: Proc<A>): FluxK<A> =
    FluxK.async { _, cb -> fa(cb) }

  override fun <A> asyncF(k: ProcF<ForFluxK, A>): FluxK<A> =
    FluxK.asyncF { _, cb -> k(cb) }

  override fun <A> FluxKOf<A>.continueOn(ctx: CoroutineContext): FluxK<A> =
    fix().continueOn(ctx)
}

@extension
interface FluxKEffect :
  Effect<ForFluxK>,
  FluxKAsync {
  override fun <A> FluxKOf<A>.runAsync(cb: (Either<Throwable, A>) -> FluxKOf<Unit>): FluxK<Unit> =
    fix().runAsync(cb)
}

@extension
interface FluxKConcurrentEffect :
  ConcurrentEffect<ForFluxK>,
  FluxKEffect {
  override fun <A> FluxKOf<A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> FluxKOf<Unit>): FluxK<Disposable> =
    fix().runAsyncCancellable(cb)
}

fun FluxK.Companion.monadFlat(): FluxKMonad = monad()

fun FluxK.Companion.monadConcat(): FluxKMonad = object : FluxKMonad {
  override fun <A, B> FluxKOf<A>.flatMap(f: (A) -> FluxKOf<B>): FluxK<B> =
    fix().concatMap { f(it).fix() }
}

fun FluxK.Companion.monadSwitch(): FluxKMonad = object : FluxKMonadError {
  override fun <A, B> FluxKOf<A>.flatMap(f: (A) -> FluxKOf<B>): FluxK<B> =
    fix().switchMap { f(it).fix() }
}

fun FluxK.Companion.monadErrorFlat(): FluxKMonadError = monadError()

fun FluxK.Companion.monadErrorConcat(): FluxKMonadError = object : FluxKMonadError {
  override fun <A, B> FluxKOf<A>.flatMap(f: (A) -> FluxKOf<B>): FluxK<B> =
    fix().concatMap { f(it).fix() }
}

fun FluxK.Companion.monadErrorSwitch(): FluxKMonadError = object : FluxKMonadError {
  override fun <A, B> FluxKOf<A>.flatMap(f: (A) -> FluxKOf<B>): FluxK<B> =
    fix().switchMap { f(it).fix() }
}

// TODO FluxK does not yet have a Concurrent instance
fun <A> FluxK.Companion.fx(c: suspend AsyncSyntax<ForFluxK>.() -> A): FluxK<A> =
  defer { FluxK.async().fx.async(c).fix() }

@extension
interface FluxKTimer : Timer<ForFluxK> {
  override fun sleep(duration: Duration): FluxK<Unit> =
    FluxK(Mono.delay(java.time.Duration.ofNanos(duration.nanoseconds))
      .map { Unit }.toFlux())
}

@extension
interface FluxKFunctorFilter : FunctorFilter<ForFluxK> {
  override fun <A, B> Kind<ForFluxK, A>.filterMap(f: (A) -> Option<B>): FluxK<B> =
    fix().filterMap(f)

  override fun <A, B> Kind<ForFluxK, A>.map(f: (A) -> B): FluxK<B> =
    fix().map(f)
}

@extension
interface FluxKMonadFilter : MonadFilter<ForFluxK> {
  override fun <A> empty(): FluxK<A> =
    Flux.empty<A>().k()

  override fun <A, B> Kind<ForFluxK, A>.filterMap(f: (A) -> Option<B>): FluxK<B> =
    fix().filterMap(f)

  override fun <A, B> Kind<ForFluxK, A>.ap(ff: Kind<ForFluxK, (A) -> B>): FluxK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForFluxK, A>.flatMap(f: (A) -> Kind<ForFluxK, B>): FluxK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, FluxKOf<Either<A, B>>>): FluxK<B> =
    FluxK.tailRecM(a, f)

  override fun <A, B> Kind<ForFluxK, A>.map(f: (A) -> B): FluxK<B> =
    fix().map(f)

  override fun <A, B, Z> Kind<ForFluxK, A>.map2(fb: Kind<ForFluxK, B>, f: (Tuple2<A, B>) -> Z): FluxK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): FluxK<A> =
    FluxK.just(a)
}
