package arrow

@instance(SequenceKW::class)
interface SequenceKWSemigroupInstance<A> : Semigroup<SequenceKW<A>> {
    override fun combine(a: SequenceKW<A>, b: SequenceKW<A>): SequenceKW<A> = (a + b).k()
}

@instance(SequenceKW::class)
interface SequenceKWMonoidInstance<A> : Monoid<SequenceKW<A>> {
    override fun combine(a: SequenceKW<A>, b: SequenceKW<A>): SequenceKW<A> = (a + b).k()

    override fun empty(): SequenceKW<A> = emptySequence<A>().k()
}

@instance(SequenceKW::class)
interface SequenceKWEqInstance<A> : Eq<SequenceKW<A>> {

    fun EQ(): Eq<A>

    override fun eqv(a: SequenceKW<A>, b: SequenceKW<A>): Boolean =
            a.zip(b) { aa, bb -> EQ().eqv(aa, bb) }.fold(true) { acc, bool ->
                acc && bool
            }

}

interface SequenceKWFunctorInstance : arrow.Functor<SequenceKWHK> {
    override fun <A, B> map(fa: arrow.SequenceKWKind<A>, f: kotlin.Function1<A, B>): arrow.SequenceKW<B> =
            fa.ev().map(f)
}

object SequenceKWFunctorInstanceImplicits {
    fun instance(): SequenceKWFunctorInstance = arrow.SequenceKW.Companion.functor()
}

fun arrow.SequenceKW.Companion.functor(): SequenceKWFunctorInstance =
        object : SequenceKWFunctorInstance, arrow.Functor<SequenceKWHK> {}

interface SequenceKWApplicativeInstance : arrow.Applicative<SequenceKWHK> {
    override fun <A, B> ap(fa: arrow.SequenceKWKind<A>, ff: arrow.SequenceKWKind<kotlin.Function1<A, B>>): arrow.SequenceKW<B> =
            fa.ev().ap(ff)

    override fun <A, B> map(fa: arrow.SequenceKWKind<A>, f: kotlin.Function1<A, B>): arrow.SequenceKW<B> =
            fa.ev().map(f)

    override fun <A, B, Z> map2(fa: arrow.SequenceKWKind<A>, fb: arrow.SequenceKWKind<B>, f: kotlin.Function1<arrow.Tuple2<A, B>, Z>): arrow.SequenceKW<Z> =
            fa.ev().map2(fb, f)

    override fun <A> pure(a: A): arrow.SequenceKW<A> =
            arrow.SequenceKW.pure(a)
}

object SequenceKWApplicativeInstanceImplicits {
    fun instance(): SequenceKWApplicativeInstance = arrow.SequenceKW.Companion.applicative()
}

fun arrow.SequenceKW.Companion.applicative(): SequenceKWApplicativeInstance =
        object : SequenceKWApplicativeInstance, arrow.Applicative<SequenceKWHK> {}

interface SequenceKWMonadInstance : arrow.Monad<SequenceKWHK> {
    override fun <A, B> ap(fa: arrow.SequenceKWKind<A>, ff: arrow.SequenceKWKind<kotlin.Function1<A, B>>): arrow.SequenceKW<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: arrow.SequenceKWKind<A>, f: kotlin.Function1<A, arrow.SequenceKWKind<B>>): arrow.SequenceKW<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, arrow.SequenceKWKind<arrow.Either<A, B>>>): arrow.SequenceKW<B> =
            arrow.SequenceKW.tailRecM(a, f)

    override fun <A, B> map(fa: arrow.SequenceKWKind<A>, f: kotlin.Function1<A, B>): arrow.SequenceKW<B> =
            fa.ev().map(f)

    override fun <A, B, Z> map2(fa: arrow.SequenceKWKind<A>, fb: arrow.SequenceKWKind<B>, f: kotlin.Function1<arrow.Tuple2<A, B>, Z>): arrow.SequenceKW<Z> =
            fa.ev().map2(fb, f)

    override fun <A> pure(a: A): arrow.SequenceKW<A> =
            arrow.SequenceKW.pure(a)
}

