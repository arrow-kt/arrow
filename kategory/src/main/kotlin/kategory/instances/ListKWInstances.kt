package kategory

interface ListKWMonoid<A> : Monoid<ListKW<A>> {
    override fun combine(a: ListKW<A>, b: ListKW<A>): ListKW<A> = (a + b).k()

    override fun empty(): ListKW<A> = emptyList<A>().k()
}
