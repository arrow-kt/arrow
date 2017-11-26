package kotlin_

import kategory.Eq
import kategory.Monoid
import kategory.Semigroup

object UnitSemigroupInstance: Semigroup<Unit> {
    override fun combine(a: Unit, b: Unit) = Unit
}

object UnitSemigroupInstanceImplicits {
    @JvmStatic fun instance(): Semigroup<Unit> = UnitSemigroupInstance
}

object UnitMonoidInstance: Monoid<Unit>, Semigroup<Unit> by UnitSemigroupInstance {
    override fun empty() = Unit
}

object UnitMonoidInstanceImplicits {
    @JvmStatic fun instance(): Monoid<Unit> = UnitMonoidInstance
}

object UnitEqInstance : Eq<Unit> {
    override fun eqv(a: Unit, b: Unit): Boolean = true
}

object UnitEqInstanceImplicits {
    @JvmStatic fun instance(): Eq<Unit> = UnitEqInstance
}