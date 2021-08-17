@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public data class Tuple5<out A, out B, out C, out D, out E>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D,
  val fifth: E
) {

  override fun toString(): String =
    "($first, $second, $third, $fourth, $fifth)"

  public companion object
}

public operator fun <A : Comparable<A>, B : Comparable<B>, C : Comparable<C>, D : Comparable<D>, E : Comparable<E>>
Tuple5<A, B, C, D, E>.compareTo(other: Tuple5<A, B, C, D, E>): Int {
  val first = first.compareTo(other.first)
  return if (first == 0) {
    val second = second.compareTo(other.second)
    if (second == 0) {
      val third = third.compareTo(other.third)
      if (third == 0) {
        val fourth = fourth.compareTo(other.fourth)
        if (fourth == 0) fifth.compareTo(other.fifth)
        else fourth
      } else third
    } else second
  } else first
}
