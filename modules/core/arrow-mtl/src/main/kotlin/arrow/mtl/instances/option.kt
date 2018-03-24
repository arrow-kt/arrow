package arrow.mtl.instances

import arrow.Kind
import arrow.core.*
import arrow.instance
import arrow.instances.traverse
import arrow.instances.traverseFilter
import arrow.mtl.typeclasses.MonadFilter
import arrow.mtl.typeclasses.TraverseFilter
import arrow.typeclasses.Applicative

@instance(Option::class)
interface OptionTraverseFilterInstance : TraverseFilter<ForOption> {
    override fun <A> Kind<ForOption, A>.filter(f: (A) -> Boolean): Option<A> =
            fix().filter(f)

    override fun <G, A, B> Applicative<G>.traverseFilter(fa: Kind<ForOption, A>, f: (A) -> Kind<G, Option<B>>): arrow.Kind<G, Option<B>> =
            fa.fix().traverseFilter(f, this)

    override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Option<B> =
            fix().map(f)

    override fun <G, A, B> Applicative<G>.traverse(fa: Kind<ForOption, A>, f: (A) -> Kind<G, B>): arrow.Kind<G, Option<B>> =
            fa.fix().traverse(f, this)

    override fun <A> exists(fa: OptionOf<A>, p: kotlin.Function1<A, kotlin.Boolean>): kotlin.Boolean =
            fa.fix().exists(p)

    override fun <A, B> foldLeft(fa: OptionOf<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: OptionOf<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.fix().foldRight(lb, f)

    override fun <A> forall(fa: OptionOf<A>, p: kotlin.Function1<A, kotlin.Boolean>): kotlin.Boolean =
            fa.fix().forall(p)

    override fun <A> isEmpty(fa: OptionOf<A>): kotlin.Boolean =
            fa.fix().isEmpty()

    override fun <A> nonEmpty(fa: OptionOf<A>): kotlin.Boolean =
            fa.fix().nonEmpty()
}

@instance(Option::class)
interface OptionMonadFilterInstance : MonadFilter<ForOption> {
    override fun <A> empty(): Option<A> =
            Option.empty()

    override fun <A, B> Kind<ForOption, A>.ap(ff: Kind<ForOption, (A) -> B>): Option<B> =
            fix().ap(ff)

    override fun <A, B> Kind<ForOption, A>.flatMap(f: (A) -> Kind<ForOption, B>): Option<B> =
            fix().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, OptionOf<Either<A, B>>>): Option<B> =
            Option.tailRecM(a, f)

    override fun <A, B> Kind<ForOption, A>.map(f: (A) -> B): Option<B> =
            fix().map(f)

    override fun <A> pure(a: A): Option<A> =
            Option.pure(a)

    override fun <A> Kind<ForOption, A>.filter(f: (A) -> Boolean): Option<A> =
            fix().filter(f)
}
