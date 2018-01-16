package arrow.syntax.internal

import java.util.concurrent.ConcurrentHashMap

object Platform {

    fun <K, V> newConcurrentMap(): MutableMap<K, V> =
            ConcurrentHashMap()
}