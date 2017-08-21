package kategory

interface ListKWInstances :
        Functor<ListKWHK>,
        Applicative<ListKWHK>,
        Monad<ListKWHK>,
        Traverse<ListKWHK>,
        Foldable<ListKWHK> {

    override fun <A> pure(a: A): ListKW<A> = listOf(a).k()

    override fun <A, B> flatMap(fa: HK<ListKWHK, A>, f: (A) -> HK<ListKWHK, B>): ListKW<B> = fa.ev().list.flatMap { f(it).ev().list }.k()

    override fun <A, B> map(fa: HK<ListKWHK, A>, f: (A) -> B): ListKW<B> = fa.ev().map(f).k()

    override fun <A, B, Z> map2(fa: HK<ListKWHK, A>, fb: HK<ListKWHK, B>, f: (Tuple2<A, B>) -> Z): ListKW<Z> =
            fa.ev().flatMap { a ->
                fb.ev().map { b ->
                    f(Tuple2(a, b))
                }
            }.ev()

    override fun <A, B> foldL(fa: HK<ListKWHK, A>, b: B, f: (B, A) -> B): B = fa.ev().fold(b, f)

    override fun <A, B> foldR(fa: HK<ListKWHK, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
        fun loop(fa_p: ListKW<A>): Eval<B> = when {
            fa_p.list.isEmpty() -> lb
            else -> f(fa_p.ev().list.first(), Eval.defer { loop(fa_p.list.drop(1).k()) })
        }
        return Eval.defer { loop(fa.ev()) }
    }

    override fun <G, A, B> traverse(fa: HK<ListKWHK, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, HK<ListKWHK, B>> =
            foldR(fa, Eval.always { GA.pure(emptyList<B>().k()) }) { a, eval ->
                GA.map2Eval(f(a), eval) { (listOf(it.a) + it.b).k() }
            }.value()

    @Suppress("UNCHECKED_CAST")
    private tailrec fun <A, B> go(
            buf: ArrayList<B>,
            f: (A) -> HK<ListKWHK, Either<A, B>>,
            v: ListKW<Either<A, B>>) {
        if (!v.isEmpty()) {
            val head: Either<A, B> = v.first()
            when (head) {
                is Either.Right<A, B> -> {
                    buf += head.b
                    go(buf, f, v.drop(1).k())
                }
                is Either.Left<A, B> -> go(buf, f, (f(head.a).ev() + v.drop(1)).k())
            }
        }
    }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<ListKWHK, Either<A, B>>): ListKW<B> {
        val buf = ArrayList<B>()
        go(buf, f, f(a).ev())
        return ListKW(buf)
    }
}

interface ListKWMonoid<A> : Monoid<ListKW<A>> {
    override fun combine(a: ListKW<A>, b: ListKW<A>): ListKW<A> = (a + b).k()

    override fun empty(): ListKW<A> = emptyList<A>().k()
}

interface ListKWMonoidK : MonoidK<ListKWHK> {
    override fun <A> combineK(x: HK<ListKWHK, A>, y: HK<ListKWHK, A>): ListKW<A> = (x.ev() + y.ev()).k()

    override fun <A> empty(): ListKW<A> = emptyList<A>().k()
}
