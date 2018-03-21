package arrow.instances

import arrow.typeclasses.Eq

object BooleanEqInstance : Eq<Boolean> {
    override fun Boolean.eqv(b: Boolean): Boolean = this == b
}

object BooleanEqInstanceImplicits {
    @JvmStatic
    fun instance(): BooleanEqInstance = BooleanEqInstance
}