package arrow.instances

import arrow.typeclasses.*

object StringSemigroupInstance : Semigroup<String> {
    override fun combine(a: String, b: String): String = "$a$b"
}

object StringSemigroupInstanceImplicits {

    fun instance(): StringSemigroupInstance = StringSemigroupInstance
}

object StringMonoidInstance : Monoid<String> {
    override fun empty(): String = ""

    override fun combine(a: String, b: String): String = StringSemigroupInstance.combine(a, b)
}

object StringMonoidInstanceImplicits {

    fun instance(): StringMonoidInstance = StringMonoidInstance
}

object StringEqInstance : Eq<String> {
    override fun eqv(a: String, b: String): Boolean = a == b
}

object StringOrderInstance : Order<String> {
    override fun compare(a: String, b: String): Int = a.compareTo(b)
}

object StringShowInstance : Show<String> {
    override fun show(a: String): String = a
}

object StringEqInstanceImplicits {
    fun instance(): StringEqInstance = StringEqInstance
}

object StringOrderInstanceImplicits {
    fun instance(): StringOrderInstance = StringOrderInstance
}

object StringShowInstanceImplicits {
    fun instance(): StringShowInstance = StringShowInstance
}