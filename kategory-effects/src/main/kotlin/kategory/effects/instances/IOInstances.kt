package kategory

interface IOMonadErrorInstance : IOMonadInstance, MonadError<IOHK, Throwable> {
    override fun <A> attempt(fa: IOKind<A>): IO<Either<Throwable, A>> =
            fa.ev().attempt()

    override fun <A> handleErrorWith(fa: IOKind<A>, f: (Throwable) -> IOKind<A>): IO<A> =
            fa.ev().handleErrorWith(f)

    override fun <A> raiseError(e: Throwable): IO<A> =
            IO.raiseError(e)
}

object IOMonadErrorInstanceImplicits {
    @JvmStatic
    fun instance(): IOMonadErrorInstance = object : IOMonadErrorInstance {}
}

interface IOMonoidInstance<A> : Monoid<HK<IOHK, A>>, Semigroup<HK<IOHK, A>> {

    fun SM(): Monoid<A>

    override fun combine(ioa: HK<IOHK, A>, iob: HK<IOHK, A>): IO<A> = ioa.ev().flatMap { a1: A -> iob.ev().map { a2: A -> SM().combine(a1, a2) } }

    override fun empty(): IO<A> = IO.pure(SM().empty())
}

object IOMonoidInstanceImplicits {
    @JvmStatic
    fun <A> instance(SM: Monoid<A>): IOMonoidInstance<A> = object : IOMonoidInstance<A> {
        override fun SM(): Monoid<A> = SM
    }
}

interface IOSemigroupInstance<A> : Semigroup<HK<IOHK, A>> {

    fun SG(): Semigroup<A>

    override fun combine(ioa: HK<IOHK, A>, iob: HK<IOHK, A>): IO<A> = ioa.ev().flatMap { a1: A -> iob.ev().map { a2: A -> SG().combine(a1, a2) } }
}

object IOSemigroupInstanceImplicits {
    @JvmStatic
    fun <A> instance(SG: Semigroup<A>): IOSemigroupInstance<A> = object : IOSemigroupInstance<A> {
        override fun SG(): Semigroup<A> = SG
    }
}
