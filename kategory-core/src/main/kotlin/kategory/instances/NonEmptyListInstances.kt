package kategory

interface NonEmptyListSemigroup<A> : Semigroup<NonEmptyList<A>> {
    override fun combine(a: NonEmptyList<A>, b: NonEmptyList<A>): NonEmptyList<A> = a + b
}

object NonEmptyListSemigroupInstanceImplicits {
    @JvmStatic
    fun <A> instance(): NonEmptyListSemigroup<A> = object : NonEmptyListSemigroup<A> {}
}
