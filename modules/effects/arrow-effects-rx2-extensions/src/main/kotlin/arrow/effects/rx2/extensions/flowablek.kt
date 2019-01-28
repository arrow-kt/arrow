package arrow.effects.rx2.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.effects.rx2.FlowableK
import arrow.effects.rx2.FlowableKOf
import arrow.effects.rx2.ForFlowableK
import arrow.effects.rx2.extensions.flowablek.async.async
import arrow.effects.rx2.extensions.flowablek.effect.effect
import arrow.effects.rx2.extensions.flowablek.monad.monad
import arrow.effects.rx2.extensions.flowablek.monadDefer.monadDefer
import arrow.effects.rx2.extensions.flowablek.monadError.monadError
import arrow.effects.rx2.fix
import arrow.effects.typeclasses.*
import arrow.extension
import arrow.typeclasses.*
import io.reactivex.BackpressureStrategy
import kotlin.coroutines.CoroutineContext

@extension
interface FlowableKFunctor : Functor<ForFlowableK> {
  override fun <A, B> FlowableKOf<A>.map(f: (A) -> B): FlowableK<B> =
    fix().map(f)
}

@extension
interface FlowableKApplicative : Applicative<ForFlowableK> {
  override fun <A, B> FlowableKOf<A>.ap(ff: FlowableKOf<(A) -> B>): FlowableK<B> =
    fix().ap(ff)

  override fun <A, B> FlowableKOf<A>.map(f: (A) -> B): FlowableK<B> =
    fix().map(f)

  override fun <A> just(a: A): FlowableK<A> =
    FlowableK.just(a)
}

@extension
interface FlowableKMonad : Monad<ForFlowableK> {
  override fun <A, B> FlowableKOf<A>.ap(ff: FlowableKOf<(A) -> B>): FlowableK<B> =
    fix().ap(ff)

  override fun <A, B> FlowableKOf<A>.flatMap(f: (A) -> FlowableKOf<B>): FlowableK<B> =
    fix().flatMap(f)

  override fun <A, B> FlowableKOf<A>.map(f: (A) -> B): FlowableK<B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, FlowableKOf<Either<A, B>>>): FlowableK<B> =
    FlowableK.tailRecM(a, f)

  override fun <A> just(a: A): FlowableK<A> =
    FlowableK.just(a)
}

@extension
interface FlowableKFoldable : Foldable<ForFlowableK> {
  override fun <A, B> FlowableKOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> FlowableKOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): arrow.core.Eval<B> =
    fix().foldRight(lb, f)
}

@extension
interface FlowableKTraverse : Traverse<ForFlowableK> {
  override fun <A, B> FlowableKOf<A>.map(f: (A) -> B): FlowableK<B> =
    fix().map(f)

  override fun <G, A, B> FlowableKOf<A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, FlowableK<B>> =
    fix().traverse(AP, f)

  override fun <A, B> FlowableKOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> FlowableKOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): arrow.core.Eval<B> =
    fix().foldRight(lb, f)
}

