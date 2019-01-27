package arrow.effects.reactor.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.effects.reactor.FluxK
import arrow.effects.reactor.FluxKOf
import arrow.effects.reactor.ForFluxK
import arrow.effects.reactor.extensions.fluxk.monad.monad
import arrow.effects.reactor.extensions.fluxk.monadDefer.monadDefer
import arrow.effects.reactor.extensions.fluxk.monadError.monadError
import arrow.effects.reactor.fix
import arrow.effects.typeclasses.*
import arrow.effects.typeclasses.suspended.monaddefer.Fx
import arrow.extension
import arrow.typeclasses.*
import kotlin.coroutines.CoroutineContext

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
  FluxKApplicative{
  override fun <A> raiseError(e: Throwable): FluxK<A> =
    FluxK.raiseError(e)

  override fun <A> FluxKOf<A>.handleErrorWith(f: (Throwable) -> FluxKOf<A>): FluxK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface FluxKMonadError :
  MonadError<ForFluxK, Throwable>,
  FluxKMonad{
  override fun <A> raiseError(e: Throwable): FluxK<A> =
    FluxK.raiseError(e)

  override fun <A> FluxKOf<A>.handleErrorWith(f: (Throwable) -> FluxKOf<A>): FluxK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface FluxKMonadThrow: MonadThrow<ForFluxK>, FluxKMonadError

@extension
interface FluxKBracket: Bracket<ForFluxK, Throwable>, FluxKMonadThrow {
  override fun <A, B> FluxKOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> Kind<ForFluxK, Unit>, use: (A) -> FluxKOf<B>): FluxK<B> =
    fix().bracketCase({ use(it) }, { a, e -> release(a, e) })
}

@extension
interface FluxKMonadDefer :
  MonadDefer<ForFluxK>,
  FluxKBracket{
  override fun <A> defer(fa: () -> FluxKOf<A>): FluxK<A> =
    FluxK.defer(fa)
}

@extension
interface FluxKAsync :
  Async<ForFluxK>,
  FluxKMonadDefer{
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
  FluxKAsync{
  override fun <A> FluxKOf<A>.runAsync(cb: (Either<Throwable, A>) -> FluxKOf<Unit>): FluxK<Unit> =
    fix().runAsync(cb)
}

@extension
interface FluxKConcurrentEffect :
  ConcurrentEffect<ForFluxK>,
  FluxKEffect{
  override fun <A> FluxKOf<A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> FluxKOf<Unit>): FluxK<Disposable> =
    fix().runAsyncCancellable(cb)
}

fun FluxK.Companion.monadFlat(): FluxKMonad = monad()

fun FluxK.Companion.monadConcat(): FluxKMonad = object : FluxKMonad{
  override fun <A, B> FluxKOf<A>.flatMap(f: (A) -> FluxKOf<B>): FluxK<B> =
    fix().concatMap { f(it).fix() }
}

fun FluxK.Companion.monadSwitch(): FluxKMonad = object : FluxKMonadError{
  override fun <A, B> FluxKOf<A>.flatMap(f: (A) -> FluxKOf<B>): FluxK<B> =
    fix().switchMap { f(it).fix() }
}

fun FluxK.Companion.monadErrorFlat(): FluxKMonadError = monadError()

fun FluxK.Companion.monadErrorConcat(): FluxKMonadError = object : FluxKMonadError{
  override fun <A, B> FluxKOf<A>.flatMap(f: (A) -> FluxKOf<B>): FluxK<B> =
    fix().concatMap { f(it).fix() }
}

fun FluxK.Companion.monadErrorSwitch(): FluxKMonadError = object : FluxKMonadError{
  override fun <A, B> FluxKOf<A>.flatMap(f: (A) -> FluxKOf<B>): FluxK<B> =
    fix().switchMap { f(it).fix() }
}

//TODO ObservableK does not yet have a Concurrent instance
@extension
interface FluxKFx : Fx<ForFluxK> {
  override fun monadDefer(): MonadDefer<ForFluxK> =
    FluxK.monadDefer()
}