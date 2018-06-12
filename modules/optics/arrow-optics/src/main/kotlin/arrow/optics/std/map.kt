package arrow.optics

import arrow.data.MapK
import arrow.data.SetK
import arrow.data.fix
import arrow.data.k

/**
 * [Iso] that defines the equality between a Unit value [Map] and a [Set] with its keys
 */
fun <K> MapK.Companion.toSetK(): Iso<MapK<K, Unit>, SetK<K>> = Iso(
  get = { it.fix().keys.k() },
  reverseGet = { keys -> keys.fix().map { it to Unit }.toMap().k() }
)
