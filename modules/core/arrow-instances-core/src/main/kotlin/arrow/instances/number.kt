package arrow.instances

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

fun Byte.Companion.show(): Show<Byte> =
        lazyOf(object : ByteShowInstance {}).value

fun Byte.Companion.eq(): Eq<Byte> =
        lazyOf(object : ByteEqInstance {}).value

fun Byte.Companion.order(): Order<Byte> =
        lazyOf(object : ByteOrderInstance {}).value

fun Byte.Companion.semigroup(): Semigroup<Byte> =
        lazyOf(object : ByteSemigroupInstance {}).value

fun Byte.Companion.monoid(): Monoid<Byte> =
        lazyOf(object : ByteMonoidInstance {}).value

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

fun Double.Companion.show(): Show<Double> =
        lazyOf(object : DoubleShowInstance {}).value

fun Double.Companion.eq(): Eq<Double> =
        lazyOf(object : DoubleEqInstance {}).value

fun Double.Companion.order(): Order<Double> =
        lazyOf(object : DoubleOrderInstance {}).value

fun Double.Companion.semigroup(): Semigroup<Double> =
        lazyOf(object : DoubleSemigroupInstance {}).value

fun Double.Companion.monoid(): Monoid<Double> =
        lazyOf(object : DoubleMonoidInstance {}).value

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

fun Int.Companion.show(): Show<Int> =
        lazyOf(object : IntShowInstance {}).value

fun Int.Companion.eq(): Eq<Int> =
        lazyOf(object : IntEqInstance {}).value

fun Int.Companion.order(): Order<Int> =
        lazyOf(object : IntOrderInstance {}).value

fun Int.Companion.semigroup(): Semigroup<Int> =
        lazyOf(object : IntSemigroupInstance {}).value

fun Int.Companion.monoid(): Monoid<Int> =
        lazyOf(object : IntMonoidInstance {}).value

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

fun Long.Companion.show(): Show<Long> =
        lazyOf(object : LongShowInstance {}).value

fun Long.Companion.eq(): Eq<Long> =
        lazyOf(object : LongEqInstance {}).value

fun Long.Companion.order(): Order<Long> =
        lazyOf(object : LongOrderInstance {}).value

fun Long.Companion.semigroup(): Semigroup<Long> =
        lazyOf(object : LongSemigroupInstance {}).value

fun Long.Companion.monoid(): Monoid<Long> =
        lazyOf(object : LongMonoidInstance {}).value

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

fun Short.Companion.show(): Show<Short> =
        lazyOf(object : ShortShowInstance {}).value

fun Short.Companion.eq(): Eq<Short> =
        lazyOf(object : ShortEqInstance {}).value

fun Short.Companion.order(): Order<Short> =
        lazyOf(object : ShortOrderInstance {}).value

fun Short.Companion.semigroup(): Semigroup<Short> =
        lazyOf(object : ShortSemigroupInstance {}).value

fun Short.Companion.monoid(): Monoid<Short> =
        lazyOf(object : ShortMonoidInstance {}).value

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

fun Float.Companion.show(): Show<Float> =
        lazyOf(object : FloatShowInstance {}).value

fun Float.Companion.eq(): Eq<Float> =
        lazyOf(object : FloatEqInstance {}).value

fun Float.Companion.order(): Order<Float> =
        lazyOf(object : FloatOrderInstance {}).value

fun Float.Companion.semigroup(): Semigroup<Float> =
        lazyOf(object : FloatSemigroupInstance {}).value

fun Float.Companion.monoid(): Monoid<Float> =
        lazyOf(object : FloatMonoidInstance {}).value
