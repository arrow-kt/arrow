@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public data class Tuple4<out A, out B, out C, out D>(
  val first: A,
  val second: B,
  val third: C,
  val fourth: D
) {

  override fun toString(): String =
    "($first, $second, $third, $fourth)"

  public companion object
}

public operator fun <A : Comparable<A>, B : Comparable<B>, C : Comparable<C>, D : Comparable<D>> Tuple4<A, B, C, D>.compareTo(other: Tuple4<A, B, C, D>): Int {
  val first = first.compareTo(other.first)
  return if (first == 0) {
    val second = second.compareTo(other.second)
    if (second == 0) {
      val third = third.compareTo(other.third)
      if (third == 0) fourth.compareTo(other.fourth)
      else third
    } else second
  } else first
}
