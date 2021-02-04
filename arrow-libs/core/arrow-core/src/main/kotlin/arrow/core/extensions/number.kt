package arrow.core.extensions

import arrow.core.Ordering
import arrow.typeclasses.Eq
import arrow.typeclasses.EqDeprecation
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Semiring
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation

// ////////
// Byte
// ////////
@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Semigroup.byte()", "arrow.core.Semigroup", "arrow.core.byte"))
interface ByteSemigroup : Semigroup<Byte> {
  override fun Byte.combine(b: Byte): Byte = (this + b).toByte()
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Monoid.byte()", "arrow.core.Monoid", "arrow.core.byte"))
interface ByteMonoid : Monoid<Byte>, ByteSemigroup {
  override fun empty(): Byte = 0
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Semiring.byte()", "arrow.core.Semiring", "arrow.core.byte"))
interface ByteSemiring : Semiring<Byte> {
  override fun one(): Byte = 1
  override fun zero(): Byte = 0

  override fun Byte.combine(b: Byte): Byte = (this + b).toByte()
  override fun Byte.combineMultiplicate(b: Byte): Byte = (this * b).toByte()
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Order.byte()", "arrow.core.Order", "arrow.core.byte"))
interface ByteOrder : Order<Byte> {
  override fun Byte.compare(b: Byte): Ordering = Ordering.fromInt(this.compareTo(b))
  override fun Byte.compareTo(b: Byte): Int = this.compareTo(b)
}

@Deprecated(EqDeprecation)
interface ByteEq : Eq<Byte> {
  override fun Byte.eqv(b: Byte): Boolean = this == b
}

@Deprecated(ShowDeprecation)
interface ByteShow : Show<Byte> {
  override fun Byte.show(): String = toString()
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Hash.byte()", "arrow.core.Hash", "arrow.core.byte"))
interface ByteHash : Hash<Byte>, ByteEq {
  override fun Byte.hash(): Int = hashCode()
}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Hash.byte()", "arrow.core.Hash", "arrow.core.byte"))
fun Byte.Companion.hash(): Hash<Byte> =
  object : ByteHash {}

@Deprecated(ShowDeprecation)
fun Byte.Companion.show(): Show<Byte> =
  object : ByteShow {}

@Deprecated(EqDeprecation)
fun Byte.Companion.eq(): Eq<Byte> =
  object : ByteEq {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Order.byte()", "arrow.core.Order", "arrow.core.byte"))
fun Byte.Companion.order(): Order<Byte> =
  object : ByteOrder {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Semigroup.byte()", "arrow.core.Semigroup", "arrow.core.byte"))
fun Byte.Companion.semigroup(): Semigroup<Byte> =
  object : ByteSemigroup {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Monoid.byte()", "arrow.core.Monoid", "arrow.core.byte"))
fun Byte.Companion.monoid(): Monoid<Byte> =
  object : ByteMonoid {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Semiring.byte()", "arrow.core.Semiring", "arrow.core.byte"))
fun Byte.Companion.semiring(): Semiring<Byte> =
  object : ByteSemiring {}

// ////////
// Double
// ////////
@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Semigroup.double()", "arrow.core.Semigroup", "arrow.core.double"))
interface DoubleSemigroup : Semigroup<Double> {
  override fun Double.combine(b: Double): Double = this + b
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Monoid.double()", "arrow.core.Monoid", "arrow.core.double"))
interface DoubleMonoid : Monoid<Double>, DoubleSemigroup {
  override fun empty(): Double = .0
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Semiring.double()", "arrow.core.Semiring", "arrow.core.double"))
interface DoubleSemiring : Semiring<Double> {
  override fun one(): Double = 1.0
  override fun zero(): Double = 0.0

