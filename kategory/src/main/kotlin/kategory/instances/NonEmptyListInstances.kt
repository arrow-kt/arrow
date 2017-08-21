package kategory

interface NonEmptyListInstances :
        Functor<NonEmptyListHK>,
        Applicative<NonEmptyListHK>,
        Monad<NonEmptyListHK>,
        Comonad<NonEmptyListHK>,
        Bimonad<NonEmptyListHK>,
        Traverse<NonEmptyListHK>,
        Foldable<NonEmptyListHK> {

    override fun <A> pure(a: A): NonEmptyList<A> = a.k()

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

    override fun <A, B> foldL(fa: HK<NonEmptyListHK, A>, b: B, f: (B, A) -> B): B = fa.ev().tail.fold(f(b, fa.ev().head), f)

    override fun <A, B> foldR(fa: HK<NonEmptyListHK, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
        fun loop(fa_p: NonEmptyList<A>): Eval<B> = when {
            fa_p.tail.isEmpty() -> f(fa_p.ev().head, lb)
            else -> f(fa_p.ev().head, Eval.defer { loop(NonEmptyList(fa_p.ev().tail.first(), fa_p.tail.drop(1))) })
        }
        return Eval.defer { loop(fa.ev()) }
    }

    override fun <G, A, B> traverse(fa: HK<NonEmptyListHK, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, HK<NonEmptyListHK, B>> =
            GA.map2Eval(f(fa.ev().head), Eval.always {
                ListKW.traverse().traverse(fa.ev().tail.k(), f, GA)
            }, {
                NonEmptyList(it.a, it.b.ev().list)
            }).value()

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
