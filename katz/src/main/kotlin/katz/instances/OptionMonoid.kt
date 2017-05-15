package katz

class OptionMonoid<A>(val SG: Semigroup<A>) : Monoid<Option<A>> {
    override fun combine(a: Option<A>, b: Option<A>): Option<A> =
            when (a) {
                is Option.Some<A> -> when (b) {
                    is Option.Some<A> -> Option.Some(SG.combine(a.value, b.value))
                    is Option.None -> b
                }
                is Option.None -> a
            }

    override fun empty(): Option<A> =
            Option.None
}
