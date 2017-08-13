package kategory

interface ListKWInstances :
        Functor<ListKW.F>,
        Applicative<ListKW.F>,
        Monad<ListKW.F>,
        Traverse<ListKW.F> {

    override fun <A> pure(a: A): ListKW<A> =
            ListKW.listOfK(a)

    override fun <A, B> flatMap(fa: HK<ListKW.F, A>, f: (A) -> HK<ListKW.F, B>): ListKW<B> =
            fa.ev().flatMap { f(it).ev() }

    override fun <A, B> map(fa: HK<ListKW.F, A>, f: (A) -> B): HK<ListKW.F, B> =
            fa.ev().map(f)

    override fun <A, B, Z> map2(fa: HK<ListKW.F, A>, fb: HK<ListKW.F, B>, f: (Tuple2<A, B>) -> Z): HK<ListKW.F, Z> =
            fa.ev().flatMap { a -> fb.ev().map { b -> f(Tuple2(a, b)) } }

    override fun <A, B> foldL(fa: HK<ListKW.F, A>, b: B, f: (B, A) -> B): B =
            fa.ev().fold(b, f)


    override fun <A, B> foldR(fa: HK<ListKW.F, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
        fun loop(fa_p: ListKW<A>): Eval<B> = when {
            fa_p.isEmpty() -> lb
            else -> f(fa_p.first(), Eval.defer { loop(fa_p.drop(1).k()) })
        }
        return Eval.defer { loop(fa.ev()) }
    }

    override fun <G, A, B> traverse(fa: HK<ListKW.F, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, HK<ListKW.F, B>> =
            foldR(fa, Eval.always { GA.pure(ListKW.listOfK<B>()) }) { a, eval ->
                GA.map2Eval(f(a), eval) { ListKW.listOfK(it.a) + it.b }
            }.value()

    @Suppress("UNCHECKED_CAST")
    private tailrec fun <A, B> go(
            buf: ArrayList<B>,
            f: (A) -> HK<ListKW.F, Either<A, B>>,
            v: ListKW<Either<A, B>>) {
        if (!v.isEmpty()) {
            val head: Either<A, B> = v.first()
            when (head) {
                is Either.Right<A, B> -> {
                    buf += head.b
                    go(buf, f, ListKW.listOfK(v.drop(1)))
                }
                is Either.Left<A, B> -> go(buf, f, f(head.a).ev() + v.drop(1))
            }
        }
    }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<ListKW.F, Either<A, B>>): ListKW<B> {
        val buf = ArrayList<B>()
        go(buf, f, f(a).ev())
        return ListKW.listOfK(buf)
    }
}

interface ListKWMonoid<A> : Monoid<ListKW<A>> {
    override fun combine(a: ListKW<A>, b: ListKW<A>): ListKW<A> =
            a + b

    override fun empty(): ListKW<A> =
            ListKW.listOfK()
}

interface ListKWMonoidK : MonoidK<ListKW.F> {
    override fun <A> combineK(x: HK<ListKW.F, A>, y: HK<ListKW.F, A>): ListKW<A> =
            x.ev() + y.ev()

    override fun <A> empty(): HK<ListKW.F, A> =
            ListKW.listOfK()
}