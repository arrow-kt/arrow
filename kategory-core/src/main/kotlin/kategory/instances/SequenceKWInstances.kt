package kategory

interface SequenceKWSemigroupInstance<A> : Semigroup<SequenceKW<A>> {
    override fun combine(a: SequenceKW<A>, b: SequenceKW<A>): SequenceKW<A> = (a + b).k()
}

object SequenceKWSemigroupInstanceImplicits {
    @JvmStatic
    fun <A> instance(): SequenceKWSemigroupInstance<A> = object : SequenceKWSemigroupInstance<A> {}
}

interface SequenceKWMonoidInstance<A> : Monoid<SequenceKW<A>> {
    override fun combine(a: SequenceKW<A>, b: SequenceKW<A>): SequenceKW<A> = (a + b).k()

    override fun empty(): SequenceKW<A> = emptySequence<A>().k()
}

object SequenceKWMonoidInstanceImplicits {
    @JvmStatic
    fun <A> instance(): SequenceKWMonoidInstance<A> = object : SequenceKWMonoidInstance<A> {}
}