package arrow.instances

import arrow.typeclasses.*

class NumberSemigroup<A : Number>(val f: (A, A) -> A) : Semigroup<A> {
  override fun A.combine(b: A): A = f(this, b)
}

//////////
// Byte
//////////

private val SGByte: Semigroup<Byte> = NumberSemigroup({ one, two -> (one + two).toByte() })

object ByteMonoidInstance : Monoid<Byte>, Semigroup<Byte> by SGByte {
  override fun empty(): Byte = 0
}

object ByteSemigroupInstance : Semigroup<Byte> by SGByte

object ByteOrderInstance : Order<Byte> {
  override fun Byte.compare(b: Byte): Int = compareTo(b)
}

object ByteEqInstance : Eq<Byte> {
  override fun Byte.eqv(b: Byte): Boolean = this == b
}

object ByteShowInstance : Show<Byte> {
  override fun Byte.show(): String = toString()
}

//////////
// Double
//////////

private val SGDouble: Semigroup<Double> = NumberSemigroup(Double::plus)

object DoubleMonoid : Monoid<Double>, Semigroup<Double> by SGDouble {
  override fun empty(): Double = .0
}

object DoubleSemigroupInstance : Semigroup<Double> by SGDouble

object DoubleOrderInstance : Order<Double> {
  override fun Double.compare(b: Double): Int = compareTo(b)
}

object DoubleEqInstance : Eq<Double> {
  override fun Double.eqv(b: Double): Boolean = this == b
}

object DoubleShowInstance : Show<Double> {
  override fun Double.show(): String = toString()
}

//////////
// Int
//////////

private val SGInt: Semigroup<Int> = NumberSemigroup(Int::plus)

object IntSemigroupInstance : Semigroup<Int> by SGInt

object IntMonoidInstance : Monoid<Int>, Semigroup<Int> by SGInt {
  override fun empty(): Int = 0
}

object IntEqInstance : Eq<Int> {
  override fun Int.eqv(b: Int): Boolean = this == b
}

object IntShowInstance : Show<Int> {
  override fun Int.show(): String = toString()
}

object IntOrderInstance : Order<Int> {
  override fun Int.compare(b: Int): Int = compareTo(b)
}

//////////
// Long
//////////

private val SGLong: Semigroup<Long> = NumberSemigroup(Long::plus)

object LongMonoidInstance : Monoid<Long>, Semigroup<Long> by SGLong {
  override fun empty(): Long = 0L
}

object LongSemigroupInstance : Semigroup<Long> by SGLong

object LongOrderInstance : Order<Long> {
  override fun Long.compare(b: Long): Int = compareTo(b)
}

object LongEqInstance : Eq<Long> {
  override fun Long.eqv(b: Long): Boolean = this == b
}

object LongShowInstance : Show<Long> {
  override fun Long.show(): String = toString()
}

//////////
// Short
//////////

private val SGShort: Semigroup<Short> = NumberSemigroup({ one, two -> (one + two).toShort() })

object ShortMonoid : Monoid<Short>, Semigroup<Short> by SGShort {
  override fun empty(): Short = 0
}

object ShortSemigroupInstance : Semigroup<Short> by SGShort

object ShortOrderInstance : Order<Short> {
  override fun Short.compare(b: Short): Int = compareTo(b)
}

object ShortEqInstance : Eq<Short> {
  override fun Short.eqv(b: Short): Boolean = this == b
}

object ShortShowInstance : Show<Short> {
  override fun Short.show(): String = toString()
}

//////////
// Float
//////////

private val SGFloat: Semigroup<Float> = NumberSemigroup(Float::plus)

object FloatMonoid : Monoid<Float>, Semigroup<Float> by SGFloat {
  override fun empty(): Float = .0f
}

object FloatSemigroupInstance : Semigroup<Float> by SGFloat

object FloatOrderInstance : Order<Float> {
  override fun Float.compare(b: Float): Int = compareTo(b)
}

object FloatEqInstance : Eq<Float> {
  override fun Float.eqv(b: Float): Boolean = this == b
}

object FloatShowInstance : Show<Float> {
  override fun Float.show(): String = toString()
}
