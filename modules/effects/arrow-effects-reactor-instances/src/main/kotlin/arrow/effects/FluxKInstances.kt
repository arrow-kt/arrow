package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.effects.fluxk.monad.monad
import arrow.effects.fluxk.monadError.monadError
import arrow.effects.typeclasses.*
import arrow.extension
import arrow.typeclasses.*
import kotlin.coroutines.CoroutineContext

@extension
interface FluxKFunctorInstance : Functor<ForFluxK> {
  override fun <A, B> Kind<ForFluxK, A>.map(f: (A) -> B): FluxK<B> =
    fix().map(f)
}

@extension
interface FluxKApplicativeInstance : Applicative<ForFluxK> {
  override fun <A> just(a: A): FluxK<A> =
    FluxK.just(a)

  override fun <A, B> FluxKOf<A>.ap(ff: FluxKOf<(A) -> B>): FluxK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForFluxK, A>.map(f: (A) -> B): FluxK<B> =
    fix().map(f)
}

@extension
interface FluxKMonadInstance : Monad<ForFluxK> {
  override fun <A, B> FluxKOf<A>.ap(ff: FluxKOf<(A) -> B>): FluxK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForFluxK, A>.flatMap(f: (A) -> Kind<ForFluxK, B>): FluxK<B> =
    fix().flatMap(f)

  override fun <A, B> Kind<ForFluxK, A>.map(f: (A) -> B): FluxK<B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, FluxKOf<arrow.core.Either<A, B>>>): FluxK<B> =
    FluxK.tailRecM(a, f)

  override fun <A> just(a: A): FluxK<A> =
    FluxK.just(a)
}

@extension
interface FluxKFoldableInstance : Foldable<ForFluxK> {
  override fun <A, B> Kind<ForFluxK, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForFluxK, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): arrow.core.Eval<B> =
    fix().foldRight(lb, f)
}

@extension
interface FluxKTraverseInstance : Traverse<ForFluxK> {
  override fun <A, B> Kind<ForFluxK, A>.map(f: (A) -> B): FluxK<B> =
    fix().map(f)

  override fun <G, A, B> FluxKOf<A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, FluxK<B>> =
    fix().traverse(AP, f)

  override fun <A, B> Kind<ForFluxK, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForFluxK, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): arrow.core.Eval<B> =
    fix().foldRight(lb, f)
}

@extension
interface FluxKApplicativeErrorInstance :
  ApplicativeError<ForFluxK, Throwable>,
  FluxKApplicativeInstance {
  override fun <A> raiseError(e: Throwable): FluxK<A> =
    FluxK.raiseError(e)

  override fun <A> FluxKOf<A>.handleErrorWith(f: (Throwable) -> FluxKOf<A>): FluxK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface FluxKMonadErrorInstance :
  MonadError<ForFluxK, Throwable>,
  FluxKMonadInstance {
  override fun <A> raiseError(e: Throwable): FluxK<A> =
    FluxK.raiseError(e)

  override fun <A> FluxKOf<A>.handleErrorWith(f: (Throwable) -> FluxKOf<A>): FluxK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface FluxKMonadThrowInstance : MonadThrow<ForFluxK>, FluxKMonadErrorInstance

@extension
interface FluxKBracketInstance : Bracket<ForFluxK, Throwable>, FluxKMonadThrowInstance {
  override fun <A, B> Kind<ForFluxK, A>.bracketCase(release: (A, ExitCase<Throwable>) -> Kind<ForFluxK, Unit>, use: (A) -> Kind<ForFluxK, B>): FluxK<B> =
    fix().bracketCase({ use(it) }, { a, e -> release(a, e) })
}

@extension
interface FluxKMonadDeferInstance :
  MonadDefer<ForFluxK>,
  FluxKBracketInstance {
  override fun <A> defer(fa: () -> FluxKOf<A>): FluxK<A> =
    FluxK.defer(fa)
}

@extension
interface FluxKAsyncInstance :
  Async<ForFluxK>,
  FluxKMonadDeferInstance {
  override fun <A> async(fa: Proc<A>): FluxK<A> =
    FluxK.runAsync(fa)

  override fun <A> FluxKOf<A>.continueOn(ctx: CoroutineContext): FluxK<A> =
    fix().continueOn(ctx)
}

@extension
interface FluxKEffectInstance :
  Effect<ForFluxK>,
  FluxKAsyncInstance {
  override fun <A> FluxKOf<A>.runAsync(cb: (Either<Throwable, A>) -> FluxKOf<Unit>): FluxK<Unit> =
    fix().runAsync(cb)
}

@extension
interface FluxKConcurrentEffectInstance :
  ConcurrentEffect<ForFluxK>,
  FluxKEffectInstance {
  override fun <A> Kind<ForFluxK, A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> FluxKOf<Unit>): FluxK<Disposable> =
    fix().runAsyncCancellable(cb)
}

fun FluxK.Companion.monadFlat(): FluxKMonadInstance = monad()

fun FluxK.Companion.monadConcat(): FluxKMonadInstance = object : FluxKMonadInstance {
  override fun <A, B> Kind<ForFluxK, A>.flatMap(f: (A) -> Kind<ForFluxK, B>): FluxK<B> =
    fix().concatMap { f(it).fix() }
}

fun FluxK.Companion.monadSwitch(): FluxKMonadInstance = object : FluxKMonadErrorInstance {
  override fun <A, B> Kind<ForFluxK, A>.flatMap(f: (A) -> Kind<ForFluxK, B>): FluxK<B> =
    fix().switchMap { f(it).fix() }
}

fun FluxK.Companion.monadErrorFlat(): FluxKMonadErrorInstance = monadError()

fun FluxK.Companion.monadErrorConcat(): FluxKMonadErrorInstance = object : FluxKMonadErrorInstance {
  override fun <A, B> Kind<ForFluxK, A>.flatMap(f: (A) -> Kind<ForFluxK, B>): FluxK<B> =
    fix().concatMap { f(it).fix() }
}

fun FluxK.Companion.monadErrorSwitch(): FluxKMonadErrorInstance = object : FluxKMonadErrorInstance {
  override fun <A, B> Kind<ForFluxK, A>.flatMap(f: (A) -> Kind<ForFluxK, B>): FluxK<B> =
    fix().switchMap { f(it).fix() }
}

object FluxKContext : FluxKConcurrentEffectInstance, FluxKTraverseInstance {
  override fun <A, B> FluxKOf<A>.map(f: (A) -> B): FluxK<B> =
    fix().map(f)
}

@Deprecated(ExtensionsDSLDeprecated)
infix fun <A> ForFluxK.Companion.extensions(f: FluxKContext.() -> A): A =
  f(FluxKContext)
