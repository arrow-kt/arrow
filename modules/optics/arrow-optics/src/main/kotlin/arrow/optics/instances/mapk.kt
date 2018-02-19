package arrow.optics.instances

import arrow.Kind
import arrow.core.Option
import arrow.data.ForMapK
import arrow.data.ListK
import arrow.data.MapK
import arrow.data.MapKOf
import arrow.data.MapKPartialOf
import arrow.data.fix
import arrow.data.getOption
import arrow.data.k
import arrow.data.traverse
import arrow.instance
import arrow.optics.Lens
import arrow.optics.Optional
import arrow.optics.PLens
import arrow.optics.POptional
import arrow.optics.Traversal
import arrow.optics.fromTraversable
import arrow.optics.typeclasses.At
import arrow.optics.typeclasses.Each
import arrow.optics.typeclasses.FilterIndex
import arrow.optics.typeclasses.Index
import arrow.syntax.either.left
import arrow.syntax.either.right
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
interface MapKEachInstance<K> : Each<MapKPartialOf<K>, K> {
    override fun each(): Traversal<MapKPartialOf<K>, K> =
            Traversal.fromTraversable()
}

@instance(MapK::class)
interface MapKFilterIndexInstance<K, V> : FilterIndex<MapKOf<K, V>, K, V> {

    override fun filter(p: (K) -> Boolean): Traversal<MapKOf<K, V>, V> = object : Traversal<MapKOf<K, V>, V> {
        override fun <F> modifyF(FA: Applicative<F>, s: Kind<Kind<ForMapK, K>, V>, f: (V) -> Kind<F, V>): Kind<F, Kind<Kind<ForMapK, K>, V>> =
                ListK.traverse().traverse(s.fix().map.toList().k(), { (k, v) ->
                    FA.map(if (p(k)) f(v) else FA.pure(v)) {
                        k to it
                    }
                }, FA).let {
                    FA.map(it) {
                        it.toMap().k()
                    }
                }

    }
}

@instance(MapK::class)
interface MapKIndexInstance<K, V> : Index<MapKOf<K, V>, K, V> {
    override fun index(i: K): Optional<MapKOf<K, V>, V> = POptional(
            getOrModify = { it.fix()[i]?.right() ?: it.left() },
            set = { v -> { m -> m.fix().mapValues { (k, vv) -> if (k == i) v else vv }.k() } }
    )
}