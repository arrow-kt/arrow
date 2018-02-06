package arrow.instances

import arrow.*
import arrow.core.*
import arrow.data.*
import arrow.typeclasses.*

@instance(ListKW::class)
interface ListKWSemigroupInstance<A> : Semigroup<ListKW<A>> {
    override fun combine(a: ListKW<A>, b: ListKW<A>): ListKW<A> = (a + b).k()
}

@instance(ListKW::class)
interface ListKWMonoidInstance<A> : ListKWSemigroupInstance<A>, Monoid<ListKW<A>> {
    override fun empty(): ListKW<A> = emptyList<A>().k()
}

@instance(ListKW::class)
interface ListKWEqInstance<A> : Eq<ListKW<A>> {

    fun EQ(): Eq<A>

    override fun eqv(a: ListKW<A>, b: ListKW<A>): Boolean =
            a.zip(b) { aa, bb -> EQ().eqv(aa, bb) }.fold(true) { acc, bool ->
                acc && bool
            }

}

@instance(ListKW::class)
interface ListKWFunctorInstance : Functor<ListKWHK> {
    override fun <A, B> map(fa: ListKWKind<A>, f: kotlin.Function1<A, B>): ListKW<B> =
            fa.ev().map(f)
}

@instance(ListKW::class)
interface ListKWApplicativeInstance : Applicative<ListKWHK> {
    override fun <A, B> ap(fa: ListKWKind<A>, ff: ListKWKind<kotlin.Function1<A, B>>): ListKW<B> =
            fa.ev().ap(ff)

    override fun <A, B> map(fa: ListKWKind<A>, f: kotlin.Function1<A, B>): ListKW<B> =
            fa.ev().map(f)

    override fun <A, B, Z> map2(fa: ListKWKind<A>, fb: ListKWKind<B>, f: kotlin.Function1<Tuple2<A, B>, Z>): ListKW<Z> =
            fa.ev().map2(fb, f)

    override fun <A> pure(a: A): ListKW<A> =
            ListKW.pure(a)
}

@instance(ListKW::class)
interface ListKWMonadInstance : Monad<ListKWHK> {
    override fun <A, B> ap(fa: ListKWKind<A>, ff: ListKWKind<kotlin.Function1<A, B>>): ListKW<B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: ListKWKind<A>, f: kotlin.Function1<A, ListKWKind<B>>): ListKW<B> =
            fa.ev().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, ListKWKind<Either<A, B>>>): ListKW<B> =
            ListKW.tailRecM(a, f)

    override fun <A, B> map(fa: ListKWKind<A>, f: kotlin.Function1<A, B>): ListKW<B> =
            fa.ev().map(f)

    override fun <A, B, Z> map2(fa: ListKWKind<A>, fb: ListKWKind<B>, f: kotlin.Function1<Tuple2<A, B>, Z>): ListKW<Z> =
            fa.ev().map2(fb, f)

    override fun <A> pure(a: A): ListKW<A> =
            ListKW.pure(a)
}

@instance(ListKW::class)
interface ListKWFoldableInstance : Foldable<ListKWHK> {
    override fun <A, B> foldLeft(fa: ListKWKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: ListKWKind<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.ev().foldRight(lb, f)

    override fun <A> isEmpty(fa: ListKWKind<A>): kotlin.Boolean =
            fa.ev().isEmpty()
}

@instance(ListKW::class)
interface ListKWTraverseInstance : Traverse<ListKWHK> {
    override fun <A, B> map(fa: ListKWKind<A>, f: kotlin.Function1<A, B>): ListKW<B> =
            fa.ev().map(f)

    override fun <G, A, B> traverse(fa: ListKWKind<A>, f: kotlin.Function1<A, HK<G, B>>, GA: Applicative<G>): HK<G, ListKW<B>> =
            fa.ev().traverse(f, GA)

    override fun <A, B> foldLeft(fa: ListKWKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: ListKWKind<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.ev().foldRight(lb, f)

    override fun <A> isEmpty(fa: ListKWKind<A>): kotlin.Boolean =
            fa.ev().isEmpty()
}

@instance(ListKW::class)
interface ListKWSemigroupKInstance : SemigroupK<ListKWHK> {
    override fun <A> combineK(x: ListKWKind<A>, y: ListKWKind<A>): ListKW<A> =
            x.ev().combineK(y)
}

@instance(ListKW::class)
interface ListKWMonoidKInstance : MonoidK<ListKWHK> {
    override fun <A> empty(): ListKW<A> =
            ListKW.empty()

    override fun <A> combineK(x: ListKWKind<A>, y: ListKWKind<A>): ListKW<A> =
            x.ev().combineK(y)
}