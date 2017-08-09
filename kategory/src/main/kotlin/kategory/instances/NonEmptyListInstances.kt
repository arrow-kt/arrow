package kategory

interface NonEmptyListInstances :
        Functor<NonEmptyListHK>,
        Applicative<NonEmptyListHK>,
        Monad<NonEmptyListHK>,
        Comonad<NonEmptyListHK>,
        Bimonad<NonEmptyListHK> {

    override fun <A> pure(a: A): NonEmptyList<A> = NonEmptyList.of(a)

    override fun <A, B> flatMap(fa: NonEmptyListKind<A>, f: (A) -> NonEmptyListKind<B>): NonEmptyList<B> = fa.ev().flatMap { f(it).ev() }

    override fun <A, B> map(fa: NonEmptyListKind<A>, f: (A) -> B): NonEmptyList<B> = fa.ev().map(f)

    @Suppress("UNCHECKED_CAST")
    private tailrec fun <A, B> go(
            buf: ArrayList<B>,
            f: (A) -> HK<NonEmptyListHK, Either<A, B>>,
            v: NonEmptyList<Either<A, B>>) {
        val head: Either<A, B> = v.head
        when (head) {
            is Either.Right<A, B> -> {
                buf += head.b
                val x = NonEmptyList.fromList(v.tail)
                when (x) {
                    is Option.Some<NonEmptyList<Either<A, B>>> -> go(buf, f, x.value)
                    is Option.None -> Unit
                }
            }
            is Either.Left<A, B> -> go(buf, f, f(head.a).ev() + v.tail)
        }
    }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<NonEmptyListHK, Either<A, B>>): NonEmptyList<B> {
        val buf = ArrayList<B>()
        go(buf, f, f(a).ev())
        return NonEmptyList.fromListUnsafe(buf)
    }

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

    override fun <A> extract(fa: NonEmptyListKind<A>): A = fa.ev().head

}

interface NonEmptyListSemigroup<A> : Semigroup<NonEmptyList<A>> {
    override fun combine(a: NonEmptyList<A>, b: NonEmptyList<A>): NonEmptyList<A> = a + b
}

interface NonEmptyListSemigroupK : SemigroupK<NonEmptyListHK> {
    override fun <A> combineK(x: HK<NonEmptyListHK, A>, y: HK<NonEmptyListHK, A>): NonEmptyList<A> = x.ev().plus(y.ev())
}