  override fun Double.combine(b: Double): Double = this + b
  override fun Double.combineMultiplicate(b: Double): Double = this * b
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Order.double()", "arrow.core.Order", "arrow.core.double"))
interface DoubleOrder : Order<Double> {
  override fun Double.compare(b: Double): Ordering = Ordering.fromInt(this.compareTo(b))
  override fun Double.compareTo(b: Double): Int = this.compareTo(b)
}

@Deprecated(EqDeprecation)
interface DoubleEq : Eq<Double> {
  override fun Double.eqv(b: Double): Boolean = this == b
}

@Deprecated(ShowDeprecation)
interface DoubleShow : Show<Double> {
  override fun Double.show(): String = toString()
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Hash.double()", "arrow.core.Hash", "arrow.core.double"))
interface DoubleHash : Hash<Double>, DoubleEq {
  override fun Double.hash(): Int = hashCode()
}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Hash.double()", "arrow.core.Hash", "arrow.core.double"))
fun Double.Companion.hash(): Hash<Double> =
  object : DoubleHash {}

@Deprecated(ShowDeprecation)
fun Double.Companion.show(): Show<Double> =
  object : DoubleShow {}

@Deprecated(EqDeprecation)
fun Double.Companion.eq(): Eq<Double> =
  object : DoubleEq {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Order.double()", "arrow.core.Order", "arrow.core.double"))
fun Double.Companion.order(): Order<Double> =
  object : DoubleOrder {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Semigroup.double()", "arrow.core.Semigroup", "arrow.core.double"))
fun Double.Companion.semigroup(): Semigroup<Double> =
  object : DoubleSemigroup {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Monoid.double()", "arrow.core.Monoid", "arrow.core.double"))
fun Double.Companion.monoid(): Monoid<Double> =
  object : DoubleMonoid {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Semiring.double()", "arrow.core.Semiring", "arrow.core.double"))
fun Double.Companion.semiring(): Semiring<Double> =
  object : DoubleSemiring {}

// ////////
// Int
// ////////
@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Semigroup.int()", "arrow.core.Semigroup", "arrow.core.int"))
interface IntSemigroup : Semigroup<Int> {
  override fun Int.combine(b: Int): Int = this + b
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Monoid.int()", "arrow.core.Monoid", "arrow.core.int"))
interface IntMonoid : Monoid<Int>, IntSemigroup {
  override fun empty(): Int = 0
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Semiring.int()", "arrow.core.Semiring", "arrow.core.int"))
interface IntSemiring : Semiring<Int> {
  override fun one(): Int = 1
  override fun zero(): Int = 0

  override fun Int.combine(b: Int): Int = this + b
  override fun Int.combineMultiplicate(b: Int): Int = this * b
}

@Deprecated(EqDeprecation)
interface IntEq : Eq<Int> {
  override fun Int.eqv(b: Int): Boolean = this == b
}

@Deprecated(ShowDeprecation)
interface IntShow : Show<Int> {
  override fun Int.show(): String = toString()
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Order.int()", "arrow.core.Order", "arrow.core.int"))
interface IntOrder : Order<Int> {
  override fun Int.compare(b: Int): Ordering = Ordering.fromInt(this.compareTo(b))
  override fun Int.compareTo(b: Int): Int = this.compareTo(b)
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Hash.int()", "arrow.core.Hash", "arrow.core.int"))
interface IntHash : Hash<Int> {
  override fun Int.hash(): Int = hashCode()
}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Hash.int()", "arrow.core.Hash", "arrow.core.int"))
fun Int.Companion.hash(): Hash<Int> = object : IntHash {}

@Deprecated(ShowDeprecation)
fun Int.Companion.show(): Show<Int> =
  object : IntShow {}

@Deprecated(EqDeprecation)
fun Int.Companion.eq(): Eq<Int> =
  object : IntEq {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Order.int()", "arrow.core.Order", "arrow.core.int"))
fun Int.Companion.order(): Order<Int> =
  object : IntOrder {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Semigroup.int()", "arrow.core.Semigroup", "arrow.core.int"))
fun Int.Companion.semigroup(): Semigroup<Int> =
  object : IntSemigroup {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Monoid.int()", "arrow.core.Monoid", "arrow.core.int"))
fun Int.Companion.monoid(): Monoid<Int> =
  object : IntMonoid {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Semiring.int()", "arrow.core.Semiring", "arrow.core.int"))
fun Int.Companion.semiring(): Semiring<Int> =
  object : IntSemiring {}

// ////////
// Long
// ////////
@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Semigroup.long()", "arrow.core.Semigroup", "arrow.core.long"))
interface LongSemigroup : Semigroup<Long> {
  override fun Long.combine(b: Long): Long = this + b
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Monoid.long()", "arrow.core.Monoid", "arrow.core.long"))
interface LongMonoid : Monoid<Long>, LongSemigroup {
  override fun empty(): Long = 0L
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Semiring.long()", "arrow.core.Semiring", "arrow.core.long"))
interface LongSemiring : Semiring<Long> {
  override fun one(): Long = 1
  override fun zero(): Long = 0

