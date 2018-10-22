package arrow.optics.instances

import arrow.Kind
import arrow.core.*
import arrow.data.*
import arrow.extension
import arrow.optics.*
import arrow.optics.typeclasses.At
import arrow.optics.typeclasses.Each
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.typeclasses.Applicative

/**
 * [At] instance definition for [MapK].
 */
@extension
interface MapKAtInstance<K, V> : At<MapK<K, V>, K, Option<V>> {
  override fun at(i: K): Lens<MapK<K, V>, Option<V>> = PLens(
    get = { it.fix().getOption(i) },
    set = { map, optV ->
      optV.fold({
        (map - i).k()
      }, {
        (map + (i to it)).k()
      })
    }
  )
}

/**
 * [Traversal] for [MapK] that has focus in each [V].
 *
 * @receiver [MapK.Companion] to make it statically available.
 * @return [Traversal] with source [MapK] and focus every [V] of the source.
 */
fun <K, V> MapK.Companion.traversal(): Traversal<MapK<K, V>, V> = object : Traversal<MapK<K, V>, V> {
  override fun <F> modifyF(FA: Applicative<F>, s: MapK<K, V>, f: (V) -> Kind<F, V>): Kind<F, MapK<K, V>> =
    s.traverse(FA, f)
}

/**
 * [Each] instance definition for [Map].
 */
@extension
interface MapKEachInstance<K, V> : Each<MapK<K, V>, V> {
  override fun each(): Traversal<MapK<K, V>, V> =
    MapK.traversal()
}

/**
 * [FilterIndex] instance definition for [Map].
 */
@extension
interface MapKFilterIndexInstance<K, V> : FilterIndex<MapK<K, V>, K, V> {
  override fun filter(p: (K) -> Boolean): Traversal<MapK<K, V>, V> = object : Traversal<MapK<K, V>, V> {
    override fun <F> modifyF(FA: Applicative<F>, s: MapK<K, V>, f: (V) -> Kind<F, V>): Kind<F, MapK<K, V>> = FA.run {
      s.toList().k().traverse(FA) { (k, v) ->
        (if (p(k)) f(v) else just(v)).map {
          k to it
        }
      }.map { it.toMap().k() }
    }
  }
}

/**
 * [Index] instance definition for [Map].
 */
@extension
interface MapKIndexInstance<K, V> : Index<MapK<K, V>, K, V> {
  override fun index(i: K): Optional<MapK<K, V>, V> = POptional(
    getOrModify = { it[i]?.right() ?: it.left() },
    set = { m, v -> m.mapValues { (k, vv) -> if (k == i) v else vv }.k() }
  )
}