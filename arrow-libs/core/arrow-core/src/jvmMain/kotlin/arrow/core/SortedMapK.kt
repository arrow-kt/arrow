package arrow.core

import java.util.SortedMap

fun <A, B, C> SortedMap<A, B>.foldLeft(b: SortedMap<A, C>, f: (SortedMap<A, C>, Map.Entry<A, B>) -> SortedMap<A, C>): SortedMap<A, C> {
  var result = b
  this.forEach { result = f(result, it) }
  return result
}

@Deprecated("SortedMap<K, A>.foldRight is being deprecated because its functionality differs from other definitions of foldRight within arrow.")
fun <A : Comparable<A>, B, C> SortedMap<A, B>.foldRight(b: SortedMap<A, C>, f: (Map.Entry<A, B>, SortedMap<A, C>) -> SortedMap<A, C>): SortedMap<A, C> =
  this.entries.reversed().fold(b) { x: SortedMap<A, C>, y: Map.Entry<A, B> -> f(y, x) }
