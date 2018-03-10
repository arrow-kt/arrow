package arrow.instances

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup

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

object StringEqInstanceImplicits {

    fun instance(): StringEqInstance = StringEqInstance
}

object StringHashInstance : Eq<String>, Hash<String> {
    override fun eqv(a: String, b: String): Boolean = StringEqInstance.eqv(a, b)

    override fun hash(a: String): Int = a.hashCode()
}

object StringHashInstanceImplicits {

    fun instance(): StringHashInstance = StringHashInstance
}
