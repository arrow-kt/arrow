package arrow.instances

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.data.*
import arrow.instance
import arrow.typeclasses.*

@instance(NonEmptyList::class)
interface NonEmptyListSemigroupInstance<A> : Semigroup<NonEmptyList<A>> {
    override fun NonEmptyList<A>.combine(b: NonEmptyList<A>): NonEmptyList<A> = this + b
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
    override fun NonEmptyList<A>.show(): String =
            toString()
}

@instance(NonEmptyList::class)
interface NonEmptyListFunctorInstance : Functor<ForNonEmptyList> {
    override fun <A, B> Kind<ForNonEmptyList, A>.map(f: (A) -> B): NonEmptyList<B> =
            fix().map(f)
}

@instance(NonEmptyList::class)
interface NonEmptyListApplicativeInstance : Applicative<ForNonEmptyList> {
    override fun <A, B> Kind<ForNonEmptyList, A>.ap(ff: Kind<ForNonEmptyList, (A) -> B>): NonEmptyList<B> =
            fix().ap(ff)

    override fun <A, B> Kind<ForNonEmptyList, A>.map(f: (A) -> B): NonEmptyList<B> =
            fix().map(f)

    override fun <A> pure(a: A): NonEmptyList<A> =
            NonEmptyList.pure(a)
}

@instance(NonEmptyList::class)
interface NonEmptyListMonadInstance : Monad<ForNonEmptyList> {
    override fun <A, B> Kind<ForNonEmptyList, A>.ap(ff: Kind<ForNonEmptyList, (A) -> B>): NonEmptyList<B> =
            fix().ap(ff)

    override fun <A, B> Kind<ForNonEmptyList, A>.flatMap(f: (A) -> Kind<ForNonEmptyList, B>): NonEmptyList<B> =
            fix().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, NonEmptyListOf<Either<A, B>>>): NonEmptyList<B> =
            NonEmptyList.tailRecM(a, f)

    override fun <A, B> Kind<ForNonEmptyList, A>.map(f: (A) -> B): NonEmptyList<B> =
            fix().map(f)

    override fun <A> pure(a: A): NonEmptyList<A> =
            NonEmptyList.pure(a)
}

@instance(NonEmptyList::class)
interface NonEmptyListComonadInstance : Comonad<ForNonEmptyList> {
    override fun <A, B> Kind<ForNonEmptyList, A>.coflatMap(f: (Kind<ForNonEmptyList, A>) -> B): NonEmptyList<B> =
            fix().coflatMap(f)

    override fun <A> Kind<ForNonEmptyList, A>.extract(): A =
            fix().extract()

    override fun <A, B> Kind<ForNonEmptyList, A>.map(f: (A) -> B): NonEmptyList<B> =
            fix().map(f)
}

@instance(NonEmptyList::class)
interface NonEmptyListBimonadInstance : Bimonad<ForNonEmptyList> {
    override fun <A, B> Kind<ForNonEmptyList, A>.ap(ff: Kind<ForNonEmptyList, (A) -> B>): NonEmptyList<B> =
            fix().ap(ff)

    override fun <A, B> Kind<ForNonEmptyList, A>.flatMap(f: (A) -> Kind<ForNonEmptyList, B>): NonEmptyList<B> =
            fix().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, NonEmptyListOf<Either<A, B>>>): NonEmptyList<B> =
            NonEmptyList.tailRecM(a, f)

    override fun <A, B> Kind<ForNonEmptyList, A>.map(f: (A) -> B): NonEmptyList<B> =
            fix().map(f)

    override fun <A> pure(a: A): NonEmptyList<A> =
            NonEmptyList.pure(a)

    override fun <A, B> Kind<ForNonEmptyList, A>.coflatMap(f: (Kind<ForNonEmptyList, A>) -> B): NonEmptyList<B> =
            fix().coflatMap(f)

    override fun <A> Kind<ForNonEmptyList, A>.extract(): A =
            fix().extract()
}

@instance(NonEmptyList::class)
interface NonEmptyListFoldableInstance : Foldable<ForNonEmptyList> {
    override fun <A, B> foldLeft(fa: NonEmptyListOf<A>, b: B, f: Function2<B, A, B>): B =
            fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: NonEmptyListOf<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.fix().foldRight(lb, f)

    override fun <A> Kind<ForNonEmptyList, A>.isEmpty(): kotlin.Boolean =
            fix().isEmpty()
}

@instance(NonEmptyList::class)
interface NonEmptyListTraverseInstance : Traverse<ForNonEmptyList> {
    override fun <A, B> Kind<ForNonEmptyList, A>.map(f: (A) -> B): NonEmptyList<B> =
            this@map.fix().map(f)

    override fun <G, A, B> Kind<ForNonEmptyList, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, NonEmptyList<B>> =
            fix().traverse(AP, f)

    override fun <A, B> foldLeft(fa: NonEmptyListOf<A>, b: B, f: Function2<B, A, B>): B =
            fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: NonEmptyListOf<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.fix().foldRight(lb, f)

    override fun <A> Kind<ForNonEmptyList, A>.isEmpty(): kotlin.Boolean =
            fix().isEmpty()
}

@instance(NonEmptyList::class)
interface NonEmptyListSemigroupKInstance : SemigroupK<ForNonEmptyList> {
    override fun <A> Kind<ForNonEmptyList, A>.combineK(y: Kind<ForNonEmptyList, A>): NonEmptyList<A> =
            fix().combineK(null, y)
}