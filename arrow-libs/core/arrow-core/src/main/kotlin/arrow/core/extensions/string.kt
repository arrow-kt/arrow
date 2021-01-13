package arrow.core.extensions

import arrow.core.Ordering
import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Semigroup.string()", "arrow.core.Semigroup", "arrow.core.string"))
interface StringSemigroup : Semigroup<String> {
  override fun String.combine(b: String): String = "${this}$b"
}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Semigroup.string()", "arrow.core.Semigroup", "arrow.core.string"))
fun String.Companion.semigroup(): Semigroup<String> =
  object : StringSemigroup {}

private object StringSemigroupInstance : StringSemigroup

fun Semigroup.Companion.String(): Semigroup<String> =
  StringSemigroupInstance

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Hash.string()", "arrow.core.Hash", "arrow.core.string"))
interface StringMonoid : Monoid<String>, StringSemigroup {
  override fun empty(): String = ""
}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Show.string()", "arrow.core.Show", "arrow.core.string"))
fun String.Companion.monoid(): Monoid<String> =
  object : StringMonoid {}

private object StringMonoidInstance : StringMonoid

fun Monoid.Companion.String(): Monoid<String> =
  StringMonoidInstance

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Hash.string()", "arrow.core.Hash", "arrow.core.string"))
interface StringEq : Eq<String> {
  override fun String.eqv(b: String): Boolean = this == b
}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Show.string()", "arrow.core.Show", "arrow.core.string"))
fun String.Companion.eq(): Eq<String> =
  object : StringEq {}

private object StringEqInstance : StringEq

fun Eq.Companion.String(): Eq<String> =
  StringEqInstance

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Hash.string()", "arrow.core.Hash", "arrow.core.string"))
interface StringShow : Show<String> {
  override fun String.show(): String = "\"${this.escape()}\""

  private fun String.escape(): String =
    replace("\n", "\\n").replace("\r", "\\r")
      .replace("\"", "\\\"").replace("\'", "\\\'")
      .replace("\t", "\\t").replace("\b", "\\b")
}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Show.string()", "arrow.core.Show", "arrow.core.string"))
fun String.Companion.show(): Show<String> =
  object : StringShow {}

private object StringShowInstance : StringShow

fun Show.Companion.String(): Show<String> =
  StringShowInstance

interface StringOrder : Order<String> {
  override fun String.compare(b: String): Ordering =
    Ordering.fromInt(this.compareTo(b))

  override fun String.compareTo(b: String): Int = this.compareTo(b)
}

private object StringOrderInstance : StringOrder

fun Order.Companion.String(): Order<String> =
  StringOrderInstance

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Order.string()", "arrow.core.Order", "arrow.core.string"))
fun String.Companion.order(): Order<String> =
  object : StringOrder {}

interface StringHash : Hash<String>, StringEq {
  override fun String.hash(): Int = hashCode()
}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Hash.string()", "arrow.core.Hash", "arrow.core.string"))
fun String.Companion.hash(): Hash<String> =
  object : StringHash {}

private object StringHashInstance : StringHash

fun Hash.Companion.String(): Hash<String> =
  StringHashInstance

object StringContext : StringShow, StringOrder, StringMonoid

@Deprecated("ForString extensions has been deprecated. Use concrete methods on String")
object ForString {

  @Deprecated("ForString extensions has been deprecated. Use concrete methods on String")
  infix fun <L> extensions(f: StringContext.() -> L): L =
    f(StringContext)
}
