package java_lang

import arrow.instances.*
import arrow.typeclasses.Semigroup

object IntegerMonoidInstanceImplicits {
    fun instance(): IntMonoid = IntMonoid
}

object IntegerSemigroupInstanceImplicits {
    fun instance(): Semigroup<Int> = IntMonoid
}

object IntegerEqInstanceImplicits {
    fun instance(): IntEqInstance = IntEqInstance
}

object IntegerHashInstanceImplicits {
    fun instance(): IntHashInstance = IntHashInstance
}

object IntegerShowInstanceImplicits {
    fun instance(): IntShowInstance = IntShowInstance
}

object IntegerOrderInstanceImplicits {
    fun instance(): IntOrderInstance = IntOrderInstance
}

object LongMonoidInstanceImplicits {
    fun instance(): LongMonoid = LongMonoid
}

object LongSemigroupInstanceImplicits {
    fun instance(): Semigroup<Long> = LongMonoid
}

object LongOrderInstanceImplicits {
    fun instance(): LongOrderInstance = LongOrderInstance
}

object LongEqInstanceImplicits {
    fun instance(): LongEqInstance = LongEqInstance
}

object LongHashInstanceImplicits {
    fun instance(): LongHashInstance = LongHashInstance
}

object LongShowInstanceImplicits {
    fun instance(): LongShowInstance = LongShowInstance
}

object ShortMonoidInstanceImplicits {
    fun instance(): ShortMonoid = ShortMonoid
}

object ShortSemigroupInstanceImplicits {
    fun instance(): Semigroup<Short> = ShortMonoid
}

object ShortOrderInstanceImplicits {
    fun instance(): ShortOrderInstance = ShortOrderInstance
}

object ShortEqInstanceImplicits {
    fun instance(): ShortEqInstance = ShortEqInstance
}

object ShortHashInstanceImplicits {
    fun instance(): ShortHashInstance = ShortHashInstance
}

object ShortShowInstanceImplicits {
    fun instance(): ShortShowInstance = ShortShowInstance
}

object ByteMonoidInstanceImplicits {
    fun instance(): ByteMonoid = ByteMonoid
}

object ByteSemigroupInstanceImplicits {
    fun instance(): Semigroup<Byte> = ByteMonoid
}

object ByteOrderInstanceImplicits {
    fun instance(): ByteOrderInstance = ByteOrderInstance
}

object ByteEqInstanceImplicits {
    fun instance(): ByteEqInstance = ByteEqInstance
}

object ByteHashInstanceImplicits {
    fun instance(): ByteHashInstance = ByteHashInstance
}

object ByteShowInstanceImplicits {
    fun instance(): ByteShowInstance = ByteShowInstance
}

object DoubleMonoidInstanceImplicits {
    fun instance(): DoubleMonoid = DoubleMonoid
}

object DoubleSemigroupInstanceImplicits {
    fun instance(): Semigroup<Double> = DoubleMonoid
}

object DoubleOrderInstanceImplicits {
    fun instance(): DoubleOrderInstance = DoubleOrderInstance
}

object DoubleEqInstanceImplicits {
    fun instance(): DoubleEqInstance = DoubleEqInstance
}

object DoubleHashInstanceImplicits {
    fun instance(): DoubleHashInstance = DoubleHashInstance
}

object DoubleShowInstanceImplicits {
    fun instance(): DoubleShowInstance = DoubleShowInstance
}

object FloatMonoidInstanceImplicits {
    fun instance(): FloatMonoid = FloatMonoid
}

object FloatSemigroupInstanceImplicits {
    fun instance(): Semigroup<Float> = FloatMonoid
}

object FloatOrderInstanceImplicits {
    fun instance(): FloatOrderInstance = FloatOrderInstance
}

object FloatEqInstanceImplicits {
    fun instance(): FloatEqInstance = FloatEqInstance
}

object FloatHashInstanceImplicits {
    fun instance(): FloatHashInstance = FloatHashInstance
}

object FloatShowInstanceImplicits {
    fun instance(): FloatShowInstance = FloatShowInstance
}
