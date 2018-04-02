package arrow.instances

import arrow.typeclasses.Eq

object CharEqInstance : Eq<Char> {
  override fun Char.eqv(b: Char): Boolean = this == b
}