object SequenceKWMonadInstanceImplicits {
    fun instance(): SequenceKWMonadInstance = arrow.SequenceKW.Companion.monad()
}

fun arrow.SequenceKW.Companion.monad(): SequenceKWMonadInstance =
        object : SequenceKWMonadInstance, arrow.Monad<SequenceKWHK> {}

interface SequenceKWFoldableInstance : arrow.Foldable<SequenceKWHK> {
    override fun <A, B> foldLeft(fa: arrow.SequenceKWKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: arrow.SequenceKWKind<A>, lb: arrow.Eval<B>, f: kotlin.Function2<A, arrow.Eval<B>, arrow.Eval<B>>): arrow.Eval<B> =
            fa.ev().foldRight(lb, f)
}

object SequenceKWFoldableInstanceImplicits {
    fun instance(): SequenceKWFoldableInstance = arrow.SequenceKW.Companion.foldable()
}

fun arrow.SequenceKW.Companion.foldable(): SequenceKWFoldableInstance =
        object : SequenceKWFoldableInstance, arrow.Foldable<SequenceKWHK> {}

interface SequenceKWTraverseInstance : arrow.Traverse<SequenceKWHK> {
    override fun <A, B> map(fa: arrow.SequenceKWKind<A>, f: kotlin.Function1<A, B>): arrow.SequenceKW<B> =
            fa.ev().map(f)

    override fun <G, A, B> traverse(fa: arrow.SequenceKWKind<A>, f: kotlin.Function1<A, arrow.HK<G, B>>, GA: arrow.Applicative<G>): arrow.HK<G, arrow.SequenceKW<B>> =
            fa.ev().traverse(f, GA)

    override fun <A, B> foldLeft(fa: arrow.SequenceKWKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: arrow.SequenceKWKind<A>, lb: arrow.Eval<B>, f: kotlin.Function2<A, arrow.Eval<B>, arrow.Eval<B>>): arrow.Eval<B> =
            fa.ev().foldRight(lb, f)
}

object SequenceKWTraverseInstanceImplicits {
    fun instance(): SequenceKWTraverseInstance = arrow.SequenceKW.Companion.traverse()
}

fun arrow.SequenceKW.Companion.traverse(): SequenceKWTraverseInstance =
        object : SequenceKWTraverseInstance, arrow.Traverse<SequenceKWHK> {}

interface SequenceKWSemigroupKInstance : arrow.SemigroupK<SequenceKWHK> {
    override fun <A> combineK(x: arrow.SequenceKWKind<A>, y: arrow.SequenceKWKind<A>): arrow.SequenceKW<A> =
            x.ev().combineK(y)
}

object SequenceKWSemigroupKInstanceImplicits {
    fun instance(): SequenceKWSemigroupKInstance = arrow.SequenceKW.Companion.semigroupK()
}

fun arrow.SequenceKW.Companion.semigroupK(): SequenceKWSemigroupKInstance =
        object : SequenceKWSemigroupKInstance, arrow.SemigroupK<SequenceKWHK> {}

interface SequenceKWMonoidKInstance : arrow.MonoidK<SequenceKWHK> {
    override fun <A> empty(): arrow.SequenceKW<A> =
            arrow.SequenceKW.empty()

    override fun <A> combineK(x: arrow.SequenceKWKind<A>, y: arrow.SequenceKWKind<A>): arrow.SequenceKW<A> =
            x.ev().combineK(y)
}

object SequenceKWMonoidKInstanceImplicits {
    fun instance(): SequenceKWMonoidKInstance = arrow.SequenceKW.Companion.monoidK()
}

fun arrow.SequenceKW.Companion.monoidK(): SequenceKWMonoidKInstance =
        object : SequenceKWMonoidKInstance, arrow.MonoidK<SequenceKWHK> {}
