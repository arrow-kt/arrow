package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.effects.typeclasses.Async
import arrow.effects.typeclasses.Effect
import arrow.effects.typeclasses.MonadDefer
import arrow.effects.typeclasses.Proc
import arrow.instance
import arrow.typeclasses.*
import kotlin.coroutines.CoroutineContext

@instance(FluxK::class)
interface FluxKFunctorInstance : Functor<ForFluxK> {
  override fun <A, B> Kind<ForFluxK, A>.map(f: (A) -> B): FluxK<B> =
      fix().map(f)
}

@instance(FluxK::class)
interface FluxKApplicativeInstance : Applicative<ForFluxK> {
  override fun <A> just(a: A): FluxK<A> =
      FluxK.just(a)

  override fun <A, B> FluxKOf<A>.ap(ff: FluxKOf<(A) -> B>): FluxK<B> =
      fix().ap(ff)

  override fun <A, B> Kind<ForFluxK, A>.map(f: (A) -> B): FluxK<B> =
      fix().map(f)
}

@instance(FluxK::class)
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

@instance(FluxK::class)
interface FluxKFoldableInstance : Foldable<ForFluxK> {
  override fun <A, B> Kind<ForFluxK, A>.foldLeft(b: B, f: (B, A) -> B): B =
      fix().foldLeft(b, f)

  override fun <A, B> Kind<ForFluxK, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): arrow.core.Eval<B> =
      fix().foldRight(lb, f)
}

@instance(FluxK::class)
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

@instance(FluxK::class)
interface FluxKApplicativeErrorInstance :
    FluxKApplicativeInstance,
    ApplicativeError<ForFluxK, Throwable> {
  override fun <A> raiseError(e: Throwable): FluxK<A> =
      FluxK.raiseError(e)

  override fun <A> FluxKOf<A>.handleErrorWith(f: (Throwable) -> FluxKOf<A>): FluxK<A> =
      fix().handleErrorWith { f(it).fix() }
}

@instance(FluxK::class)
interface FluxKMonadErrorInstance :
    FluxKMonadInstance,
    MonadError<ForFluxK, Throwable> {
  override fun <A> raiseError(e: Throwable): FluxK<A> =
      FluxK.raiseError(e)

  override fun <A> FluxKOf<A>.handleErrorWith(f: (Throwable) -> FluxKOf<A>): FluxK<A> =
      fix().handleErrorWith { f(it).fix() }
}

@instance(FluxK::class)
interface FluxKMonadDeferInstance :
    FluxKMonadErrorInstance,
    MonadDefer<ForFluxK> {
  override fun <A> defer(fa: () -> FluxKOf<A>): FluxK<A> =
      FluxK.defer(fa)
}

@instance(FluxK::class)
interface FluxKAsyncInstance :
    FluxKMonadDeferInstance,
    Async<ForFluxK> {
  override fun <A> async(fa: Proc<A>): FluxK<A> =
      FluxK.runAsync(fa)

  override fun <A> FluxKOf<A>.continueOn(ctx: CoroutineContext): FluxK<A> =
    fix().continueOn(ctx)
}

@instance(FluxK::class)
interface FluxKEffectInstance :
    FluxKAsyncInstance,
    Effect<ForFluxK> {
  override fun <A> FluxKOf<A>.runAsync(cb: (Either<Throwable, A>) -> FluxKOf<Unit>): FluxK<Unit> =
      fix().runAsync(cb)
}

object FluxKContext : FluxKEffectInstance, FluxKTraverseInstance {
  override fun <A, B> FluxKOf<A>.map(f: (A) -> B): FluxK<B> =
    fix().map(f)
}

infix fun <A> ForFluxK.Companion.extensions(f: FluxKContext.() -> A): A =
    f(FluxKContext)
