package kategory

data class Law(val name: String, val test: () -> Unit)

inline fun <reified A> A.equalUnderTheLaw(b: A, eq: Eq<A>): Boolean =
        eq.eqv(this, b)