package arrow.optics

import arrow.data.MapK
import arrow.data.SetK
import arrow.data.k

/**
 * [Iso] that defines the equality between a Unit value [Map] and a [Set] with its keys
 */
fun <K> MapK.Companion.toSetK(): Iso<MapK<K, Unit>, SetK<K>> = Iso(
  get = { it.keys.k() },
  reverseGet = { keys -> keys.map { it to Unit }.toMap().k() }
)