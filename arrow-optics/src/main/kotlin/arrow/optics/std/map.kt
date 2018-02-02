package arrow.optics

import arrow.data.*

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
fun <K> mapKWToSetKW(): Iso<MapKW<K, Unit>, SetKW<K>> = Iso(
        get = { it.keys.k() },
        reverseGet = { keys -> keys.map { it to Unit }.toMap().k() }
)
