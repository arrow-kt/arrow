package arrow.core.extensions

import arrow.typeclasses.*

//////////
// Byte
//////////
interface ByteSemigroup : Semigroup<Byte> {
  override fun Byte.combine(b: Byte): Byte = (this + b).toByte()
}

interface ByteMonoid: Monoid<Byte>, ByteSemigroup {
  override fun empty(): Byte = 0
}

interface ByteSemiring: Semiring<Byte> {
    override fun zero(): Byte = 0
    override fun one(): Byte = 1

    override fun Byte.combine(b: Byte): Byte = (this + b).toByte()
    override fun Byte.combineMultiplicate(b: Byte): Byte = (this * b).toByte()
}

interface ByteOrder : Order<Byte> {
  override fun Byte.compare(b: Byte): Int = compareTo(b)
}

interface ByteEq : Eq<Byte> {
  override fun Byte.eqv(b: Byte): Boolean = this == b
}

interface ByteShow : Show<Byte> {
  override fun Byte.show(): String = toString()
}

interface ByteHash: Hash<Byte>, ByteEq {
  override fun Byte.hash(): Int = hashCode()
}

fun Byte.Companion.hash(): Hash<Byte> =
  object : ByteHash{}

fun Byte.Companion.show(): Show<Byte> =
  object : ByteShow{}

fun Byte.Companion.eq(): Eq<Byte> =
  object : ByteEq{}

fun Byte.Companion.order(): Order<Byte> =
  object : ByteOrder{}

fun Byte.Companion.semigroup(): Semigroup<Byte> =
  object : ByteSemigroup{}

fun Byte.Companion.monoid(): Monoid<Byte> =
  object : ByteMonoid{}

fun Byte.Companion.semiring(): Semiring<Byte> =
  object : ByteSemiring{}

//////////
// Double
//////////

interface DoubleSemigroup : Semigroup<Double> {
  override fun Double.combine(b: Double): Double = this + b
}

interface DoubleMonoid: Monoid<Double>, DoubleSemigroup {
  override fun empty(): Double = .0
}

interface DoubleSemiring: Semiring<Double> {
  override fun zero(): Double = .0
  override fun one(): Double = 1.0

  override fun Double.combine(b: Double): Double = this + b
  override fun Double.combineMultiplicate(b: Double): Double = this * b
}

interface DoubleOrder : Order<Double> {
  override fun Double.compare(b: Double): Int = compareTo(b)
}

interface DoubleEq : Eq<Double> {
  override fun Double.eqv(b: Double): Boolean = this == b
}

interface DoubleShow : Show<Double> {
  override fun Double.show(): String = toString()
}

interface DoubleHash: Hash<Double>, DoubleEq {
  override fun Double.hash(): Int = hashCode()
}

fun Double.Companion.hash(): Hash<Double> =
  object : DoubleHash{}

fun Double.Companion.show(): Show<Double> =
  object : DoubleShow{}

fun Double.Companion.eq(): Eq<Double> =
  object : DoubleEq{}

fun Double.Companion.order(): Order<Double> =
  object : DoubleOrder{}

fun Double.Companion.semigroup(): Semigroup<Double> =
  object : DoubleSemigroup{}

fun Double.Companion.monoid(): Monoid<Double> =
  object : DoubleMonoid{}

fun Double.Companion.semiring(): Semiring<Double> =
  object : DoubleSemiring{}

//////////
// Int
//////////
interface IntSemigroup : Semigroup<Int> {
  override fun Int.combine(b: Int): Int = this + b
}

interface IntMonoid: Monoid<Int>, IntSemigroup {
  override fun empty(): Int = 0
}

interface IntSemiring: Semiring<Int> {
    override fun zero(): Int = 0
    override fun one(): Int = 1

    override fun Int.combine(b: Int): Int = this + b
    override fun Int.combineMultiplicate(b: Int): Int = this * b
}

interface IntEq : Eq<Int> {
  override fun Int.eqv(b: Int): Boolean = this == b
}

interface IntShow : Show<Int> {
  override fun Int.show(): String = toString()
}

interface IntOrder : Order<Int> {
  override fun Int.compare(b: Int): Int = compareTo(b)
}

interface IntHash: Hash<Int>, IntEq {
  override fun Int.hash(): Int = hashCode()
}

fun Int.Companion.hash(): Hash<Int> =
  object : IntHash{}

fun Int.Companion.show(): Show<Int> =
  object : IntShow{}

fun Int.Companion.eq(): Eq<Int> =
  object : IntEq{}

fun Int.Companion.order(): Order<Int> =
  object : IntOrder{}

fun Int.Companion.semigroup(): Semigroup<Int> =
  object : IntSemigroup{}

fun Int.Companion.monoid(): Monoid<Int> =
  object : IntMonoid{}

