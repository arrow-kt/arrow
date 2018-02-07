package arrow.instances

import arrow.*
import arrow.core.*
import arrow.data.*
import arrow.typeclasses.*

@instance(SetKW::class)
interface SetKWSemigroupInstance<A> : Semigroup<SetKW<A>> {
    override fun combine(a: SetKW<A>, b: SetKW<A>): SetKW<A> = (a + b).k()
}

@instance(SetKW::class)
interface SetKWMonoidInstance<A> : SetKWSemigroupInstance<A>, Monoid<SetKW<A>> {
    override fun empty(): SetKW<A> = emptySet<A>().k()
}

@instance(SetKW::class)
interface SetKWEqInstance<A> : Eq<SetKW<A>> {

    fun EQ(): Eq<A>

    override fun eqv(a: SetKW<A>, b: SetKW<A>): Boolean =
            if (a.size == b.size) a.set.map { aa ->
                b.find { bb -> EQ().eqv(aa, bb) } != null
            }.fold(true) { acc, bool ->
                acc && bool
            }
            else false

}

@instance(SetKW::class)
interface SetKWFoldableInstance : Foldable<ForSetKW> {
    override fun <A, B> foldLeft(fa: SetKWKind<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.reify().foldLeft(b, f)

    override fun <A, B> foldRight(fa: SetKWKind<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.reify().foldRight(lb, f)

    override fun <A> isEmpty(fa: SetKWKind<A>): kotlin.Boolean =
            fa.reify().isEmpty()
}

@instance(SetKW::class)
interface SetKWSemigroupKInstance : SemigroupK<ForSetKW> {
    override fun <A> combineK(x: SetKWKind<A>, y: SetKWKind<A>): SetKW<A> =
            x.reify().combineK(y)
}

@instance(SetKW::class)
interface SetKWMonoidKInstance : MonoidK<ForSetKW> {
    override fun <A> empty(): SetKW<A> =
            SetKW.empty()

    override fun <A> combineK(x: SetKWKind<A>, y: SetKWKind<A>): SetKW<A> =
            x.reify().combineK(y)
}