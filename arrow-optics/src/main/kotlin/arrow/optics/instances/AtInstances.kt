package arrow.optics.instances

import arrow.core.Option
import arrow.core.toT
import arrow.data.ListKW
import arrow.data.ListKWHK
import arrow.data.ListKWKind
import arrow.data.MapKW
import arrow.data.SetKW
import arrow.data.ev
import arrow.data.getOption
import arrow.data.k
import arrow.data.traverse
import arrow.instance
import arrow.optics.Lens
import arrow.optics.Traversal
import arrow.optics.typeclasses.At
import arrow.optics.typeclasses.FilterIndex
import arrow.syntax.collections.firstOption
import arrow.syntax.option.toOption

@instance(MapKW::class)
interface MapKWAtInstance<K, V> : At<MapKW<K, V>, K, Option<V>> {
    override fun at(i: K): Lens<MapKW<K, V>, Option<V>> = Lens(
            get = { it.ev().getOption(i) },
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

@instance(SetKW::class)
interface SetKWAtInstance<A> : At<SetKW<A>, A, Boolean> {
    override fun at(i: A): Lens<SetKW<A>, Boolean> = Lens(
            get = { it.contains(i) },
            set = { b -> { (if (b) it + i else it - i).k() } }
    )
}