fun Int.Companion.semiring(): Semiring<Int> =
  object : IntSemiring{}

//////////
// Long
//////////

interface LongSemigroup : Semigroup<Long> {
  override fun Long.combine(b: Long): Long = this + b
}

interface LongMonoid: Monoid<Long>, LongSemigroup {
  override fun empty(): Long = 0L
}

interface LongSemiring: Semiring<Long> {
    override fun zero(): Long = 0
    override fun one(): Long = 1

    override fun Long.combine(b: Long): Long = this + b
    override fun Long.combineMultiplicate(b: Long): Long = this * b
}

interface LongOrder : Order<Long> {
  override fun Long.compare(b: Long): Int = compareTo(b)
}

interface LongEq : Eq<Long> {
  override fun Long.eqv(b: Long): Boolean = this == b
}

interface LongShow : Show<Long> {
  override fun Long.show(): String = toString()
}

interface LongHash: Hash<Long>, LongEq {
  override fun Long.hash(): Int = hashCode()
}

fun Long.Companion.hash(): Hash<Long> =
  object : LongHash{}

fun Long.Companion.show(): Show<Long> =
  object : LongShow{}

fun Long.Companion.eq(): Eq<Long> =
  object : LongEq{}

fun Long.Companion.order(): Order<Long> =
  object : LongOrder{}

fun Long.Companion.semigroup(): Semigroup<Long> =
  object : LongSemigroup{}

fun Long.Companion.monoid(): Monoid<Long> =
  object : LongMonoid{}

fun Long.Companion.semiring(): Semiring<Long> =
  object : LongSemiring{}

//////////
// Short
//////////

interface ShortSemigroup : Semigroup<Short> {
  override fun Short.combine(b: Short): Short = (this + b).toShort()
}

interface ShortMonoid: Monoid<Short>, ShortSemigroup {
  override fun empty(): Short = 0
}

interface ShortSemiring: Semiring<Short> {
    override fun zero(): Short = 0
    override fun one(): Short = 1

    override fun Short.combine(b: Short): Short = (this + b).toShort()
    override fun Short.combineMultiplicate(b: Short): Short = (this * b).toShort()
}

interface ShortOrder : Order<Short> {
  override fun Short.compare(b: Short): Int = compareTo(b)
}

interface ShortEq : Eq<Short> {
  override fun Short.eqv(b: Short): Boolean = this == b
}

interface ShortShow : Show<Short> {
  override fun Short.show(): String = toString()
}

interface ShortHash: Hash<Short>, ShortEq {
  override fun Short.hash(): Int = hashCode()
}

fun Short.Companion.hash(): Hash<Short> =
  object : ShortHash{}

fun Short.Companion.show(): Show<Short> =
  object : ShortShow{}

fun Short.Companion.eq(): Eq<Short> =
  object : ShortEq{}

fun Short.Companion.order(): Order<Short> =
  object : ShortOrder{}

fun Short.Companion.semigroup(): Semigroup<Short> =
  object : ShortSemigroup{}

fun Short.Companion.monoid(): Monoid<Short> =
  object : ShortMonoid{}

fun Short.Companion.semiring(): Semiring<Short> =
  object : ShortSemiring{}

//////////
// Float
//////////

interface FloatSemigroup : Semigroup<Float> {
  override fun Float.combine(b: Float): Float = this + b
}

interface FloatMonoid: Monoid<Float>, FloatSemigroup {
  override fun empty(): Float = 0f
}

interface FloatSemiring: Semiring<Float> {
    override fun zero(): Float = 0f
    override fun one(): Float = 1f

    override fun Float.combine(b: Float): Float = this + b
    override fun Float.combineMultiplicate(b: Float): Float = this * b
}

interface FloatOrder : Order<Float> {
  override fun Float.compare(b: Float): Int = compareTo(b)
}

interface FloatEq : Eq<Float> {
  override fun Float.eqv(b: Float): Boolean = this == b
}

interface FloatShow : Show<Float> {
  override fun Float.show(): String = toString()
}

interface FloatHash: Hash<Float>, FloatEq {
  override fun Float.hash(): Int = hashCode()
}

fun Float.Companion.hash(): Hash<Float> =
  object : FloatHash{}

fun Float.Companion.show(): Show<Float> =
  object : FloatShow{}

fun Float.Companion.eq(): Eq<Float> =
  object : FloatEq{}

fun Float.Companion.order(): Order<Float> =
  object : FloatOrder{}

fun Float.Companion.semigroup(): Semigroup<Float> =
  object : FloatSemigroup{}

fun Float.Companion.monoid(): Monoid<Float> =
  object : FloatMonoid{}

fun Float.Companion.semiring(): Semiring<Float> =
  object : FloatSemiring{}
