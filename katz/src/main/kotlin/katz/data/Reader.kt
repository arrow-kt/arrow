package katz

class Reader<D, A>(val rd: (D) -> A) : Kleisli<Id.F, D, A>({ d -> Id(rd(d)) }) {

    companion object Factory {

        fun <D, A> pure(x: A): Reader<D, A> = Reader { _ -> x }

        fun <D> ask(): Reader<D, D> = Reader { it }
    }

    fun <B> map(fa: (A) -> B): Reader<D, B> = map(IdMonad, fa)

    fun <B> flatMap(fa: (A) -> Reader<D, B>): Reader<D, B> = flatMap(IdMonad, fa)

    fun <B> zip(o: Reader<D, B>): Reader<D, Pair<A, B>> = zip(IdMonad, o)
}

fun <D, A> ((D) -> A).reader(): Reader<D, A> = Reader(this)
