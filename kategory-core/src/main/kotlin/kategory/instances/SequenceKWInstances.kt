package kategory

@instance(SequenceKW::class)
interface SequenceKWSemigroupInstance<A> : Semigroup<SequenceKW<A>> {
    override fun combine(a: SequenceKW<A>, b: SequenceKW<A>): SequenceKW<A> = (a + b).k()
}

@instance(SequenceKW::class)
interface SequenceKWMonoidInstance<A> : Monoid<SequenceKW<A>> {
    override fun combine(a: SequenceKW<A>, b: SequenceKW<A>): SequenceKW<A> = (a + b).k()

    override fun empty(): SequenceKW<A> = emptySequence<A>().k()
}
