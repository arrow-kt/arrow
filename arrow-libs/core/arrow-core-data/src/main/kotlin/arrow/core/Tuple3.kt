@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.KindDeprecation
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
class ForTuple3 private constructor() {
  companion object
}

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
typealias Tuple3Of<A, B, C> = arrow.Kind3<ForTuple3, A, B, C>

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
typealias Tuple3PartialOf<A, B> = arrow.Kind2<ForTuple3, A, B>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
inline fun <A, B, C> Tuple3Of<A, B, C>.fix(): Tuple3<A, B, C> =
  this as Tuple3<A, B, C>

@Deprecated("Deprecated in favor of Kotlin's Triple", ReplaceWith("Triple(a, b, c)"))
data class Tuple3<out A, out B, out C>(val a: A, val b: B, val c: C) : Tuple3Of<A, B, C> {
  @Deprecated(ShowDeprecation)
  fun show(SA: Show<A>, SB: Show<B>, SC: Show<C>): String =
    "(" + listOf(SA.run { a.show() }, SB.run { b.show() }, SC.run { c.show() }).joinToString(", ") + ")"

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
