package arrow.mtl.instances

import arrow.*
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Option

@instance(Option::class)
interface OptionTraverseFilterInstance : arrow.TraverseFilter<OptionHK> {
    override fun <A> filter(fa: arrow.OptionKind<A>, f: kotlin.Function1<A, kotlin.Boolean>): Option<A> =
            fa.ev().filter(f)

    override fun <G, A, B> traverseFilter(fa: arrow.OptionKind<A>, f: kotlin.Function1<A, arrow.HK<G, Option<B>>>, GA: arrow.Applicative<G>): arrow.HK<G, Option<B>> =
            fa.ev().traverseFilter(f, GA)

    override fun <A, B> map(fa: arrow.OptionKind<A>, f: kotlin.Function1<A, B>): Option<B> =
            fa.ev().map(f)

    override fun <G, A, B> traverse(fa: arrow.OptionKind<A>, f: kotlin.Function1<A, arrow.HK<G, B>>, GA: arrow.Applicative<G>): arrow.HK<G, Option<B>> =
            fa.ev().traverse(f, GA)

    override fun <A> exists(fa: arrow.OptionKind<A>, p: kotlin.Function1<A, kotlin.Boolean>): kotlin.Boolean =
            fa.ev().exists(p)

    override fun <A, B> foldLeft(fa: arrow.OptionKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: arrow.OptionKind<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
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
    override fun <A> empty(): Option<A> =
            Option.empty()

    override fun <A, B> ap(fa: arrow.OptionKind<A>, ff: arrow.OptionKind<kotlin.Function1<A, B>>): Option<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: arrow.OptionKind<A>, f: kotlin.Function1<A, arrow.OptionKind<B>>): Option<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, arrow.OptionKind<Either<A, B>>>): Option<B> =
            Option.tailRecM(a, f)

    override fun <A, B> map(fa: arrow.OptionKind<A>, f: kotlin.Function1<A, B>): Option<B> =
            fa.ev().map(f)

    override fun <A> pure(a: A): Option<A> =
            Option.pure(a)

    override fun <A> filter(fa: arrow.OptionKind<A>, f: kotlin.Function1<A, kotlin.Boolean>): Option<A> =
            fa.ev().filter(f)
}