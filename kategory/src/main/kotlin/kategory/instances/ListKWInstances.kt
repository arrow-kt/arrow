package kategory

interface ListKWInstances :
        Functor<ListKW.F>,
        Applicative<ListKW.F>,
        Monad<ListKW.F>,
        Traverse<ListKW.F> {

    override fun <A> pure(a: A): ListKW<A> = ListKW.listOfK(a)

    override fun <A, B> flatMap(fa: HK<ListKW.F, A>, f: (A) -> HK<ListKW.F, B>): ListKW<B> {
        return fa.ev().flatMap { f(it).ev() }
    }

    override fun <A, B> map(fa: HK<ListKW.F, A>, f: (A) -> B): HK<ListKW.F, B> {
        return fa.ev().map(f)
    }

    override fun <A, B, Z> map2(fa: HK<ListKW.F, A>, fb: HK<ListKW.F, B>, f: (Tuple2<A, B>) -> Z): HK<ListKW.F, Z> {
        return fa.ev().flatMap { a -> fb.ev().map { b -> f(Tuple2(a, b)) } }
    }

    override fun <A, B> foldL(fa: HK<ListKW.F, A>, b: B, f: (B, A) -> B): B = fa.ev().fold(b, f)


    override fun <A, B> foldR(fa: HK<ListKW.F, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
        fun loop(fa_p: ListKW<A>): Eval<B> = when {
            fa_p.isEmpty() -> lb
            else -> f(fa_p.first(), Eval.defer { loop(fa_p.drop(1).toListKW()) })
        }
        return Eval.defer { loop(fa.ev()) }
    }

    override fun <G, A, B> traverse(fa: HK<ListKW.F, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, HK<ListKW.F, B>> {
        return foldR(fa, Eval.always { GA.pure(ListKW.listOfK<B>()) }) { a, eval ->
            GA.map2Eval(f(a), eval) { ListKW.listOfK(it.a) + it.b }
        }.value()
    }

    override fun <A, B> tailRecM(a: A, f: (A) -> HK<ListKW.F, Either<A, B>>): ListKW<B> {
        return f(a).ev().flatMap {
            when (it) {
                is Either.Left -> tailRecM(it.a, f)
                is Either.Right -> pure(it.b)
            }
        }
    }

}

interface ListKWSemigroup<A> : Semigroup<ListKW<A>> {
    override fun combine(a: ListKW<A>, b: ListKW<A>): ListKW<A> = a + b
}

interface ListKWSemigroupK : SemigroupK<ListKW.F> {
    override fun <A> combineK(x: HK<ListKW.F, A>, y: HK<ListKW.F, A>): ListKW<A> = x.ev() + y.ev()
}

interface ListKWMonoid<A> : Monoid<ListKW<A>> {
    override fun empty(): ListKW<A> = ListKW.listOfK()

    override fun combine(a: ListKW<A>, b: ListKW<A>): ListKW<A> = a + b
}