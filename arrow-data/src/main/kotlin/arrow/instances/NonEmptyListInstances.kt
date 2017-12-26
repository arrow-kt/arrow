package arrow

@instance(NonEmptyList::class)
interface NonEmptyListSemigroupInstance<A> : Semigroup<NonEmptyList<A>> {
    override fun combine(a: NonEmptyList<A>, b: NonEmptyList<A>): NonEmptyList<A> = a + b
}

@instance(NonEmptyList::class)
interface NonEmptyListEqInstance<A> : Eq<NonEmptyList<A>> {

    fun EQ(): Eq<A>

    override fun eqv(a: NonEmptyList<A>, b: NonEmptyList<A>): Boolean =
            a.all.zip(b.all) { aa, bb -> EQ().eqv(aa, bb) }.fold(true) { acc, bool ->
                acc && bool
            }
}