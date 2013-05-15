package org.funktionale.utils

/**
 * Created by IntelliJ IDEA.
 * @author Mario Arias
 * Date: 15/05/13
 * Time: 13:03
 */
trait GetterOperation<K, V> {
    val getter: (K) -> V
    public fun get(key: K): V {
        return getter(key)
    }
}

class GetterOperationImpl<K, V>(override val getter: (K) -> V): GetterOperation<K, V>

