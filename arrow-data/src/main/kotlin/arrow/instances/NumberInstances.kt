package arrow

class NumberSemigroup<A : Number>(val f: (A, A) -> A) : Semigroup<A> {
    override fun combine(a: A, b: A): A = f(a, b)
}

object ByteMonoid : Monoid<Byte>, Semigroup<Byte> by SGByte {
    override fun empty(): Byte = 0
}

object ByteMonoidInstanceImplicits {
    fun instance(): ByteMonoid = ByteMonoid
}

private val SGByte: Semigroup<Byte> = NumberSemigroup({ one, two -> (one + two).toByte() })

object ByteSemigroupInstanceImplicits {
    fun instance(): Semigroup<Byte> = SGByte
}

object ByteOrderInstance : Order<Byte> {
    override fun compare(a: Byte, b: Byte): Int = a.compareTo(b)
}

object ByteOrderInstanceImplicits {
    fun instance(): Order<Byte> = ByteOrderInstance
}

object ByteEqInstance: Eq<Byte> {
    override fun eqv(a: Byte, b: Byte): Boolean = a == b
}

object ByteEqInstanceImplicits {
    fun instance(): ByteEqInstance = ByteEqInstance
}

object DoubleMonoid : Monoid<Double>, Semigroup<Double> by SGDouble {
    override fun empty(): Double = .0
}

object DoubleMonoidInstanceImplicits {
    fun instance(): DoubleMonoid = DoubleMonoid
}

private val SGDouble: Semigroup<Double> = NumberSemigroup(Double::plus)

object DoubleSemigroupInstanceImplicits {
    fun instance(): Semigroup<Double> = SGDouble
}

object DoubleOrderInstance : Order<Double> {
    override fun compare(a: Double, b: Double): Int = a.compareTo(b)
}

object DoubleOrderInstanceImplicits {
    fun instance(): Order<Double> = DoubleOrderInstance
}

object DoubleEqInstance: Eq<Double> {
    override fun eqv(a: Double, b: Double): Boolean = a == b
}

object DoubleEqInstanceImplicits {
    fun instance(): DoubleEqInstance = DoubleEqInstance
}

object IntMonoid : Monoid<Int>, Semigroup<Int> by SGInt {
    override fun empty(): Int = 0
}

object IntMonoidInstanceImplicits {
    fun instance(): IntMonoid = IntMonoid
}

private val SGInt: Semigroup<Int> = NumberSemigroup(Int::plus)

object IntSemigroupInstanceImplicits {
    fun instance(): Semigroup<Int> = SGInt
}

object IntEqInstance: Eq<Int> {
    override fun eqv(a: Int, b: Int): Boolean = a == b
}

object IntEqInstanceImplicits {
    fun instance(): IntEqInstance = IntEqInstance
}

object IntOrderInstance : Order<Int> {
    override fun compare(a: Int, b: Int): Int = a.compareTo(b)
}

object IntOrderInstanceImplicits {
    fun instance(): Order<Int> = IntOrderInstance
}

object LongMonoid : Monoid<Long>, Semigroup<Long> by SGLong {
    override fun empty(): Long = 0L
}

object LongMonoidInstanceImplicits {
    fun instance(): LongMonoid = LongMonoid
}

private val SGLong: Semigroup<Long> = NumberSemigroup(Long::plus)

object LongSemigroupInstanceImplicits {
    fun instance(): Semigroup<Long> = SGLong
}

object LongOrderInstance : Order<Long> {
    override fun compare(a: Long, b: Long): Int = a.compareTo(b)
}

object LongOrderInstanceImplicits {
    fun instance(): Order<Long> = LongOrderInstance
}

object LongEqInstance: Eq<Long> {
    override fun eqv(a: Long, b: Long): Boolean = a == b
}

object LongEqInstanceImplicits {
    fun instance(): LongEqInstance = LongEqInstance
}

object ShortMonoid : Monoid<Short>, Semigroup<Short> by SGShort {
    override fun empty(): Short = 0
}

object ShortMonoidInstanceImplicits {
    fun instance(): ShortMonoid = ShortMonoid
}

private val SGShort: Semigroup<Short> = NumberSemigroup({ one, two -> (one + two).toShort() })

object ShortSemigroupInstanceImplicits {
    fun instance(): Semigroup<Short> = SGShort
}

object ShortOrderInstance : Order<Short> {
    override fun compare(a: Short, b: Short): Int = a.compareTo(b)
}

object ShortOrderInstanceImplicits {
    fun instance(): Order<Short> = ShortOrderInstance
}

object ShortEqInstance: Eq<Short> {
    override fun eqv(a: Short, b: Short): Boolean = a == b
}

object ShortEqInstanceImplicits {
    fun instance(): ShortEqInstance = ShortEqInstance
}

object FloatMonoid : Monoid<Float>, Semigroup<Float> by SGFloat {
    override fun empty(): Float = .0f
}

object FloatMonoidInstanceImplicits {
    fun instance(): FloatMonoid = FloatMonoid
}

private val SGFloat: Semigroup<Float> = NumberSemigroup(Float::plus)

object FloatSemigroupInstanceImplicits {
    fun instance(): Semigroup<Float> = SGFloat
}

object FloatOrderInstance : Order<Float> {
    override fun compare(a: Float, b: Float): Int = a.compareTo(b)
}

object FloatOrderInstanceImplicits {
    fun instance(): Order<Float> = FloatOrderInstance
}

object FloatEqInstance: Eq<Float> {
    override fun eqv(a: Float, b: Float): Boolean = a == b
}

object FloatEqInstanceImplicits {
    fun instance(): FloatEqInstance = FloatEqInstance
}