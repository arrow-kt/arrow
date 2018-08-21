package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.effects.typeclasses.*
import arrow.instance
import arrow.typeclasses.*
import kotlin.coroutines.experimental.CoroutineContext
import arrow.effects.ap as ioAp
import arrow.effects.handleErrorWith as ioHandleErrorWith

@instance(IO::class)
interface IOFunctorInstance : Functor<ForIO> {
  override fun <A, B> Kind<ForIO, A>.map(f: (A) -> B): IO<B> =
    fix().map(f)
}

@instance(IO::class)
interface IOApplicativeInstance : Applicative<ForIO> {
  override fun <A, B> Kind<ForIO, A>.map(f: (A) -> B): IO<B> =
    fix().map(f)

  override fun <A> just(a: A): IO<A> =
    IO.just(a)

  override fun <A, B> Kind<ForIO, A>.ap(ff: IOOf<(A) -> B>): IO<B> =
    fix().ioAp(ff)
}

@instance(IO::class)
interface IOMonadInstance : Monad<ForIO> {
  override fun <A, B> Kind<ForIO, A>.flatMap(f: (A) -> Kind<ForIO, B>): IO<B> =
    fix().flatMap(f)

  override fun <A, B> Kind<ForIO, A>.map(f: (A) -> B): IO<B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, IOOf<arrow.core.Either<A, B>>>): IO<B> =
    IO.tailRecM(a, f)

  override fun <A> just(a: A): IO<A> =
    IO.just(a)
}

@instance(IO::class)
interface IOApplicativeErrorInstance : IOApplicativeInstance, ApplicativeError<ForIO, Throwable> {
  override fun <A> Kind<ForIO, A>.attempt(): IO<Either<Throwable, A>> =
    fix().attempt()

  override fun <A> Kind<ForIO, A>.handleErrorWith(f: (Throwable) -> Kind<ForIO, A>): IO<A> =
    fix().ioHandleErrorWith(f)

  override fun <A> raiseError(e: Throwable): IO<A> =
    IO.raiseError(e)
}

@instance(IO::class)
interface IOMonadErrorInstance : IOMonadInstance, MonadError<ForIO, Throwable> {
  override fun <A> Kind<ForIO, A>.attempt(): IO<Either<Throwable, A>> =
    fix().attempt()

  override fun <A> Kind<ForIO, A>.handleErrorWith(f: (Throwable) -> Kind<ForIO, A>): IO<A> =
    fix().ioHandleErrorWith(f)

  override fun <A> raiseError(e: Throwable): IO<A> =
    IO.raiseError(e)
}

@instance(IO::class)
interface IOMonadDeferInstance : IOMonadErrorInstance, MonadDefer<ForIO> {
  override fun <A> defer(fa: () -> IOOf<A>): IO<A> =
    IO.defer(fa)

  override fun lazy(): IO<Unit> = IO.lazy
}

@instance(IO::class)
interface IOAsyncInstance : IOMonadDeferInstance, Async<ForIO> {
  override fun <A> async(fa: Proc<A>): IO<A> =
    IO.async(fa)

  override fun <A> IOOf<A>.continueOn(ctx: CoroutineContext): Kind<ForIO, A> =
    fix().continueOn(ctx)

  override fun <A> invoke(f: () -> A): IO<A> =
    IO.invoke(f)
}

@instance(IO::class)
interface IOEffectInstance : IOAsyncInstance, Effect<ForIO> {
  override fun <A> Kind<ForIO, A>.runAsync(cb: (Either<Throwable, A>) -> Kind<ForIO, Unit>): IO<Unit> =
    fix().runAsync(cb)
}

@instance(IO::class)
interface IOCancellableEffectInstance : IOEffectInstance, CancellableEffect<ForIO> {
  override fun <A> Kind<ForIO, A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> Kind<ForIO, Unit>): IO<Disposable> =
    fix().runAsyncCancellable(OnCancel.ThrowCancellationException, cb)
}

@instance(IO::class)
interface IOMonoidInstance<A> : Monoid<Kind<ForIO, A>>, Semigroup<Kind<ForIO, A>> {

  fun SM(): Monoid<A>

  override fun IOOf<A>.combine(b: IOOf<A>): IO<A> =
    fix().flatMap { a1: A -> b.fix().map { a2: A -> SM().run { a1.combine(a2) } } }

  override fun empty(): IO<A> = IO.just(SM().empty())
}

@instance(IO::class)
interface IOSemigroupInstance<A> : Semigroup<Kind<ForIO, A>> {

  fun SG(): Semigroup<A>

  override fun IOOf<A>.combine(b: IOOf<A>): IO<A> =
    fix().flatMap { a1: A -> b.fix().map { a2: A -> SG().run { a1.combine(a2) } } }
}

object IOContext : IOEffectInstance

infix fun <A> ForIO.Companion.extensions(f: IOContext.() -> A): A =
  f(IOContext)