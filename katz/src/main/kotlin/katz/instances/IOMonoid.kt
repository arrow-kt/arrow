package katz

class IOMonoid<A>(val SM: Monoid<A>) : Monoid<HK<IO.F, A>> {
    override fun empty(): HK<IO.F, A> =
            IO.pure(SM.empty())

    override fun combine(ioa: HK<IO.F, A>, iob: HK<IO.F, A>): HK<IO.F, A> =
            ioa.ev().flatMap { a1 -> iob.ev().map { a2 -> SM.combine(a1, a2) } }

    companion object {
        inline operator fun <reified A> invoke(SM: Monoid<A> = monoid<A>(), dummy: Unit = Unit) =
                IOMonoid(SM)
    }
}
