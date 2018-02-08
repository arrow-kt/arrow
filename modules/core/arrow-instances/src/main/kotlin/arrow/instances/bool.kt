package arrow.instances

import arrow.typeclasses.Eq

object BooleanEqInstance : Eq<Boolean> {
    override fun eqv(a: Boolean, b: Boolean): Boolean = a == b
}

object BooleanEqInstanceImplicits {
    @JvmStatic
    fun instance(): BooleanEqInstance = BooleanEqInstance
}