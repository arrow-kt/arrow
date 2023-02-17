@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.typeclasses.Semigroup
import arrow.typeclasses.SemigroupDeprecation
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public operator fun <A : Comparable<A>, B : Comparable<B>> Pair<A, B>.compareTo(other: Pair<A, B>): Int {
  val first = first.compareTo(other.first)
  return if (first == 0) second.compareTo(other.second)
  else first
}

public fun <A, B> Pair<A, B>.combine(combineA: (A, A) -> A, combineB: (B, B) -> B, b: Pair<A, B>): Pair<A, B> {
  val (xa, xb) = this
  val (ya, yb) = b
  return Pair(combineA(xa, ya), combineB(xb, yb))
}

@Deprecated(SemigroupDeprecation, ReplaceWith("combine({ x, y -> SA.run { x + y } }, { x, y -> SB.run { x + y } }, b)"))
public fun <A, B> Pair<A, B>.combine(SA: Semigroup<A>, SB: Semigroup<B>, b: Pair<A, B>): Pair<A, B> =
  combine({ x, y -> SA.run { x + y } }, { x, y -> SB.run { x + y } }, b)
