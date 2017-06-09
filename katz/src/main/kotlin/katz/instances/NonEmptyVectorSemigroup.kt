package katz

class NonEmptyVectorSemigroup<A> : Semigroup<NonEmptyVector<A>> {
    override fun combine(a: NonEmptyVector<A>, b: NonEmptyVector<A>): NonEmptyVector<A> = a + b
}