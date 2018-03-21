package arrow.instances

import arrow.*
import arrow.core.*
import arrow.data.*
import arrow.typeclasses.*

@instance(ListK::class)
interface ListKSemigroupInstance<A> : Semigroup<ListK<A>> {
    override fun combine(a: ListK<A>, b: ListK<A>): ListK<A> = (a + b).k()
}

@instance(ListK::class)
interface ListKMonoidInstance<A> : ListKSemigroupInstance<A>, Monoid<ListK<A>> {
    override fun empty(): ListK<A> = emptyList<A>().k()
}

@instance(ListK::class)
interface ListKEqInstance<A> : Eq<ListK<A>> {

    fun EQ(): Eq<A>

    override fun ListK<A>.eqv(b: ListK<A>): Boolean =
            zip(b) { aa, bb -> EQ().run { aa.eqv(bb) } }.fold(true) { acc, bool ->
                acc && bool
            }
}

@instance(ListK::class)
interface ListKShowInstance<A> : Show<ListK<A>> {
    override fun show(a: ListK<A>): String =
            a.toString()
}

@instance(ListK::class)
interface ListKFunctorInstance : Functor<ForListK> {
    override fun <A, B> map(fa: ListKOf<A>, f: kotlin.Function1<A, B>): ListK<B> =
            fa.fix().map(f)
}

@instance(ListK::class)
interface ListKApplicativeInstance : Applicative<ForListK> {
    override fun <A, B> ap(fa: ListKOf<A>, ff: ListKOf<kotlin.Function1<A, B>>): ListK<B> =
            fa.fix().ap(ff)

    override fun <A, B> map(fa: ListKOf<A>, f: kotlin.Function1<A, B>): ListK<B> =
            fa.fix().map(f)

    override fun <A, B, Z> map2(fa: ListKOf<A>, fb: ListKOf<B>, f: kotlin.Function1<Tuple2<A, B>, Z>): ListK<Z> =
            fa.fix().map2(fb, f)

    override fun <A> pure(a: A): ListK<A> =
            ListK.pure(a)
}

@instance(ListK::class)
interface ListKMonadInstance : Monad<ForListK> {
    override fun <A, B> ap(fa: ListKOf<A>, ff: ListKOf<kotlin.Function1<A, B>>): ListK<B> =
            fa.fix().ap(ff)

    override fun <A, B> flatMap(fa: ListKOf<A>, f: kotlin.Function1<A, ListKOf<B>>): ListK<B> =
            fa.fix().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, ListKOf<Either<A, B>>>): ListK<B> =
            ListK.tailRecM(a, f)

    override fun <A, B> map(fa: ListKOf<A>, f: kotlin.Function1<A, B>): ListK<B> =
            fa.fix().map(f)

    override fun <A, B, Z> map2(fa: ListKOf<A>, fb: ListKOf<B>, f: kotlin.Function1<Tuple2<A, B>, Z>): ListK<Z> =
            fa.fix().map2(fb, f)

    override fun <A> pure(a: A): ListK<A> =
            ListK.pure(a)
}

@instance(ListK::class)
interface ListKFoldableInstance : Foldable<ForListK> {
    override fun <A, B> foldLeft(fa: ListKOf<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: ListKOf<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.fix().foldRight(lb, f)

    override fun <A> isEmpty(fa: ListKOf<A>): kotlin.Boolean =
            fa.fix().isEmpty()
}

@instance(ListK::class)
interface ListKTraverseInstance : Traverse<ForListK> {
    override fun <A, B> map(fa: ListKOf<A>, f: kotlin.Function1<A, B>): ListK<B> =
            fa.fix().map(f)

    override fun <G, A, B> traverse(fa: ListKOf<A>, f: kotlin.Function1<A, Kind<G, B>>, GA: Applicative<G>): Kind<G, ListK<B>> =
            fa.fix().traverse(f, GA)

    override fun <A, B> foldLeft(fa: ListKOf<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: ListKOf<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.fix().foldRight(lb, f)

    override fun <A> isEmpty(fa: ListKOf<A>): kotlin.Boolean =
            fa.fix().isEmpty()
}

@instance(ListK::class)
interface ListKSemigroupKInstance : SemigroupK<ForListK> {
    override fun <A> combineK(x: ListKOf<A>, y: ListKOf<A>): ListK<A> =
            x.fix().combineK(y)
}

@instance(ListK::class)
interface ListKMonoidKInstance : MonoidK<ForListK> {
    override fun <A> empty(): ListK<A> =
            ListK.empty()

    override fun <A> combineK(x: ListKOf<A>, y: ListKOf<A>): ListK<A> =
            x.fix().combineK(y)
}
