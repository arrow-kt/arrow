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
interface SetKHashInstance<A> : SetKEqInstance<A>, Hash<SetK<A>> {

    override fun EQ(): Eq<A> = HS()

    fun HS(): Hash<A>

    override fun hash(a: SetK<A>): Int = a.hashCode()
}

@instance(SetK::class)
interface SetKShowInstance<A> : Show<SetK<A>> {
    override fun show(a: SetK<A>): String =
            a.toString()
}

@instance(SetK::class)
interface SetKFoldableInstance : Foldable<ForSetK> {
    override fun <A, B> foldLeft(fa: SetKOf<A>, b: B, f: kotlin.Function2<B, A, B>): B =
            fa.fix().foldLeft(b, f)

    override fun <A, B> foldRight(fa: SetKOf<A>, lb: Eval<B>, f: kotlin.Function2<A, Eval<B>, Eval<B>>): Eval<B> =
            fa.fix().foldRight(lb, f)

    override fun <A> isEmpty(fa: SetKOf<A>): kotlin.Boolean =
            fa.fix().isEmpty()
}

@instance(SetK::class)
interface SetKSemigroupKInstance : SemigroupK<ForSetK> {
    override fun <A> combineK(x: SetKOf<A>, y: SetKOf<A>): SetK<A> =
            x.fix().combineK(y)
}

@instance(SetK::class)
interface SetKMonoidKInstance : MonoidK<ForSetK> {
    override fun <A> empty(): SetK<A> =
            SetK.empty()

    override fun <A> combineK(x: SetKOf<A>, y: SetKOf<A>): SetK<A> =
            x.fix().combineK(y)
}
