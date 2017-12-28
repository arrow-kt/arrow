package arrow

interface EvalFunctorInstance : arrow.Functor<EvalHK> {
    override fun <A, B> map(fa: arrow.EvalKind<A>, f: kotlin.Function1<A, B>): arrow.Eval<B> =
            fa.ev().map(f)
}

object EvalFunctorInstanceImplicits {
    fun instance(): EvalFunctorInstance = arrow.Eval.Companion.functor()
}

fun arrow.Eval.Companion.functor(): EvalFunctorInstance =
        object : EvalFunctorInstance, arrow.Functor<EvalHK> {}

interface EvalApplicativeInstance : arrow.Applicative<EvalHK> {
    override fun <A, B> ap(fa: arrow.EvalKind<A>, ff: arrow.EvalKind<kotlin.Function1<A, B>>): arrow.Eval<B> =
            fa.ev().ap(ff)

    override fun <A, B> map(fa: arrow.EvalKind<A>, f: kotlin.Function1<A, B>): arrow.Eval<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): arrow.Eval<A> =
            arrow.Eval.pure(a)
}

object EvalApplicativeInstanceImplicits {
    fun instance(): EvalApplicativeInstance = arrow.Eval.Companion.applicative()
}

fun arrow.Eval.Companion.applicative(): EvalApplicativeInstance =
        object : EvalApplicativeInstance, arrow.Applicative<EvalHK> {}

interface EvalMonadInstance : arrow.Monad<EvalHK> {
    override fun <A, B> ap(fa: arrow.EvalKind<A>, ff: arrow.EvalKind<kotlin.Function1<A, B>>): arrow.Eval<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: arrow.EvalKind<A>, f: kotlin.Function1<A, arrow.EvalKind<B>>): arrow.Eval<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, arrow.EvalKind<arrow.Either<A, B>>>): arrow.Eval<B> =
            arrow.Eval.tailRecM(a, f)

    override fun <A, B> map(fa: arrow.EvalKind<A>, f: kotlin.Function1<A, B>): arrow.Eval<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): arrow.Eval<A> =
            arrow.Eval.pure(a)
}

object EvalMonadInstanceImplicits {
    fun instance(): EvalMonadInstance = arrow.Eval.Companion.monad()
}

fun arrow.Eval.Companion.monad(): EvalMonadInstance =
        object : EvalMonadInstance, arrow.Monad<EvalHK> {}

interface EvalComonadInstance : arrow.Comonad<EvalHK> {
    override fun <A, B> coflatMap(fa: arrow.EvalKind<A>, f: kotlin.Function1<arrow.EvalKind<A>, B>): arrow.Eval<B> =
            fa.ev().coflatMap(f)

    override fun <A> extract(fa: arrow.EvalKind<A>): A =
            fa.ev().extract()

    override fun <A, B> map(fa: arrow.EvalKind<A>, f: kotlin.Function1<A, B>): arrow.Eval<B> =
            fa.ev().map(f)
}

object EvalComonadInstanceImplicits {
    fun instance(): EvalComonadInstance = arrow.Eval.Companion.comonad()
}

fun arrow.Eval.Companion.comonad(): EvalComonadInstance =
        object : EvalComonadInstance, arrow.Comonad<EvalHK> {}

interface EvalBimonadInstance : arrow.Bimonad<EvalHK> {
    override fun <A, B> ap(fa: arrow.EvalKind<A>, ff: arrow.EvalKind<kotlin.Function1<A, B>>): arrow.Eval<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: arrow.EvalKind<A>, f: kotlin.Function1<A, arrow.EvalKind<B>>): arrow.Eval<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, arrow.EvalKind<arrow.Either<A, B>>>): arrow.Eval<B> =
            arrow.Eval.tailRecM(a, f)

    override fun <A, B> map(fa: arrow.EvalKind<A>, f: kotlin.Function1<A, B>): arrow.Eval<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): arrow.Eval<A> =
            arrow.Eval.pure(a)

    override fun <A, B> coflatMap(fa: arrow.EvalKind<A>, f: kotlin.Function1<arrow.EvalKind<A>, B>): arrow.Eval<B> =
            fa.ev().coflatMap(f)

    override fun <A> extract(fa: arrow.EvalKind<A>): A =
            fa.ev().extract()
}

object EvalBimonadInstanceImplicits {
    fun instance(): EvalBimonadInstance = arrow.Eval.Companion.bimonad()
}

fun arrow.Eval.Companion.bimonad(): EvalBimonadInstance =
        object : EvalBimonadInstance, arrow.Bimonad<EvalHK> {}
