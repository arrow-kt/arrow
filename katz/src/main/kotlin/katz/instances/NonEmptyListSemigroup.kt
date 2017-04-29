package katz

class NonEmptyListSemigroup<A> : Semigroup<NonEmptyList<A>> {
    override fun combine(a: NonEmptyList<A>, b: NonEmptyList<A>): NonEmptyList<A> =
            NonEmptyList.fromListUnsafe(a.all + b.all)
}
