package katz

object ShortMonoid : Monoid<Short>, Semigroup<Short> by SG, GlobalInstance<Monoid<Short>>() {
    override fun empty(): Short = 0
}

private val SG: Semigroup<Short> = NumberSemigroup({ one, two -> (one + two).toShort() })