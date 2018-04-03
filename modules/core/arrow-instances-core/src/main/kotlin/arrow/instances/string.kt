package arrow.instances

import arrow.typeclasses.*

interface StringSemigroupInstance : Semigroup<String> {
    override fun String.combine(b: String): String = "${this}$b"
}

fun String.Companion.semigroup(): Semigroup<String> =
        lazyOf(object : StringSemigroupInstance {}).value

interface StringMonoidInstance : Monoid<String>, StringSemigroupInstance {
    override fun empty(): String = ""
}

fun String.Companion.monoid(): Monoid<String> =
        lazyOf(object : StringMonoidInstance {}).value

interface StringEqInstance : Eq<String> {
    override fun String.eqv(b: String): Boolean = this == b
}

fun String.Companion.eq(): Eq<String> =
        lazyOf(object : StringEqInstance {}).value

interface StringShowInstance : Show<String> {
    override fun String.show(): String = this
}

fun String.Companion.show(): Show<String> =
        lazyOf(object : StringShowInstance {}).value

interface StringOrderInstance : Order<String> {
    override fun String.compare(b: String): Int = this.compareTo(b)
}

fun String.Companion.order(): Order<String> =
        lazyOf(object : StringOrderInstance {}).value