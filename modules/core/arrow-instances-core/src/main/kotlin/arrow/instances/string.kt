package arrow.instances

import arrow.typeclasses.*

interface StringSemigroupInstance : Semigroup<String> {
  override fun String.combine(b: String): String = "${this}$b"
}

fun String.Companion.semigroup(): Semigroup<String> =
  object : StringSemigroupInstance {}

interface StringMonoidInstance : Monoid<String>, StringSemigroupInstance {
  override fun empty(): String = ""
}

fun String.Companion.monoid(): Monoid<String> =
  object : StringMonoidInstance {}

interface StringEqInstance : Eq<String> {
  override fun String.eqv(b: String): Boolean = this == b
}

fun String.Companion.eq(): Eq<String> =
  object : StringEqInstance {}

interface StringShowInstance : Show<String> {
  override fun String.show(): String = this
}

fun String.Companion.show(): Show<String> =
  object : StringShowInstance {}

interface StringOrderInstance : Order<String> {
  override fun String.compare(b: String): Int = this.compareTo(b)
}

fun String.Companion.order(): Order<String> =
  object : StringOrderInstance {}

interface StringHashInstance : Hash<String>, StringEqInstance {
  override fun String.hash(): Int = hashCode()
}

fun String.Companion.hash(): Hash<String> =
  object : StringHashInstance {}

object StringContext : StringShowInstance, StringOrderInstance, StringMonoidInstance

object ForString {
  infix fun <L> extensions(f: StringContext.() -> L): L =
    f(StringContext)
}