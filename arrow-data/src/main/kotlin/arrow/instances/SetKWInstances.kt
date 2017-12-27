package arrow

@instance(SetKW::class)
interface SetKWSemigroupInstance<A> : Semigroup<SetKW<A>> {
    override fun combine(a: SetKW<A>, b: SetKW<A>): SetKW<A> = (a + b).k()
}

@instance(SetKW::class)
interface SetKWMonoidInstance<A> : SetKWSemigroupInstance<A>, Monoid<SetKW<A>> {
    override fun empty(): SetKW<A> = emptySet<A>().k()
}

@instance(SetKW::class)
interface SetKWEqInstance<A> : Eq<SetKW<A>> {

    fun EQ(): Eq<A>

    override fun eqv(a: SetKW<A>, b: SetKW<A>): Boolean =
            if (a.size == b.size) a.set.map { aa ->
                b.find { bb -> EQ().eqv(aa, bb) }.nonEmpty()
            }.fold(true) { acc, bool ->
                acc && bool
            }
            else false

}