package arrow.instances

import arrow.*
import arrow.core.*
import arrow.data.*
import arrow.typeclasses.*

@instance(SequenceKW::class)
interface SequenceKWSemigroupInstance<A> : Semigroup<SequenceKW<A>> {
    override fun combine(a: SequenceKW<A>, b: SequenceKW<A>): SequenceKW<A> = (a + b).k()
}

@instance(SequenceKW::class)
interface SequenceKWMonoidInstance<A> : Monoid<SequenceKW<A>> {
    override fun combine(a: SequenceKW<A>, b: SequenceKW<A>): SequenceKW<A> = (a + b).k()

    override fun empty(): SequenceKW<A> = emptySequence<A>().k()
}

@instance(SequenceKW::class)
interface SequenceKWEqInstance<A> : Eq<SequenceKW<A>> {

    fun EQ(): Eq<A>

    override fun eqv(a: SequenceKW<A>, b: SequenceKW<A>): Boolean =
            a.zip(b) { aa, bb -> EQ().eqv(aa, bb) }.fold(true) { acc, bool ->
                acc && bool
            }

}

@instance(SequenceKW::class)
interface SequenceKWFunctorInstance : Functor<ForSequenceKW> {
    override fun <A, B> map(fa: SequenceKWKind<A>, f: kotlin.Function1<A, B>): SequenceKW<B> =
            fa.reify().map(f)
}

@instance(SequenceKW::class)
interface SequenceKWApplicativeInstance : Applicative<ForSequenceKW> {
    override fun <A, B> ap(fa: SequenceKWKind<A>, ff: SequenceKWKind<kotlin.Function1<A, B>>): SequenceKW<B> =
            fa.reify().ap(ff)

    override fun <A, B> map(fa: SequenceKWKind<A>, f: kotlin.Function1<A, B>): SequenceKW<B> =
            fa.reify().map(f)

    override fun <A, B, Z> map2(fa: SequenceKWKind<A>, fb: SequenceKWKind<B>, f: kotlin.Function1<Tuple2<A, B>, Z>): SequenceKW<Z> =
            fa.reify().map2(fb, f)

    override fun <A> pure(a: A): SequenceKW<A> =
            SequenceKW.pure(a)
}

@instance(SequenceKW::class)
interface SequenceKWMonadInstance : Monad<ForSequenceKW> {
    override fun <A, B> ap(fa: SequenceKWKind<A>, ff: SequenceKWKind<kotlin.Function1<A, B>>): SequenceKW<B> =
            fa.reify().ap(ff)

    override fun <A, B> flatMap(fa: SequenceKWKind<A>, f: kotlin.Function1<A, SequenceKWKind<B>>): SequenceKW<B> =
            fa.reify().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, SequenceKWKind<Either<A, B>>>): SequenceKW<B> =
            SequenceKW.tailRecM(a, f)

    override fun <A, B> map(fa: SequenceKWKind<A>, f: kotlin.Function1<A, B>): SequenceKW<B> =
            fa.reify().map(f)

    override fun <A, B, Z> map2(fa: SequenceKWKind<A>, fb: SequenceKWKind<B>, f: kotlin.Function1<Tuple2<A, B>, Z>): SequenceKW<Z> =
            fa.reify().map2(fb, f)

    override fun <A> pure(a: A): SequenceKW<A> =
            SequenceKW.pure(a)
}

@instance(SequenceKW::class)
interface SequenceKWFoldableInstance : Foldable<ForSequenceKW> {
    override fun <A, B> foldLeft(fa: SequenceKWKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.reify().foldLeft(b, f)

    override fun <A, B> foldRight(fa: SequenceKWKind<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.reify().foldRight(lb, f)
}

@instance(SequenceKW::class)
interface SequenceKWTraverseInstance : Traverse<ForSequenceKW> {
    override fun <A, B> map(fa: SequenceKWKind<A>, f: kotlin.Function1<A, B>): SequenceKW<B> =
            fa.reify().map(f)

    override fun <G, A, B> traverse(fa: SequenceKWKind<A>, f: kotlin.Function1<A, Kind<G, B>>, GA: Applicative<G>): Kind<G, SequenceKW<B>> =
            fa.reify().traverse(f, GA)

    override fun <A, B> foldLeft(fa: SequenceKWKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.reify().foldLeft(b, f)

    override fun <A, B> foldRight(fa: SequenceKWKind<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.reify().foldRight(lb, f)
}

@instance(SequenceKW::class)
interface SequenceKWSemigroupKInstance : SemigroupK<ForSequenceKW> {
    override fun <A> combineK(x: SequenceKWKind<A>, y: SequenceKWKind<A>): SequenceKW<A> =
            x.reify().combineK(y)
}

@instance(SequenceKW::class)
interface SequenceKWMonoidKInstance : MonoidK<ForSequenceKW> {
    override fun <A> empty(): SequenceKW<A> =
            SequenceKW.empty()

    override fun <A> combineK(x: SequenceKWKind<A>, y: SequenceKWKind<A>): SequenceKW<A> =
            x.reify().combineK(y)
}