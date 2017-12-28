package arrow

@instance(Id::class)
interface IdEqInstance<A> : Eq<Id<A>> {

    fun EQ(): Eq<A>

    override fun eqv(a: Id<A>, b: Id<A>): Boolean =
            EQ().eqv(a.value, b.value)
}

@instance(Id::class)
interface IdFunctorInstance : arrow.Functor<IdHK> {
    override fun <A, B> map(fa: arrow.IdKind<A>, f: kotlin.Function1<A, B>): arrow.Id<B> =
            fa.ev().map(f)
}

@instance(Id::class)
interface IdApplicativeInstance : arrow.Applicative<IdHK> {
    override fun <A, B> ap(fa: arrow.IdKind<A>, ff: arrow.IdKind<kotlin.Function1<A, B>>): arrow.Id<B> =
            fa.ev().ap(ff)

    override fun <A, B> map(fa: arrow.IdKind<A>, f: kotlin.Function1<A, B>): arrow.Id<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): arrow.Id<A> =
            arrow.Id.pure(a)
}

@instance(Id::class)
interface IdMonadInstance : arrow.Monad<IdHK> {
    override fun <A, B> ap(fa: arrow.IdKind<A>, ff: arrow.IdKind<kotlin.Function1<A, B>>): arrow.Id<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: arrow.IdKind<A>, f: kotlin.Function1<A, arrow.IdKind<B>>): arrow.Id<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, arrow.IdKind<arrow.Either<A, B>>>): arrow.Id<B> =
            arrow.Id.tailRecM(a, f)

    override fun <A, B> map(fa: arrow.IdKind<A>, f: kotlin.Function1<A, B>): arrow.Id<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): arrow.Id<A> =
            arrow.Id.pure(a)
}

@instance(Id::class)
interface IdComonadInstance : arrow.Comonad<IdHK> {
    override fun <A, B> coflatMap(fa: arrow.IdKind<A>, f: kotlin.Function1<arrow.IdKind<A>, B>): arrow.Id<B> =
            fa.ev().coflatMap(f)

    override fun <A> extract(fa: arrow.IdKind<A>): A =
            fa.ev().extract()

    override fun <A, B> map(fa: arrow.IdKind<A>, f: kotlin.Function1<A, B>): arrow.Id<B> =
            fa.ev().map(f)
}

@instance(Id::class)
interface IdBimonadInstance : arrow.Bimonad<IdHK> {
    override fun <A, B> ap(fa: arrow.IdKind<A>, ff: arrow.IdKind<kotlin.Function1<A, B>>): arrow.Id<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: arrow.IdKind<A>, f: kotlin.Function1<A, arrow.IdKind<B>>): arrow.Id<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, arrow.IdKind<arrow.Either<A, B>>>): arrow.Id<B> =
            arrow.Id.tailRecM(a, f)

    override fun <A, B> map(fa: arrow.IdKind<A>, f: kotlin.Function1<A, B>): arrow.Id<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): arrow.Id<A> =
            arrow.Id.pure(a)

    override fun <A, B> coflatMap(fa: arrow.IdKind<A>, f: kotlin.Function1<arrow.IdKind<A>, B>): arrow.Id<B> =
            fa.ev().coflatMap(f)

    override fun <A> extract(fa: arrow.IdKind<A>): A =
            fa.ev().extract()
}

@instance(Id::class)
interface IdFoldableInstance : arrow.Foldable<IdHK> {
    override fun <A, B> foldLeft(fa: arrow.IdKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: arrow.IdKind<A>, lb: arrow.Eval<B>, f: kotlin.Function2<A, arrow.Eval<B>, arrow.Eval<B>>): arrow.Eval<B> =
            fa.ev().foldRight(lb, f)
}

fun <A, G, B> Id<A>.traverse(f: (A) -> HK<G, B>, GA: Applicative<G>): HK<G, Id<B>> = GA.map(f(this.ev().value), { Id(it) })

@instance(Id::class)
interface IdTraverseInstance : arrow.Traverse<IdHK> {
    override fun <A, B> map(fa: arrow.IdKind<A>, f: kotlin.Function1<A, B>): arrow.Id<B> =
            fa.ev().map(f)

    override fun <G, A, B> traverse(fa: arrow.IdKind<A>, f: kotlin.Function1<A, arrow.HK<G, B>>, GA: arrow.Applicative<G>): arrow.HK<G, arrow.Id<B>> =
            fa.ev().traverse(f, GA)

    override fun <A, B> foldLeft(fa: arrow.IdKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: arrow.IdKind<A>, lb: arrow.Eval<B>, f: kotlin.Function2<A, arrow.Eval<B>, arrow.Eval<B>>): arrow.Eval<B> =
            fa.ev().foldRight(lb, f)
}
