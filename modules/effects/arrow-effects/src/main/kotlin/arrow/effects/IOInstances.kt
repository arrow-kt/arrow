package arrow.effects

import arrow.Kind
import arrow.core.Either
import arrow.instance
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.MonadError
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup

@instance(IO::class)
interface IOApplicativeErrorInstance : IOApplicativeInstance, ApplicativeError<ForIO, Throwable> {
    override fun <A> attempt(fa: IOOf<A>): IO<Either<Throwable, A>> =
            fa.extract().attempt()

    override fun <A> handleErrorWith(fa: IOOf<A>, f: (Throwable) -> IOOf<A>): IO<A> =
            fa.extract().handleErrorWith(f)

    override fun <A> raiseError(e: Throwable): IO<A> =
            IO.raiseError(e)
}

@instance(IO::class)
interface IOMonadErrorInstance : IOApplicativeErrorInstance, IOMonadInstance, MonadError<ForIO, Throwable> {
    override fun <A, B> ap(fa: IOOf<A>, ff: IOOf<(A) -> B>): IO<B> =
            super<IOMonadInstance>.ap(fa, ff).extract()

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
            fa.extract().runAsync(cb)
}

@instance(IO::class)
interface IOMonoidInstance<A> : Monoid<Kind<ForIO, A>>, Semigroup<Kind<ForIO, A>> {

    fun SM(): Monoid<A>

    override fun combine(a: IOOf<A>, b: IOOf<A>): IO<A> =
            a.extract().flatMap { a1: A -> b.extract().map { a2: A -> SM().combine(a1, a2) } }

    override fun empty(): IO<A> = IO.pure(SM().empty())
}

@instance(IO::class)
interface IOSemigroupInstance<A> : Semigroup<Kind<ForIO, A>> {

    fun SG(): Semigroup<A>

    override fun combine(a: IOOf<A>, b: IOOf<A>): IO<A> =
            a.extract().flatMap { a1: A -> b.extract().map { a2: A -> SG().combine(a1, a2) } }
}
