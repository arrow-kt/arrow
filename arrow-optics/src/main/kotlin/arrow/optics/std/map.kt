package arrow.optics

import arrow.data.*
import arrow.optics.Iso
import arrow.optics.PIso

/**
 * [PIso] that defines the equality between a [Map] and a [arrow.MapKW]
 */
fun <K, A, B> pMapToMapKW(): PIso<Map<K, A>, Map<K, B>, MapKW<K, A>, MapKW<K, B>> = PIso(
        get = { it.k() },
        reverseGet = { it.map }
)

/**
 * [Iso] that defines the equality between a [Map] and a [arrow.MapKW]
 */
fun <K, A> mapToMapKW(): Iso<Map<K, A>, MapKW<K, A>> = pMapToMapKW()

/**
 * [Iso] that defines the equality between a Unit value [Map] and a [Set] with its keys
 */
fun <K> mapToSet(): Iso<Map<K, Unit>, Set<K>> = Iso(
        get = { it.keys },
        reverseGet = { keys -> keys.map { it to Unit }.toMap() }
)

/**
 * [Iso] that defines the equality between a [MapKW] and a [ListKW] of key value products.
 */
fun <K, A> mapToList(): Iso<MapKW<K, A>, ListKW<Pair<K, A>>> = Iso(
        get = { it.toList().k() },
        reverseGet = { it.toMap().k() }
)