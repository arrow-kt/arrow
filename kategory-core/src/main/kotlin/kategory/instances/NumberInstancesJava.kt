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

object LongMonoidInstanceImplicits {
    @JvmStatic fun instance(): LongMonoid = LongMonoid
}

object LongSemigroupInstanceImplicits {
    @JvmStatic fun instance(): Semigroup<Long> = LongMonoid
}

object ShortMonoidInstanceImplicits {
    @JvmStatic fun instance(): ShortMonoid = ShortMonoid
}

object ShortSemigroupInstanceImplicits {
    @JvmStatic fun instance(): Semigroup<Short> = ShortMonoid
}

object ByteMonoidInstanceImplicits {
    @JvmStatic fun instance(): ByteMonoid = ByteMonoid
}

object ByteSemigroupInstanceImplicits {
    @JvmStatic fun instance(): Semigroup<Byte> = ByteMonoid
}

object DoubleMonoidInstanceImplicits {
    @JvmStatic fun instance(): DoubleMonoid = DoubleMonoid
}

object DoubleSemigroupInstanceImplicits {
    @JvmStatic fun instance(): Semigroup<Double> = DoubleMonoid
}

object FloatMonoidInstanceImplicits {
    @JvmStatic fun instance(): FloatMonoid = FloatMonoid
}

object FloatSemigroupInstanceImplicits {
    @JvmStatic fun instance(): Semigroup<Float> = FloatMonoid
}
