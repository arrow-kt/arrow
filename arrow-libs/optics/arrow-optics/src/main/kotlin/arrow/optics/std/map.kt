package arrow.optics

import arrow.core.Option
import arrow.core.getOption
import arrow.core.left
import arrow.core.right
import arrow.optics.typeclasses.At
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.typeclasses.Monoid

/**
 * [Iso] that defines the equality between a Unit value [Map] and a [Set] with its keys
 */
fun <K> PIso.Companion.mapToSet(): Iso<Map<K, Unit>, Set<K>> =
  Iso(
    get = { it.keys },
    reverseGet = { keys -> keys.map { it to Unit }.toMap() }
  )

fun <K, V> FilterIndex.Companion.map(): FilterIndex<Map<K, V>, K, V> =
  FilterIndex { p ->
    object : Every<Map<K, V>, V> {
      override fun <R> foldMap(M: Monoid<R>, source: Map<K, V>, map: (V) -> R): R = M.run {
        source.entries.fold(empty()) { acc, (k, v) ->
          if (p(k)) acc.combine(map(v)) else acc
        }
      }

      override fun modify(source: Map<K, V>, map: (focus: V) -> V): Map<K, V> =
        source.mapValues { (k, v) -> if (p(k)) map(v) else v }
    }
  }

fun <K, V> Index.Companion.map(): Index<Map<K, V>, K, V> =
  Index { i ->
    POptional(
      getOrModify = { it[i]?.right() ?: it.left() },
      set = { m, v -> m.mapValues { (k, vv) -> if (k == i) v else vv } }
    )
  }

fun <K, V> PTraversal.Companion.map(): Traversal<Map<K, V>, V> =
  Traversal { source, f -> source.mapValues { (_, v) -> f(v) } }

fun <K, V> At.Companion.map(): At<Map<K, V>, K, Option<V>> =
  At { i ->
    PLens(
      get = { it.getOption(i) },
      set = { map, optV ->
        optV.fold({
          (map - i)
        }, {
          (map + (i to it))
        })
      }
    )
  }

fun <K, V> PEvery.Companion.map(): Every<Map<K, V>, V> =
  object : Every<Map<K, V>, V> {
    override fun <R> foldMap(M: Monoid<R>, source: Map<K, V>, map: (V) -> R): R =
      M.run { source.values.fold(empty()) { acc, v -> acc.combine(map(v)) } }

    override fun modify(source: Map<K, V>, map: (focus: V) -> V): Map<K, V> =
      source.mapValues { (_, v) -> map(v) }
  }
