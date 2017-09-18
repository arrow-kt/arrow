package kategory

@instance(ListKW::class)
interface ListKWSemigroupInstance<A> : Semigroup<ListKW<A>> {
    override fun combine(a: ListKW<A>, b: ListKW<A>): ListKW<A> = (a + b).k()
}

@instance(ListKW::class)
interface ListKWMonoidInstance<A> : ListKWSemigroupInstance<A>, Monoid<ListKW<A>> {
    override fun empty(): ListKW<A> = emptyList<A>().k()
}
