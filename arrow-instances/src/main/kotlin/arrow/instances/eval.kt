package arrow

@instance(Eval::class)
interface EvalFunctorInstance : arrow.Functor<EvalHK> {
    override fun <A, B> map(fa: arrow.EvalKind<A>, f: kotlin.Function1<A, B>): arrow.Eval<B> =
            fa.ev().map(f)
}

@instance(Eval::class)
interface EvalApplicativeInstance : arrow.Applicative<EvalHK> {
    override fun <A, B> ap(fa: arrow.EvalKind<A>, ff: arrow.EvalKind<kotlin.Function1<A, B>>): arrow.Eval<B> =
            fa.ev().ap(ff)

    override fun <A, B> map(fa: arrow.EvalKind<A>, f: kotlin.Function1<A, B>): arrow.Eval<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): arrow.Eval<A> =
            arrow.Eval.pure(a)
}

@instance(Eval::class)
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

@instance(Eval::class)
interface EvalComonadInstance : arrow.Comonad<EvalHK> {
    override fun <A, B> coflatMap(fa: arrow.EvalKind<A>, f: kotlin.Function1<arrow.EvalKind<A>, B>): arrow.Eval<B> =
            fa.ev().coflatMap(f)

    override fun <A> extract(fa: arrow.EvalKind<A>): A =
            fa.ev().extract()

    override fun <A, B> map(fa: arrow.EvalKind<A>, f: kotlin.Function1<A, B>): arrow.Eval<B> =
            fa.ev().map(f)
}

@instance(Eval::class)
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