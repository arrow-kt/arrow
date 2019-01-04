package arrow.core.extensions

import arrow.typeclasses.*

interface StringSemigroup : Semigroup<String> {
  override fun String.combine(b: String): String = "${this}$b"
}

fun String.Companion.semigroup(): Semigroup<String> =
  object : StringSemigroup{}

interface StringMonoid: Monoid<String>, StringSemigroup {
  override fun empty(): String = ""
}

fun String.Companion.monoid(): Monoid<String> =
  object : StringMonoid{}

interface StringEq : Eq<String> {
  override fun String.eqv(b: String): Boolean = this == b
}

fun String.Companion.eq(): Eq<String> =
  object : StringEq{}

interface StringShow : Show<String> {
  override fun String.show(): String = this
}

fun String.Companion.show(): Show<String> =
  object : StringShow{}

interface StringOrder : Order<String> {
  override fun String.compare(b: String): Int = this.compareTo(b)
}

fun String.Companion.order(): Order<String> =
  object : StringOrder{}

interface StringHash: Hash<String>, StringEq {
  override fun String.hash(): Int = hashCode()
}

fun String.Companion.hash(): Hash<String> =
  object : StringHash{}

object StringContext : StringShow, StringOrder, StringMonoid

object ForString {
  infix fun <L> extensions(f: StringContext.() -> L): L =
    f(StringContext)
}