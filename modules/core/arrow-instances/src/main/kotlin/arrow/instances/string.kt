package arrow.instances

import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup

object StringSemigroupInstance : Semigroup<String> {
    override fun String.combine(b: String): String = "${this}$b"
}

object StringSemigroupInstanceImplicits {

    fun instance(): StringSemigroupInstance = StringSemigroupInstance
}

object StringMonoidInstance : Monoid<String> {
    override fun empty(): String = ""

    override fun String.combine(b: String): String = StringSemigroupInstance.run { combine(b) }
}

object StringMonoidInstanceImplicits {

    fun instance(): StringMonoidInstance = StringMonoidInstance
}

object StringEqInstance : Eq<String> {
    override fun String.eqv(b: String): Boolean = this == b
}

object StringEqInstanceImplicits {

    fun instance(): StringEqInstance = StringEqInstance
}