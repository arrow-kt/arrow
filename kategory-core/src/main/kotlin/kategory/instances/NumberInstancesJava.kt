package java_lang

import kategory.*

object IntegerMonoidInstanceImplicits {
    @JvmStatic fun instance(): IntMonoid = IntMonoid
}

object IntegerSemigroupInstanceImplicits {
    @JvmStatic fun instance(): Semigroup<Int> = IntMonoid
}

object IntegerEqInstanceImplicits {
    @JvmStatic fun instance(): IntEqInstance = IntEqInstance
}

object IntegerOrderInstanceImplicits {
    @JvmStatic fun instance(): IntOrderInstance = IntOrderInstance
}

object LongMonoidInstanceImplicits {
    @JvmStatic fun instance(): LongMonoid = LongMonoid
}

object LongSemigroupInstanceImplicits {
    @JvmStatic fun instance(): Semigroup<Long> = LongMonoid
}

object LongOrderInstanceImplicits {
    @JvmStatic fun instance(): LongOrderInstance = LongOrderInstance
}

object LongEqInstanceImplicits {
    @JvmStatic fun instance(): LongEqInstance = LongEqInstance
}

object ShortMonoidInstanceImplicits {
    @JvmStatic fun instance(): ShortMonoid = ShortMonoid
}

object ShortSemigroupInstanceImplicits {
    @JvmStatic fun instance(): Semigroup<Short> = ShortMonoid
}

object ShortOrderInstanceImplicits {
    @JvmStatic fun instance(): ShortOrderInstance = ShortOrderInstance
}

object ShortEqInstanceImplicits {
    @JvmStatic fun instance(): ShortEqInstance = ShortEqInstance
}

object ByteMonoidInstanceImplicits {
    @JvmStatic fun instance(): ByteMonoid = ByteMonoid
}

object ByteSemigroupInstanceImplicits {
    @JvmStatic fun instance(): Semigroup<Byte> = ByteMonoid
}

object ByteOrderInstanceImplicits {
    @JvmStatic fun instance(): ByteOrderInstance = ByteOrderInstance
}

object ByteEqInstanceImplicits {
    @JvmStatic fun instance(): ByteEqInstance = ByteEqInstance
}

object DoubleMonoidInstanceImplicits {
    @JvmStatic fun instance(): DoubleMonoid = DoubleMonoid
}

object DoubleSemigroupInstanceImplicits {
    @JvmStatic fun instance(): Semigroup<Double> = DoubleMonoid
}

object DoubleOrderInstanceImplicits {
    @JvmStatic fun instance(): DoubleOrderInstance = DoubleOrderInstance
}

object DoubleEqInstanceImplicits {
    @JvmStatic fun instance(): DoubleEqInstance = DoubleEqInstance
}

object FloatMonoidInstanceImplicits {
    @JvmStatic fun instance(): FloatMonoid = FloatMonoid
}

object FloatSemigroupInstanceImplicits {
    @JvmStatic fun instance(): Semigroup<Float> = FloatMonoid
}

object FloatOrderInstanceImplicits {
    @JvmStatic fun instance(): FloatOrderInstance = FloatOrderInstance
}

object FloatEqInstanceImplicits {
    @JvmStatic fun instance(): FloatEqInstance = FloatEqInstance
}
