package arrow.core

import kotlinx.library.watchdog.ExemptionReason
import kotlinx.library.watchdog.IntentionallyMutableCollection
import java.util.SortedMap

@IntentionallyMutableCollection(reason = ExemptionReason.API_DESIGN)
public fun <A, B, C> SortedMap<A, B>.foldLeft(b: SortedMap<A, C>, f: (SortedMap<A, C>, Map.Entry<A, B>) -> SortedMap<A, C>): SortedMap<A, C> {
  var result = b
  this.forEach { result = f(result, it) }
  return result
}
