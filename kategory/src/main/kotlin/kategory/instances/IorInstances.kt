package kategory

interface IorInstances<L> :
        Functor<HK<Ior.F, L>>,
        Applicative<HK<Ior.F, L>>,
        Monad<HK<Ior.F, L>> {

    fun SL(): Semigroup<L>

    override fun <A, B> flatMap(fa: IorKind<L, A>, f: (A) -> IorKind<L, B>): Ior<L, B> = fa.ev().flatMap(SL(), { f(it).ev() })

    override fun <A> pure(a: A): Ior<L, A> = Ior.Right(a)

    override fun <A, B> map(fa: IorKind<L, A>, f: (A) -> B): Ior<L, B> = fa.ev().map(f)

    private tailrec fun <A, B> loop(v: Ior<L, Either<A, B>>, f: (A) -> IorKind<L, Either<A, B>>): Ior<L, B> = when (v) {
            is Ior.Left -> Ior.Left(v.value)
            is Ior.Right -> when (v.value) {
                is Either.Right -> Ior.Right(v.value.b)
                is Either.Left -> loop(f(v.value.a).ev().ev(), f)
            }
            is Ior.Both -> when (v.rightValue) {
                is Either.Right -> Ior.Both(v.leftValue, v.rightValue.b)
                is Either.Left -> {
                    val fnb = f(v.rightValue.a).ev()
                    when (fnb) {
                        is Ior.Left -> Ior.Left(SL().combine(v.leftValue, fnb.value))
                        is Ior.Right -> loop(Ior.Both(v.leftValue, fnb.value), f)
                        is Ior.Both -> loop(Ior.Both(SL().combine(v.leftValue, fnb.leftValue), fnb.rightValue), f)
                    }
                }
            }
        }

    override fun <A, B> tailRecM(a: A, f: (A) -> IorKind<L, Either<A, B>>): Ior<L, B> = loop(f(a).ev(), f)
}

interface IorTraverse<A> :
        Foldable<HK<Ior.F, A>>,
        Traverse<HK<Ior.F, A>> {
    override fun <G, B, C> traverse(fa: HK<HK<Ior.F, A>, B>, f: (B) -> HK<G, C>, GA: Applicative<G>): HK<G, HK<HK<Ior.F, A>, C>> = fa.ev().fold({ GA.pure(Ior.Left(it)) }, { GA.map(f(it), { Ior.Right(it) }) }, { _, b -> GA.map(f(b), { Ior.Right(it) }) })

    override fun <B, C> foldL(fa: HK<HK<Ior.F, A>, B>, c: C, f: (C, B) -> C): C = fa.ev().fold({ c }, { f(c, it) }, { _, b -> f(c, b) })

    override fun <B, C> foldR(fa: HK<HK<Ior.F, A>, B>, lc: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> = fa.ev().fold({ lc }, { f(it, lc) }, { _, b -> f(b, lc) })
}