package katz

interface NonEmptyListComonad : Comonad<NonEmptyList.F> {
    override fun <A, B> coflatMap(fa: NonEmptyListKind<A>, f: (NonEmptyListKind<A>) -> B): NonEmptyListKind<B> {
        val buf = mutableListOf<B>()
        tailrec fun consume(list: List<A>): List<B> =
                if (list.isEmpty()) {
                    buf
                } else {
                    val tail = list.subList(1, list.size)
                    buf += f(NonEmptyList(list[0], tail))
                    consume(tail)
                }
        return NonEmptyList(f(fa), consume(fa.ev().tail))
    }

    override fun <A> extract(fa: NonEmptyListKind<A>): A =
            fa.ev().head
}
