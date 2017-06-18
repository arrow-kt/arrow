package kategory

inline fun <reified A> ListMonoid(): Monoid<List<A>> = object : Monoid<List<A>>, GlobalInstance<Monoid<List<A>>>() {
    override fun empty(): List<A> = emptyList()

    override fun combine(a: List<A>, b: List<A>): List<A> = a + b
}