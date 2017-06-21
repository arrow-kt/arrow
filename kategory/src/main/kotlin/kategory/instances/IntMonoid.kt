package kategory

object IntMonoid : Monoid<Int>, Semigroup<Int> by SG, GlobalInstance<Monoid<Int>>() {
    override fun empty(): Int = 0
}

private val SG: Semigroup<Int> = NumberSemigroup(Int::plus)