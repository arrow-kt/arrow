@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

data class Tuple4<out A, out B, out C, out D>(
  @Deprecated("Use first instead", ReplaceWith("first"))
  val a: A,
  @Deprecated("Use second instead", ReplaceWith("second"))
  val b: B,
  @Deprecated("Use third instead", ReplaceWith("third"))
  val c: C,
  @Deprecated("Use fourth instead", ReplaceWith("fourth"))
  val d: D
) {

  val first: A = a
  val second: B = b
  val third: C = c
  val fourth: D = d

  override fun toString(): String =
    "($a, $b, $c, $d)"

  companion object
}

operator fun <A : Comparable<A>, B : Comparable<B>, C : Comparable<C>, D : Comparable<D>> Tuple4<A, B, C, D>.compareTo(other: Tuple4<A, B, C, D>): Int {
  val first = a.compareTo(other.a)
  return if (first == 0) {
    val second = b.compareTo(other.b)
    if (second == 0) {
      val third = c.compareTo(other.c)
      if (third == 0) d.compareTo(other.d)
      else third
    } else second
  } else first
}
