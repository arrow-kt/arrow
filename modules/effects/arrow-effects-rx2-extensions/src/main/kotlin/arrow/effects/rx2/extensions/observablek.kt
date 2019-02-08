package arrow.effects.rx2.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.effects.rx2.ForObservableK
import arrow.effects.rx2.ObservableK
import arrow.effects.rx2.ObservableKOf
import arrow.effects.rx2.extensions.observablek.monad.monad
import arrow.effects.rx2.extensions.observablek.monadDefer.monadDefer
import arrow.effects.rx2.extensions.observablek.monadError.monadError
import arrow.effects.rx2.fix
import arrow.effects.typeclasses.*
import arrow.effects.typeclasses.suspended.monaddefer.Fx
import arrow.extension
import arrow.typeclasses.*
import kotlin.coroutines.CoroutineContext

@extension
interface ObservableKFunctor : Functor<ForObservableK> {
  override fun <A, B> ObservableKOf<A>.map(f: (A) -> B): ObservableK<B> =
    fix().map(f)
}

@extension
interface ObservableKApplicative : Applicative<ForObservableK> {
  override fun <A, B> ObservableKOf<A>.ap(ff: ObservableKOf<(A) -> B>): ObservableK<B> =
    fix().ap(ff)

  override fun <A, B> ObservableKOf<A>.map(f: (A) -> B): ObservableK<B> =
    fix().map(f)

  override fun <A> just(a: A): ObservableK<A> =
    ObservableK.just(a)
}

@extension
interface ObservableKMonad : Monad<ForObservableK> {
  override fun <A, B> ObservableKOf<A>.ap(ff: ObservableKOf<(A) -> B>): ObservableK<B> =
    fix().ap(ff)

  override fun <A, B> ObservableKOf<A>.flatMap(f: (A) -> ObservableKOf<B>): ObservableK<B> =
    fix().flatMap(f)

  override fun <A, B> ObservableKOf<A>.map(f: (A) -> B): ObservableK<B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, ObservableKOf<Either<A, B>>>): ObservableK<B> =
    ObservableK.tailRecM(a, f)

  override fun <A> just(a: A): ObservableK<A> =
    ObservableK.just(a)
}

@extension
interface ObservableKFoldable : Foldable<ForObservableK> {
  override fun <A, B> ObservableKOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> ObservableKOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): arrow.core.Eval<B> =
    fix().foldRight(lb, f)
}

@extension
interface ObservableKTraverse : Traverse<ForObservableK> {
  override fun <A, B> ObservableKOf<A>.map(f: (A) -> B): ObservableK<B> =
    fix().map(f)

  override fun <G, A, B> ObservableKOf<A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, ObservableK<B>> =
    fix().traverse(AP, f)

  override fun <A, B> ObservableKOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> ObservableKOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): arrow.core.Eval<B> =
    fix().foldRight(lb, f)
}

@extension
interface ObservableKApplicativeError :
  ApplicativeError<ForObservableK, Throwable>,
  ObservableKApplicative{
  override fun <A> raiseError(e: Throwable): ObservableK<A> =
    ObservableK.raiseError(e)

  override fun <A> ObservableKOf<A>.handleErrorWith(f: (Throwable) -> ObservableKOf<A>): ObservableK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface ObservableKMonadError :
  MonadError<ForObservableK, Throwable>,
  ObservableKMonad{
  override fun <A> raiseError(e: Throwable): ObservableK<A> =
    ObservableK.raiseError(e)

  override fun <A> ObservableKOf<A>.handleErrorWith(f: (Throwable) -> ObservableKOf<A>): ObservableK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface ObservableKMonadThrow: MonadThrow<ForObservableK>, ObservableKMonadError

@extension
interface ObservableKBracket: Bracket<ForObservableK, Throwable>, ObservableKMonadThrow {
  override fun <A, B> ObservableKOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> ObservableKOf<Unit>, use: (A) -> ObservableKOf<B>): ObservableK<B> =
    fix().bracketCase({ use(it) }, { a, e -> release(a, e) })
}

@extension
interface ObservableKMonadDefer: MonadDefer<ForObservableK>, ObservableKBracket {
  override fun <A> defer(fa: () -> ObservableKOf<A>): ObservableK<A> =
    ObservableK.defer(fa)
}

@extension
interface ObservableKAsync: Async<ForObservableK>, ObservableKMonadDefer {
  override fun <A> async(fa: Proc<A>): ObservableK<A> =
    ObservableK.async { _, cb -> fa(cb) }

  override fun <A> asyncF(k: ProcF<ForObservableK, A>): ObservableK<A> =
    ObservableK.asyncF { _, cb -> k(cb) }

  override fun <A> ObservableKOf<A>.continueOn(ctx: CoroutineContext): ObservableK<A> =
    fix().continueOn(ctx)
}

@extension
interface ObservableKEffect: Effect<ForObservableK>, ObservableKAsync {
  override fun <A> ObservableKOf<A>.runAsync(cb: (Either<Throwable, A>) -> ObservableKOf<Unit>): ObservableK<Unit> =
    fix().runAsync(cb)
}

@extension
interface ObservableKConcurrentEffect: ConcurrentEffect<ForObservableK>, ObservableKEffect {
  override fun <A> ObservableKOf<A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> ObservableKOf<Unit>): ObservableK<Disposable> =
    fix().runAsyncCancellable(cb)
}

fun ObservableK.Companion.monadFlat(): ObservableKMonad = monad()

fun ObservableK.Companion.monadConcat(): ObservableKMonad = object : ObservableKMonad{
  override fun <A, B> ObservableKOf<A>.flatMap(f: (A) -> ObservableKOf<B>): ObservableK<B> =
    fix().concatMap { f(it).fix() }
}

fun ObservableK.Companion.monadSwitch(): ObservableKMonad = object : ObservableKMonadError{
  override fun <A, B> ObservableKOf<A>.flatMap(f: (A) -> ObservableKOf<B>): ObservableK<B> =
    fix().switchMap { f(it).fix() }
}

fun ObservableK.Companion.monadErrorFlat(): ObservableKMonadError = monadError()

fun ObservableK.Companion.monadErrorConcat(): ObservableKMonadError = object : ObservableKMonadError{
  override fun <A, B> ObservableKOf<A>.flatMap(f: (A) -> ObservableKOf<B>): ObservableK<B> =
    fix().concatMap { f(it).fix() }
}

fun ObservableK.Companion.monadErrorSwitch(): ObservableKMonadError = object : ObservableKMonadError{
  override fun <A, B> ObservableKOf<A>.flatMap(f: (A) -> ObservableKOf<B>): ObservableK<B> =
    fix().switchMap { f(it).fix() }
}

//TODO ObservableK does not yet have a Concurrent instance
@extension
interface ObservableKFx : Fx<ForObservableK> {
  override fun monadDefer(): MonadDefer<ForObservableK> = ObservableK.monadDefer()
}