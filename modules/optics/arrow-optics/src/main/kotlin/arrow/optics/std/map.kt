package arrow.optics

import arrow.data.*

/**
 * [PIso] that defines the equality between a [Map] and a [arrow.MapK]
 */
fun <K, A, B> pMapToMapK(): PIso<Map<K, A>, Map<K, B>, MapK<K, A>, MapK<K, B>> = PIso(
        get = { it.k() },
        reverseGet = { it.map }
)

/**
 * [Iso] that defines the equality between a [Map] and a [arrow.MapK]
 */
fun <K, A> mapToMapK(): Iso<Map<K, A>, MapK<K, A>> = pMapToMapK()

/**
 * [Iso] that defines the equality between a Unit value [Map] and a [Set] with its keys
 */
fun <K> mapKToSetK(): Iso<MapK<K, Unit>, SetK<K>> = Iso(
        get = { it.keys.k() },
        reverseGet = { keys -> keys.map { it to Unit }.toMap().k() }
)
