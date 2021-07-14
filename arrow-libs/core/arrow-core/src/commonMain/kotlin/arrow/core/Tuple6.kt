@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public data class Tuple6<out A, out B, out C, out D, out E, out F>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D,
  val fifth: E,
  val sixth: F
) {

  override fun toString(): String =
    "($first, $second, $third, $fourth, $fifth, $sixth)"

  public companion object
}

public operator fun <A : Comparable<A>, B : Comparable<B>, C : Comparable<C>, D : Comparable<D>, E : Comparable<E>, F : Comparable<F>>
Tuple6<A, B, C, D, E, F>.compareTo(other: Tuple6<A, B, C, D, E, F>): Int {
  val first = first.compareTo(other.first)
  return if (first == 0) {
    val second = second.compareTo(other.second)
    if (second == 0) {
      val third = third.compareTo(other.third)
      if (third == 0) {
        val fourth = fourth.compareTo(other.fourth)
        if (fourth == 0) {
          val fifth = fifth.compareTo(other.fifth)
          if (fifth == 0) sixth.compareTo(other.sixth)
          else fifth
        } else fourth
      } else third
    } else second
  } else first
}
