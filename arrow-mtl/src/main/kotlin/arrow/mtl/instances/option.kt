package arrow.mtl.instances

import arrow.*

@instance(Option::class)
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

@instance(Option::class)
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