package arrow.instances

import arrow.*
import arrow.core.*
import arrow.data.*
import arrow.typeclasses.*

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

@instance(NonEmptyList::class)
interface NonEmptyListFunctorInstance : Functor<ForNonEmptyList> {
    override fun <A, B> map(fa: NonEmptyListOf<A>, f: kotlin.Function1<A, B>): NonEmptyList<B> =
            fa.extract().map(f)
}

@instance(NonEmptyList::class)
interface NonEmptyListApplicativeInstance : Applicative<ForNonEmptyList> {
    override fun <A, B> ap(fa: NonEmptyListOf<A>, ff: NonEmptyListOf<kotlin.Function1<A, B>>): NonEmptyList<B> =
            fa.extract().ap(ff)

    override fun <A, B> map(fa: NonEmptyListOf<A>, f: kotlin.Function1<A, B>): NonEmptyList<B> =
            fa.extract().map(f)

    override fun <A> pure(a: A): NonEmptyList<A> =
            NonEmptyList.pure(a)
}

@instance(NonEmptyList::class)
interface NonEmptyListMonadInstance : Monad<ForNonEmptyList> {
    override fun <A, B> ap(fa: NonEmptyListOf<A>, ff: NonEmptyListOf<kotlin.Function1<A, B>>): NonEmptyList<B> =
            fa.extract().ap(ff)

    override fun <A, B> flatMap(fa: NonEmptyListOf<A>, f: kotlin.Function1<A, NonEmptyListOf<B>>): NonEmptyList<B> =
            fa.extract().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, NonEmptyListOf<Either<A, B>>>): NonEmptyList<B> =
            NonEmptyList.tailRecM(a, f)

    override fun <A, B> map(fa: NonEmptyListOf<A>, f: kotlin.Function1<A, B>): NonEmptyList<B> =
            fa.extract().map(f)

    override fun <A> pure(a: A): NonEmptyList<A> =
            NonEmptyList.pure(a)
}

@instance(NonEmptyList::class)
interface NonEmptyListComonadInstance : Comonad<ForNonEmptyList> {
    override fun <A, B> coflatMap(fa: NonEmptyListOf<A>, f: kotlin.Function1<NonEmptyListOf<A>, B>): NonEmptyList<B> =
            fa.extract().coflatMap(f)

    override fun <A> extract(fa: NonEmptyListOf<A>): A =
            fa.extract().extract()

    override fun <A, B> map(fa: NonEmptyListOf<A>, f: kotlin.Function1<A, B>): NonEmptyList<B> =
            fa.extract().map(f)
}

@instance(NonEmptyList::class)
interface NonEmptyListBimonadInstance : Bimonad<ForNonEmptyList> {
    override fun <A, B> ap(fa: NonEmptyListOf<A>, ff: NonEmptyListOf<kotlin.Function1<A, B>>): NonEmptyList<B> =
            fa.extract().ap(ff)

    override fun <A, B> flatMap(fa: NonEmptyListOf<A>, f: kotlin.Function1<A, NonEmptyListOf<B>>): NonEmptyList<B> =
            fa.extract().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, NonEmptyListOf<Either<A, B>>>): NonEmptyList<B> =
            NonEmptyList.tailRecM(a, f)

    override fun <A, B> map(fa: NonEmptyListOf<A>, f: kotlin.Function1<A, B>): NonEmptyList<B> =
            fa.extract().map(f)

    override fun <A> pure(a: A): NonEmptyList<A> =
            NonEmptyList.pure(a)

    override fun <A, B> coflatMap(fa: NonEmptyListOf<A>, f: kotlin.Function1<NonEmptyListOf<A>, B>): NonEmptyList<B> =
            fa.extract().coflatMap(f)

    override fun <A> extract(fa: NonEmptyListOf<A>): A =
            fa.extract().extract()
}

@instance(NonEmptyList::class)
interface NonEmptyListFoldableInstance : Foldable<ForNonEmptyList> {
    override fun <A, B> foldLeft(fa: NonEmptyListOf<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.extract().foldLeft(b, f)

    override fun <A, B> foldRight(fa: NonEmptyListOf<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.extract().foldRight(lb, f)

    override fun <A> isEmpty(fa: NonEmptyListOf<A>): kotlin.Boolean =
            fa.extract().isEmpty()
}

@instance(NonEmptyList::class)
interface NonEmptyListTraverseInstance : Traverse<ForNonEmptyList> {
    override fun <A, B> map(fa: NonEmptyListOf<A>, f: kotlin.Function1<A, B>): NonEmptyList<B> =
            fa.extract().map(f)

    override fun <G, A, B> traverse(fa: NonEmptyListOf<A>, f: kotlin.Function1<A, Kind<G, B>>, GA: Applicative<G>): Kind<G, NonEmptyList<B>> =
            fa.extract().traverse(f, GA)

    override fun <A, B> foldLeft(fa: NonEmptyListOf<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.extract().foldLeft(b, f)

    override fun <A, B> foldRight(fa: NonEmptyListOf<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.extract().foldRight(lb, f)

    override fun <A> isEmpty(fa: NonEmptyListOf<A>): kotlin.Boolean =
            fa.extract().isEmpty()
}

@instance(NonEmptyList::class)
interface NonEmptyListSemigroupKInstance : SemigroupK<ForNonEmptyList> {
    override fun <A> combineK(x: NonEmptyListOf<A>, y: NonEmptyListOf<A>): NonEmptyList<A> =
            x.extract().combineK(y)
}