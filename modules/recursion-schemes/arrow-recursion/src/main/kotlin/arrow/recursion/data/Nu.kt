package arrow.recursion.data

import arrow.higherkind
import arrow.recursion.Coalgebra

/**
 * Type level combinator for obtaining the greatest fixed point of a type.
 * This type is the type level encoding of ana.
 */
@higherkind
class Nu<out F>(val a: Any?, val unNu: Coalgebra<F, Any?>) : NuOf<F> {
  companion object {
    // Necessary because of Coalgebra's variance
    @Suppress("UNCHECKED_CAST", "ExplicitItLambdaParameter")
    operator fun <F, A> invoke(a: A, unNu: Coalgebra<F, A>) = Nu(a) { it -> unNu(it as A) }
  }
}
