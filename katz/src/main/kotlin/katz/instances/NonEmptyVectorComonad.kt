package katz

interface NonEmptyVectorComonad : Comonad<NonEmptyVector.F> {

    override fun <A, B> coflatMap(fa: NonEmptyVectorKind<A>, f: (NonEmptyVectorKind<A>) -> B): NonEmptyVectorKind<B> {
//        val buf = mutableListOf<B>()
//
//        tailrec fun consume(list: List<A>): List<B> =
//                if (list.isEmpty()) {
//                    buf
//                } else {
//                    val tail = list.subList(1, list.size)
//                    buf += f(NonEmptyVector(list[0], Vector<A>(tail)))
//                    consume(tail)
//                }
//
//        return NonEmptyVector(f(fa), Vector(consume(fa.ev().tail)))

        val transformedHead: B = f(fa)
        val tail: Array<A> = fa.ev().tail
        val newHead: A = tail[0]
        val newTail = tail.copyOfRange(1, tail.size)
        val newNonEmptyVector: NonEmptyVector<A> = NonEmptyVector(newHead, newTail)
        val transformedNewNonEmptyVector: B = f(newNonEmptyVector)

        TODO()
    }

    override fun <A> extract(fa: NonEmptyVectorKind<A>): A =
            fa.ev().head
}