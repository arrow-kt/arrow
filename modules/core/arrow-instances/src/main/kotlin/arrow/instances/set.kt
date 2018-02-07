package arrow.instances

import arrow.*
import arrow.core.*
import arrow.data.*
import arrow.typeclasses.*

@instance(SetK::class)
interface SetKSemigroupInstance<A> : Semigroup<SetK<A>> {
    override fun combine(a: SetK<A>, b: SetK<A>): SetK<A> = (a + b).k()
}

@instance(SetK::class)
interface SetKMonoidInstance<A> : SetKSemigroupInstance<A>, Monoid<SetK<A>> {
    override fun empty(): SetK<A> = emptySet<A>().k()
}

@instance(SetK::class)
interface SetKEqInstance<A> : Eq<SetK<A>> {

    fun EQ(): Eq<A>

    override fun eqv(a: SetK<A>, b: SetK<A>): Boolean =
            if (a.size == b.size) a.set.map { aa ->
                b.find { bb -> EQ().eqv(aa, bb) } != null
            }.fold(true) { acc, bool ->
                acc && bool
            }
            else false

}

@instance(SetK::class)
interface SetKFoldableInstance : Foldable<ForSetK> {
    override fun <A, B> foldLeft(fa: SetKOf<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.reify().foldLeft(b, f)

    override fun <A, B> foldRight(fa: SetKOf<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.reify().foldRight(lb, f)

    override fun <A> isEmpty(fa: SetKOf<A>): kotlin.Boolean =
            fa.reify().isEmpty()
}

@instance(SetK::class)
interface SetKSemigroupKInstance : SemigroupK<ForSetK> {
    override fun <A> combineK(x: SetKOf<A>, y: SetKOf<A>): SetK<A> =
            x.reify().combineK(y)
}

@instance(SetK::class)
interface SetKMonoidKInstance : MonoidK<ForSetK> {
    override fun <A> empty(): SetK<A> =
            SetK.empty()

    override fun <A> combineK(x: SetKOf<A>, y: SetKOf<A>): SetK<A> =
            x.reify().combineK(y)
}