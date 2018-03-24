package arrow.instances

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Tuple2
import arrow.data.*
import arrow.instance
import arrow.typeclasses.*

@instance(SequenceK::class)
interface SequenceKSemigroupInstance<A> : Semigroup<SequenceK<A>> {
    override fun SequenceK<A>.combine(b: SequenceK<A>): SequenceK<A> = (this + b).k()
}

@instance(SequenceK::class)
interface SequenceKMonoidInstance<A> : Monoid<SequenceK<A>> {
    override fun SequenceK<A>.combine(b: SequenceK<A>): SequenceK<A> = (this + b).k()

    override fun empty(): SequenceK<A> = emptySequence<A>().k()
}

@instance(SequenceK::class)
interface SequenceKEqInstance<A> : Eq<SequenceK<A>> {

    fun EQ(): Eq<A>

    override fun SequenceK<A>.eqv(b: SequenceK<A>): Boolean =
            zip(b) { aa, bb -> EQ().run { aa.eqv(bb) } }.fold(true) { acc, bool ->
                acc && bool
            }

}

@instance(SequenceK::class)
interface SequenceKShowInstance<A> : Show<SequenceK<A>> {
    override fun show(a: SequenceK<A>): String =
            a.toString()
}

@instance(SequenceK::class)
interface SequenceKFunctorInstance : Functor<ForSequenceK> {
    override fun <A, B> map(fa: SequenceKOf<A>, f: kotlin.Function1<A, B>): SequenceK<B> =
            fa.fix().map(f)
}

@instance(SequenceK::class)
interface SequenceKApplicativeInstance : Applicative<ForSequenceK> {
    override fun <A, B> Kind<ForSequenceK, A>.ap(ff: Kind<ForSequenceK, (A) -> B>): SequenceK<B> =
            fix().ap(ff)

    override fun <A, B> map(fa: SequenceKOf<A>, f: kotlin.Function1<A, B>): SequenceK<B> =
            fa.fix().map(f)

    override fun <A, B, Z> Kind<ForSequenceK, A>.map2(fb: Kind<ForSequenceK, B>, f: (Tuple2<A, B>) -> Z): SequenceK<Z> =
            fix().map2(fb, f)

    override fun <A> pure(a: A): SequenceK<A> =
            SequenceK.pure(a)
}

@instance(SequenceK::class)
interface SequenceKMonadInstance : Monad<ForSequenceK> {
    override fun <A, B> Kind<ForSequenceK, A>.ap(ff: Kind<ForSequenceK, (A) -> B>): SequenceK<B> =
            fix().ap(ff)

    override fun <A, B> Kind<ForSequenceK, A>.flatMap(f: (A) -> Kind<ForSequenceK, B>): SequenceK<B> =
            fix().flatMap(f)

    override fun <A, B> tailRecM(a: A, f: kotlin.Function1<A, SequenceKOf<Either<A, B>>>): SequenceK<B> =
            SequenceK.tailRecM(a, f)

    override fun <A, B> map(fa: SequenceKOf<A>, f: kotlin.Function1<A, B>): SequenceK<B> =
            fa.fix().map(f)

    override fun <A, B, Z> Kind<ForSequenceK, A>.map2(fb: Kind<ForSequenceK, B>, f: (Tuple2<A, B>) -> Z): SequenceK<Z> =
            fix().map2(fb, f)

    override fun <A> pure(a: A): SequenceK<A> =
            SequenceK.pure(a)
}

@instance(SequenceK::class)
interface SequenceKFoldableInstance : Foldable<ForSequenceK> {
    override fun <A, B> foldLeft(fa: SequenceKOf<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: SequenceKOf<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.fix().foldRight(lb, f)
}

@instance(SequenceK::class)
interface SequenceKTraverseInstance : Traverse<ForSequenceK> {
    override fun <A, B> map(fa: SequenceKOf<A>, f: kotlin.Function1<A, B>): SequenceK<B> =
            fa.fix().map(f)

    override fun <G, A, B> Applicative<G>.traverse(fa: SequenceKOf<A>, f: kotlin.Function1<A, Kind<G, B>>): Kind<G, SequenceK<B>> =
            fa.fix().traverse(this, f)

    override fun <A, B> foldLeft(fa: SequenceKOf<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: SequenceKOf<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.fix().foldRight(lb, f)
}

@instance(SequenceK::class)
interface SequenceKSemigroupKInstance : SemigroupK<ForSequenceK> {
    override fun <A> combineK(x: SequenceKOf<A>, y: SequenceKOf<A>): SequenceK<A> =
            x.fix().combineK(y)
}

@instance(SequenceK::class)
interface SequenceKMonoidKInstance : MonoidK<ForSequenceK> {
    override fun <A> empty(): SequenceK<A> =
            SequenceK.empty()

    override fun <A> combineK(x: SequenceKOf<A>, y: SequenceKOf<A>): SequenceK<A> =
            x.fix().combineK(y)
}