package katz

import java.util.Vector

interface NonEmptyVectorComonad : Comonad<NonEmptyVector.F> {

    override fun <A, B> coflatMap(fa: NonEmptyVectorKind<A>, f: (NonEmptyVectorKind<A>) -> B): NonEmptyVectorKind<B> {
        val buf = mutableListOf<B>()
        tailrec fun consume(list: List<A>): List<B> =
                if (list.isEmpty()) {
                    buf
                } else {
                    val tail = list.subList(1, list.size)
                    buf += f(NonEmptyVector(list[0], Vector<A>(tail)))
                    consume(tail)
                }

        return NonEmptyVector(f(fa), Vector(consume(fa.ev().tail)))
    }

    override fun <A> extract(fa: NonEmptyVectorKind<A>): A =
            fa.ev().head
}