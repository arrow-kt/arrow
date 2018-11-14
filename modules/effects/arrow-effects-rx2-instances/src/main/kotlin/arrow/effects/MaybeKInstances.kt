package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.effects.typeclasses.*
import arrow.extension
import arrow.typeclasses.*
import kotlin.coroutines.CoroutineContext

@extension
interface MaybeKFunctorInstance : Functor<ForMaybeK> {
  override fun <A, B> Kind<ForMaybeK, A>.map(f: (A) -> B): MaybeK<B> =
    fix().map(f)
}

@extension
interface MaybeKApplicativeInstance : Applicative<ForMaybeK> {
  override fun <A, B> MaybeKOf<A>.ap(ff: MaybeKOf<(A) -> B>): MaybeK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForMaybeK, A>.map(f: (A) -> B): MaybeK<B> =
    fix().map(f)

  override fun <A> just(a: A): MaybeK<A> =
    MaybeK.just(a)
}

@extension
interface MaybeKMonadInstance : Monad<ForMaybeK> {
  override fun <A, B> MaybeKOf<A>.ap(ff: MaybeKOf<(A) -> B>): MaybeK<B> =
    fix().ap(ff)

  override fun <A, B> MaybeKOf<A>.flatMap(f: (A) -> Kind<ForMaybeK, B>): MaybeK<B> =
    fix().flatMap(f)

  override fun <A, B> MaybeKOf<A>.map(f: (A) -> B): MaybeK<B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, MaybeKOf<arrow.core.Either<A, B>>>): MaybeK<B> =
    MaybeK.tailRecM(a, f)

  override fun <A> just(a: A): MaybeK<A> =
    MaybeK.just(a)
}

@extension
interface MaybeKFoldableInstance : Foldable<ForMaybeK> {

  override fun <A, B> Kind<ForMaybeK, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForMaybeK, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)

  override fun <A> Kind<ForMaybeK, A>.isEmpty(): Boolean =
    fix().isEmpty()

  override fun <A> Kind<ForMaybeK, A>.exists(p: (A) -> Boolean): Boolean =
    fix().exists(p)

  override fun <A> MaybeKOf<A>.forAll(p: (A) -> Boolean): Boolean =
    fix().forall(p)

  override fun <A> Kind<ForMaybeK, A>.nonEmpty(): Boolean =
    fix().nonEmpty()
}

@extension
interface MaybeKApplicativeErrorInstance :
  ApplicativeError<ForMaybeK, Throwable>,
  MaybeKApplicativeInstance {
  override fun <A> raiseError(e: Throwable): MaybeK<A> =
    MaybeK.raiseError(e)

  override fun <A> MaybeKOf<A>.handleErrorWith(f: (Throwable) -> MaybeKOf<A>): MaybeK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface MaybeKMonadErrorInstance :
  MonadError<ForMaybeK, Throwable>,
  MaybeKMonadInstance {
  override fun <A> raiseError(e: Throwable): MaybeK<A> =
    MaybeK.raiseError(e)

  override fun <A> MaybeKOf<A>.handleErrorWith(f: (Throwable) -> MaybeKOf<A>): MaybeK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@extension
interface MaybeKMonadThrowInstance : MonadThrow<ForMaybeK>, MaybeKMonadErrorInstance

@extension
interface MaybeKBracketInstance : Bracket<ForMaybeK, Throwable>, MaybeKMonadThrowInstance {
  override fun <A, B> Kind<ForMaybeK, A>.bracketCase(release: (A, ExitCase<Throwable>) -> Kind<ForMaybeK, Unit>, use: (A) -> Kind<ForMaybeK, B>): MaybeK<B> =
    fix().bracketCase({ use(it) }, { a, e -> release(a, e) })
}

@extension
interface MaybeKMonadDeferInstance : MonadDefer<ForMaybeK>, MaybeKBracketInstance {
  override fun <A> defer(fa: () -> MaybeKOf<A>): MaybeK<A> =
    MaybeK.defer(fa)
}

@extension
interface MaybeKAsyncInstance : Async<ForMaybeK>, MaybeKMonadDeferInstance {
  override fun <A> async(fa: Proc<A>): MaybeK<A> =
    MaybeK.async(fa)

  override fun <A> MaybeKOf<A>.continueOn(ctx: CoroutineContext): MaybeK<A> =
    fix().continueOn(ctx)
}

@extension
interface MaybeKEffectInstance :
  Effect<ForMaybeK>,
  MaybeKAsyncInstance {
  override fun <A> MaybeKOf<A>.runAsync(cb: (Either<Throwable, A>) -> MaybeKOf<Unit>): MaybeK<Unit> =
    fix().runAsync(cb)
}

object MaybeKContext : MaybeKEffectInstance

@Deprecated(ExtensionsDSLDeprecated)
infix fun <A> ForMaybeK.Companion.extensions(f: MaybeKContext.() -> A): A =
  f(MaybeKContext)
