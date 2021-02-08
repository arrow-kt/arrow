@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.typeclasses.Hash
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation
import arrow.typeclasses.defaultSalt

class ForTuple3 private constructor() {
  companion object
}
typealias Tuple3Of<A, B, C> = arrow.Kind3<ForTuple3, A, B, C>
typealias Tuple3PartialOf<A, B> = arrow.Kind2<ForTuple3, A, B>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
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

fun <A, B, C> Triple<A, B, C>.hashWithSalt(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  salt: Int
): Int =
  HA.run {
    HB.run {
      HC.run {
        first.hashWithSalt(
          second.hashWithSalt(
            third.hashWithSalt(salt)
          ))
      }
    }
  }

fun <A, B, C> Triple<A, B, C>.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>
): Int = hashWithSalt(HA, HB, HC, defaultSalt)

private class TripleHash<A, B, C>(
  private val HA: Hash<A>,
  private val HB: Hash<B>,
  private val HC: Hash<C>
) : Hash<Triple<A, B, C>> {
  override fun Triple<A, B, C>.hashWithSalt(salt: Int): Int =
    hashWithSalt(HA, HB, HC, salt)
}

fun <A, B, C> Hash.Companion.triple(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>
): Hash<Triple<A, B, C>> =
  TripleHash(HA, HB, HC)

operator fun <A : Comparable<A>, B : Comparable<B>, C : Comparable<C>> Triple<A, B, C>.compareTo(other: Triple<A, B, C>): Int {
  val first = first.compareTo(other.first)
  return if (first == 0) {
    val second = second.compareTo(other.second)
    if (second == 0) third.compareTo(other.third)
    else second
  } else first
}
