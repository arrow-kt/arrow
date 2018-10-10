package arrow.effects.io.instances

import arrow.Kind
import arrow.core.Either
import arrow.effects.*
import arrow.effects.typeclasses.*
import arrow.instance
import arrow.typeclasses.*
import kotlin.coroutines.experimental.CoroutineContext
import arrow.effects.ap as ioAp
import arrow.effects.handleErrorWith as ioHandleErrorWith

@instance
interface IOFunctorInstance : Functor<ForIO> {
  override fun <A, B> Kind<ForIO, A>.map(f: (A) -> B): IO<B> =
    fix().map(f)
}

@instance
interface IOApplicativeInstance : Applicative<ForIO> {
  override fun <A, B> Kind<ForIO, A>.map(f: (A) -> B): IO<B> =
    fix().map(f)

  override fun <A> just(a: A): IO<A> =
    IO.just(a)

  override fun <A, B> Kind<ForIO, A>.ap(ff: IOOf<(A) -> B>): IO<B> =
    fix().ioAp(ff)
}

@instance
interface IOMonadInstance : Monad<ForIO> {
  override fun <A, B> Kind<ForIO, A>.flatMap(f: (A) -> Kind<ForIO, B>): IO<B> =
    fix().flatMap(f)

  override fun <A, B> Kind<ForIO, A>.map(f: (A) -> B): IO<B> =
    fix().map(f)

  override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, IOOf<Either<A, B>>>): IO<B> =
    IO.tailRecM(a, f)

  override fun <A> just(a: A): IO<A> =
    IO.just(a)
}

@instance
interface IOApplicativeErrorInstance : ApplicativeError<ForIO, Throwable>, IOApplicativeInstance {
  override fun <A> Kind<ForIO, A>.attempt(): IO<Either<Throwable, A>> =
    fix().attempt()

  override fun <A> Kind<ForIO, A>.handleErrorWith(f: (Throwable) -> Kind<ForIO, A>): IO<A> =
    fix().ioHandleErrorWith(f)

  override fun <A> raiseError(e: Throwable): IO<A> =
    IO.raiseError(e)
}

@instance
interface IOMonadErrorInstance : MonadError<ForIO, Throwable>, IOMonadInstance {
  override fun <A> Kind<ForIO, A>.attempt(): IO<Either<Throwable, A>> =
    fix().attempt()

  override fun <A> Kind<ForIO, A>.handleErrorWith(f: (Throwable) -> Kind<ForIO, A>): IO<A> =
    fix().ioHandleErrorWith(f)

  override fun <A> raiseError(e: Throwable): IO<A> =
    IO.raiseError(e)
}

@instance
interface IOMonadDeferInstance : MonadDefer<ForIO>, IOMonadErrorInstance {
  override fun <A> defer(fa: () -> IOOf<A>): IO<A> =
    IO.defer(fa)

  override fun lazy(): IO<Unit> = IO.lazy
}

@instance
interface IOAsyncInstance : Async<ForIO>, IOMonadDeferInstance {
  override fun <A> async(fa: Proc<A>): IO<A> =
    IO.async(fa)

  override fun <A> IOOf<A>.continueOn(ctx: CoroutineContext): IO<A> =
    fix().continueOn(ctx)

  override fun <A> invoke(f: () -> A): IO<A> =
    IO.invoke(f)
}

@instance
interface IOEffectInstance : Effect<ForIO>, IOAsyncInstance {
  override fun <A> Kind<ForIO, A>.runAsync(cb: (Either<Throwable, A>) -> Kind<ForIO, Unit>): IO<Unit> =
    fix().runAsync(cb)
}

@instance
interface IOConcurrentEffectInstance : ConcurrentEffect<ForIO>, IOEffectInstance {
  override fun <A> Kind<ForIO, A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> Kind<ForIO, Unit>): IO<Disposable> =
    fix().runAsyncCancellable(OnCancel.ThrowCancellationException, cb)
}

interface IOSemigroupInstance<A> : Semigroup<IO<A>> {

  fun SG(): Semigroup<A>

  override fun IO<A>.combine(b: IO<A>): IO<A> =
    flatMap { a1: A -> b.map { a2: A -> SG().run { a1.combine(a2) } } }
}

@instance
interface IOMonoidInstance<A> : Monoid<IO<A>>, IOSemigroupInstance<A> {

  override fun SG(): Semigroup<A> = SM()

  fun SM(): Monoid<A>

  override fun IO<A>.combine(b: IO<A>): IO<A> =
    flatMap { a1: A -> b.map { a2: A -> SM().run { a1.combine(a2) } } }

  override fun empty(): IO<A> = IO.just(SM().empty())
}

object IOContext : IOConcurrentEffectInstance

infix fun <A> ForIO.Companion.extensions(f: IOContext.() -> A): A =
  f(IOContext)