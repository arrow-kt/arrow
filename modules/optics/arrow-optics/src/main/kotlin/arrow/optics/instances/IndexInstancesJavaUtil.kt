package java_util

import arrow.optics.Optional
import arrow.optics.POptional
import arrow.optics.typeclasses.Index
import arrow.syntax.either.left
import arrow.syntax.either.right

interface ListIndexInstance<A> : Index<List<A>, Int, A> {
    override fun index(i: Int): Optional<List<A>, A> = POptional(
            getOrModify = { it.getOrNull(i)?.right() ?: it.left() },
            set = { a -> { l -> l.mapIndexed { index: Int, aa: A -> if (index == i) a else aa } } }
    )
}

object ListIndexInstanceImplicits {
    @JvmStatic
    fun <A> instance(): Index<List<A>, Int, A> = object : ListIndexInstance<A> {}
}

interface MapIndexInstance<K, V> : Index<Map<K, V>, K, V> {
    override fun index(i: K): Optional<Map<K, V>, V> = POptional(
            getOrModify = { it[i]?.right() ?: it.left() },
            set = { v -> { m -> m.mapValues { (k, vv) -> if (k == i) v else vv } } }
    )
}

object MapIndexInstanceImplicits {
    @JvmStatic
    fun <K, V> instance(): Index<Map<K, V>, K, V> = object : MapIndexInstance<K, V> {}
}