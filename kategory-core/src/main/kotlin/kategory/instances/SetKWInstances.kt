package kategory

interface SetKWSemigroupInstance<A> : Semigroup<SetKW<A>> {
    override fun combine(a: SetKW<A>, b: SetKW<A>): SetKW<A> = (a + b).k()
}

object SetKWSemigroupInstanceImplicits {
    @JvmStatic
    fun <A> instance(): SetKWSemigroupInstance<A> = object : SetKWSemigroupInstance<A> {}
}

interface SetKWMonoidInstance<A> : Monoid<SetKW<A>> {
    override fun combine(a: SetKW<A>, b: SetKW<A>): SetKW<A> = (a + b).k()

    override fun empty(): SetKW<A> = emptySet<A>().k()
}

object SetKWMonoidInstanceImplicits {
    @JvmStatic
    fun <A> instance(): SetKWMonoidInstance<A> = object : SetKWMonoidInstance<A> {}
}
