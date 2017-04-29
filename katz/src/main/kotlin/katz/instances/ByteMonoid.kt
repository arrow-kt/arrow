package katz

object ByteMonoid : Monoid<Byte>, Semigroup<Byte> by SG, GlobalInstance<Monoid<Byte>>() {
    override fun empty(): Byte = 0
}

private val SG: Semigroup<Byte> = NumberSemigroup({ one, two -> (one + two).toByte() })