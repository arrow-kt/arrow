package kategory

object LongMonoid : Monoid<Long>, Semigroup<Long> by SG, GlobalInstance<Monoid<Long>>() {
    override fun empty(): Long = 0L
}

private val SG: Semigroup<Long> = NumberSemigroup(Long::plus)