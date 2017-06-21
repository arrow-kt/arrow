package kategory

class NonEmptyListSemigroup<A> : Semigroup<NonEmptyList<A>> {
    override fun combine(a: NonEmptyList<A>, b: NonEmptyList<A>): NonEmptyList<A> = a + b
}
