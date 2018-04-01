package arrow.instances

import arrow.instance
import arrow.typeclasses.*

//////////
// Byte
//////////

@instance(Byte::class)
interface ByteSemigroupInstance : Semigroup<Byte> {
    override fun Byte.combine(b: Byte): Byte = (this + b).toByte()
}

fun Byte.Companion.semigroup(): Semigroup<Byte> =
        lazyOf(object: ByteSemigroupInstance {}).value

object ByteMonoidInstance : Monoid<Byte>, ByteSemigroupInstance {
    override fun empty(): Byte = 0
}

fun Byte.Companion.monoid(): Monoid<Byte> = ByteMonoidInstance

@instance(Byte::class)
interface ByteOrderInstance : Order<Byte> {
    override fun Byte.compare(b: Byte): Int = compareTo(b)
}

fun Byte.Companion.order(): Order<Byte> =
        lazyOf(object: ByteOrderInstance {}).value

@instance(Byte::class)
interface ByteEqInstance : Eq<Byte> {
    override fun Byte.eqv(b: Byte): Boolean = this == b
}

@instance(Byte::class)
interface ByteShowInstance : Show<Byte> {
    override fun Byte.show(): String = toString()
}

//////////
// Double
//////////

@instance(Double::class)
interface DoubleSemigroupInstance : Semigroup<Double> {
    override fun Double.combine(b: Double): Double = this + b
}

@instance(Double::class)
interface DoubleMonoidInstance : Monoid<Double>, DoubleSemigroupInstance {
    override fun empty(): Double = .0
}

@instance(Double::class)
interface DoubleOrderInstance : Order<Double> {
    override fun Double.compare(b: Double): Int = compareTo(b)
}

@instance(Double::class)
interface DoubleEqInstance : Eq<Double> {
    override fun Double.eqv(b: Double): Boolean = this == b
}

@instance(Double::class)
interface DoubleShowInstance : Show<Double> {
    override fun Double.show(): String = toString()
}

//////////
// Int
//////////
@instance(Int::class)
interface IntSemigroupInstance : Semigroup<Int> {
    override fun Int.combine(b: Int): Int = this + b
}

@instance(Int::class)
interface IntMonoidInstance : Monoid<Int>, IntSemigroupInstance {
    override fun empty(): Int = 0
}

@instance(Int::class)
interface IntEqInstance : Eq<Int> {
    override fun Int.eqv(b: Int): Boolean = this == b
}

@instance(Int::class)
interface IntShowInstance : Show<Int> {
    override fun Int.show(): String = toString()
}

@instance(Int::class)
interface IntOrderInstance : Order<Int> {
    override fun Int.compare(b: Int): Int = compareTo(b)
}

//////////
// Long
//////////

@instance(Long::class)
interface LongSemigroupInstance : Semigroup<Long> {
    override fun Long.combine(b: Long): Long = this + b
}

@instance(Long::class)
interface LongMonoidInstance : Monoid<Long>, LongSemigroupInstance {
    override fun empty(): Long = 0L
}

@instance(Long::class)
interface  LongOrderInstance : Order<Long> {
    override fun Long.compare(b: Long): Int = compareTo(b)
}

@instance(Long::class)
interface  LongEqInstance : Eq<Long> {
    override fun Long.eqv(b: Long): Boolean = this == b
}

@instance(Long::class)
interface  LongShowInstance : Show<Long> {
    override fun Long.show(): String = toString()
}

//////////
// Short
//////////

interface ShortSemigroupInstance : Semigroup<Short> {
    override fun Short.combine(b: Short): Short = (this + b).toShort()
}

fun Short.Companion.semigroup(): Semigroup<Short> = lazyOf(object: ShortSemigroupInstance {}).value

@instance(Short::class)
interface ShortMonoidInstance : Monoid<Short>, ShortSemigroupInstance {
    override fun empty(): Short = 0
}

@instance(Short::class)
interface  ShortOrderInstance : Order<Short> {
    override fun Short.compare(b: Short): Int = compareTo(b)
}

@instance(Short::class)
interface  ShortEqInstance : Eq<Short> {
    override fun Short.eqv(b: Short): Boolean = this == b
}

@instance(Short::class)
interface  ShortShowInstance : Show<Short> {
    override fun Short.show(): String = toString()
}

//////////
// Float
//////////

@instance(Float::class)
interface FloatSemigroupInstance : Semigroup<Float> {
    override fun Float.combine(b: Float): Float = this + b
}

@instance(Float::class)
interface FloatMonoidInstance : Monoid<Float>, FloatSemigroupInstance {
    override fun empty(): Float = 0f
}

@instance(Float::class)
interface  FloatOrderInstance : Order<Float> {
    override fun Float.compare(b: Float): Int = compareTo(b)
}

@instance(Float::class)
interface  FloatEqInstance : Eq<Float> {
    override fun Float.eqv(b: Float): Boolean = this == b
}

@instance(Float::class)
interface  FloatShowInstance : Show<Float> {
    override fun Float.show(): String = toString()
}
