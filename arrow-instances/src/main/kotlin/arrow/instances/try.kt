package arrow

@instance(Try::class)
interface TryMonadErrorInstance : TryMonadInstance, MonadError<TryHK, Throwable> {

    override fun <A> raiseError(e: Throwable): Try<A> = Failure(e)

    override fun <A> handleErrorWith(fa: TryKind<A>, f: (Throwable) -> TryKind<A>): Try<A> = fa.ev().recoverWith { f(it).ev() }

}

@instance(Try::class)
interface TryEqInstance<A> : Eq<Try<A>> {

    fun EQA(): Eq<A>

    override fun eqv(a: Try<A>, b: Try<A>): Boolean = when (a) {
        is Success -> when (b) {
            is Failure -> false
            is Success -> EQA().eqv(a.value, b.value)
        }
        is Failure -> when (b) {
        //currently not supported by implicit resolution to have implicit that does not occur in type params
            is Failure -> a.exception == b.exception
            is Success -> false
        }
    }

}

interface TryFunctorInstance : arrow.Functor<TryHK> {
    override fun <A, B> map(fa: arrow.TryKind<A>, f: kotlin.Function1<A, B>): arrow.Try<B> =
            fa.ev().map(f)
}

object TryFunctorInstanceImplicits {
    fun instance(): TryFunctorInstance = arrow.Try.Companion.functor()
}

fun arrow.Try.Companion.functor(): TryFunctorInstance =
        object : TryFunctorInstance, arrow.Functor<TryHK> {}

interface TryApplicativeInstance : arrow.Applicative<TryHK> {
    override fun <A, B> ap(fa: arrow.TryKind<A>, ff: arrow.TryKind<kotlin.Function1<A, B>>): arrow.Try<B> =
            fa.ev().ap(ff)

    override fun <A, B> map(fa: arrow.TryKind<A>, f: kotlin.Function1<A, B>): arrow.Try<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): arrow.Try<A> =
            arrow.Try.pure(a)
}

object TryApplicativeInstanceImplicits {
    fun instance(): TryApplicativeInstance = arrow.Try.Companion.applicative()
}

fun arrow.Try.Companion.applicative(): TryApplicativeInstance =
        object : TryApplicativeInstance, arrow.Applicative<TryHK> {}

interface TryMonadInstance : arrow.Monad<TryHK> {
    override fun <A, B> ap(fa: arrow.TryKind<A>, ff: arrow.TryKind<kotlin.Function1<A, B>>): arrow.Try<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: arrow.TryKind<A>, f: kotlin.Function1<A, arrow.TryKind<B>>): arrow.Try<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, arrow.TryKind<arrow.Either<A, B>>>): arrow.Try<B> =
            arrow.Try.tailRecM(a, f)

    override fun <A, B> map(fa: arrow.TryKind<A>, f: kotlin.Function1<A, B>): arrow.Try<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): arrow.Try<A> =
            arrow.Try.pure(a)
}

object TryMonadInstanceImplicits {
    fun instance(): TryMonadInstance = arrow.Try.Companion.monad()
}

fun arrow.Try.Companion.monad(): TryMonadInstance =
        object : TryMonadInstance, arrow.Monad<TryHK> {}

interface TryFoldableInstance : arrow.Foldable<TryHK> {
    override fun <A> exists(fa: arrow.TryKind<A>, p: kotlin.Function1<A, kotlin.Boolean>): kotlin.Boolean =
            fa.ev().exists(p)

    override fun <A, B> foldLeft(fa: arrow.TryKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: arrow.TryKind<A>, lb: arrow.Eval<B>, f: kotlin.Function2<A, arrow.Eval<B>, arrow.Eval<B>>): arrow.Eval<B> =
            fa.ev().foldRight(lb, f)
}

object TryFoldableInstanceImplicits {
    fun instance(): TryFoldableInstance = arrow.Try.Companion.foldable()
}

fun arrow.Try.Companion.foldable(): TryFoldableInstance =
        object : TryFoldableInstance, arrow.Foldable<TryHK> {}

interface TryTraverseInstance : arrow.Traverse<TryHK> {
    override fun <A, B> map(fa: arrow.TryKind<A>, f: kotlin.Function1<A, B>): arrow.Try<B> =
            fa.ev().map(f)

    override fun <G, A, B> traverse(fa: arrow.TryKind<A>, f: kotlin.Function1<A, arrow.HK<G, B>>, GA: arrow.Applicative<G>): arrow.HK<G, arrow.Try<B>> =
            fa.ev().traverse(f, GA)

    override fun <A> exists(fa: arrow.TryKind<A>, p: kotlin.Function1<A, kotlin.Boolean>): kotlin.Boolean =
            fa.ev().exists(p)

    override fun <A, B> foldLeft(fa: arrow.TryKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: arrow.TryKind<A>, lb: arrow.Eval<B>, f: kotlin.Function2<A, arrow.Eval<B>, arrow.Eval<B>>): arrow.Eval<B> =
            fa.ev().foldRight(lb, f)
}

object TryTraverseInstanceImplicits {
    fun instance(): TryTraverseInstance = arrow.Try.Companion.traverse()
}

fun arrow.Try.Companion.traverse(): TryTraverseInstance =
        object : TryTraverseInstance, arrow.Traverse<TryHK> {}
