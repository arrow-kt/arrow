package arrow.optics.instances

import arrow.core.Option
import arrow.data.MapK
import arrow.data.SetK
import arrow.data.reify
import arrow.data.getOption
import arrow.data.k
import arrow.instance
import arrow.optics.Lens
import arrow.optics.typeclasses.At

@instance(MapK::class)
interface MapKAtInstance<K, V> : At<MapK<K, V>, K, Option<V>> {
    override fun at(i: K): Lens<MapK<K, V>, Option<V>> = Lens(
            get = { it.extract().getOption(i) },
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

@instance(SetK::class)
interface SetKAtInstance<A> : At<SetK<A>, A, Boolean> {
    override fun at(i: A): Lens<SetK<A>, Boolean> = Lens(
            get = { it.contains(i) },
            set = { b -> { (if (b) it + i else it - i).k() } }
    )
}
