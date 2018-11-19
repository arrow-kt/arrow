package arrow.instances

import arrow.deprecation.ExtensionsDSLDeprecated
import arrow.typeclasses.*

//////////
// Byte
//////////
interface ByteSemigroupInstance : Semigroup<Byte> {
  override fun Byte.combine(b: Byte): Byte = (this + b).toByte()
}

interface ByteMonoidInstance : Monoid<Byte>, ByteSemigroupInstance {
  override fun empty(): Byte = 0
}

interface ByteOrderInstance : Order<Byte> {
  override fun Byte.compare(b: Byte): Int = compareTo(b)
}

interface ByteEqInstance : Eq<Byte> {
  override fun Byte.eqv(b: Byte): Boolean = this == b
}

interface ByteShowInstance : Show<Byte> {
  override fun Byte.show(): String = toString()
}

interface ByteHashInstance : Hash<Byte>, ByteEqInstance {
  override fun Byte.hash(): Int = hashCode()
}

fun Byte.Companion.hash(): Hash<Byte> =
  object : ByteHashInstance {}

fun Byte.Companion.show(): Show<Byte> =
  object : ByteShowInstance {}

fun Byte.Companion.eq(): Eq<Byte> =
  object : ByteEqInstance {}

fun Byte.Companion.order(): Order<Byte> =
  object : ByteOrderInstance {}

fun Byte.Companion.semigroup(): Semigroup<Byte> =
  object : ByteSemigroupInstance {}

fun Byte.Companion.monoid(): Monoid<Byte> =
  object : ByteMonoidInstance {}

object ByteContext : ByteShowInstance, ByteOrderInstance, ByteMonoidInstance

object ForByte {
  @Deprecated(ExtensionsDSLDeprecated)
  infix fun <L> extensions(f: ByteContext.() -> L): L =
    f(ByteContext)
}

//////////
// Double
//////////

interface DoubleSemigroupInstance : Semigroup<Double> {
  override fun Double.combine(b: Double): Double = this + b
}

interface DoubleMonoidInstance : Monoid<Double>, DoubleSemigroupInstance {
  override fun empty(): Double = .0
}

interface DoubleOrderInstance : Order<Double> {
  override fun Double.compare(b: Double): Int = compareTo(b)
}

interface DoubleEqInstance : Eq<Double> {
  override fun Double.eqv(b: Double): Boolean = this == b
}

interface DoubleShowInstance : Show<Double> {
  override fun Double.show(): String = toString()
}

interface DoubleHashInstance : Hash<Double>, DoubleEqInstance {
  override fun Double.hash(): Int = hashCode()
}

fun Double.Companion.hash(): Hash<Double> =
  object : DoubleHashInstance {}

fun Double.Companion.show(): Show<Double> =
  object : DoubleShowInstance {}

fun Double.Companion.eq(): Eq<Double> =
  object : DoubleEqInstance {}

fun Double.Companion.order(): Order<Double> =
  object : DoubleOrderInstance {}

fun Double.Companion.semigroup(): Semigroup<Double> =
  object : DoubleSemigroupInstance {}

fun Double.Companion.monoid(): Monoid<Double> =
  object : DoubleMonoidInstance {}

object DoubleContext : DoubleShowInstance, DoubleOrderInstance, DoubleMonoidInstance

object ForDouble {
  @Deprecated(ExtensionsDSLDeprecated)

  infix fun <L> extensions(f: DoubleContext.() -> L): L =
    f(DoubleContext)
}

//////////
// Int
//////////
interface IntSemigroupInstance : Semigroup<Int> {
  override fun Int.combine(b: Int): Int = this + b
}

interface IntMonoidInstance : Monoid<Int>, IntSemigroupInstance {
  override fun empty(): Int = 0
}

interface IntEqInstance : Eq<Int> {
  override fun Int.eqv(b: Int): Boolean = this == b
}

interface IntShowInstance : Show<Int> {
  override fun Int.show(): String = toString()
}

interface IntOrderInstance : Order<Int> {
  override fun Int.compare(b: Int): Int = compareTo(b)
}

interface IntHashInstance : Hash<Int>, IntEqInstance {
  override fun Int.hash(): Int = hashCode()
}

fun Int.Companion.hash(): Hash<Int> =
  object : IntHashInstance {}

fun Int.Companion.show(): Show<Int> =
  object : IntShowInstance {}

fun Int.Companion.eq(): Eq<Int> =
  object : IntEqInstance {}

fun Int.Companion.order(): Order<Int> =
  object : IntOrderInstance {}

fun Int.Companion.semigroup(): Semigroup<Int> =
  object : IntSemigroupInstance {}

fun Int.Companion.monoid(): Monoid<Int> =
  object : IntMonoidInstance {}

object IntContext : IntShowInstance, IntOrderInstance, IntMonoidInstance

