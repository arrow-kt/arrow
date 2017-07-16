package kategory

class IOMonoid<A>(val SM: Monoid<A>, val SG: Semigroup<HK<IO.F, A>> = IOSemigroup(SM)) : Monoid<HK<IO.F, A>>, Semigroup<HK<IO.F, A>> by SG {

    override fun empty(): IO<A> =
            IO.pure(SM.empty())

    companion object {
        inline operator fun <reified A> invoke(SM: Monoid<A> = monoid<A>(), dummy: Unit = Unit) =
                IOMonoid(SM)
    }
}