  override fun Long.combine(b: Long): Long = this + b
  override fun Long.combineMultiplicate(b: Long): Long = this * b
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Order.long()", "arrow.core.Order", "arrow.core.long"))
interface LongOrder : Order<Long> {
  override fun Long.compare(b: Long): Ordering = Ordering.fromInt(this.compareTo(b))
  override fun Long.compareTo(b: Long): Int = this.compareTo(b)
}

@Deprecated(EqDeprecation)
interface LongEq : Eq<Long> {
  override fun Long.eqv(b: Long): Boolean = this == b
}

@Deprecated(ShowDeprecation)
interface LongShow : Show<Long> {
  override fun Long.show(): String = toString()
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Hash.long()", "arrow.core.Hash", "arrow.core.long"))
interface LongHash : Hash<Long>, LongEq {
  override fun Long.hash(): Int = hashCode()
}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Hash.long()", "arrow.core.Hash", "arrow.core.long"))
fun Long.Companion.hash(): Hash<Long> =
  object : LongHash {}

@Deprecated(ShowDeprecation)
fun Long.Companion.show(): Show<Long> =
  object : LongShow {}

@Deprecated(EqDeprecation)
fun Long.Companion.eq(): Eq<Long> =
  object : LongEq {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Order.long()", "arrow.core.Order", "arrow.core.long"))
fun Long.Companion.order(): Order<Long> =
  object : LongOrder {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Semigroup.long()", "arrow.core.Semigroup", "arrow.core.long"))
fun Long.Companion.semigroup(): Semigroup<Long> =
  object : LongSemigroup {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Monoid.long()", "arrow.core.Monoid", "arrow.core.long"))
fun Long.Companion.monoid(): Monoid<Long> =
  object : LongMonoid {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Semiring.long()", "arrow.core.Semiring", "arrow.core.long"))
fun Long.Companion.semiring(): Semiring<Long> =
  object : LongSemiring {}

// ////////
// Short
// ////////
@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Semigroup.short()", "arrow.core.Semigroup", "arrow.core.short"))
interface ShortSemigroup : Semigroup<Short> {
  override fun Short.combine(b: Short): Short = (this + b).toShort()
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Monoid.short()", "arrow.core.Monoid", "arrow.core.short"))
interface ShortMonoid : Monoid<Short>, ShortSemigroup {
  override fun empty(): Short = 0
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Semiring.short()", "arrow.core.Semiring", "arrow.core.short"))
interface ShortSemiring : Semiring<Short> {
  override fun one(): Short = 1
  override fun zero(): Short = 0

