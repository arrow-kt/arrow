package arrow.instances

import arrow.typeclasses.Eq

object CharEqInstance : Eq<Char> {
    override fun eqv(a: Char, b: Char): Boolean = a == b
}

object CharEqInstanceImplicits {
    @JvmStatic
    fun instance(): CharEqInstance = CharEqInstance
}