@extension
interface FlowableKApplicativeError :
  ApplicativeError<ForFlowableK, Throwable>,
  FlowableKApplicative{
  override fun <A> raiseError(e: Throwable): FlowableK<A> =
    FlowableK.raiseError(e)

  override fun <A> FlowableKOf<A>.handleErrorWith(f: (Throwable) -> FlowableKOf<A>): FlowableK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface FlowableKMonadError :
  MonadError<ForFlowableK, Throwable>,
  FlowableKMonad{
  override fun <A> raiseError(e: Throwable): FlowableK<A> =
    FlowableK.raiseError(e)

  override fun <A> FlowableKOf<A>.handleErrorWith(f: (Throwable) -> FlowableKOf<A>): FlowableK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface FlowableKMonadThrow: MonadThrow<ForFlowableK>, FlowableKMonadError

@extension
interface FlowableKBracket: Bracket<ForFlowableK, Throwable>, FlowableKMonadThrow {
  override fun <A, B> FlowableKOf<A>.bracketCase(release: (A, ExitCase<Throwable>) -> FlowableKOf<Unit>, use: (A) -> FlowableKOf<B>): FlowableK<B> =
    fix().bracketCase({ use(it) }, { a, e -> release(a, e) })
}

@extension
interface FlowableKMonadDefer: MonadDefer<ForFlowableK>, FlowableKBracket {
  override fun <A> defer(fa: () -> FlowableKOf<A>): FlowableK<A> =
    FlowableK.defer(fa)

  fun BS(): BackpressureStrategy = BackpressureStrategy.BUFFER
}

@extension
interface FlowableKAsync :
  Async<ForFlowableK>,
  FlowableKMonadDefer{
  override fun <A> async(fa: Proc<A>): FlowableK<A> =
    FlowableK.async({ _, cb -> fa(cb) }, BS())

  override fun <A> asyncF(k: ProcF<ForFlowableK, A>): FlowableKOf<A> =
    FlowableK.asyncF({ _, cb -> k(cb) }, BS())

  override fun <A> FlowableKOf<A>.continueOn(ctx: CoroutineContext): FlowableK<A> =
    fix().continueOn(ctx)
}

@extension
interface FlowableKEffect :
  Effect<ForFlowableK>,
  FlowableKAsync{
  override fun <A> FlowableKOf<A>.runAsync(cb: (Either<Throwable, A>) -> FlowableKOf<Unit>): FlowableK<Unit> =
    fix().runAsync(cb)
}

@extension
interface FlowableKConcurrentEffect: ConcurrentEffect<ForFlowableK>, FlowableKEffect {
  override fun <A> FlowableKOf<A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> FlowableKOf<Unit>): FlowableK<Disposable> =
    fix().runAsyncCancellable(cb)
}

fun FlowableK.Companion.monadFlat(): FlowableKMonad = monad()

fun FlowableK.Companion.monadConcat(): FlowableKMonad = object : FlowableKMonad{
  override fun <A, B> FlowableKOf<A>.flatMap(f: (A) -> FlowableKOf<B>): FlowableK<B> =
    fix().concatMap { f(it).fix() }
}

fun FlowableK.Companion.monadSwitch(): FlowableKMonad = object : FlowableKMonad{
  override fun <A, B> FlowableKOf<A>.flatMap(f: (A) -> FlowableKOf<B>): FlowableK<B> =
    fix().switchMap { f(it).fix() }
}

fun FlowableK.Companion.monadErrorFlat(): FlowableKMonadError = monadError()

fun FlowableK.Companion.monadErrorConcat(): FlowableKMonadError = object : FlowableKMonadError{
  override fun <A, B> FlowableKOf<A>.flatMap(f: (A) -> FlowableKOf<B>): FlowableK<B> =
    fix().concatMap { f(it).fix() }
}

fun FlowableK.Companion.monadErrorSwitch(): FlowableKMonadError = object : FlowableKMonadError{
  override fun <A, B> FlowableKOf<A>.flatMap(f: (A) -> FlowableKOf<B>): FlowableK<B> =
    fix().switchMap { f(it).fix() }
}

fun FlowableK.Companion.monadSuspendBuffer(): FlowableKMonadDefer = monadDefer()

fun FlowableK.Companion.monadSuspendDrop(): FlowableKMonadDefer = object : FlowableKMonadDefer{
  override fun BS(): BackpressureStrategy = BackpressureStrategy.DROP
}

fun FlowableK.Companion.monadSuspendError(): FlowableKMonadDefer = object : FlowableKMonadDefer{
  override fun BS(): BackpressureStrategy = BackpressureStrategy.ERROR
}

fun FlowableK.Companion.monadSuspendLatest(): FlowableKMonadDefer = object : FlowableKMonadDefer{
  override fun BS(): BackpressureStrategy = BackpressureStrategy.LATEST
}

fun FlowableK.Companion.monadSuspendMissing(): FlowableKMonadDefer = object : FlowableKMonadDefer{
  override fun BS(): BackpressureStrategy = BackpressureStrategy.MISSING
}

fun FlowableK.Companion.asyncBuffer(): FlowableKAsync = async()

fun FlowableK.Companion.asyncDrop(): FlowableKAsync = object : FlowableKAsync{
  override fun BS(): BackpressureStrategy = BackpressureStrategy.DROP
}

fun FlowableK.Companion.asyncError(): FlowableKAsync = object : FlowableKAsync{
  override fun BS(): BackpressureStrategy = BackpressureStrategy.ERROR
}

fun FlowableK.Companion.asyncLatest(): FlowableKAsync = object : FlowableKAsync{
  override fun BS(): BackpressureStrategy = BackpressureStrategy.LATEST
}

fun FlowableK.Companion.asyncMissing(): FlowableKAsync = object : FlowableKAsync{
  override fun BS(): BackpressureStrategy = BackpressureStrategy.MISSING
}

fun FlowableK.Companion.effectBuffer(): FlowableKEffect = effect()

fun FlowableK.Companion.effectDrop(): FlowableKEffect = object : FlowableKEffect{
  override fun BS(): BackpressureStrategy = BackpressureStrategy.DROP
}

fun FlowableK.Companion.effectError(): FlowableKEffect = object : FlowableKEffect{
  override fun BS(): BackpressureStrategy = BackpressureStrategy.ERROR
}

fun FlowableK.Companion.effectLatest(): FlowableKEffect = object : FlowableKEffect{
  override fun BS(): BackpressureStrategy = BackpressureStrategy.LATEST
}

fun FlowableK.Companion.effectMissing(): FlowableKEffect = object : FlowableKEffect{
  override fun BS(): BackpressureStrategy = BackpressureStrategy.MISSING
}
