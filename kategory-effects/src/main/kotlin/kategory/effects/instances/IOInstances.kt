package kategory.effects

import kategory.*

@instance(IO::class)
interface IOMonadErrorInstance : IOMonadInstance, MonadError<IOHK, Throwable> {
    override fun <A> attempt(fa: IOKind<A>): IO<Either<Throwable, A>> =
            fa.ev().attempt()

    override fun <A> handleErrorWith(fa: IOKind<A>, f: (Throwable) -> IOKind<A>): IO<A> =
            fa.ev().handleErrorWith(f)

    override fun <A> raiseError(e: Throwable): IO<A> =
            IO.raiseError(e)
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
