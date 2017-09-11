package kategory

interface SetKWMonoid<A> : Monoid<SetKW<A>> {
    override fun combine(a: SetKW<A>, b: SetKW<A>): SetKW<A> = (a + b).k()

    override fun empty(): SetKW<A> = emptySet<A>().k()
}

