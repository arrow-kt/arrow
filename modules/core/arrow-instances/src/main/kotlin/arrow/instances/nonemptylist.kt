package arrow.instances

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.data.*
import arrow.instance
import arrow.typeclasses.*

@instance(NonEmptyList::class)
interface NonEmptyListSemigroupInstance<A> : Semigroup<NonEmptyList<A>> {
    override fun combine(a: NonEmptyList<A>, b: NonEmptyList<A>): NonEmptyList<A> = a + b
}

@instance(NonEmptyList::class)
interface NonEmptyListEqInstance<A> : Eq<NonEmptyList<A>> {

    fun EQ(): Eq<A>

    override fun NonEmptyList<A>.eqv(b: NonEmptyList<A>): Boolean =
            all.zip(b.all) { aa, bb -> EQ().run { aa.eqv(bb) } }.fold(true) { acc, bool ->
                acc && bool
            }
}

@instance(NonEmptyList::class)
interface NonEmptyListShowInstance<A> : Show<NonEmptyList<A>> {
    override fun show(a: NonEmptyList<A>): String =
            a.toString()
}

@instance(NonEmptyList::class)
interface NonEmptyListFunctorInstance : Functor<ForNonEmptyList> {
    override fun <A, B> map(fa: NonEmptyListOf<A>, f: kotlin.Function1<A, B>): NonEmptyList<B> =
            fa.fix().map(f)
}

@instance(NonEmptyList::class)
interface NonEmptyListApplicativeInstance : Applicative<ForNonEmptyList> {
    override fun <A, B> ap(fa: NonEmptyListOf<A>, ff: NonEmptyListOf<kotlin.Function1<A, B>>): NonEmptyList<B> =
            fa.fix().ap(ff)

    override fun <A, B> map(fa: NonEmptyListOf<A>, f: kotlin.Function1<A, B>): NonEmptyList<B> =
            fa.fix().map(f)

    override fun <A> pure(a: A): NonEmptyList<A> =
            NonEmptyList.pure(a)
}

@instance(NonEmptyList::class)
interface NonEmptyListMonadInstance : Monad<ForNonEmptyList> {
    override fun <A, B> ap(fa: NonEmptyListOf<A>, ff: NonEmptyListOf<kotlin.Function1<A, B>>): NonEmptyList<B> =
            fa.fix().ap(ff)

    override fun <A, B> flatMap(fa: NonEmptyListOf<A>, f: kotlin.Function1<A, NonEmptyListOf<B>>): NonEmptyList<B> =
            fa.fix().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, NonEmptyListOf<Either<A, B>>>): NonEmptyList<B> =
            NonEmptyList.tailRecM(a, f)

    override fun <A, B> map(fa: NonEmptyListOf<A>, f: kotlin.Function1<A, B>): NonEmptyList<B> =
            fa.fix().map(f)

    override fun <A> pure(a: A): NonEmptyList<A> =
            NonEmptyList.pure(a)
}

@instance(NonEmptyList::class)
interface NonEmptyListComonadInstance : Comonad<ForNonEmptyList> {
    override fun <A, B> coflatMap(fa: NonEmptyListOf<A>, f: kotlin.Function1<NonEmptyListOf<A>, B>): NonEmptyList<B> =
            fa.fix().coflatMap(f)

    override fun <A> extract(fa: NonEmptyListOf<A>): A =
            fa.fix().extract()

    override fun <A, B> map(fa: NonEmptyListOf<A>, f: kotlin.Function1<A, B>): NonEmptyList<B> =
            fa.fix().map(f)
}

@instance(NonEmptyList::class)
interface NonEmptyListBimonadInstance : Bimonad<ForNonEmptyList> {
    override fun <A, B> ap(fa: NonEmptyListOf<A>, ff: NonEmptyListOf<kotlin.Function1<A, B>>): NonEmptyList<B> =
            fa.fix().ap(ff)

    override fun <A, B> flatMap(fa: NonEmptyListOf<A>, f: kotlin.Function1<A, NonEmptyListOf<B>>): NonEmptyList<B> =
            fa.fix().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, NonEmptyListOf<Either<A, B>>>): NonEmptyList<B> =
            NonEmptyList.tailRecM(a, f)

    override fun <A, B> map(fa: NonEmptyListOf<A>, f: kotlin.Function1<A, B>): NonEmptyList<B> =
            fa.fix().map(f)

    override fun <A> pure(a: A): NonEmptyList<A> =
            NonEmptyList.pure(a)

    override fun <A, B> coflatMap(fa: NonEmptyListOf<A>, f: kotlin.Function1<NonEmptyListOf<A>, B>): NonEmptyList<B> =
            fa.fix().coflatMap(f)

    override fun <A> extract(fa: NonEmptyListOf<A>): A =
            fa.fix().extract()
}

@instance(NonEmptyList::class)
interface NonEmptyListFoldableInstance : Foldable<ForNonEmptyList> {
    override fun <A, B> foldLeft(fa: NonEmptyListOf<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: NonEmptyListOf<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.fix().foldRight(lb, f)

    override fun <A> isEmpty(fa: NonEmptyListOf<A>): kotlin.Boolean =
            fa.fix().isEmpty()
}

@instance(NonEmptyList::class)
interface NonEmptyListTraverseInstance : Traverse<ForNonEmptyList> {
    override fun <A, B> map(fa: NonEmptyListOf<A>, f: kotlin.Function1<A, B>): NonEmptyList<B> =
            fa.fix().map(f)

    override fun <G, A, B> traverse(fa: NonEmptyListOf<A>, f: kotlin.Function1<A, Kind<G, B>>, GA: Applicative<G>): Kind<G, NonEmptyList<B>> =
            fa.fix().traverse(f, GA)

    override fun <A, B> foldLeft(fa: NonEmptyListOf<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: NonEmptyListOf<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.fix().foldRight(lb, f)

    override fun <A> isEmpty(fa: NonEmptyListOf<A>): kotlin.Boolean =
            fa.fix().isEmpty()
}

@instance(NonEmptyList::class)
interface NonEmptyListSemigroupKInstance : SemigroupK<ForNonEmptyList> {
    override fun <A> combineK(x: NonEmptyListOf<A>, y: NonEmptyListOf<A>): NonEmptyList<A> =
            x.fix().combineK(y)
}