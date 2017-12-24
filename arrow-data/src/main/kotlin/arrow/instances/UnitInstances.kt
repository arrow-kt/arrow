package kotlin_

import arrow.Eq
import arrow.Monoid
import arrow.Semigroup

object UnitSemigroupInstance: Semigroup<Unit> {
    override fun combine(a: Unit, b: Unit) = Unit
}

object UnitSemigroupInstanceImplicits {
    fun instance(): Semigroup<Unit> = UnitSemigroupInstance
}

object UnitMonoidInstance: Monoid<Unit>, Semigroup<Unit> by UnitSemigroupInstance {
    override fun empty() = Unit
}

object UnitMonoidInstanceImplicits {
    fun instance(): Monoid<Unit> = UnitMonoidInstance
}

object UnitEqInstance : Eq<Unit> {
    override fun eqv(a: Unit, b: Unit): Boolean = true
}

object UnitEqInstanceImplicits {
    fun instance(): Eq<Unit> = UnitEqInstance
}