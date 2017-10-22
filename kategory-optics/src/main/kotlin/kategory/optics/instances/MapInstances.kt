package kategory.optics

import kategory.MapKW
import kategory.k

/**
 * [PIso] that defines the equality between a [Map] and a [kategory.MapKW]
 */
fun <K, A, B> pMapToMapKW(): PIso<Map<K, A>, Map<K, B>, MapKW<K, A>, MapKW<K, B>> = PIso(
        get = { it.k() },
        reverseGet = { it.map }
)

/**
 * [Iso] that defines the equality between a [Map] and a [kategory.MapKW]
 */
fun <K, A> mapToMapKW(): Iso<Map<K, A>, MapKW<K, A>> = pMapToMapKW()

/**
 * [Iso] that defines the equality between a Unit value [Map] and a [Set] with its keys
 */
fun <K> mapToSet(): Iso<Map<K, Unit>, Set<K>> = Iso(
        get= { it.keys },
        reverseGet = { keys -> keys.map { it to Unit }.toMap() }
)