package arrow.core

import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Semiring

// ////////
// Byte
// ////////
private object ByteSemigroup : Semigroup<Byte> {
  override fun Byte.combine(b: Byte): Byte = (this + b).toByte()
}

private object ByteMonoid : Monoid<Byte> {
  override fun empty(): Byte = 0
  override fun Byte.combine(b: Byte): Byte = (this + b).toByte()
}

private object ByteSemiring : Semiring<Byte> {
  override fun one(): Byte = 1
  override fun zero(): Byte = 0

  override fun Byte.combine(b: Byte): Byte = (this + b).toByte()
  override fun Byte.combineMultiplicate(b: Byte): Byte = (this * b).toByte()
}

fun Semigroup.Companion.byte(): Semigroup<Byte> =
  ByteSemigroup

fun Monoid.Companion.byte(): Monoid<Byte> =
  ByteMonoid

fun Semiring.Companion.byte(): Semiring<Byte> =
  ByteSemiring

// ////////
// Double
// ////////
private object DoubleSemigroup : Semigroup<Double> {
  override fun Double.combine(b: Double): Double = this + b
}

private object DoubleMonoid : Monoid<Double> {
  override fun empty(): Double = .0
  override fun Double.combine(b: Double): Double = this + b
}

private object DoubleSemiring : Semiring<Double> {
  override fun one(): Double = 1.0
  override fun zero(): Double = 0.0

  override fun Double.combine(b: Double): Double = this + b
  override fun Double.combineMultiplicate(b: Double): Double = this * b
}

fun Semigroup.Companion.double(): Semigroup<Double> =
  DoubleSemigroup

fun Monoid.Companion.double(): Monoid<Double> =
  DoubleMonoid

fun Semiring.Companion.double(): Semiring<Double> =
  DoubleSemiring

// ////////
// Int
// ////////
private object IntSemigroup : Semigroup<Int> {
  override fun Int.combine(b: Int): Int = this + b
}

private object IntMonoid : Monoid<Int> {
  override fun empty(): Int = 0
  override fun Int.combine(b: Int): Int = this + b
}

private object IntSemiring : Semiring<Int> {
  override fun one(): Int = 1
  override fun zero(): Int = 0

  override fun Int.combine(b: Int): Int = this + b
  override fun Int.combineMultiplicate(b: Int): Int = this * b
}

fun Semigroup.Companion.int(): Semigroup<Int> =
  IntSemigroup

fun Monoid.Companion.int(): Monoid<Int> =
  IntMonoid

fun Semiring.Companion.int(): Semiring<Int> =
  IntSemiring

// ////////
// Long
// ////////
private object LongSemigroup : Semigroup<Long> {
  override fun Long.combine(b: Long): Long = this + b
}

private object LongMonoid : Monoid<Long> {
  override fun empty(): Long = 0L
  override fun Long.combine(b: Long): Long = this + b
}

private object LongSemiring : Semiring<Long> {
  override fun one(): Long = 1
  override fun zero(): Long = 0

  override fun Long.combine(b: Long): Long = this + b
  override fun Long.combineMultiplicate(b: Long): Long = this * b
}

fun Semigroup.Companion.long(): Semigroup<Long> =
  LongSemigroup

fun Monoid.Companion.long(): Monoid<Long> =
  LongMonoid

fun Semiring.Companion.long(): Semiring<Long> =
  LongSemiring

// ////////
// Short
// ////////
private object ShortSemigroup : Semigroup<Short> {
  override fun Short.combine(b: Short): Short = (this + b).toShort()
}

private object ShortMonoid : Monoid<Short> {
  override fun empty(): Short = 0
  override fun Short.combine(b: Short): Short = (this + b).toShort()
}

private object ShortSemiring : Semiring<Short> {
  override fun one(): Short = 1
  override fun zero(): Short = 0

  override fun Short.combine(b: Short): Short = (this + b).toShort()
  override fun Short.combineMultiplicate(b: Short): Short = (this * b).toShort()
}

fun Semigroup.Companion.short(): Semigroup<Short> =
  ShortSemigroup

fun Monoid.Companion.short(): Monoid<Short> =
  ShortMonoid

fun Semiring.Companion.short(): Semiring<Short> =
  ShortSemiring

// ////////
// Float
// ////////
private object FloatSemigroup : Semigroup<Float> {
  override fun Float.combine(b: Float): Float = this + b
}

private object FloatMonoid : Monoid<Float> {
  override fun empty(): Float = 0f
  override fun Float.combine(b: Float): Float = this + b
}

private object FloatSemiring : Semiring<Float> {
  override fun one(): Float = 1f
  override fun zero(): Float = 0f

  override fun Float.combine(b: Float): Float = this + b
  override fun Float.combineMultiplicate(b: Float): Float = this * b
}

fun Semigroup.Companion.float(): Semigroup<Float> =
  FloatSemigroup

fun Monoid.Companion.float(): Monoid<Float> =
  FloatMonoid

fun Semiring.Companion.float(): Semiring<Float> =
  FloatSemiring
