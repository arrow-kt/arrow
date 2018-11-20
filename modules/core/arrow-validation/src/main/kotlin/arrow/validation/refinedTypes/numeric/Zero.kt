@file:Suppress("UNCHECKED_CAST")

package arrow.validation.refinedTypes.numeric

object Zero {
  fun <A : Number> value(): A = 0 as A
}