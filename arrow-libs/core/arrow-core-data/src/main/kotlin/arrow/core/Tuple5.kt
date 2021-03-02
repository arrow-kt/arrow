@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

data class Tuple5<out A, out B, out C, out D, out E>(
  @Deprecated("Use first instead", ReplaceWith("first"))
  val a: A,
  @Deprecated("Use second instead", ReplaceWith("second"))
  val b: B,
  @Deprecated("Use third instead", ReplaceWith("third"))
  val c: C,
  @Deprecated("Use fourth instead", ReplaceWith("fourth"))
  val d: D,
  @Deprecated("Use fifth instead", ReplaceWith("fifth"))
  val e: E
) {

  val first: A = a
  val second: B = b
  val third: C = c
  val fourth: D = d
  val fifth: E = e

  override fun toString(): String =
    "($a, $b, $c, $d, $e)"

  companion object
}

operator fun <A : Comparable<A>, B : Comparable<B>, C : Comparable<C>, D : Comparable<D>, E : Comparable<E>>
Tuple5<A, B, C, D, E>.compareTo(other: Tuple5<A, B, C, D, E>): Int {
  val first = a.compareTo(other.a)
  return if (first == 0) {
    val second = b.compareTo(other.b)
    if (second == 0) {
      val third = c.compareTo(other.c)
      if (third == 0) {
        val fourth = d.compareTo(other.d)
        if (fourth == 0) e.compareTo(other.e)
        else fourth
      } else third
    } else second
  } else first
}
