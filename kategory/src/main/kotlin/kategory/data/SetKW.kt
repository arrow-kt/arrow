package kategory

@higherkind
@deriving(Monad::class, Traverse::class, MonoidK::class)
data class SetKW<A>(val set: Set<A>) : SetKWKind<A>, Set<A> by set {

    fun <B> flatMap(f: (A) -> SetKWKind<B>): SetKW<B> = this.set.flatMap { f(it).ev().set }.toSet().k()

    fun <B> map(f: (A) -> B): SetKW<B> = this.set.map(f).toSet().k()

    fun <B> foldL(b: B, f: (B, A) -> B): B = this.fold(b, f)

    fun <B> foldR(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
        fun loop(fa_p: SetKW<A>): Eval<B> = when {
            fa_p.set.isEmpty() -> lb
            else -> f(fa_p.set.first(), Eval.defer { loop(fa_p.set.drop(1).toSet().k()) })
        }
        return Eval.defer { loop(this) }
    }

    fun <G, B> traverse(f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, SetKW<B>> =
            foldR(Eval.always { GA.pure(emptySet<B>().k()) }) { a, eval ->
                GA.map2Eval(f(a), eval) { (setOf(it.a) + it.b).k() }
            }.value()

    fun <B, Z> map2(fb: SetKWKind<B>, f: (Tuple2<A, B>) -> Z): SetKW<Z> =
            this.ev().flatMap { a ->
                fb.ev().map { b ->
                    f(Tuple2(a, b))
                }
            }.ev()

    companion object {

        fun <A> pure(a: A): SetKW<A> = setOf(a).k()

        fun <A> empty(): SetKW<A> = emptySet<A>().k()

        private tailrec fun <A, B> go(
                buf: MutableSet<B>,
                f: (A) -> HK<SetKWHK, Either<A, B>>,
                v: SetKW<Either<A, B>>) {
            if (!v.isEmpty()) {
                val head: Either<A, B> = v.first()
                when (head) {
                    is Either.Right<A, B> -> {
                        buf += head.b
                        go(buf, f, v.drop(1).toSet().k())
                    }
                    is Either.Left<A, B> -> go(buf, f, (f(head.a).ev() + v.drop(1)).k())
                }
            }
        }

        fun <A, B> tailRecM(a: A, f: (A) -> HK<SetKWHK, Either<A, B>>): SetKW<B> {
            val buf = mutableSetOf<B>()
            SetKW.go(buf, f, f(a).ev())
            return SetKW(buf.toSet())
        }

        fun functor(): SetKWHKMonadInstance = SetKW.monad()

        fun applicative(): SetKWHKMonadInstance = SetKW.monad()

        fun <A> semigroup(): SetKWMonoid<A> = object : SetKWMonoid<A> {}

        fun semigroupK(): SetKWHKMonoidKInstance = SetKW.monoidK()

        fun <A> monoid(): SetKWMonoid<A> = object : SetKWMonoid<A> {}

        fun foldable(): SetKWHKTraverseInstance = SetKW.traverse()
    }
}

fun <A> SetKW<A>.combineK(y: SetKWKind<A>): SetKW<A> = (this.set + y.ev().set).k()

fun <A> Set<A>.k(): SetKW<A> = SetKW(this)