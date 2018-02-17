package arrow.instances

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash

object BooleanEqInstance : Eq<Boolean> {
    override fun eqv(a: Boolean, b: Boolean): Boolean = a == b
}

object BooleanEqInstanceImplicits {
    @JvmStatic
    fun instance(): BooleanEqInstance = BooleanEqInstance
}

object BooleanHashInstance : Eq<Boolean>, Hash<Boolean> {
    override fun eqv(a: Boolean, b: Boolean): Boolean = a == b

    override fun hash(a: Boolean): Int = a.hashCode()
}

object BooleanHashInstanceImplicits {
    @JvmStatic
    fun instance(): BooleanEqInstance = BooleanEqInstance
}
