package katz

class NumberSemigroup<A : Number>(val f: (A, A) -> A) : Semigroup<A> {
    override fun combine(a: A, b: A): A =
            f(a, b)
}
