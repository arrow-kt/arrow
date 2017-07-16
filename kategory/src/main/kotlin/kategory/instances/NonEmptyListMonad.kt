package kategory

interface NonEmptyListMonad : Monad<NonEmptyList.F> {
    override fun <A> pure(a: A): NonEmptyList<A> = NonEmptyList.of(a)

    override fun <A, B> flatMap(fa: NonEmptyListKind<A>, f: (A) -> NonEmptyListKind<B>): NonEmptyList<B> =
            fa.ev().flatMap { f(it).ev() }

    override fun <A, B> map(fa: NonEmptyListKind<A>, f: (A) -> B): NonEmptyList<B> =
            fa.ev().map(f)

    @Suppress("UNCHECKED_CAST")
    private tailrec fun <A, B> go(buf: ArrayList<B>, f: (A) -> HK<NonEmptyList.F, Either<A, B>>, v: NonEmptyList<Either<A, B>>): Unit =
            when (v.head) {
                is Either.Right<*> -> {
                    buf += v.head.b as B
                    val x = NonEmptyList.fromList(v.tail)
                    when (x) {
                        is Option.Some<NonEmptyList<Either<A, B>>> -> go(buf, f, x.value)
                        is Option.None -> Unit
                    }
                }
                is Either.Left<*> -> go(buf, f, f(v.head.a as A).ev() + v.tail)
            }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<NonEmptyList.F, Either<A, B>>): NonEmptyList<B> {
        val buf = ArrayList<B>()
        go(buf, f, f(a).ev())
        return NonEmptyList.fromListUnsafe(buf)
    }
}