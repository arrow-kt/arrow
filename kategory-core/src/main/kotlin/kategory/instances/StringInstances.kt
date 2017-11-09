package kategory

object StringSemigroupInstance : Semigroup<String> {
    override fun combine(a: String, b: String): String = "$a$b"
}

object StringSemigroupInstanceImplicits {
    @JvmStatic
    fun instance(): StringSemigroupInstance = StringSemigroupInstance
}

object StringMonoidInstance : Monoid<String> {
    override fun empty(): String = ""

    override fun combine(a: String, b: String): String = StringSemigroupInstance.combine(a, b)
}

object StringMonoidInstanceImplicits {
    @JvmStatic
    fun instance(): StringMonoidInstance = StringMonoidInstance
}

object StringEqInstance : Eq<String> {
    override fun eqv(a: String, b: String): Boolean = a == b
}

object StringEqInstanceImplicits {
    @JvmStatic
    fun instance(): StringEqInstance = StringEqInstance
}