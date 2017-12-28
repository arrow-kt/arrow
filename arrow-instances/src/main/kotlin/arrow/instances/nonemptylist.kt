package arrow

@instance(NonEmptyList::class)
interface NonEmptyListSemigroupInstance<A> : Semigroup<NonEmptyList<A>> {
    override fun combine(a: NonEmptyList<A>, b: NonEmptyList<A>): NonEmptyList<A> = a + b
}

@instance(NonEmptyList::class)
interface NonEmptyListEqInstance<A> : Eq<NonEmptyList<A>> {

    fun EQ(): Eq<A>

    override fun eqv(a: NonEmptyList<A>, b: NonEmptyList<A>): Boolean =
            a.all.zip(b.all) { aa, bb -> EQ().eqv(aa, bb) }.fold(true) { acc, bool ->
                acc && bool
            }
}

interface NonEmptyListFunctorInstance : arrow.Functor<NonEmptyListHK> {
    override fun <A, B> map(fa: arrow.NonEmptyListKind<A>, f: kotlin.Function1<A, B>): arrow.NonEmptyList<B> =
            fa.ev().map(f)
}

object NonEmptyListFunctorInstanceImplicits {
    fun instance(): NonEmptyListFunctorInstance = arrow.NonEmptyList.Companion.functor()
}

fun arrow.NonEmptyList.Companion.functor(): NonEmptyListFunctorInstance =
        object : NonEmptyListFunctorInstance, arrow.Functor<NonEmptyListHK> {}

interface NonEmptyListApplicativeInstance : arrow.Applicative<NonEmptyListHK> {
    override fun <A, B> ap(fa: arrow.NonEmptyListKind<A>, ff: arrow.NonEmptyListKind<kotlin.Function1<A, B>>): arrow.NonEmptyList<B> =
            fa.ev().ap(ff)

    override fun <A, B> map(fa: arrow.NonEmptyListKind<A>, f: kotlin.Function1<A, B>): arrow.NonEmptyList<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): arrow.NonEmptyList<A> =
            arrow.NonEmptyList.pure(a)
}

object NonEmptyListApplicativeInstanceImplicits {
    fun instance(): NonEmptyListApplicativeInstance = arrow.NonEmptyList.Companion.applicative()
}

fun arrow.NonEmptyList.Companion.applicative(): NonEmptyListApplicativeInstance =
        object : NonEmptyListApplicativeInstance, arrow.Applicative<NonEmptyListHK> {}

interface NonEmptyListMonadInstance : arrow.Monad<NonEmptyListHK> {
    override fun <A, B> ap(fa: arrow.NonEmptyListKind<A>, ff: arrow.NonEmptyListKind<kotlin.Function1<A, B>>): arrow.NonEmptyList<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: arrow.NonEmptyListKind<A>, f: kotlin.Function1<A, arrow.NonEmptyListKind<B>>): arrow.NonEmptyList<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, arrow.NonEmptyListKind<arrow.Either<A, B>>>): arrow.NonEmptyList<B> =
            arrow.NonEmptyList.tailRecM(a, f)

    override fun <A, B> map(fa: arrow.NonEmptyListKind<A>, f: kotlin.Function1<A, B>): arrow.NonEmptyList<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): arrow.NonEmptyList<A> =
            arrow.NonEmptyList.pure(a)
}

object NonEmptyListMonadInstanceImplicits {
    fun instance(): NonEmptyListMonadInstance = arrow.NonEmptyList.Companion.monad()
}

fun arrow.NonEmptyList.Companion.monad(): NonEmptyListMonadInstance =
        object : NonEmptyListMonadInstance, arrow.Monad<NonEmptyListHK> {}

interface NonEmptyListComonadInstance : arrow.Comonad<NonEmptyListHK> {
    override fun <A, B> coflatMap(fa: arrow.NonEmptyListKind<A>, f: kotlin.Function1<arrow.NonEmptyListKind<A>, B>): arrow.NonEmptyList<B> =
            fa.ev().coflatMap(f)

    override fun <A> extract(fa: arrow.NonEmptyListKind<A>): A =
            fa.ev().extract()

    override fun <A, B> map(fa: arrow.NonEmptyListKind<A>, f: kotlin.Function1<A, B>): arrow.NonEmptyList<B> =
            fa.ev().map(f)
}

object NonEmptyListComonadInstanceImplicits {
    fun instance(): NonEmptyListComonadInstance = arrow.NonEmptyList.Companion.comonad()
}

fun arrow.NonEmptyList.Companion.comonad(): NonEmptyListComonadInstance =
        object : NonEmptyListComonadInstance, arrow.Comonad<NonEmptyListHK> {}

