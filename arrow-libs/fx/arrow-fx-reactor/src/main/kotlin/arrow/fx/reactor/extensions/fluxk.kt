package arrow.fx.reactor.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Option
import arrow.fx.Timer
import arrow.fx.reactor.DeprecateReactor
import arrow.fx.reactor.FluxK
import arrow.fx.reactor.FluxKOf
import arrow.fx.reactor.ForFluxK
import arrow.fx.reactor.extensions.fluxk.async.async
import arrow.fx.reactor.extensions.fluxk.monad.monad
import arrow.fx.reactor.extensions.fluxk.monadError.monadError
import arrow.fx.reactor.fix
import arrow.fx.reactor.k
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

@Deprecated(DeprecateReactor)
interface FluxKFunctor : Functor<ForFluxK> {
  override fun <A, B> FluxKOf<A>.map(f: (A) -> B): FluxK<B> =
    fix().map(f)
}

@Deprecated(DeprecateReactor)
interface FluxKApplicative : Applicative<ForFluxK> {
  override fun <A> just(a: A): FluxK<A> =
    FluxK.just(a)

  override fun <A, B> FluxKOf<A>.ap(ff: FluxKOf<(A) -> B>): FluxK<B> =
    fix().ap(ff)

  override fun <A, B> FluxKOf<A>.map(f: (A) -> B): FluxK<B> =
    fix().map(f)

  override fun <A, B> Kind<ForFluxK, A>.apEval(ff: Eval<Kind<ForFluxK, (A) -> B>>): Eval<Kind<ForFluxK, B>> =
    Eval.now(fix().ap(FluxK.defer { ff.value() }))
}

@Deprecated(DeprecateReactor)
interface FluxKMonad : Monad<ForFluxK>, FluxKApplicative {
  override fun <A, B> FluxKOf<A>.ap(ff: FluxKOf<(A) -> B>): FluxK<B> =
    fix().ap(ff)

  override fun <A, B> FluxKOf<A>.flatMap(f: (A) -> FluxKOf<B>): FluxK<B> =
    fix().flatMap(f)

  override fun <A, B> FluxKOf<A>.map(f: (A) -> B): FluxK<B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, FluxKOf<arrow.core.Either<A, B>>>): FluxK<B> =
    FluxK.tailRecM(a, f)

  override fun <A, B> Kind<ForFluxK, A>.apEval(ff: Eval<Kind<ForFluxK, (A) -> B>>): Eval<Kind<ForFluxK, B>> =
    Eval.now(fix().ap(FluxK.defer { ff.value() }))
}

@Deprecated(DeprecateReactor)
interface FluxKFoldable : Foldable<ForFluxK> {
  override fun <A, B> FluxKOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> FluxKOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): arrow.core.Eval<B> =
    fix().foldRight(lb, f)
}

@Deprecated(DeprecateReactor)
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

@Deprecated(DeprecateReactor)
interface FluxKApplicativeError :
  ApplicativeError<ForFluxK, Throwable>,
  FluxKApplicative {
  override fun <A> raiseError(e: Throwable): FluxK<A> =
    FluxK.raiseError(e)

  override fun <A> FluxKOf<A>.handleErrorWith(f: (Throwable) -> FluxKOf<A>): FluxK<A> =
    fix().fluxHandleErrorWith { f(it).fix() }
}

@Deprecated(DeprecateReactor)
interface FluxKMonadError :
  MonadError<ForFluxK, Throwable>,
  FluxKMonad {
  override fun <A> raiseError(e: Throwable): FluxK<A> =
    FluxK.raiseError(e)

  override fun <A> FluxKOf<A>.handleErrorWith(f: (Throwable) -> FluxKOf<A>): FluxK<A> =
    fix().fluxHandleErrorWith { f(it).fix() }
}

@Deprecated(DeprecateReactor)
interface FluxKMonadThrow : MonadThrow<ForFluxK>, FluxKMonadError

@Deprecated(DeprecateReactor)
interface FluxKBracket : Bracket<ForFluxK, Throwable>, FluxKMonadThrow {
  override fun <A, B> FluxKOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> Kind<ForFluxK, Unit>, use: (A) -> FluxKOf<B>): FluxK<B> =
    fix().bracketCase({ use(it) }, { a, e -> release(a, e) })
}