  override fun Short.combine(b: Short): Short = (this + b).toShort()
  override fun Short.combineMultiplicate(b: Short): Short = (this * b).toShort()
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Order.short()", "arrow.core.Order", "arrow.core.short"))
interface ShortOrder : Order<Short> {
  override fun Short.compare(b: Short): Ordering = Ordering.fromInt(this.compareTo(b))
  override fun Short.compareTo(b: Short): Int = this.compareTo(b)
}

@Deprecated(EqDeprecation)
interface ShortEq : Eq<Short> {
  override fun Short.eqv(b: Short): Boolean = this == b
}

@Deprecated(ShowDeprecation)
interface ShortShow : Show<Short> {
  override fun Short.show(): String = toString()
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Hash.short()", "arrow.core.Hash", "arrow.core.short"))
interface ShortHash : Hash<Short>, ShortEq {
  override fun Short.hash(): Int = hashCode()
}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Hash.short()", "arrow.core.Hash", "arrow.core.short"))
fun Short.Companion.hash(): Hash<Short> =
  object : ShortHash {}

@Deprecated(ShowDeprecation)
fun Short.Companion.show(): Show<Short> =
  object : ShortShow {}

@Deprecated(EqDeprecation)
fun Short.Companion.eq(): Eq<Short> =
  object : ShortEq {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Order.short()", "arrow.core.Order", "arrow.core.short"))
fun Short.Companion.order(): Order<Short> =
  object : ShortOrder {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Semigroup.short()", "arrow.core.Semigroup", "arrow.core.short"))
fun Short.Companion.semigroup(): Semigroup<Short> =
  object : ShortSemigroup {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Monoid.short()", "arrow.core.Monoid", "arrow.core.short"))
fun Short.Companion.monoid(): Monoid<Short> =
  object : ShortMonoid {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Semiring.short()", "arrow.core.Semiring", "arrow.core.short"))
fun Short.Companion.semiring(): Semiring<Short> =
  object : ShortSemiring {}

// ////////
// Float
// ////////
@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Semigroup.float()", "arrow.core.Semigroup", "arrow.core.float"))
interface FloatSemigroup : Semigroup<Float> {
  override fun Float.combine(b: Float): Float = this + b
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Monoid.float()", "arrow.core.Monoid", "arrow.core.float"))
interface FloatMonoid : Monoid<Float>, FloatSemigroup {
  override fun empty(): Float = 0f
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Semiring.float()", "arrow.core.Semiring", "arrow.core.float"))
interface FloatSemiring : Semiring<Float> {
  override fun one(): Float = 1f
  override fun zero(): Float = 0f

  override fun Float.combine(b: Float): Float = this + b
  override fun Float.combineMultiplicate(b: Float): Float = this * b
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Order.float()", "arrow.core.Order", "arrow.core.float"))
interface FloatOrder : Order<Float> {
  override fun Float.compare(b: Float): Ordering = Ordering.fromInt(this.compareTo(b))
  override fun Float.compareTo(b: Float): Int = this.compareTo(b)
}

@Deprecated(EqDeprecation)
interface FloatEq : Eq<Float> {
  override fun Float.eqv(b: Float): Boolean = this == b
}

@Deprecated(ShowDeprecation)
interface FloatShow : Show<Float> {
  override fun Float.show(): String = toString()
}

@Deprecated("Typeclass interface implementation will not be exposed directly anymore", ReplaceWith("Hash.float()", "arrow.core.Hash", "arrow.core.float"))
interface FloatHash : Hash<Float>, FloatEq {
  override fun Float.hash(): Int = hashCode()
}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Hash.float()", "arrow.core.Hash", "arrow.core.float"))
fun Float.Companion.hash(): Hash<Float> =
  object : FloatHash {}

@Deprecated(ShowDeprecation)
fun Float.Companion.show(): Show<Float> =
  object : FloatShow {}

@Deprecated(EqDeprecation)
fun Float.Companion.eq(): Eq<Float> =
  object : FloatEq {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Order.float()", "arrow.core.Order", "arrow.core.float"))
fun Float.Companion.order(): Order<Float> =
  object : FloatOrder {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Semigroup.float()", "arrow.core.Semigroup", "arrow.core.float"))
fun Float.Companion.semigroup(): Semigroup<Float> =
  object : FloatSemigroup {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Monoid.float()", "arrow.core.Monoid", "arrow.core.float"))
fun Float.Companion.monoid(): Monoid<Float> =
  object : FloatMonoid {}

@Deprecated("Typeclass instance have been moved to the companion object of the typeclass", ReplaceWith("Semiring.float()", "arrow.core.Semiring", "arrow.core.float"))
fun Float.Companion.semiring(): Semiring<Float> =
  object : FloatSemiring {}
