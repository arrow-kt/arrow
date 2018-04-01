package arrow.instances

import arrow.instance
import arrow.typeclasses.*

@instance(String::class)
interface StringSemigroupInstance : Semigroup<String> {
    override fun String.combine(b: String): String = "${this}$b"
}

@instance(String::class)
interface StringMonoidInstance : Monoid<String>, StringSemigroupInstance {
    override fun empty(): String = ""
}

@instance(String::class)
interface StringEqInstance : Eq<String> {
    override fun String.eqv(b: String): Boolean = this == b
}

@instance(String::class)
interface StringShowInstance : Show<String> {
    override fun String.show(): String = this
}

@instance(String::class)
interface StringOrderInstance : Order<String> {
    override fun String.compare(b: String): Int = this.compareTo(b)
}