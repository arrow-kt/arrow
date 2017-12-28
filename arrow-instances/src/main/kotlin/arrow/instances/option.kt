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

interface OptionFunctorInstance : arrow.Functor<OptionHK> {
    override fun <A, B> map(fa: arrow.OptionKind<A>, f: kotlin.Function1<A, B>): arrow.Option<B> =
            fa.ev().map(f)
}

object OptionFunctorInstanceImplicits {
    fun instance(): OptionFunctorInstance = arrow.Option.Companion.functor()
}

fun arrow.Option.Companion.functor(): OptionFunctorInstance =
        object : OptionFunctorInstance, arrow.Functor<OptionHK> {}

interface OptionApplicativeInstance : arrow.Applicative<OptionHK> {
    override fun <A, B> ap(fa: arrow.OptionKind<A>, ff: arrow.OptionKind<kotlin.Function1<A, B>>): arrow.Option<B> =
            fa.ev().ap(ff)

    override fun <A, B> map(fa: arrow.OptionKind<A>, f: kotlin.Function1<A, B>): arrow.Option<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): arrow.Option<A> =
            arrow.Option.pure(a)
}

object OptionApplicativeInstanceImplicits {
    fun instance(): OptionApplicativeInstance = arrow.Option.Companion.applicative()
}

fun arrow.Option.Companion.applicative(): OptionApplicativeInstance =
        object : OptionApplicativeInstance, arrow.Applicative<OptionHK> {}

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

object OptionMonadInstanceImplicits {
    fun instance(): OptionMonadInstance = arrow.Option.Companion.monad()
}

fun arrow.Option.Companion.monad(): OptionMonadInstance =
        object : OptionMonadInstance, arrow.Monad<OptionHK> {}

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

object OptionFoldableInstanceImplicits {
    fun instance(): OptionFoldableInstance = arrow.Option.Companion.foldable()
}

fun arrow.Option.Companion.foldable(): OptionFoldableInstance =
        object : OptionFoldableInstance, arrow.Foldable<OptionHK> {}

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

object OptionTraverseInstanceImplicits {
    fun instance(): OptionTraverseInstance = arrow.Option.Companion.traverse()
}

fun arrow.Option.Companion.traverse(): OptionTraverseInstance =
        object : OptionTraverseInstance, arrow.Traverse<OptionHK> {}

interface OptionTraverseFilterInstance : arrow.TraverseFilter<OptionHK> {
    override fun <A> filter(fa: arrow.OptionKind<A>, f: kotlin.Function1<A, kotlin.Boolean>): arrow.Option<A> =
            fa.ev().filter(f)

    override fun <G, A, B> traverseFilter(fa: arrow.OptionKind<A>, f: kotlin.Function1<A, arrow.HK<G, arrow.Option<B>>>, GA: arrow.Applicative<G>): arrow.HK<G, arrow.Option<B>> =
            fa.ev().traverseFilter(f, GA)

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

object OptionTraverseFilterInstanceImplicits {
    fun instance(): OptionTraverseFilterInstance = arrow.Option.Companion.traverseFilter()
}

fun arrow.Option.Companion.traverseFilter(): OptionTraverseFilterInstance =
        object : OptionTraverseFilterInstance, arrow.TraverseFilter<OptionHK> {}

interface OptionMonadFilterInstance : arrow.MonadFilter<OptionHK> {
    override fun <A> empty(): arrow.Option<A> =
            arrow.Option.empty()

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

    override fun <A> filter(fa: arrow.OptionKind<A>, f: kotlin.Function1<A, kotlin.Boolean>): arrow.Option<A> =
            fa.ev().filter(f)
}

object OptionMonadFilterInstanceImplicits {
    fun instance(): OptionMonadFilterInstance = arrow.Option.Companion.monadFilter()
}

fun arrow.Option.Companion.monadFilter(): OptionMonadFilterInstance =
        object : OptionMonadFilterInstance, arrow.MonadFilter<OptionHK> {}
