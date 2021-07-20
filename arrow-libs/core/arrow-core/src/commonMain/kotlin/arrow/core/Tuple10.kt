@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public data class Tuple10<out A, out B, out C, out D, out E, out F, out G, out H, out I, out J>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D,
  val fifth: E,
  val sixth: F,
  val seventh: G,
  val eighth: H,
  val ninth: I,
  val tenth: J
) {

  override fun toString(): String =
    "($first, $second, $third, $fourth, $fifth, $sixth, $seventh, $eighth, $ninth, $tenth)"

  public companion object
}

public operator fun <A : Comparable<A>, B : Comparable<B>, C : Comparable<C>, D : Comparable<D>, E : Comparable<E>, F : Comparable<F>, G : Comparable<G>, H : Comparable<H>, I : Comparable<I>, J : Comparable<J>>
Tuple10<A, B, C, D, E, F, G, H, I, J>.compareTo(other: Tuple10<A, B, C, D, E, F, G, H, I, J>): Int {
  val first = first.compareTo(other.first)
  return if (first == 0) {
    val second = second.compareTo(other.second)
    if (second == 0) {
      val third = third.compareTo(other.third)
      if (third == 0) {
        val fourth = fourth.compareTo(other.fourth)
        if (fourth == 0) {
          val fifth = fifth.compareTo(other.fifth)
          if (fifth == 0) {
            val sixth = sixth.compareTo(other.sixth)
            if (sixth == 0) {
              val seventh = seventh.compareTo(other.seventh)
              if (seventh == 0) {
                val eighth = eighth.compareTo(other.eighth)
                if (eighth == 0) {
                  val ninth = ninth.compareTo(other.ninth)
                  if (ninth == 0) tenth.compareTo(other.tenth)
                  else ninth
                } else eighth
              } else seventh
            } else sixth
          } else fifth
        } else fourth
      } else third
    } else second
  } else first
}
