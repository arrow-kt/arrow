@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.typeclasses.Semigroup
import arrow.typeclasses.SemigroupDeprecation
import arrow.typeclasses.combine
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public operator fun <A : Comparable<A>, B : Comparable<B>> Pair<A, B>.compareTo(other: Pair<A, B>): Int {
  val first = first.compareTo(other.first)
  return if (first == 0) second.compareTo(other.second)
  else first
}


@Deprecated(
  "$SemigroupDeprecation\n$NicheAPI",
  ReplaceWith(
    "Pair(SA.combine(first, b.first), SB.combine(second, b.second))",
    "arrow.typeclasses.combine"
  )
)
public fun <A, B> Pair<A, B>.combine(SA: Semigroup<A>, SB: Semigroup<B>, b: Pair<A, B>): Pair<A, B> =
  Pair(SA.combine(first, b.first), SB.combine(second, b.second))
