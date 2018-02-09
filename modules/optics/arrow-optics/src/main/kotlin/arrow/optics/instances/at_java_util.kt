package java_util

import arrow.core.Option
import arrow.data.getOption
import arrow.optics.Lens
import arrow.optics.PLens
import arrow.optics.typeclasses.At

interface MapAtInstance<K, V> : At<Map<K, V>, K, Option<V>> {
    override fun at(i: K): Lens<Map<K, V>, Option<V>> = Lens(
            get = { it.getOption(i) },
            set = { optV ->
                { map ->
                    optV.fold({
                        (map - i)
                    }, {
                        (map + (i to it))
                    })
                }
            }
    )
}

object MapAtInstanceImplicits {
    @JvmStatic
    fun <K, V> instance(): At<Map<K, V>, K, Option<V>> = object : MapAtInstance<K, V> {}
}

interface SetAtInstance<A> : At<Set<A>, A, Boolean> {
    override fun at(i: A): Lens<Set<A>, Boolean> = PLens(
            get = { it.contains(i) },
            set = { b -> { (if (b) it + i else it - i) } }
    )
}

object SetAtInstanceImplicits {
    @JvmStatic
    fun <A> instance(): At<Set<A>, A, Boolean> = object : SetAtInstance<A> {}
}