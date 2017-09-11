package kategory

interface ListKWSemigroupInstance<A> : Semigroup<ListKW<A>> {
    override fun combine(a: ListKW<A>, b: ListKW<A>): ListKW<A> = (a + b).k()
}

object ListKWSemigroupInstanceImplicits {
    @JvmStatic
    fun <A> instance(): ListKWSemigroupInstance<A> = object : ListKWSemigroupInstance<A> {}
}

interface ListKWMonoidInstance<A> : ListKWSemigroupInstance<A>, Monoid<ListKW<A>> {
    override fun empty(): ListKW<A> = emptyList<A>().k()
}

object ListKWMonoidInstanceImplicits {
    @JvmStatic
    fun <A> instance(): ListKWMonoidInstance<A> = object : ListKWMonoidInstance<A> {}
}
