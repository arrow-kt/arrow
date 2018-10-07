package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.effects.typeclasses.*
import arrow.extension
import arrow.typeclasses.*
import kotlin.coroutines.experimental.CoroutineContext

@extension
interface ObservableKFunctorInstance : Functor<ForObservableK> {
  override fun <A, B> Kind<ForObservableK, A>.map(f: (A) -> B): ObservableK<B> =
    fix().map(f)
}

@extension
interface ObservableKApplicativeInstance : Applicative<ForObservableK> {
  override fun <A, B> ObservableKOf<A>.ap(ff: ObservableKOf<(A) -> B>): ObservableK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForObservableK, A>.map(f: (A) -> B): ObservableK<B> =
    fix().map(f)

  override fun <A> just(a: A): ObservableK<A> =
    ObservableK.just(a)
}

@extension
interface ObservableKMonadInstance : Monad<ForObservableK> {
  override fun <A, B> ObservableKOf<A>.ap(ff: ObservableKOf<(A) -> B>): ObservableK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForObservableK, A>.flatMap(f: (A) -> Kind<ForObservableK, B>): ObservableK<B> =
    fix().flatMap(f)

  override fun <A, B> Kind<ForObservableK, A>.map(f: (A) -> B): ObservableK<B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, ObservableKOf<arrow.core.Either<A, B>>>): ObservableK<B> =
    ObservableK.tailRecM(a, f)

  override fun <A> just(a: A): ObservableK<A> =
    ObservableK.just(a)
}

@extension
interface ObservableKFoldableInstance : Foldable<ForObservableK> {
  override fun <A, B> Kind<ForObservableK, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForObservableK, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): arrow.core.Eval<B> =
    fix().foldRight(lb, f)
}

@extension
interface ObservableKTraverseInstance : Traverse<ForObservableK> {
  override fun <A, B> Kind<ForObservableK, A>.map(f: (A) -> B): ObservableK<B> =
    fix().map(f)

  override fun <G, A, B> ObservableKOf<A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, ObservableK<B>> =
    fix().traverse(AP, f)

  override fun <A, B> Kind<ForObservableK, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForObservableK, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): arrow.core.Eval<B> =
    fix().foldRight(lb, f)
}

@extension
interface ObservableKApplicativeErrorInstance :
  ObservableKApplicativeInstance,
  ApplicativeError<ForObservableK, Throwable> {
  override fun <A> raiseError(e: Throwable): ObservableK<A> =
    ObservableK.raiseError(e)

  override fun <A> ObservableKOf<A>.handleErrorWith(f: (Throwable) -> ObservableKOf<A>): ObservableK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface ObservableKMonadErrorInstance :
  ObservableKMonadInstance,
  MonadError<ForObservableK, Throwable> {
  override fun <A> raiseError(e: Throwable): ObservableK<A> =
    ObservableK.raiseError(e)

  override fun <A> ObservableKOf<A>.handleErrorWith(f: (Throwable) -> ObservableKOf<A>): ObservableK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface ObservableKMonadDeferInstance :
  ObservableKMonadErrorInstance,
  MonadDefer<ForObservableK> {
  override fun <A> defer(fa: () -> ObservableKOf<A>): ObservableK<A> =
    ObservableK.defer(fa)
}

@extension
interface ObservableKAsyncInstance :
  ObservableKMonadDeferInstance,
  Async<ForObservableK> {
  override fun <A> async(fa: Proc<A>): ObservableK<A> =
    ObservableK.runAsync(fa)

  override fun <A> ObservableKOf<A>.continueOn(ctx: CoroutineContext): ObservableK<A> =
    fix().continueOn(ctx)
}

@extension
interface ObservableKEffectInstance :
  ObservableKAsyncInstance,
  Effect<ForObservableK> {
  override fun <A> ObservableKOf<A>.runAsync(cb: (Either<Throwable, A>) -> ObservableKOf<Unit>): ObservableK<Unit> =
    fix().runAsync(cb)
}

@extension
interface ObservableKConcurrentEffectInstance : ObservableKEffectInstance, ConcurrentEffect<ForObservableK> {
  override fun <A> Kind<ForObservableK, A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> ObservableKOf<Unit>): ObservableK<Disposable> =
    fix().runAsyncCancellable(cb)
}

object ObservableKContext : ObservableKConcurrentEffectInstance, ObservableKTraverseInstance {
  override fun <A, B> Kind<ForObservableK, A>.map(f: (A) -> B): ObservableK<B> =
    fix().map(f)
}

infix fun <A> ForObservableK.Companion.extensions(f: ObservableKContext.() -> A): A =
  f(ObservableKContext)
