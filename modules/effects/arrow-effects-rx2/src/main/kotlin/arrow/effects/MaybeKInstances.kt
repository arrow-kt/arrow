package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.effects.typeclasses.Async
import arrow.effects.typeclasses.Effect
import arrow.effects.typeclasses.MonadDefer
import arrow.effects.typeclasses.Proc
import arrow.instance
import arrow.typeclasses.*

@instance(MaybeK::class)
interface MaybeKFunctorInstance : Functor<ForMaybeK> {
  override fun <A, B> Kind<ForMaybeK, A>.map(f: (A) -> B): MaybeK<B> =
    fix().map(f)
}

@instance(MaybeK::class)
interface MaybeKApplicativeInstance : Applicative<ForMaybeK> {
  override fun <A, B> MaybeKOf<A>.ap(ff: MaybeKOf<(A) -> B>): MaybeK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForMaybeK, A>.map(f: (A) -> B): MaybeK<B> =
    fix().map(f)

  override fun <A> just(a: A): MaybeK<A> =
    MaybeK.just(a)
}

@instance(MaybeK::class)
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

@instance(MaybeK::class)
interface MaybeKApplicativeErrorInstance :
  MaybeKApplicativeInstance,
  ApplicativeError<ForMaybeK, Throwable> {
  override fun <A> raiseError(e: Throwable): MaybeK<A> =
    MaybeK.raiseError(e)

  override fun <A> MaybeKOf<A>.handleErrorWith(f: (Throwable) -> MaybeKOf<A>): MaybeK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@instance(MaybeK::class)
interface MaybeKMonadErrorInstance :
  MaybeKMonadInstance,
  MonadError<ForMaybeK, Throwable> {
  override fun <A> raiseError(e: Throwable): MaybeK<A> =
    MaybeK.raiseError(e)

  override fun <A> MaybeKOf<A>.handleErrorWith(f: (Throwable) -> MaybeKOf<A>): MaybeK<A> =
    fix().handleErrorWith { f(it).fix() }
}

@instance(MaybeK::class)
interface MaybeKMonadDeferInstance :
  MaybeKMonadErrorInstance,
  MonadDefer<ForMaybeK> {
  override fun <A> defer(fa: () -> MaybeKOf<A>): MaybeK<A> =
    MaybeK.defer(fa)
}

@instance(MaybeK::class)
interface MaybeKAsyncInstance :
  MaybeKMonadDeferInstance,
  Async<ForMaybeK> {
  override fun <A> async(fa: Proc<A>): MaybeK<A> =
    MaybeK.async(fa)
}

@instance(MaybeK::class)
interface MaybeKEffectInstance :
  MaybeKAsyncInstance,
  Effect<ForMaybeK> {
  override fun <A> MaybeKOf<A>.runAsync(cb: (Either<Throwable, A>) -> MaybeKOf<Unit>): MaybeK<Unit> =
    fix().runAsync(cb)
}