object ForInt {
  @Deprecated(ExtensionsDSLDeprecated)
  infix fun <L> extensions(f: IntContext.() -> L): L =
    f(IntContext)
}

//////////
// Long
//////////

interface LongSemigroupInstance : Semigroup<Long> {
  override fun Long.combine(b: Long): Long = this + b
}

interface LongMonoidInstance : Monoid<Long>, LongSemigroupInstance {
  override fun empty(): Long = 0L
}

interface LongOrderInstance : Order<Long> {
  override fun Long.compare(b: Long): Int = compareTo(b)
}

interface LongEqInstance : Eq<Long> {
  override fun Long.eqv(b: Long): Boolean = this == b
}

interface LongShowInstance : Show<Long> {
  override fun Long.show(): String = toString()
}

interface LongHashInstance : Hash<Long>, LongEqInstance {
  override fun Long.hash(): Int = hashCode()
}

fun Long.Companion.hash(): Hash<Long> =
  object : LongHashInstance {}

fun Long.Companion.show(): Show<Long> =
  object : LongShowInstance {}

fun Long.Companion.eq(): Eq<Long> =
  object : LongEqInstance {}

fun Long.Companion.order(): Order<Long> =
  object : LongOrderInstance {}

fun Long.Companion.semigroup(): Semigroup<Long> =
  object : LongSemigroupInstance {}

fun Long.Companion.monoid(): Monoid<Long> =
  object : LongMonoidInstance {}

object LongContext : LongShowInstance, LongOrderInstance, LongMonoidInstance

object ForLong {
  @Deprecated(ExtensionsDSLDeprecated)
  infix fun <L> extensions(f: LongContext.() -> L): L =
    f(LongContext)
}

//////////
// Short
//////////

interface ShortSemigroupInstance : Semigroup<Short> {
  override fun Short.combine(b: Short): Short = (this + b).toShort()
}

interface ShortMonoidInstance : Monoid<Short>, ShortSemigroupInstance {
  override fun empty(): Short = 0
}

interface ShortOrderInstance : Order<Short> {
  override fun Short.compare(b: Short): Int = compareTo(b)
}

interface ShortEqInstance : Eq<Short> {
  override fun Short.eqv(b: Short): Boolean = this == b
}

interface ShortShowInstance : Show<Short> {
  override fun Short.show(): String = toString()
}

interface ShortHashInstance : Hash<Short>, ShortEqInstance {
  override fun Short.hash(): Int = hashCode()
}

fun Short.Companion.hash(): Hash<Short> =
  object : ShortHashInstance {}

fun Short.Companion.show(): Show<Short> =
  object : ShortShowInstance {}

fun Short.Companion.eq(): Eq<Short> =
  object : ShortEqInstance {}

fun Short.Companion.order(): Order<Short> =
  object : ShortOrderInstance {}

fun Short.Companion.semigroup(): Semigroup<Short> =
  object : ShortSemigroupInstance {}

fun Short.Companion.monoid(): Monoid<Short> =
  object : ShortMonoidInstance {}

object ShortContext : ShortShowInstance, ShortOrderInstance, ShortMonoidInstance

object ForShort {
  @Deprecated(ExtensionsDSLDeprecated)
  infix fun <L> extensions(f: ShortContext.() -> L): L =
    f(ShortContext)
}

//////////
// Float
//////////

interface FloatSemigroupInstance : Semigroup<Float> {
  override fun Float.combine(b: Float): Float = this + b
}

interface FloatMonoidInstance : Monoid<Float>, FloatSemigroupInstance {
  override fun empty(): Float = 0f
}

interface FloatOrderInstance : Order<Float> {
  override fun Float.compare(b: Float): Int = compareTo(b)
}

interface FloatEqInstance : Eq<Float> {
  override fun Float.eqv(b: Float): Boolean = this == b
}

interface FloatShowInstance : Show<Float> {
  override fun Float.show(): String = toString()
}

interface FloatHashInstance : Hash<Float>, FloatEqInstance {
  override fun Float.hash(): Int = hashCode()
}

fun Float.Companion.hash(): Hash<Float> =
  object : FloatHashInstance {}

fun Float.Companion.show(): Show<Float> =
  object : FloatShowInstance {}

fun Float.Companion.eq(): Eq<Float> =
  object : FloatEqInstance {}

fun Float.Companion.order(): Order<Float> =
  object : FloatOrderInstance {}

fun Float.Companion.semigroup(): Semigroup<Float> =
  object : FloatSemigroupInstance {}

fun Float.Companion.monoid(): Monoid<Float> =
  object : FloatMonoidInstance {}

object FloatContext : FloatShowInstance, FloatOrderInstance, FloatMonoidInstance

object ForFloat {
  @Deprecated(ExtensionsDSLDeprecated)
  infix fun <L> extensions(f: FloatContext.() -> L): L =
    f(FloatContext)
}