@Deprecated(DeprecateReactor)
interface FluxKMonadDefer :
  MonadDefer<ForFluxK>,
  FluxKBracket {
  override fun <A> defer(fa: () -> FluxKOf<A>): FluxK<A> =
    FluxK.defer(fa)
}

@Deprecated(DeprecateReactor)
interface FluxKAsync :
  Async<ForFluxK>,
  FluxKMonadDefer {
  override fun <A> async(fa: Proc<A>): FluxK<A> =
    FluxK.async(fa)

  override fun <A> asyncF(k: ProcF<ForFluxK, A>): FluxK<A> =
    FluxK.asyncF(k)

  override fun <A> FluxKOf<A>.continueOn(ctx: CoroutineContext): FluxK<A> =
    fix().continueOn(ctx)
}

@Deprecated(DeprecateReactor)
interface FluxKEffect :
  Effect<ForFluxK>,
  FluxKAsync {
  override fun <A> FluxKOf<A>.runAsync(cb: (Either<Throwable, A>) -> FluxKOf<Unit>): FluxK<Unit> =
    fix().runAsync(cb)
}

@Deprecated(DeprecateReactor)
interface FluxKConcurrentEffect :
  ConcurrentEffect<ForFluxK>,
  FluxKEffect {
  override fun <A> FluxKOf<A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> FluxKOf<Unit>): FluxK<Disposable> =
    fix().runAsyncCancellable(cb)
}

@Deprecated(DeprecateReactor)
fun FluxK.Companion.monadFlat(): FluxKMonad = monad()

@Deprecated(DeprecateReactor)
fun FluxK.Companion.monadConcat(): FluxKMonad = object : FluxKMonad {
  override fun <A, B> FluxKOf<A>.flatMap(f: (A) -> FluxKOf<B>): FluxK<B> =
    fix().concatMap { f(it).fix() }
}

@Deprecated(DeprecateReactor)
fun FluxK.Companion.monadSwitch(): FluxKMonad = object : FluxKMonadError {
  override fun <A, B> FluxKOf<A>.flatMap(f: (A) -> FluxKOf<B>): FluxK<B> =
    fix().switchMap { f(it).fix() }
}

@Deprecated(DeprecateReactor)
fun FluxK.Companion.monadErrorFlat(): FluxKMonadError = monadError()

@Deprecated(DeprecateReactor)
fun FluxK.Companion.monadErrorConcat(): FluxKMonadError = object : FluxKMonadError {
  override fun <A, B> FluxKOf<A>.flatMap(f: (A) -> FluxKOf<B>): FluxK<B> =
    fix().concatMap { f(it).fix() }
}

@Deprecated(DeprecateReactor)
fun FluxK.Companion.monadErrorSwitch(): FluxKMonadError = object : FluxKMonadError {
  override fun <A, B> FluxKOf<A>.flatMap(f: (A) -> FluxKOf<B>): FluxK<B> =
    fix().switchMap { f(it).fix() }
}

// TODO FluxK does not yet have a Concurrent instance
@Deprecated(DeprecateReactor)
fun <A> FluxK.Companion.fx(c: suspend AsyncSyntax<ForFluxK>.() -> A): FluxK<A> =
  FluxK.async().fx.async(c).fix()

@Deprecated(DeprecateReactor)
interface FluxKTimer : Timer<ForFluxK> {
  override fun sleep(duration: Duration): FluxK<Unit> =
    FluxK(
      Mono.delay(java.time.Duration.ofNanos(duration.nanoseconds))
        .map { Unit }.toFlux()
    )
}

@Deprecated(DeprecateReactor)
interface FluxKFunctorFilter : FunctorFilter<ForFluxK>, FluxKFunctor {
  override fun <A, B> Kind<ForFluxK, A>.filterMap(f: (A) -> Option<B>): FluxK<B> =
    fix().filterMap(f)
}

@Deprecated(DeprecateReactor)
interface FluxKMonadFilter : MonadFilter<ForFluxK>, FluxKMonad {
  override fun <A> empty(): FluxK<A> =
    Flux.empty<A>().k()

  override fun <A, B> Kind<ForFluxK, A>.filterMap(f: (A) -> Option<B>): FluxK<B> =
    fix().filterMap(f)
}
