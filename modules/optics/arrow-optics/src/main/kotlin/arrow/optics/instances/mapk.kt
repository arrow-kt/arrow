package arrow.optics.instances

import arrow.Kind
import arrow.core.Left
import arrow.core.Option
import arrow.core.Right
import arrow.data.*
import arrow.instance
import arrow.optics.*
import arrow.optics.typeclasses.At
import arrow.optics.typeclasses.Each
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.typeclasses.Applicative

@instance(MapK::class)
interface MapKAtInstance<K, V> : At<MapK<K, V>, K, Option<V>> {
    override fun at(i: K): Lens<MapK<K, V>, Option<V>> = PLens(
            get = { it.fix().getOption(i) },
            set = { optV ->
                { map ->
                    optV.fold({
                        (map - i).k()
                    }, {
                        (map + (i to it)).k()
                    })
                }
            }
    )
}

@instance(MapK::class)
interface MapKEachInstance<K, V> : Each<MapKOf<K, V>, V> {
    override fun each(): Traversal<MapKOf<K, V>, V> =
            Traversal.fromTraversable(MapK.traverse())
}

@instance(MapK::class)
interface MapKFilterIndexInstance<K, V> : FilterIndex<MapKOf<K, V>, K, V> {

    override fun filter(p: (K) -> Boolean): Traversal<MapKOf<K, V>, V> = object : Traversal<MapKOf<K, V>, V> {
        override fun <F> modifyF(FA: Applicative<F>, s: Kind<Kind<ForMapK, K>, V>, f: (V) -> Kind<F, V>): Kind<F, Kind<Kind<ForMapK, K>, V>> = FA.run {
            ListK.traverse().run {
                traverse(s.fix().map.toList().k(), { (k, v) ->
                    (if (p(k)) f(v) else pure(v)).map() {
                        k to it
                    }
                })
            }.let {
                it.map() {
                    it.toMap().k()
                }
            }
        }
    }
}

@instance(MapK::class)
interface MapKIndexInstance<K, V> : Index<MapKOf<K, V>, K, V> {
    override fun index(i: K): Optional<MapKOf<K, V>, V> = POptional(
            getOrModify = { it.fix()[i]?.let(::Right) ?: it.let(::Left) },
            set = { v -> { m -> m.fix().mapValues { (k, vv) -> if (k == i) v else vv }.k() } }
    )
}