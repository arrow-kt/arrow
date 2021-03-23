package arrow.optics

import arrow.core.MapK
import arrow.core.SetK
import arrow.core.k

/**
 * [Iso] that defines the equality between a Unit value [Map] and a [Set] with its keys
 */
@Deprecated(
  "MapK is being deprMapInstanceTest.ktecated, use the function defined for Map instead.",
  ReplaceWith(
    "Iso.mapToSet<K>()",
    "arrow.optics.Iso"
  ),
  DeprecationLevel.WARNING
)
fun <K> MapK.Companion.toSetK(): Iso<MapK<K, Unit>, SetK<K>> = Iso(
  get = { it.keys.k() },
  reverseGet = { keys -> keys.map { it to Unit }.toMap().k() }
)
