package arrow.instances

import arrow.*
import arrow.core.*
import arrow.data.*
import arrow.typeclasses.*

@instance(SequenceK::class)
interface SequenceKSemigroupInstance<A> : Semigroup<SequenceK<A>> {
    override fun combine(a: SequenceK<A>, b: SequenceK<A>): SequenceK<A> = (a + b).k()
}

@instance(SequenceK::class)
interface SequenceKMonoidInstance<A> : Monoid<SequenceK<A>> {
    override fun combine(a: SequenceK<A>, b: SequenceK<A>): SequenceK<A> = (a + b).k()

    override fun empty(): SequenceK<A> = emptySequence<A>().k()
}

@instance(SequenceK::class)
interface SequenceKEqInstance<A> : Eq<SequenceK<A>> {

    fun EQ(): Eq<A>

    override fun eqv(a: SequenceK<A>, b: SequenceK<A>): Boolean =
            a.zip(b) { aa, bb -> EQ().eqv(aa, bb) }.fold(true) { acc, bool ->
                acc && bool
            }

}

@instance(SequenceK::class)
interface SequenceKFunctorInstance : Functor<ForSequenceK> {
    override fun <A, B> map(fa: SequenceKOf<A>, f: kotlin.Function1<A, B>): SequenceK<B> =
            fa.reify().map(f)
}

@instance(SequenceK::class)
interface SequenceKApplicativeInstance : Applicative<ForSequenceK> {
    override fun <A, B> ap(fa: SequenceKOf<A>, ff: SequenceKOf<kotlin.Function1<A, B>>): SequenceK<B> =
            fa.reify().ap(ff)

    override fun <A, B> map(fa: SequenceKOf<A>, f: kotlin.Function1<A, B>): SequenceK<B> =
            fa.reify().map(f)

    override fun <A, B, Z> map2(fa: SequenceKOf<A>, fb: SequenceKOf<B>, f: kotlin.Function1<Tuple2<A, B>, Z>): SequenceK<Z> =
            fa.reify().map2(fb, f)

    override fun <A> pure(a: A): SequenceK<A> =
            SequenceK.pure(a)
}

@instance(SequenceK::class)
interface SequenceKMonadInstance : Monad<ForSequenceK> {
    override fun <A, B> ap(fa: SequenceKOf<A>, ff: SequenceKOf<kotlin.Function1<A, B>>): SequenceK<B> =
            fa.reify().ap(ff)

    override fun <A, B> flatMap(fa: SequenceKOf<A>, f: kotlin.Function1<A, SequenceKOf<B>>): SequenceK<B> =
            fa.reify().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, SequenceKOf<Either<A, B>>>): SequenceK<B> =
            SequenceK.tailRecM(a, f)

    override fun <A, B> map(fa: SequenceKOf<A>, f: kotlin.Function1<A, B>): SequenceK<B> =
            fa.reify().map(f)

    override fun <A, B, Z> map2(fa: SequenceKOf<A>, fb: SequenceKOf<B>, f: kotlin.Function1<Tuple2<A, B>, Z>): SequenceK<Z> =
            fa.reify().map2(fb, f)

    override fun <A> pure(a: A): SequenceK<A> =
            SequenceK.pure(a)
}

@instance(SequenceK::class)
interface SequenceKFoldableInstance : Foldable<ForSequenceK> {
    override fun <A, B> foldLeft(fa: SequenceKOf<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.reify().foldLeft(b, f)

    override fun <A, B> foldRight(fa: SequenceKOf<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.reify().foldRight(lb, f)
}

@instance(SequenceK::class)
interface SequenceKTraverseInstance : Traverse<ForSequenceK> {
    override fun <A, B> map(fa: SequenceKOf<A>, f: kotlin.Function1<A, B>): SequenceK<B> =
            fa.reify().map(f)

    override fun <G, A, B> traverse(fa: SequenceKOf<A>, f: kotlin.Function1<A, Kind<G, B>>, GA: Applicative<G>): Kind<G, SequenceK<B>> =
            fa.reify().traverse(f, GA)

    override fun <A, B> foldLeft(fa: SequenceKOf<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.reify().foldLeft(b, f)

    override fun <A, B> foldRight(fa: SequenceKOf<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.reify().foldRight(lb, f)
}

@instance(SequenceK::class)
interface SequenceKSemigroupKInstance : SemigroupK<ForSequenceK> {
    override fun <A> combineK(x: SequenceKOf<A>, y: SequenceKOf<A>): SequenceK<A> =
            x.reify().combineK(y)
}

@instance(SequenceK::class)
interface SequenceKMonoidKInstance : MonoidK<ForSequenceK> {
    override fun <A> empty(): SequenceK<A> =
            SequenceK.empty()

    override fun <A> combineK(x: SequenceKOf<A>, y: SequenceKOf<A>): SequenceK<A> =
            x.reify().combineK(y)
}