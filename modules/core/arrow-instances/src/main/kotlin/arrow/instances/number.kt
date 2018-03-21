package arrow.instances

import arrow.typeclasses.*

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
    override fun Byte.compare(b: Byte): Int = compareTo(b)
}

object ByteOrderInstanceImplicits {
    fun instance(): Order<Byte> = ByteOrderInstance
}

object ByteEqInstance : Eq<Byte> {
    override fun Byte.eqv(b: Byte): Boolean = this == b
}

object ByteEqInstanceImplicits {
    fun instance(): ByteEqInstance = ByteEqInstance
}

object ByteShowInstance : Show<Byte> {
    override fun show(a: Byte): String = a.toString()
}

object ByteShowInstanceImplicits {
    fun instance(): ByteShowInstance = ByteShowInstance
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
    override fun Double.compare(b: Double): Int = compareTo(b)
}

object DoubleOrderInstanceImplicits {
    fun instance(): Order<Double> = DoubleOrderInstance
}

object DoubleEqInstance : Eq<Double> {
    override fun Double.eqv(b: Double): Boolean = this == b
}

object DoubleEqInstanceImplicits {
    fun instance(): DoubleEqInstance = DoubleEqInstance
}

object DoubleShowInstance : Show<Double> {
    override fun show(a: Double): String = a.toString()
}

object DoubleShowInstanceImplicits {
    fun instance(): DoubleShowInstance = DoubleShowInstance
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

object IntEqInstance : Eq<Int> {
    override fun Int.eqv(b: Int): Boolean = this == b
}

object IntEqInstanceImplicits {
    fun instance(): IntEqInstance = IntEqInstance
}

object IntShowInstance : Show<Int> {
    override fun show(a: Int): String = a.toString()
}

object IntShowInstanceImplicits {
    fun instance(): IntShowInstance = IntShowInstance
}

object IntOrderInstance : Order<Int> {
    override fun Int.compare(b: Int): Int = compareTo(b)
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
    override fun Long.compare(b: Long): Int = compareTo(b)
}

object LongOrderInstanceImplicits {
    fun instance(): Order<Long> = LongOrderInstance
}

object LongEqInstance : Eq<Long> {
    override fun Long.eqv(b: Long): Boolean = this == b
}

object LongEqInstanceImplicits {
    fun instance(): LongEqInstance = LongEqInstance
}

object LongShowInstance : Show<Long> {
    override fun show(a: Long): String = a.toString()
}

object LongShowInstanceImplicits {
    fun instance(): LongShowInstance = LongShowInstance
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
    override fun Short.compare(b: Short): Int = compareTo(b)
}

object ShortOrderInstanceImplicits {
    fun instance(): Order<Short> = ShortOrderInstance
}

object ShortEqInstance : Eq<Short> {
    override fun Short.eqv(b: Short): Boolean = this == b
}

object ShortEqInstanceImplicits {
    fun instance(): ShortEqInstance = ShortEqInstance
}

object ShortShowInstance : Show<Short> {
    override fun show(a: Short): String = a.toString()
}

object ShortShowInstanceImplicits {
    fun instance(): ShortShowInstance = ShortShowInstance
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
    override fun Float.compare(b: Float): Int = compareTo(b)
}

object FloatOrderInstanceImplicits {
    fun instance(): Order<Float> = FloatOrderInstance
}

object FloatEqInstance : Eq<Float> {
    override fun Float.eqv(b: Float): Boolean = this == b
}

object FloatEqInstanceImplicits {
    fun instance(): FloatEqInstance = FloatEqInstance
}

object FloatShowInstance : Show<Float> {
    override fun show(a: Float): String = a.toString()
}

object FloatShowInstanceImplicits {
    fun instance(): FloatShowInstance = FloatShowInstance
}