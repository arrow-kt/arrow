package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.effects.flowablek.async.async
import arrow.effects.flowablek.effect.effect
import arrow.effects.flowablek.monad.monad
import arrow.effects.flowablek.monadDefer.monadDefer
import arrow.effects.flowablek.monadError.monadError
import arrow.effects.typeclasses.*
import arrow.extension
import arrow.typeclasses.*
import io.reactivex.BackpressureStrategy
import kotlin.coroutines.CoroutineContext

@extension
interface FlowableKFunctorInstance : Functor<ForFlowableK> {
  override fun <A, B> Kind<ForFlowableK, A>.map(f: (A) -> B): FlowableK<B> =
    fix().map(f)
}

@extension
interface FlowableKApplicativeInstance : Applicative<ForFlowableK> {
  override fun <A, B> FlowableKOf<A>.ap(ff: FlowableKOf<(A) -> B>): FlowableK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForFlowableK, A>.map(f: (A) -> B): FlowableK<B> =
    fix().map(f)

  override fun <A> just(a: A): FlowableK<A> =
    FlowableK.just(a)
}

@extension
interface FlowableKMonadInstance : Monad<ForFlowableK> {
  override fun <A, B> FlowableKOf<A>.ap(ff: FlowableKOf<(A) -> B>): FlowableK<B> =
    fix().ap(ff)

  override fun <A, B> FlowableKOf<A>.flatMap(f: (A) -> Kind<ForFlowableK, B>): FlowableK<B> =
    fix().flatMap(f)

  override fun <A, B> FlowableKOf<A>.map(f: (A) -> B): FlowableK<B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, FlowableKOf<arrow.core.Either<A, B>>>): FlowableK<B> =
    FlowableK.tailRecM(a, f)

  override fun <A> just(a: A): FlowableK<A> =
    FlowableK.just(a)
}

@extension
interface FlowableKFoldableInstance : Foldable<ForFlowableK> {
  override fun <A, B> Kind<ForFlowableK, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForFlowableK, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): arrow.core.Eval<B> =
    fix().foldRight(lb, f)
}

@extension
interface FlowableKTraverseInstance : Traverse<ForFlowableK> {
  override fun <A, B> Kind<ForFlowableK, A>.map(f: (A) -> B): FlowableK<B> =
    fix().map(f)

  override fun <G, A, B> FlowableKOf<A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, FlowableK<B>> =
    fix().traverse(AP, f)

  override fun <A, B> Kind<ForFlowableK, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForFlowableK, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): arrow.core.Eval<B> =
    fix().foldRight(lb, f)
}

