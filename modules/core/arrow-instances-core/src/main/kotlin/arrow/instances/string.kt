package arrow.instances

import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show

object StringSemigroupInstance : Semigroup<String> {
  override fun String.combine(b: String): String = "${this}$b"
}

object StringMonoidInstance : Monoid<String> {
  override fun empty(): String = ""

  override fun String.combine(b: String): String = StringSemigroupInstance.run { combine(b) }
}

object StringEqInstance : Eq<String> {
  override fun String.eqv(b: String): Boolean = this == b
}

object StringShowInstance : Show<String> {
  override fun String.show(): String = this
}
