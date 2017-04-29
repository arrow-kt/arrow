package katz

object FloatMonoid : Monoid<Float>, Semigroup<Float> by SG, GlobalInstance<Monoid<Float>>() {
    override fun empty(): Float = .0f
}

private val SG: Semigroup<Float> = NumberSemigroup(Float::plus)