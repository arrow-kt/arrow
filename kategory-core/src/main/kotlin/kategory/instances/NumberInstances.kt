package kategory

class NumberSemigroup<A : Number>(val f: (A, A) -> A) : Semigroup<A> {
    override fun combine(a: A, b: A): A = f(a, b)
}

object ByteMonoid : Monoid<Byte>, Semigroup<Byte> by SGByte {
    override fun empty(): Byte = 0
}

object ByteMonoidInstanceImplicits {
    @JvmStatic fun instance(): ByteMonoid = ByteMonoid
}

private val SGByte: Semigroup<Byte> = NumberSemigroup({ one, two -> (one + two).toByte() })

object ByteSemigroupInstanceImplicits {
    @JvmStatic fun instance(): Semigroup<Byte> = SGByte
}

object DoubleMonoid : Monoid<Double>, Semigroup<Double> by SGDouble {
    override fun empty(): Double = .0
}

object DoubleMonoidInstanceImplicits {
    @JvmStatic fun instance(): DoubleMonoid = DoubleMonoid
}

private val SGDouble: Semigroup<Double> = NumberSemigroup(Double::plus)

object DoubleSemigroupInstanceImplicits {
    @JvmStatic fun instance(): Semigroup<Double> = SGDouble
}

object IntMonoid : Monoid<Int>, Semigroup<Int> by SGInt {
    override fun empty(): Int = 0
}

object IntMonoidInstanceImplicits {
    @JvmStatic fun instance(): IntMonoid = IntMonoid
}

private val SGInt: Semigroup<Int> = NumberSemigroup(Int::plus)

object IntSemigroupInstanceImplicits {
    @JvmStatic fun instance(): Semigroup<Int> = SGInt
}

object IntEqInstance: Eq<Int> {
    override fun eqv(a: Int, b: Int): Boolean = a == b
}

object IntEqInstanceImplicits {
    @JvmStatic fun instance(): IntEqInstance = IntEqInstance
}

object LongMonoid : Monoid<Long>, Semigroup<Long> by SGLong {
    override fun empty(): Long = 0L
}

object LongMonoidInstanceImplicits {
    @JvmStatic fun instance(): LongMonoid = LongMonoid
}

private val SGLong: Semigroup<Long> = NumberSemigroup(Long::plus)

object LongSemigroupInstanceImplicits {
    @JvmStatic fun instance(): Semigroup<Long> = SGLong
}

object ShortMonoid : Monoid<Short>, Semigroup<Short> by SGShort {
    override fun empty(): Short = 0
}

object ShortMonoidInstanceImplicits {
    @JvmStatic fun instance(): ShortMonoid = ShortMonoid
}

private val SGShort: Semigroup<Short> = NumberSemigroup({ one, two -> (one + two).toShort() })

object ShortSemigroupInstanceImplicits {
    @JvmStatic fun instance(): Semigroup<Short> = SGShort
}

object FloatMonoid : Monoid<Float>, Semigroup<Float> by SGFloat {
    override fun empty(): Float = .0f
}

object FloatMonoidInstanceImplicits {
    @JvmStatic fun instance(): FloatMonoid = FloatMonoid
}

private val SGFloat: Semigroup<Float> = NumberSemigroup(Float::plus)

object FloatSemigroupInstanceImplicits {
    @JvmStatic fun instance(): Semigroup<Float> = SGFloat
}

