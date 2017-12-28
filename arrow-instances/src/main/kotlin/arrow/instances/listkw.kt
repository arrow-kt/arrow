package arrow

@instance(ListKW::class)
interface ListKWSemigroupInstance<A> : Semigroup<ListKW<A>> {
    override fun combine(a: ListKW<A>, b: ListKW<A>): ListKW<A> = (a + b).k()
}

@instance(ListKW::class)
interface ListKWMonoidInstance<A> : ListKWSemigroupInstance<A>, Monoid<ListKW<A>> {
    override fun empty(): ListKW<A> = emptyList<A>().k()
}

@instance(ListKW::class)
interface ListKWEqInstance<A> : Eq<ListKW<A>> {

    fun EQ(): Eq<A>

    override fun eqv(a: ListKW<A>, b: ListKW<A>): Boolean =
            a.zip(b) { aa, bb -> EQ().eqv(aa, bb) }.fold(true) { acc, bool ->
                acc && bool
            }

}

@instance(ListKW::class)
interface ListKWFunctorInstance : arrow.Functor<ListKWHK> {
    override fun <A, B> map(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, B>): arrow.ListKW<B> =
            fa.ev().map(f)
}

@instance(ListKW::class)
interface ListKWApplicativeInstance : arrow.Applicative<ListKWHK> {
    override fun <A, B> ap(fa: arrow.ListKWKind<A>, ff: arrow.ListKWKind<kotlin.Function1<A, B>>): arrow.ListKW<B> =
            fa.ev().ap(ff)

    override fun <A, B> map(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, B>): arrow.ListKW<B> =
            fa.ev().map(f)

    override fun <A, B, Z> map2(fa: arrow.ListKWKind<A>, fb: arrow.ListKWKind<B>, f: kotlin.Function1<arrow.Tuple2<A, B>, Z>): arrow.ListKW<Z> =
            fa.ev().map2(fb, f)

    override fun <A> pure(a: A): arrow.ListKW<A> =
            arrow.ListKW.pure(a)
}

@instance(ListKW::class)
interface ListKWMonadInstance : arrow.Monad<ListKWHK> {
    override fun <A, B> ap(fa: arrow.ListKWKind<A>, ff: arrow.ListKWKind<kotlin.Function1<A, B>>): arrow.ListKW<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, arrow.ListKWKind<B>>): arrow.ListKW<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, arrow.ListKWKind<arrow.Either<A, B>>>): arrow.ListKW<B> =
            arrow.ListKW.tailRecM(a, f)

    override fun <A, B> map(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, B>): arrow.ListKW<B> =
            fa.ev().map(f)

    override fun <A, B, Z> map2(fa: arrow.ListKWKind<A>, fb: arrow.ListKWKind<B>, f: kotlin.Function1<arrow.Tuple2<A, B>, Z>): arrow.ListKW<Z> =
            fa.ev().map2(fb, f)

    override fun <A> pure(a: A): arrow.ListKW<A> =
            arrow.ListKW.pure(a)
}

@instance(ListKW::class)
interface ListKWFoldableInstance : arrow.Foldable<ListKWHK> {
    override fun <A, B> foldLeft(fa: arrow.ListKWKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: arrow.ListKWKind<A>, lb: arrow.Eval<B>, f: kotlin.Function2<A, arrow.Eval<B>, arrow.Eval<B>>): arrow.Eval<B> =
            fa.ev().foldRight(lb, f)

    override fun <A> isEmpty(fa: arrow.ListKWKind<A>): kotlin.Boolean =
            fa.ev().isEmpty()
}

@instance(ListKW::class)
interface ListKWTraverseInstance : arrow.Traverse<ListKWHK> {
    override fun <A, B> map(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, B>): arrow.ListKW<B> =
            fa.ev().map(f)

    override fun <G, A, B> traverse(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, arrow.HK<G, B>>, GA: arrow.Applicative<G>): arrow.HK<G, arrow.ListKW<B>> =
            fa.ev().traverse(f, GA)

    override fun <A, B> foldLeft(fa: arrow.ListKWKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: arrow.ListKWKind<A>, lb: arrow.Eval<B>, f: kotlin.Function2<A, arrow.Eval<B>, arrow.Eval<B>>): arrow.Eval<B> =
            fa.ev().foldRight(lb, f)

    override fun <A> isEmpty(fa: arrow.ListKWKind<A>): kotlin.Boolean =
            fa.ev().isEmpty()
}

@instance(ListKW::class)
interface ListKWSemigroupKInstance : arrow.SemigroupK<ListKWHK> {
    override fun <A> combineK(x: arrow.ListKWKind<A>, y: arrow.ListKWKind<A>): arrow.ListKW<A> =
            x.ev().combineK(y)
}

@instance(ListKW::class)
interface ListKWMonoidKInstance : arrow.MonoidK<ListKWHK> {
    override fun <A> empty(): arrow.ListKW<A> =
            arrow.ListKW.empty()

    override fun <A> combineK(x: arrow.ListKWKind<A>, y: arrow.ListKWKind<A>): arrow.ListKW<A> =
            x.ev().combineK(y)
}