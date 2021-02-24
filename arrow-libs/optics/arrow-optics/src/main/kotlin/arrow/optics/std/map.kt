package arrow.optics

import arrow.Kind
import arrow.core.MapK
import arrow.core.Option
import arrow.core.SetK
import arrow.core.getOption
import arrow.core.k
import arrow.core.left
import arrow.core.right
import arrow.optics.typeclasses.At
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.typeclasses.Applicative

/**
 * [Iso] that defines the equality between a Unit value [Map] and a [Set] with its keys
 */
@Deprecated(
  "MapK is being deprecated, use the function defined for Map instead.",
  ReplaceWith(
    "Iso.mapToSet<K>()",
    "arrow.optics.Iso", "arrow.optics.mapToSet"
  ),
  DeprecationLevel.WARNING
)
fun <K> MapK.Companion.toSetK(): Iso<MapK<K, Unit>, SetK<K>> = Iso(
  get = { it.keys.k() },
  reverseGet = { keys -> keys.map { it to Unit }.toMap().k() }
)

/**
 * [Iso] that defines the equality between a Unit value [Map] and a [Set] with its keys
 */
fun <K> PIso.Companion.mapToSet(): Iso<Map<K, Unit>, Set<K>> = Iso(
  get = { it.keys },
  reverseGet = { keys -> keys.map { it to Unit }.toMap() }
)

fun <K, V> At.Companion.map(): At<Map<K, V>, K, Option<V>> = At { i ->
  PLens(
    get = { it.getOption(i) },
    set = { map, optV ->
      optV.fold(
        {
          (map - i)
        },
        {
          (map + (i to it))
        }
      )
    }
  )
}

fun <K, V> FilterIndex.Companion.map(): FilterIndex<Map<K, V>, K, V> = FilterIndex { p ->
  object : Traversal<Map<K, V>, V> {
    override fun <F> modifyF(FA: Applicative<F>, s: Map<K, V>, f: (V) -> Kind<F, V>): Kind<F, Map<K, V>> = FA.run {
      s.toList().k().traverse(FA) { (k, v) ->
        (if (p(k)) f(v) else just(v)).map {
          k to it
        }
      }.map { it.toMap() }
    }
  }
}

fun <K, V> Index.Companion.map(): Index<Map<K, V>, K, V> = Index { i ->
  POptional(
    getOrModify = { it[i]?.right() ?: it.left() },
    set = { m, v -> m.mapValues { (k, vv) -> if (k == i) v else vv } }
  )
}

fun <K, V> PTraversal.Companion.map(): Traversal<Map<K, V>, V> = object : Traversal<Map<K, V>, V> {
  override fun <F> modifyF(FA: Applicative<F>, s: Map<K, V>, f: (V) -> Kind<F, V>): Kind<F, Map<K, V>> = FA.run {
    s.k().traverse(FA, f)
  }
}