interface NonEmptyListBimonadInstance : arrow.Bimonad<NonEmptyListHK> {
    override fun <A, B> ap(fa: arrow.NonEmptyListKind<A>, ff: arrow.NonEmptyListKind<kotlin.Function1<A, B>>): arrow.NonEmptyList<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: arrow.NonEmptyListKind<A>, f: kotlin.Function1<A, arrow.NonEmptyListKind<B>>): arrow.NonEmptyList<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, arrow.NonEmptyListKind<arrow.Either<A, B>>>): arrow.NonEmptyList<B> =
            arrow.NonEmptyList.tailRecM(a, f)

    override fun <A, B> map(fa: arrow.NonEmptyListKind<A>, f: kotlin.Function1<A, B>): arrow.NonEmptyList<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): arrow.NonEmptyList<A> =
            arrow.NonEmptyList.pure(a)

    override fun <A, B> coflatMap(fa: arrow.NonEmptyListKind<A>, f: kotlin.Function1<arrow.NonEmptyListKind<A>, B>): arrow.NonEmptyList<B> =
            fa.ev().coflatMap(f)

    override fun <A> extract(fa: arrow.NonEmptyListKind<A>): A =
            fa.ev().extract()
}

object NonEmptyListBimonadInstanceImplicits {
    fun instance(): NonEmptyListBimonadInstance = arrow.NonEmptyList.Companion.bimonad()
}

fun arrow.NonEmptyList.Companion.bimonad(): NonEmptyListBimonadInstance =
        object : NonEmptyListBimonadInstance, arrow.Bimonad<NonEmptyListHK> {}

interface NonEmptyListFoldableInstance : arrow.Foldable<NonEmptyListHK> {
    override fun <A, B> foldLeft(fa: arrow.NonEmptyListKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: arrow.NonEmptyListKind<A>, lb: arrow.Eval<B>, f: kotlin.Function2<A, arrow.Eval<B>, arrow.Eval<B>>): arrow.Eval<B> =
            fa.ev().foldRight(lb, f)

    override fun <A> isEmpty(fa: arrow.NonEmptyListKind<A>): kotlin.Boolean =
            fa.ev().isEmpty()
}

object NonEmptyListFoldableInstanceImplicits {
    fun instance(): NonEmptyListFoldableInstance = arrow.NonEmptyList.Companion.foldable()
}

fun arrow.NonEmptyList.Companion.foldable(): NonEmptyListFoldableInstance =
        object : NonEmptyListFoldableInstance, arrow.Foldable<NonEmptyListHK> {}

interface NonEmptyListTraverseInstance : arrow.Traverse<NonEmptyListHK> {
    override fun <A, B> map(fa: arrow.NonEmptyListKind<A>, f: kotlin.Function1<A, B>): arrow.NonEmptyList<B> =
            fa.ev().map(f)

    override fun <G, A, B> traverse(fa: arrow.NonEmptyListKind<A>, f: kotlin.Function1<A, arrow.HK<G, B>>, GA: arrow.Applicative<G>): arrow.HK<G, arrow.NonEmptyList<B>> =
            fa.ev().traverse(f, GA)

    override fun <A, B> foldLeft(fa: arrow.NonEmptyListKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: arrow.NonEmptyListKind<A>, lb: arrow.Eval<B>, f: kotlin.Function2<A, arrow.Eval<B>, arrow.Eval<B>>): arrow.Eval<B> =
            fa.ev().foldRight(lb, f)

    override fun <A> isEmpty(fa: arrow.NonEmptyListKind<A>): kotlin.Boolean =
            fa.ev().isEmpty()
}

object NonEmptyListTraverseInstanceImplicits {
    fun instance(): NonEmptyListTraverseInstance = arrow.NonEmptyList.Companion.traverse()
}

fun arrow.NonEmptyList.Companion.traverse(): NonEmptyListTraverseInstance =
        object : NonEmptyListTraverseInstance, arrow.Traverse<NonEmptyListHK> {}

interface NonEmptyListSemigroupKInstance : arrow.SemigroupK<NonEmptyListHK> {
    override fun <A> combineK(x: arrow.NonEmptyListKind<A>, y: arrow.NonEmptyListKind<A>): arrow.NonEmptyList<A> =
            x.ev().combineK(y)
}

object NonEmptyListSemigroupKInstanceImplicits {
    fun instance(): NonEmptyListSemigroupKInstance = arrow.NonEmptyList.Companion.semigroupK()
}

fun arrow.NonEmptyList.Companion.semigroupK(): NonEmptyListSemigroupKInstance =
        object : NonEmptyListSemigroupKInstance, arrow.SemigroupK<NonEmptyListHK> {}
