package arrow.instances

import arrow.typeclasses.Eq

object CharEqInstance : Eq<Char> {
    override fun Char.eqv(b: Char): Boolean = this == b
}

object CharEqInstanceImplicits {
    @JvmStatic
    fun instance(): CharEqInstance = CharEqInstance
}