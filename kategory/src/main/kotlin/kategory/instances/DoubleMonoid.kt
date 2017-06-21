package kategory

object DoubleMonoid : Monoid<Double>, Semigroup<Double> by SG, GlobalInstance<Monoid<Double>>() {
    override fun empty(): Double = .0
}

private val SG: Semigroup<Double> = NumberSemigroup(Double::plus)