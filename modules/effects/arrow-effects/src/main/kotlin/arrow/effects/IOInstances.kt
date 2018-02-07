package arrow.effects

import arrow.HK
import arrow.core.Either
import arrow.instance
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.MonadError
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup

@instance(IO::class)
interface IOApplicativeErrorInstance : IOApplicativeInstance, ApplicativeError<IOHK, Throwable> {
    override fun <A> attempt(fa: IOKind<A>): IO<Either<Throwable, A>> =
            fa.ev().attempt()

    override fun <A> handleErrorWith(fa: IOKind<A>, f: (Throwable) -> IOKind<A>): IO<A> =
            fa.ev().handleErrorWith(f)

    override fun <A> raiseError(e: Throwable): IO<A> =
            IO.raiseError(e)
}

@instance(IO::class)
interface IOMonadErrorInstance : IOApplicativeErrorInstance, IOMonadInstance, MonadError<IOHK, Throwable> {
    override fun <A, B> ap(fa: IOKind<A>, ff: IOKind<(A) -> B>): IO<B> =
            super<IOMonadInstance>.ap(fa, ff).ev()

    override fun <A, B> map(fa: IOKind<A>, f: (A) -> B): IO<B> =
            super<IOMonadInstance>.map(fa, f)

    override fun <A> pure(a: A): IO<A> =
            super<IOMonadInstance>.pure(a)
}

@instance(IO::class)
interface IOMonadSuspendInstance : IOMonadErrorInstance, MonadSuspend<IOHK> {
    override fun <A> suspend(fa: () -> IOKind<A>): IO<A> =
            IO.suspend(fa)

    override fun lazy(): IO<Unit> = IO.lazy
}

@instance(IO::class)
interface IOAsyncInstance : IOMonadSuspendInstance, Async<IOHK> {
    override fun <A> async(fa: Proc<A>): IO<A> =
            IO.async(fa)

    override fun <A> invoke(fa: () -> A): IO<A> =
            IO.invoke(fa)
}

@instance(IO::class)
interface IOEffectInstance : IOAsyncInstance, Effect<IOHK> {
    override fun <A> runAsync(fa: HK<IOHK, A>, cb: (Either<Throwable, A>) -> IOKind<Unit>): IO<Unit> =
            fa.ev().runAsync(cb)
}

@instance(IO::class)
interface IOMonoidInstance<A> : Monoid<HK<IOHK, A>>, Semigroup<HK<IOHK, A>> {

    fun SM(): Monoid<A>

    override fun combine(a: IOKind<A>, b: IOKind<A>): IO<A> =
            a.ev().flatMap { a1: A -> b.ev().map { a2: A -> SM().combine(a1, a2) } }

    override fun empty(): IO<A> = IO.pure(SM().empty())
}

@instance(IO::class)
interface IOSemigroupInstance<A> : Semigroup<HK<IOHK, A>> {

    fun SG(): Semigroup<A>

    override fun combine(a: IOKind<A>, b: IOKind<A>): IO<A> =
            a.ev().flatMap { a1: A -> b.ev().map { a2: A -> SG().combine(a1, a2) } }
}
