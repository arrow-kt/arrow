package katz

class IOSemigroup<A>(val SM: Semigroup<A>) : Semigroup<HK<IO.F, A>> {
    override fun combine(ioa: HK<IO.F, A>, iob: HK<IO.F, A>): HK<IO.F, A> =
            ioa.ev().flatMap { a1 -> iob.ev().map { a2 -> SM.combine(a1, a2) } }

    companion object {
        inline operator fun <reified A> invoke(SM: Semigroup<A> = semigroup<A>(), dummy: Unit = Unit) =
                IOSemigroup(SM)
    }
}