@extension
interface FlowableKApplicativeErrorInstance :
  ApplicativeError<ForFlowableK, Throwable>,
  FlowableKApplicativeInstance {
  override fun <A> raiseError(e: Throwable): FlowableK<A> =
    FlowableK.raiseError(e)

  override fun <A> FlowableKOf<A>.handleErrorWith(f: (Throwable) -> FlowableKOf<A>): FlowableK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface FlowableKMonadErrorInstance :
  MonadError<ForFlowableK, Throwable>,
  FlowableKMonadInstance {
  override fun <A> raiseError(e: Throwable): FlowableK<A> =
    FlowableK.raiseError(e)

  override fun <A> FlowableKOf<A>.handleErrorWith(f: (Throwable) -> FlowableKOf<A>): FlowableK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface FlowableKMonadThrowInstance : MonadThrow<ForFlowableK>, FlowableKMonadErrorInstance

@extension
interface FlowableKBracketInstance : Bracket<ForFlowableK, Throwable>, FlowableKMonadThrowInstance {
  override fun <A, B> Kind<ForFlowableK, A>.bracketCase(release: (A, ExitCase<Throwable>) -> Kind<ForFlowableK, Unit>, use: (A) -> Kind<ForFlowableK, B>): FlowableK<B> =
    fix().bracketCase({ use(it) }, { a, e -> release(a, e) })
}

@extension
interface FlowableKMonadDeferInstance : MonadDefer<ForFlowableK>, FlowableKBracketInstance {
  override fun <A> defer(fa: () -> FlowableKOf<A>): FlowableK<A> =
    FlowableK.defer(fa)

  fun BS(): BackpressureStrategy = BackpressureStrategy.BUFFER
}

@extension
interface FlowableKAsyncInstance :
  Async<ForFlowableK>,
  FlowableKMonadDeferInstance {
  override fun <A> async(fa: Proc<A>): FlowableK<A> =
    FlowableK.async(fa, BS())

  override fun <A> FlowableKOf<A>.continueOn(ctx: CoroutineContext): FlowableK<A> =
    fix().continueOn(ctx)
}

@extension
interface FlowableKEffectInstance :
  Effect<ForFlowableK>,
  FlowableKAsyncInstance {
  override fun <A> FlowableKOf<A>.runAsync(cb: (Either<Throwable, A>) -> FlowableKOf<Unit>): FlowableK<Unit> =
    fix().runAsync(cb)
}

@extension
interface FlowableKConcurrentEffectInstance : ConcurrentEffect<ForFlowableK>, FlowableKEffectInstance {
  override fun <A> Kind<ForFlowableK, A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> FlowableKOf<Unit>): FlowableK<Disposable> =
    fix().runAsyncCancellable(cb)
}

fun FlowableK.Companion.monadFlat(): FlowableKMonadInstance = monad()

fun FlowableK.Companion.monadConcat(): FlowableKMonadInstance = object : FlowableKMonadInstance {
  override fun <A, B> Kind<ForFlowableK, A>.flatMap(f: (A) -> Kind<ForFlowableK, B>): FlowableK<B> =
    fix().concatMap { f(it).fix() }
}

fun FlowableK.Companion.monadSwitch(): FlowableKMonadInstance = object : FlowableKMonadInstance {
  override fun <A, B> Kind<ForFlowableK, A>.flatMap(f: (A) -> Kind<ForFlowableK, B>): FlowableK<B> =
    fix().switchMap { f(it).fix() }
}

fun FlowableK.Companion.monadErrorFlat(): FlowableKMonadErrorInstance = monadError()

fun FlowableK.Companion.monadErrorConcat(): FlowableKMonadErrorInstance = object : FlowableKMonadErrorInstance {
  override fun <A, B> Kind<ForFlowableK, A>.flatMap(f: (A) -> Kind<ForFlowableK, B>): FlowableK<B> =
    fix().concatMap { f(it).fix() }
}

fun FlowableK.Companion.monadErrorSwitch(): FlowableKMonadErrorInstance = object : FlowableKMonadErrorInstance {
  override fun <A, B> Kind<ForFlowableK, A>.flatMap(f: (A) -> Kind<ForFlowableK, B>): FlowableK<B> =
    fix().switchMap { f(it).fix() }
}

fun FlowableK.Companion.monadSuspendBuffer(): FlowableKMonadDeferInstance = monadDefer()

fun FlowableK.Companion.monadSuspendDrop(): FlowableKMonadDeferInstance = object : FlowableKMonadDeferInstance {
  override fun BS(): BackpressureStrategy = BackpressureStrategy.DROP
}

fun FlowableK.Companion.monadSuspendError(): FlowableKMonadDeferInstance = object : FlowableKMonadDeferInstance {
  override fun BS(): BackpressureStrategy = BackpressureStrategy.ERROR
}

fun FlowableK.Companion.monadSuspendLatest(): FlowableKMonadDeferInstance = object : FlowableKMonadDeferInstance {
  override fun BS(): BackpressureStrategy = BackpressureStrategy.LATEST
}

fun FlowableK.Companion.monadSuspendMissing(): FlowableKMonadDeferInstance = object : FlowableKMonadDeferInstance {
  override fun BS(): BackpressureStrategy = BackpressureStrategy.MISSING
}

fun FlowableK.Companion.asyncBuffer(): FlowableKAsyncInstance = async()

fun FlowableK.Companion.asyncDrop(): FlowableKAsyncInstance = object : FlowableKAsyncInstance {
  override fun BS(): BackpressureStrategy = BackpressureStrategy.DROP
}

fun FlowableK.Companion.asyncError(): FlowableKAsyncInstance = object : FlowableKAsyncInstance {
  override fun BS(): BackpressureStrategy = BackpressureStrategy.ERROR
}

fun FlowableK.Companion.asyncLatest(): FlowableKAsyncInstance = object : FlowableKAsyncInstance {
  override fun BS(): BackpressureStrategy = BackpressureStrategy.LATEST
}

fun FlowableK.Companion.asyncMissing(): FlowableKAsyncInstance = object : FlowableKAsyncInstance {
  override fun BS(): BackpressureStrategy = BackpressureStrategy.MISSING
}

fun FlowableK.Companion.effectBuffer(): FlowableKEffectInstance = effect()

fun FlowableK.Companion.effectDrop(): FlowableKEffectInstance = object : FlowableKEffectInstance {
  override fun BS(): BackpressureStrategy = BackpressureStrategy.DROP
}

fun FlowableK.Companion.effectError(): FlowableKEffectInstance = object : FlowableKEffectInstance {
  override fun BS(): BackpressureStrategy = BackpressureStrategy.ERROR
}

fun FlowableK.Companion.effectLatest(): FlowableKEffectInstance = object : FlowableKEffectInstance {
  override fun BS(): BackpressureStrategy = BackpressureStrategy.LATEST
}

fun FlowableK.Companion.effectMissing(): FlowableKEffectInstance = object : FlowableKEffectInstance {
  override fun BS(): BackpressureStrategy = BackpressureStrategy.MISSING
}

object FlowableKContext : FlowableKConcurrentEffectInstance, FlowableKTraverseInstance {
  override fun <A, B> FlowableKOf<A>.map(f: (A) -> B): FlowableK<B> =
    fix().map(f)
}

@Deprecated(ExtensionsDSLDeprecated)
infix fun <A> ForFlowableK.Companion.extensions(f: FlowableKContext.() -> A): A =
  f(FlowableKContext)
