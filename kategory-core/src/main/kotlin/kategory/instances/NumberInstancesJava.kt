package java_lang

import kategory.IntEqInstance
import kategory.IntMonoid
import kategory.Semigroup

object IntegerMonoidInstanceImplicits {
    @JvmStatic fun instance(): IntMonoid = IntMonoid
}

object IntegerSemigroupInstanceImplicits {
    @JvmStatic fun instance(): Semigroup<Int> = IntMonoid
}

object IntegerEqInstanceImplicits {
    @JvmStatic fun instance(): IntEqInstance = IntEqInstance
}