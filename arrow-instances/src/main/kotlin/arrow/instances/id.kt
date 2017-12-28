package arrow

@instance(Id::class)
interface IdEqInstance<A> : Eq<Id<A>> {

    fun EQ(): Eq<A>

    override fun eqv(a: Id<A>, b: Id<A>): Boolean =
            EQ().eqv(a.value, b.value)
}

interface IdFunctorInstance : arrow.Functor<IdHK> {
    override fun <A, B> map(fa: arrow.IdKind<A>, f: kotlin.Function1<A, B>): arrow.Id<B> =
            fa.ev().map(f)
}

object IdFunctorInstanceImplicits {
    fun instance(): IdFunctorInstance = arrow.Id.Companion.functor()
}

fun arrow.Id.Companion.functor(): IdFunctorInstance =
        object : IdFunctorInstance, arrow.Functor<IdHK> {}

interface IdApplicativeInstance : arrow.Applicative<IdHK> {
    override fun <A, B> ap(fa: arrow.IdKind<A>, ff: arrow.IdKind<kotlin.Function1<A, B>>): arrow.Id<B> =
            fa.ev().ap(ff)

    override fun <A, B> map(fa: arrow.IdKind<A>, f: kotlin.Function1<A, B>): arrow.Id<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): arrow.Id<A> =
            arrow.Id.pure(a)
}

object IdApplicativeInstanceImplicits {
    fun instance(): IdApplicativeInstance = arrow.Id.Companion.applicative()
}

fun arrow.Id.Companion.applicative(): IdApplicativeInstance =
        object : IdApplicativeInstance, arrow.Applicative<IdHK> {}

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

object IdMonadInstanceImplicits {
    fun instance(): IdMonadInstance = arrow.Id.Companion.monad()
}

fun arrow.Id.Companion.monad(): IdMonadInstance =
        object : IdMonadInstance, arrow.Monad<IdHK> {}

interface IdComonadInstance : arrow.Comonad<IdHK> {
    override fun <A, B> coflatMap(fa: arrow.IdKind<A>, f: kotlin.Function1<arrow.IdKind<A>, B>): arrow.Id<B> =
            fa.ev().coflatMap(f)

    override fun <A> extract(fa: arrow.IdKind<A>): A =
            fa.ev().extract()

    override fun <A, B> map(fa: arrow.IdKind<A>, f: kotlin.Function1<A, B>): arrow.Id<B> =
            fa.ev().map(f)
}

object IdComonadInstanceImplicits {
    fun instance(): IdComonadInstance = arrow.Id.Companion.comonad()
}

fun arrow.Id.Companion.comonad(): IdComonadInstance =
        object : IdComonadInstance, arrow.Comonad<IdHK> {}

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

object IdBimonadInstanceImplicits {
    fun instance(): IdBimonadInstance = arrow.Id.Companion.bimonad()
}

fun arrow.Id.Companion.bimonad(): IdBimonadInstance =
        object : IdBimonadInstance, arrow.Bimonad<IdHK> {}

interface IdFoldableInstance : arrow.Foldable<IdHK> {
    override fun <A, B> foldLeft(fa: arrow.IdKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: arrow.IdKind<A>, lb: arrow.Eval<B>, f: kotlin.Function2<A, arrow.Eval<B>, arrow.Eval<B>>): arrow.Eval<B> =
            fa.ev().foldRight(lb, f)
}

object IdFoldableInstanceImplicits {
    fun instance(): IdFoldableInstance = arrow.Id.Companion.foldable()
}

fun arrow.Id.Companion.foldable(): IdFoldableInstance =
        object : IdFoldableInstance, arrow.Foldable<IdHK> {}

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

object IdTraverseInstanceImplicits {
    fun instance(): IdTraverseInstance = arrow.Id.Companion.traverse()
}

fun arrow.Id.Companion.traverse(): IdTraverseInstance =
        object : IdTraverseInstance, arrow.Traverse<IdHK> {}
