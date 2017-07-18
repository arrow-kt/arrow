package kategory

interface OptionMonoid<A> : Monoid<Option<A>> {

    fun SG(): Semigroup<A>

    override fun empty(): Option<A> =
            Option.None

    override fun combine(a: Option<A>, b: Option<A>): Option<A> =
            when (a) {
                is Option.Some<A> -> when (b) {
                    is Option.Some<A> -> Option.Some(SG().combine(a.value, b.value))
                    is Option.None -> b
                }
                is Option.None -> a
            }

}

interface OptionInstances :
        Functor<Option.F>,
        Applicative<Option.F>,
        Monad<Option.F>,
        Foldable<Option.F>,
        Traverse<Option.F>,
        MonadError<Option.F, Unit> {

    override fun <A, B> map(fa: OptionKind<A>, f: (A) -> B): Option<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): Option<A> = Option.Some(a)

    override fun <A, B> ap(fa: OptionKind<A>, ff: OptionKind<(A) -> B>): Option<B> =
            ff.ev().flatMap { fa.ev().map(it) }

    override fun <A, B> flatMap(fa: OptionKind<A>, f: (A) -> OptionKind<B>): Option<B> =
            fa.ev().flatMap { f(it).ev() }

    tailrec override fun <A, B> tailRecM(a: A, f: (A) -> HK<Option.F, Either<A, B>>): Option<B> {
        val option = f(a).ev()
        return when (option) {
            is Option.Some -> {
                when (option.value) {
                    is Either.Left -> tailRecM(option.value.a, f)
                    is Either.Right -> Option.Some(option.value.b)
                }
            }
            is Option.None -> Option.None
        }
    }

    override fun <A, B> foldL(fa: HK<Option.F, A>, b: B, f: (B, A) -> B): B =
            fa.ev().let { option ->
                when (option) {
                    is Option.Some -> f(b, option.value)
                    is Option.None -> b
                }
            }

    override fun <A, B> foldR(fa: HK<Option.F, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            fa.ev().let { option ->
                when (option) {
                    is Option.Some -> f(option.value, lb)
                    is Option.None -> lb
                }
            }

    override fun <G, A, B> traverse(fa: HK<Option.F, A>, f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, HK<Option.F, B>> =
            fa.ev().let { option ->
                when (option) {
                    is Option.Some -> GA.map(f(option.value), { Option.Some(it) })
                    is Option.None -> GA.pure(Option.None)
                }
            }

    override fun <A> raiseError(e: Unit): Option<A> =
            Option.None

    override fun <A> handleErrorWith(fa: OptionKind<A>, f: (Unit) -> OptionKind<A>): Option<A> =
            fa.ev().orElse({ f(Unit).ev() })

}
