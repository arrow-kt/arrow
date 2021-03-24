package arrow.core.extensions

import arrow.core.Ordering
import arrow.typeclasses.Eq
import arrow.typeclasses.EqDeprecation
import arrow.typeclasses.Hash
import arrow.typeclasses.HashDeprecation
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.OrderDeprecation
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Semigroup.string()", "arrow.core.Semigroup"))
interface StringSemigroup : Semigroup<String> {
  override fun String.combine(b: String): String = "${this}$b"
}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Semigroup.string()", "arrow.core.Semigroup"))
fun String.Companion.semigroup(): Semigroup<String> =
  object : StringSemigroup {}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Monoid.string()", "arrow.core.Monoid"))
interface StringMonoid : Monoid<String>, StringSemigroup {
  override fun empty(): String = ""
}

@Deprecated(ShowDeprecation)
fun String.Companion.monoid(): Monoid<String> =
  object : StringMonoid {}

@Deprecated(EqDeprecation)
interface StringEq : Eq<String> {
  override fun String.eqv(b: String): Boolean = this == b
}

@Deprecated(EqDeprecation)
fun String.Companion.eq(): Eq<String> =
  object : StringEq {}

@Deprecated(ShowDeprecation)
interface StringShow : Show<String> {
  override fun String.show(): String = "\"${this.escape()}\""

  private fun String.escape(): String =
    replace("\n", "\\n").replace("\r", "\\r")
      .replace("\"", "\\\"").replace("\'", "\\\'")
      .replace("\t", "\\t").replace("\b", "\\b")
}

@Deprecated(ShowDeprecation)
fun String.Companion.show(): Show<String> =
  object : StringShow {}

@Deprecated(OrderDeprecation)
interface StringOrder : Order<String> {
  override fun String.compare(b: String): Ordering =
    Ordering.fromInt(this.compareTo(b))

  override fun String.compareTo(b: String): Int = this.compareTo(b)
}

@Deprecated(OrderDeprecation)
fun String.Companion.order(): Order<String> =
  object : StringOrder {}

@Deprecated(HashDeprecation)
interface StringHash : Hash<String>, StringEq {
  override fun String.hash(): Int = hashCode()
}

@Deprecated(HashDeprecation)
fun String.Companion.hash(): Hash<String> =
  object : StringHash {}

@Deprecated("ForString extensions has been deprecated. Use concrete methods on String")
object StringContext : StringShow, StringOrder, StringMonoid

@Deprecated("ForString extensions has been deprecated. Use concrete methods on String")
object ForString {

  @Deprecated("ForString extensions has been deprecated. Use concrete methods on String")
  infix fun <L> extensions(f: StringContext.() -> L): L =
    f(StringContext)
}
