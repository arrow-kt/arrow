@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

@Deprecated("Deprecated in favor of Kotlin's Triple", ReplaceWith("Triple(a, b, c)"))
data class Tuple3<out A, out B, out C>(val a: A, val b: B, val c: C) {

  override fun toString(): String =
    "($a, $b, $c)"

  companion object
}

operator fun <A : Comparable<A>, B : Comparable<B>, C : Comparable<C>> Triple<A, B, C>.compareTo(other: Triple<A, B, C>): Int {
  val first = first.compareTo(other.first)
  return if (first == 0) {
    val second = second.compareTo(other.second)
    if (second == 0) third.compareTo(other.third)
    else second
  } else first
}
