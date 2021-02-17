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
class ForTuple6 private constructor() {
  companion object
}

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
typealias Tuple6Of<A, B, C, D, E, F> = arrow.Kind6<ForTuple6, A, B, C, D, E, F>

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
typealias Tuple6PartialOf<A, B, C, D, E> = arrow.Kind5<ForTuple6, A, B, C, D, E>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
inline fun <A, B, C, D, E, F> Tuple6Of<A, B, C, D, E, F>.fix(): Tuple6<A, B, C, D, E, F> =
  this as Tuple6<A, B, C, D, E, F>

data class Tuple6<out A, out B, out C, out D, out E, out F>(
  @Deprecated("Use first instead", ReplaceWith("first"))
  val a: A,
  @Deprecated("Use second instead", ReplaceWith("second"))
  val b: B,
  @Deprecated("Use third instead", ReplaceWith("third"))
  val c: C,
  @Deprecated("Use fourth instead", ReplaceWith("fourth"))
  val d: D,
  @Deprecated("Use fifth instead", ReplaceWith("fifth"))
  val e: E,
  @Deprecated("Use sixth instead", ReplaceWith("sixth"))
  val f: F
) : Tuple6Of<A, B, C, D, E, F> {

  val first: A = a
  val second: B = b
  val third: C = c
  val fourth: D = d
  val fifth: E = e
  val sixth: F = f

  @Deprecated(ShowDeprecation)
  fun show(SA: Show<A>, SB: Show<B>, SC: Show<C>, SD: Show<D>, SE: Show<E>, SF: Show<F>): String =
    "(" + listOf(SA.run { a.show() }, SB.run { b.show() }, SC.run { c.show() }, SD.run { d.show() }, SE.run { e.show() }, SF.run { f.show() }).joinToString(", ") + ")"

  override fun toString(): String =
    "($a, $b, $c, $d, $e, $f)"

  companion object
}

operator fun <A : Comparable<A>, B : Comparable<B>, C : Comparable<C>, D : Comparable<D>, E : Comparable<E>, F : Comparable<F>>
Tuple6<A, B, C, D, E, F>.compareTo(other: Tuple6<A, B, C, D, E, F>): Int {
  val first = a.compareTo(other.a)
  return if (first == 0) {
    val second = b.compareTo(other.b)
    if (second == 0) {
      val third = c.compareTo(other.c)
      if (third == 0) {
        val fourth = d.compareTo(other.d)
        if (fourth == 0) {
          val fifth = e.compareTo(other.e)
          if (fifth == 0) f.compareTo(other.f)
          else fifth
        } else fourth
      } else third
    } else second
  } else first
}
