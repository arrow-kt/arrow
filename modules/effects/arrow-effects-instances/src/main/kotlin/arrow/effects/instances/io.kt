package arrow.effects.instances

import arrow.Kind
import arrow.core.Either
import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.effects.*
import arrow.effects.typeclasses.*
import arrow.extension
import arrow.typeclasses.*
import kotlin.coroutines.CoroutineContext
import arrow.effects.ap as ioAp
import arrow.effects.handleErrorWith as ioHandleErrorWith

@extension
interface IOFunctorInstance : Functor<ForIO> {
  override fun <A, B> Kind<ForIO, A>.map(f: (A) -> B): IO<B> =
    fix().map(f)
}

@extension
interface IOApplicativeInstance : Applicative<ForIO> {
  override fun <A, B> Kind<ForIO, A>.map(f: (A) -> B): IO<B> =
    fix().map(f)

  override fun <A> just(a: A): IO<A> =
    IO.just(a)

  override fun <A, B> Kind<ForIO, A>.ap(ff: IOOf<(A) -> B>): IO<B> =
    fix().ioAp(ff)
}

@extension
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

@extension
interface IOApplicativeErrorInstance : ApplicativeError<ForIO, Throwable>, IOApplicativeInstance {
  override fun <A> Kind<ForIO, A>.attempt(): IO<Either<Throwable, A>> =
    fix().attempt()

  override fun <A> Kind<ForIO, A>.handleErrorWith(f: (Throwable) -> Kind<ForIO, A>): IO<A> =
    fix().ioHandleErrorWith(f)

  override fun <A> raiseError(e: Throwable): IO<A> =
    IO.raiseError(e)
}

@extension
interface IOMonadErrorInstance : MonadError<ForIO, Throwable>, IOMonadInstance {
  override fun <A> Kind<ForIO, A>.attempt(): IO<Either<Throwable, A>> =
    fix().attempt()

  override fun <A> Kind<ForIO, A>.handleErrorWith(f: (Throwable) -> Kind<ForIO, A>): IO<A> =
    fix().ioHandleErrorWith(f)

  override fun <A> raiseError(e: Throwable): IO<A> =
    IO.raiseError(e)
}

@extension
interface IOMonadThrowInstance : MonadThrow<ForIO>, IOMonadErrorInstance

@extension
interface IOBracketInstance : Bracket<ForIO, Throwable>, IOMonadThrowInstance {
  override fun <A, B> Kind<ForIO, A>.bracketCase(release: (A, ExitCase<Throwable>) -> Kind<ForIO, Unit>, use: (A) -> Kind<ForIO, B>): IO<B> =
    fix().bracketCase({ a, e -> release(a, e).fix() }, { a -> use(a).fix() })

  override fun <A, B> Kind<ForIO, A>.bracket(release: (A) -> Kind<ForIO, Unit>, use: (A) -> Kind<ForIO, B>): IO<B> =
    fix().bracket({ a -> release(a).fix() }, { a -> use(a).fix() })

  override fun <A> Kind<ForIO, A>.guarantee(finalizer: Kind<ForIO, Unit>): IO<A> =
    fix().guarantee(finalizer.fix())

  override fun <A> Kind<ForIO, A>.guaranteeCase(finalizer: (ExitCase<Throwable>) -> Kind<ForIO, Unit>): IO<A> =
    fix().guaranteeCase { e -> finalizer(e).fix() }
}

@extension
interface IOMonadDeferInstance : MonadDefer<ForIO>, IOBracketInstance {
  override fun <A> defer(fa: () -> IOOf<A>): IO<A> =
    IO.defer(fa)

  override fun lazy(): IO<Unit> = IO.lazy
}

@extension
interface IOAsyncInstance : Async<ForIO>, IOMonadDeferInstance {
  override fun <A> async(fa: Proc<A>): IO<A> =
    IO.async(fa.toIOProc())

  override fun <A> IOOf<A>.continueOn(ctx: CoroutineContext): IO<A> =
    fix().continueOn(ctx)
}

@extension
interface IOEffectInstance : Effect<ForIO>, IOAsyncInstance {
  override fun <A> Kind<ForIO, A>.runAsync(cb: (Either<Throwable, A>) -> Kind<ForIO, Unit>): IO<Unit> =
    fix().runAsync(cb)
}

@extension
interface IOConcurrentEffectInstance : ConcurrentEffect<ForIO>, IOEffectInstance {
  override fun <A> Kind<ForIO, A>.runAsyncCancellable(cb: (Either<Throwable, A>) -> Kind<ForIO, Unit>): IO<Disposable> =
    fix().runAsyncCancellable(OnCancel.ThrowCancellationException, cb)
}

interface IOSemigroupInstance<A> : Semigroup<IO<A>> {

  fun SG(): Semigroup<A>

  override fun IO<A>.combine(b: IO<A>): IO<A> =
    flatMap { a1: A -> b.map { a2: A -> SG().run { a1.combine(a2) } } }
}

@extension
interface IOMonoidInstance<A> : Monoid<IO<A>>, IOSemigroupInstance<A> {

  override fun SG(): Semigroup<A> = SM()

  fun SM(): Monoid<A>

  override fun IO<A>.combine(b: IO<A>): IO<A> =
    flatMap { a1: A -> b.map { a2: A -> SM().run { a1.combine(a2) } } }

  override fun empty(): IO<A> = IO.just(SM().empty())
}

object IOContext : IOConcurrentEffectInstance

@Deprecated(ExtensionsDSLDeprecated)
infix fun <A> ForIO.Companion.extensions(f: IOContext.() -> A): A =
  f(IOContext)
