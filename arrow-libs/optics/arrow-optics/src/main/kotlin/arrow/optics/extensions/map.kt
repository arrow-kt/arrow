package arrow.optics.extensions

import arrow.Kind
import arrow.core.MapInstances
import arrow.core.Option
import arrow.core.Predicate
import arrow.core.getOption
import arrow.core.k
import arrow.core.left
import arrow.core.right
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.PLens
import arrow.optics.POptional
import arrow.optics.Traversal
import arrow.optics.typeclasses.At
import arrow.optics.typeclasses.Each
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.typeclasses.Applicative

@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass.",
  ReplaceWith(
    "At.map<K, V>()",
    "arrow.optics.map", "arrow.optics.typeclasses.At"
  ),
  DeprecationLevel.WARNING
)
fun <K, V> MapInstances.at(): At<Map<K, V>, K, Option<V>> = MapAt()

/**
 * [At] instance definition for [Map].
 */
@Deprecated(
  "Typeclass interface implementation will not be exposed directly anymore.",
  ReplaceWith(
    "At.map<K, V>()",
    "arrow.optics.map", "arrow.optics.typeclasses.At"
  ),
  DeprecationLevel.WARNING
)
interface MapAt<K, V> : At<Map<K, V>, K, Option<V>> {
  override fun at(i: K): Lens<Map<K, V>, Option<V>> = PLens(
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

  companion object {
    /**
     * Operator overload to instantiate typeclass instance.
     *
     * @return [Index] instance for [String]
     */
    operator fun <K, V> invoke() = object : MapAt<K, V> {}
  }
}

@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass.",
  ReplaceWith(
    "Traversal.map<K, V>()",
    "arrow.optics.Traversal", "arrow.optics.map"
  ),
  DeprecationLevel.WARNING
)
fun <K, V> MapInstances.traversal(): Traversal<Map<K, V>, V> = MapTraversal()

/**
 * [Traversal] for [Map] that focuses in each [V] of the source [Map].
 */
@Deprecated(
  "Typeclass interface implementation will not be exposed directly anymore.",
  ReplaceWith(
    "Traversal.map<K, V>()",
    "arrow.optics.Traversal", "arrow.optics.map"
  ),
  DeprecationLevel.WARNING
)
interface MapTraversal<K, V> : Traversal<Map<K, V>, V> {
  override fun <F> modifyF(FA: Applicative<F>, s: Map<K, V>, f: (V) -> Kind<F, V>): Kind<F, Map<K, V>> = FA.run {
    s.k().traverse(FA, f)
  }

  companion object {
    /**
     * Operator overload to instantiate typeclass instance.
     *
     * @return [Index] instance for [String]
     */
    operator fun <K, V> invoke(): MapTraversal<K, V> = object : MapTraversal<K, V> {}
  }
}

@Deprecated(
  "Each is being deprecated. Use the instance for Map from Traversal's companion object instead.",
  ReplaceWith(
    "Traversal.map<K, V>()",
    "arrow.optics.Traversal", "arrow.optics.map"
  ),
  DeprecationLevel.WARNING
)
fun <K, V> MapInstances.each(): Each<Map<K, V>, V> = MapEach()

/**
 * [Each] instance definition for [Map].
 */
@Deprecated(
  "Each is being deprecated. Use the instance for Map from Traversal's companion object instead.",
  ReplaceWith(
    "Traversal.map<K, V>()",
    "arrow.optics.Traversal", "arrow.optics.map"
  ),
  DeprecationLevel.WARNING
)
interface MapEach<K, V> : Each<Map<K, V>, V> {
  override fun each() = MapTraversal<K, V>()

  companion object {
    /**
     * Operator overload to instantiate typeclass instance.
     *
     * @return [Index] instance for [String]
     */
    operator fun <K, V> invoke() = object : MapEach<K, V> {}
  }
}

@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass.",
  ReplaceWith(
    "FilterIndex.map<K, V>()",
    "arrow.optics.map", "arrow.optics.typeclasses.FilterIndex"
  ),
  DeprecationLevel.WARNING
)
fun <K, V> MapInstances.filterIndex(): FilterIndex<Map<K, V>, K, V> = filterMapIndex()

/**
 * [FilterIndex] instance definition for [Map].
 */
@Deprecated(
  "Typeclass interface implementation will not be exposed directly anymore.",
  ReplaceWith(
    "FilterIndex.map<K, V>()",
    "arrow.optics.map", "arrow.optics.typeclasses.FilterIndex"
  ),
  DeprecationLevel.WARNING
)
interface filterMapIndex<K, V> : FilterIndex<Map<K, V>, K, V> {
  override fun filter(p: Predicate<K>) = object : Traversal<Map<K, V>, V> {
    override fun <F> modifyF(FA: Applicative<F>, s: Map<K, V>, f: (V) -> Kind<F, V>): Kind<F, Map<K, V>> = FA.run {
      s.toList().k().traverse(FA) { (k, v) ->
        (if (p(k)) f(v) else just(v)).map {
          k to it
        }
      }.map { it.toMap() }
    }
  }

  companion object {
    /**
     * Operator overload to instantiate typeclass instance.
     *
     * @return [Index] instance for [String]
     */
    operator fun <K, V> invoke() = object : filterMapIndex<K, V> {}
  }
}

@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass.",
  ReplaceWith(
    "Index.map<K, V>()",
    "arrow.optics.map", "arrow.optics.typeclasses.Index"
  ),
  DeprecationLevel.WARNING
)
fun <K, V> MapInstances.index(): Index<Map<K, V>, K, V> = MapIndex()

/**
 * [Index] instance definition for [Map].
 */
@Deprecated(
  "Typeclass interface implementation will not be exposed directly anymore.",
  ReplaceWith(
    "Index.map<K, V>()",
    "arrow.optics.map", "arrow.optics.typeclasses.Index"
  ),
  DeprecationLevel.WARNING
)
interface MapIndex<K, V> : Index<Map<K, V>, K, V> {
  override fun index(i: K): Optional<Map<K, V>, V> = POptional(
    getOrModify = { it[i]?.right() ?: it.left() },
    set = { m, v -> m.mapValues { (k, vv) -> if (k == i) v else vv } }
  )

  companion object {
    /**
     * Operator overload to instantiate typeclass instance.
     *
     * @return [Index] instance for [String]
     */
    operator fun <K, V> invoke() = object : MapIndex<K, V> {}
  }
}
