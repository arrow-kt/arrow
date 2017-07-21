package kategory

object StringMonoid : Monoid<String> {
    override fun empty(): String = ""

    override fun combine(a: String, b: String): String = "$a $b"
}