@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.typeclasses.Semigroup
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

public operator fun <A : Comparable<A>, B : Comparable<B>> Pair<A, B>.compareTo(other: Pair<A, B>): Int {
  val first = first.compareTo(other.first)
  return if (first == 0) second.compareTo(other.second)
  else first
}

public fun <A, B> Pair<A, B>.combine(SA: Semigroup<A>, SB: Semigroup<B>, b: Pair<A, B>): Pair<A, B> {
  val (xa, xb) = this
  val (ya, yb) = b
  return Pair(SA.run { xa.combine(ya) }, SB.run { xb.combine(yb) })
}
