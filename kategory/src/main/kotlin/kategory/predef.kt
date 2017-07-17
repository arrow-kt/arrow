package kategory

fun <A> identity(a: A): A = a

object ByteMonoid : Monoid<Byte>, Semigroup<Byte> by SGByte, GlobalInstance<Monoid<Byte>>() {
    override fun empty(): Byte = 0
}

private val SGByte: Semigroup<Byte> = NumberSemigroup({ one, two -> (one + two).toByte() })

object DoubleMonoid : Monoid<Double>, Semigroup<Double> by SGDouble, GlobalInstance<Monoid<Double>>() {
    override fun empty(): Double = .0
}

private val SGDouble: Semigroup<Double> = NumberSemigroup(Double::plus)

object IntMonoid : Monoid<Int>, Semigroup<Int> by SGInt, GlobalInstance<Monoid<Int>>() {
    override fun empty(): Int = 0
}

private val SGInt: Semigroup<Int> = NumberSemigroup(Int::plus)

object LongMonoid : Monoid<Long>, Semigroup<Long> by SGLong, GlobalInstance<Monoid<Long>>() {
    override fun empty(): Long = 0L
}

private val SGLong: Semigroup<Long> = NumberSemigroup(Long::plus)

object ShortMonoid : Monoid<Short>, Semigroup<Short> by SGShort, GlobalInstance<Monoid<Short>>() {
    override fun empty(): Short = 0
}

private val SGShort: Semigroup<Short> = NumberSemigroup({ one, two -> (one + two).toShort() })

object FloatMonoid : Monoid<Float>, Semigroup<Float> by SGFloat, GlobalInstance<Monoid<Float>>() {
    override fun empty(): Float = .0f
}

private val SGFloat: Semigroup<Float> = NumberSemigroup(Float::plus)