package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.effects.continuations.IOContinuation
import arrow.instance
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.MonadError
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.typeclasses.continuations.BindingCatchContinuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.startCoroutine

@instance(IO::class)
interface IOApplicativeErrorInstance : IOApplicativeInstance, ApplicativeError<ForIO, Throwable> {
    override fun <A> attempt(fa: IOOf<A>): IO<Either<Throwable, A>> =
            fa.fix().attempt()

    override fun <A> handleErrorWith(fa: IOOf<A>, f: (Throwable) -> IOOf<A>): IO<A> =
            fa.fix().handleErrorWith(f)

    override fun <A> raiseError(e: Throwable): IO<A> =
            IO.raiseError(e)
}

@instance(IO::class)
interface IOMonadInstance : IOApplicativeInstance, arrow.typeclasses.Monad<ForIO> {
    override fun <A, B> flatMap(fa: arrow.effects.IOOf<A>, f: kotlin.Function1<A, arrow.effects.IOOf<B>>): arrow.effects.IO<B> =
            fa.fix().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForIO, Either<A, B>>): IO<B> =
            IO.tailRecM(a, f)

    override fun <A, B> flatMapIn(cc: CoroutineContext, fa: Kind<ForIO, A>, f: (A) -> Kind<ForIO, B>): Kind<ForIO, B> =
            fa.fix().flatMapIn(cc, f)

    override fun <A, B> ap(fa: IOOf<A>, ff: IOOf<(A) -> B>): IO<B> {
        return super<IOApplicativeInstance>.ap(fa, ff)
    }

    override fun <A, B> map(fa: IOOf<A>, f: (A) -> B): IO<B> {
        return super<IOApplicativeInstance>.map(fa, f)
    }
}

@instance(IO::class)
interface IOMonadErrorInstance : IOApplicativeErrorInstance, IOMonadInstance, MonadError<ForIO, Throwable> {
    override fun <B> bindingCatch(cc: CoroutineContext, catch: (Throwable) -> Throwable, c: suspend BindingCatchContinuation<ForIO, Throwable, *>.() -> B): Kind<ForIO, B> =
            IO.async { cont: (Either<Throwable, B>) -> Unit ->
                val mapCont: (Either<Throwable, B>) -> Unit = { it.fold({ cont(Either.left(catch(it))) }, { cont(Either.right(it)) }) }
                val continuation = IOContinuation(mapCont, cc)
                val coro: suspend () -> IO<B> = {
                    val value = c(continuation)
                    cont(Either.right(value))
                    IO.pure(value)
                }
                coro.startCoroutine(continuation)
            }

    override fun <A, B> ap(fa: IOOf<A>, ff: IOOf<(A) -> B>): IO<B> =
            super<IOMonadInstance>.ap(fa, ff).fix()

    override fun <A, B> map(fa: IOOf<A>, f: (A) -> B): IO<B> =
            super<IOMonadInstance>.map(fa, f)

    override fun <A> pure(a: A): IO<A> =
            super<IOMonadInstance>.pure(a)
}

@instance(IO::class)
interface IOMonadSuspendInstance : IOMonadErrorInstance, MonadSuspend<ForIO> {
    override fun <A> suspend(fa: () -> IOOf<A>): IO<A> =
            IO.suspend(fa)

    override fun lazy(): IO<Unit> = IO.lazy
}

@instance(IO::class)
interface IOAsyncInstance : IOMonadSuspendInstance, Async<ForIO> {
    override fun <A> async(fa: Proc<A>): IO<A> =
            IO.async(fa)

    override fun <A> invoke(fa: () -> A): IO<A> =
            IO.invoke(fa)
}

@instance(IO::class)
interface IOEffectInstance : IOAsyncInstance, Effect<ForIO> {
    override fun <A> runAsync(fa: Kind<ForIO, A>, cb: (Either<Throwable, A>) -> IOOf<Unit>): IO<Unit> =
            fa.fix().runAsync(cb)
}

@instance(IO::class)
interface IOMonoidInstance<A> : Monoid<Kind<ForIO, A>>, Semigroup<Kind<ForIO, A>> {

    fun SM(): Monoid<A>

    override fun combine(a: IOOf<A>, b: IOOf<A>): IO<A> =
            a.fix().flatMap { a1: A -> b.fix().map { a2: A -> SM().combine(a1, a2) } }

    override fun empty(): IO<A> = IO.pure(SM().empty())
}

@instance(IO::class)
interface IOSemigroupInstance<A> : Semigroup<Kind<ForIO, A>> {

    fun SG(): Semigroup<A>

    override fun combine(a: IOOf<A>, b: IOOf<A>): IO<A> =
            a.fix().flatMap { a1: A -> b.fix().map { a2: A -> SG().combine(a1, a2) } }
}
