package arrow

@higherkind
data class ListKW<out A> constructor(val list: List<A>) : ListKWKind<A>, List<A> by list {

    fun <B> flatMap(f: (A) -> ListKWKind<B>): ListKW<B> = this.ev().list.flatMap { f(it).ev().list }.k()

    fun <B> map(f: (A) -> B): ListKW<B> = this.ev().list.map(f).k()

    fun <B> foldLeft(b: B, f: (B, A) -> B): B = this.ev().fold(b, f)

    fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
        fun loop(fa_p: ListKW<A>): Eval<B> = when {
            fa_p.list.isEmpty() -> lb
            else -> f(fa_p.ev().list.first(), Eval.defer { loop(fa_p.list.drop(1).k()) })
        }
        return Eval.defer { loop(this.ev()) }
    }

    fun <B> ap(ff: ListKWKind<(A) -> B>): ListKW<B> = ff.ev().flatMap { f -> map(f) }.ev()

    fun <G, B> traverse(f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, ListKW<B>> =
            foldRight(Eval.always { GA.pure(emptyList<B>().k()) }) { a, eval ->
                GA.map2Eval(f(a), eval) { (listOf(it.a) + it.b).k() }
            }.value()

    fun <B, Z> map2(fb: ListKWKind<B>, f: (Tuple2<A, B>) -> Z): ListKW<Z> =
            this.ev().flatMap { a ->
                fb.ev().map { b ->
                    f(Tuple2(a, b))
                }
            }.ev()

    fun <B> mapFilter(f: (A) -> Option<B>): ListKW<B> =
            flatMap({ a -> f(a).fold({ empty<B>() }, { pure(it) }) })

    companion object {

        fun <A> pure(a: A): ListKW<A> = listOf(a).k()

        fun <A> empty(): ListKW<A> = emptyList<A>().k()

        @Suppress("UNCHECKED_CAST")
        private tailrec fun <A, B> go(
                buf: ArrayList<B>,
                f: (A) -> HK<ListKWHK, Either<A, B>>,
                v: ListKW<Either<A, B>>) {
            if (!v.isEmpty()) {
                val head: Either<A, B> = v.first()
                when (head) {
                    is Right<A, B> -> {
                        buf += head.b
                        go(buf, f, v.drop(1).k())
                    }
                    is Left<A, B> -> go(buf, f, (f(head.a).ev() + v.drop(1)).k())
                }
            }
        }

        fun <A, B> tailRecM(a: A, f: (A) -> HK<ListKWHK, Either<A, B>>): ListKW<B> {
            val buf = ArrayList<B>()
            go(buf, f, f(a).ev())
            return ListKW(buf)
        }
    }

}

fun <A> ListKW<A>.combineK(y: ListKWKind<A>): ListKW<A> = (this.list + y.ev().list).k()

fun <A> List<A>.k(): ListKW<A> = ListKW(this)
