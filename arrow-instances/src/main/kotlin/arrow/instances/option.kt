package arrow

@instance(Option::class)
interface OptionSemigroupInstance<A> : Semigroup<Option<A>> {

    fun SG(): Semigroup<A>

    override fun combine(a: Option<A>, b: Option<A>): Option<A> =
            when (a) {
                is Some<A> -> when (b) {
                    is Some<A> -> Some(SG().combine(a.t, b.t))
                    is None -> b
                }
                is None -> a
            }
}

@instance(Option::class)
interface OptionMonoidInstance<A> : OptionSemigroupInstance<A>, Monoid<Option<A>> {
    override fun empty(): Option<A> = None
}

@instance(Option::class)
interface OptionMonadErrorInstance : OptionMonadInstance, MonadError<OptionHK, Unit> {
    override fun <A> raiseError(e: Unit): Option<A> = None

    override fun <A> handleErrorWith(fa: OptionKind<A>, f: (Unit) -> OptionKind<A>): Option<A> = fa.ev().orElse({ f(Unit).ev() })
}

@instance(Option::class)
interface OptionEqInstance<A> : Eq<Option<A>> {

    fun EQ(): Eq<A>

    override fun eqv(a: Option<A>, b: Option<A>): Boolean = when (a) {
        is Some -> when (b) {
            is None -> false
            is Some -> EQ().eqv(a.t, b.t)
        }
        is None -> when (b) {
            is None -> true
            is Some -> false
        }
    }

}

@instance(Option::class)
interface OptionFunctorInstance : arrow.Functor<OptionHK> {
    override fun <A, B> map(fa: arrow.OptionKind<A>, f: kotlin.Function1<A, B>): arrow.Option<B> =
            fa.ev().map(f)
}

@instance(Option::class)
interface OptionApplicativeInstance : arrow.Applicative<OptionHK> {
    override fun <A, B> ap(fa: arrow.OptionKind<A>, ff: arrow.OptionKind<kotlin.Function1<A, B>>): arrow.Option<B> =
            fa.ev().ap(ff)

    override fun <A, B> map(fa: arrow.OptionKind<A>, f: kotlin.Function1<A, B>): arrow.Option<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): arrow.Option<A> =
            arrow.Option.pure(a)
}

@instance(Option::class)
interface OptionMonadInstance : arrow.Monad<OptionHK> {
    override fun <A, B> ap(fa: arrow.OptionKind<A>, ff: arrow.OptionKind<kotlin.Function1<A, B>>): arrow.Option<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: arrow.OptionKind<A>, f: kotlin.Function1<A, arrow.OptionKind<B>>): arrow.Option<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, arrow.OptionKind<arrow.Either<A, B>>>): arrow.Option<B> =
            arrow.Option.tailRecM(a, f)

    override fun <A, B> map(fa: arrow.OptionKind<A>, f: kotlin.Function1<A, B>): arrow.Option<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): arrow.Option<A> =
            arrow.Option.pure(a)
}

@instance(Option::class)
interface OptionFoldableInstance : arrow.Foldable<OptionHK> {
    override fun <A> exists(fa: arrow.OptionKind<A>, p: kotlin.Function1<A, kotlin.Boolean>): kotlin.Boolean =
            fa.ev().exists(p)

    override fun <A, B> foldLeft(fa: arrow.OptionKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: arrow.OptionKind<A>, lb: arrow.Eval<B>, f: kotlin.Function2<A, arrow.Eval<B>, arrow.Eval<B>>): arrow.Eval<B> =
            fa.ev().foldRight(lb, f)

    override fun <A> forall(fa: arrow.OptionKind<A>, p: kotlin.Function1<A, kotlin.Boolean>): kotlin.Boolean =
            fa.ev().forall(p)

    override fun <A> isEmpty(fa: arrow.OptionKind<A>): kotlin.Boolean =
            fa.ev().isEmpty()

    override fun <A> nonEmpty(fa: arrow.OptionKind<A>): kotlin.Boolean =
            fa.ev().nonEmpty()
}

fun <A, G, B> Option<A>.traverse(f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, Option<B>> =
        this.ev().let { option ->
            when (option) {
                is Option.Some -> GA.map(f(option.t), { Option.Some(it) })
                is Option.None -> GA.pure(None)
            }
        }

fun <A, G, B> Option<A>.traverseFilter(f: (A) -> HK<G, Option<B>>, GA: Applicative<G>): HK<G, Option<B>> =
        this.ev().let { option ->
            when (option) {
                is Option.Some -> f(option.t)
                is Option.None -> GA.pure(None)
            }
        }

@instance(Option::class)
interface OptionTraverseInstance : arrow.Traverse<OptionHK> {
    override fun <A, B> map(fa: arrow.OptionKind<A>, f: kotlin.Function1<A, B>): arrow.Option<B> =
            fa.ev().map(f)

    override fun <G, A, B> traverse(fa: arrow.OptionKind<A>, f: kotlin.Function1<A, arrow.HK<G, B>>, GA: arrow.Applicative<G>): arrow.HK<G, arrow.Option<B>> =
            fa.ev().traverse(f, GA)

    override fun <A> exists(fa: arrow.OptionKind<A>, p: kotlin.Function1<A, kotlin.Boolean>): kotlin.Boolean =
            fa.ev().exists(p)

    override fun <A, B> foldLeft(fa: arrow.OptionKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: arrow.OptionKind<A>, lb: arrow.Eval<B>, f: kotlin.Function2<A, arrow.Eval<B>, arrow.Eval<B>>): arrow.Eval<B> =
            fa.ev().foldRight(lb, f)

    override fun <A> forall(fa: arrow.OptionKind<A>, p: kotlin.Function1<A, kotlin.Boolean>): kotlin.Boolean =
            fa.ev().forall(p)

    override fun <A> isEmpty(fa: arrow.OptionKind<A>): kotlin.Boolean =
            fa.ev().isEmpty()

    override fun <A> nonEmpty(fa: arrow.OptionKind<A>): kotlin.Boolean =
            fa.ev().nonEmpty()
}