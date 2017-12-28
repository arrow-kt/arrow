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

interface ListKWFunctorInstance : arrow.Functor<ListKWHK> {
    override fun <A, B> map(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, B>): arrow.ListKW<B> =
            fa.ev().map(f)
}

object ListKWFunctorInstanceImplicits {
    fun instance(): ListKWFunctorInstance = arrow.ListKW.Companion.functor()
}

fun arrow.ListKW.Companion.functor(): ListKWFunctorInstance =
        object : ListKWFunctorInstance, arrow.Functor<ListKWHK> {}

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

object ListKWApplicativeInstanceImplicits {
    fun instance(): ListKWApplicativeInstance = arrow.ListKW.Companion.applicative()
}

fun arrow.ListKW.Companion.applicative(): ListKWApplicativeInstance =
        object : ListKWApplicativeInstance, arrow.Applicative<ListKWHK> {}

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

object ListKWMonadInstanceImplicits {
    fun instance(): ListKWMonadInstance = arrow.ListKW.Companion.monad()
}

fun arrow.ListKW.Companion.monad(): ListKWMonadInstance =
        object : ListKWMonadInstance, arrow.Monad<ListKWHK> {}

interface ListKWFoldableInstance : arrow.Foldable<ListKWHK> {
    override fun <A, B> foldLeft(fa: arrow.ListKWKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: arrow.ListKWKind<A>, lb: arrow.Eval<B>, f: kotlin.Function2<A, arrow.Eval<B>, arrow.Eval<B>>): arrow.Eval<B> =
            fa.ev().foldRight(lb, f)

    override fun <A> isEmpty(fa: arrow.ListKWKind<A>): kotlin.Boolean =
            fa.ev().isEmpty()
}

object ListKWFoldableInstanceImplicits {
    fun instance(): ListKWFoldableInstance = arrow.ListKW.Companion.foldable()
}

fun arrow.ListKW.Companion.foldable(): ListKWFoldableInstance =
        object : ListKWFoldableInstance, arrow.Foldable<ListKWHK> {}

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

object ListKWTraverseInstanceImplicits {
    fun instance(): ListKWTraverseInstance = arrow.ListKW.Companion.traverse()
}

fun arrow.ListKW.Companion.traverse(): ListKWTraverseInstance =
        object : ListKWTraverseInstance, arrow.Traverse<ListKWHK> {}

interface ListKWSemigroupKInstance : arrow.SemigroupK<ListKWHK> {
    override fun <A> combineK(x: arrow.ListKWKind<A>, y: arrow.ListKWKind<A>): arrow.ListKW<A> =
            x.ev().combineK(y)
}

object ListKWSemigroupKInstanceImplicits {
    fun instance(): ListKWSemigroupKInstance = arrow.ListKW.Companion.semigroupK()
}

fun arrow.ListKW.Companion.semigroupK(): ListKWSemigroupKInstance =
        object : ListKWSemigroupKInstance, arrow.SemigroupK<ListKWHK> {}

interface ListKWMonoidKInstance : arrow.MonoidK<ListKWHK> {
    override fun <A> empty(): arrow.ListKW<A> =
            arrow.ListKW.empty()

    override fun <A> combineK(x: arrow.ListKWKind<A>, y: arrow.ListKWKind<A>): arrow.ListKW<A> =
            x.ev().combineK(y)
}

object ListKWMonoidKInstanceImplicits {
    fun instance(): ListKWMonoidKInstance = arrow.ListKW.Companion.monoidK()
}

fun arrow.ListKW.Companion.monoidK(): ListKWMonoidKInstance =
        object : ListKWMonoidKInstance, arrow.MonoidK<ListKWHK> {}

interface ListKWMonadCombineInstance : arrow.MonadCombine<ListKWHK> {
    override fun <A> empty(): arrow.ListKW<A> =
            arrow.ListKW.empty()

    override fun <A, B> mapFilter(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, arrow.Option<B>>): arrow.ListKW<B> =
            fa.ev().mapFilter(f)

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

    override fun <A> combineK(x: arrow.ListKWKind<A>, y: arrow.ListKWKind<A>): arrow.ListKW<A> =
            x.ev().combineK(y)
}

object ListKWMonadCombineInstanceImplicits {
    fun instance(): ListKWMonadCombineInstance = arrow.ListKW.Companion.monadCombine()
}

fun arrow.ListKW.Companion.monadCombine(): ListKWMonadCombineInstance =
        object : ListKWMonadCombineInstance, arrow.MonadCombine<ListKWHK> {}

interface ListKWFunctorFilterInstance : arrow.FunctorFilter<ListKWHK> {
    override fun <A, B> mapFilter(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, arrow.Option<B>>): arrow.ListKW<B> =
            fa.ev().mapFilter(f)

    override fun <A, B> map(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, B>): arrow.ListKW<B> =
            fa.ev().map(f)
}

object ListKWFunctorFilterInstanceImplicits {
    fun instance(): ListKWFunctorFilterInstance = arrow.ListKW.Companion.functorFilter()
}

fun arrow.ListKW.Companion.functorFilter(): ListKWFunctorFilterInstance =
        object : ListKWFunctorFilterInstance, arrow.FunctorFilter<ListKWHK> {}

interface ListKWMonadFilterInstance : arrow.MonadFilter<ListKWHK> {
    override fun <A> empty(): arrow.ListKW<A> =
            arrow.ListKW.empty()

    override fun <A, B> mapFilter(fa: arrow.ListKWKind<A>, f: kotlin.Function1<A, arrow.Option<B>>): arrow.ListKW<B> =
            fa.ev().mapFilter(f)

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

object ListKWMonadFilterInstanceImplicits {
    fun instance(): ListKWMonadFilterInstance = arrow.ListKW.Companion.monadFilter()
}

fun arrow.ListKW.Companion.monadFilter(): ListKWMonadFilterInstance =
        object : ListKWMonadFilterInstance, arrow.MonadFilter<ListKWHK> {}
